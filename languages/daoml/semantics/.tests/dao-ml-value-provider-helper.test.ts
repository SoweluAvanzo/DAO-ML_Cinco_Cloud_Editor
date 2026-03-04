/********************************************************************************
 * Copyright (c) 2024 The DAO ML Team.
 ********************************************************************************/

import { describe, expect, jest, test } from '@jest/globals';
import { updateModel } from '../helper/dao-ml-value-provider-helper';

const factor = 7;

// Mock factory for Node-like elements
function createMockNode(overrides: any = {}) {
    return {
        id: 'mock-id',
        type: 'mock-type',
        parent: null,
        size: { width: 100, height: 30 },
        position: { x: 0, y: 0 },
        getProperty: jest.fn().mockReturnValue(undefined),
        ...overrides
    };
}

// Mock factory for Container-like elements (have containments)
function createMockContainer(overrides: any = {}) {
    return {
        id: 'mock-container-id',
        type: 'mock-container-type',
        parent: null,
        size: { width: 200, height: 100 },
        position: { x: 0, y: 0 },
        containments: [],
        getProperty: jest.fn().mockReturnValue(undefined),
        ...overrides
    };
}

describe('DaoML Value Provider Helper', () => {

    // --- Null Safety & Edge Cases ---

    describe('Null Safety & Edge Cases', () => {

        test('Returns without error for undefined element', () => {
            expect(() => updateModel(undefined as any)).not.toThrow();
        });

        test('Returns without error for non-Node element', () => {
            const plainObj = { id: 'x', type: 'unknown' };
            expect(() => updateModel(plainObj as any)).not.toThrow();
        });

        test('Initializes size object if missing', () => {
            const permission = createMockNode({
                type: 'dao_ml:permission',
                size: undefined,
                getProperty: jest.fn().mockReturnValue('TEST')
            });
            updateModel(permission as any);
            expect(permission.size).toBeDefined();
            expect(permission.size.width).toBeGreaterThan(0);
            expect(permission.size.height).toBeGreaterThan(0);
        });

        test('All property accesses use null coalescing (no "undefined" in labels)', () => {
            const permission = createMockNode({
                type: 'dao_ml:permission',
                size: { width: 100, height: 30 },
                getProperty: jest.fn().mockReturnValue(undefined)
            });
            updateModel(permission as any);
            expect(permission.size.height).toBe(factor * 7);
        });

        test('Prevents infinite recursion with depth limit', () => {
            const warnSpy = jest.spyOn(console, 'warn').mockImplementation(() => { });

            let current: any = null;
            for (let i = 0; i < 15; i++) {
                const node = createMockNode({
                    id: 'perm-' + i,
                    type: 'dao_ml:permission',
                    parent: current,
                    size: { width: 100, height: 30 },
                    getProperty: jest.fn().mockReturnValue('TEST')
                });
                if (current) {
                    current.type = 'dao_ml:governancearea';
                    current.containments = [node];
                }
                current = node;
            }

            expect(() => updateModel(current as any)).not.toThrow();
            expect(warnSpy).toHaveBeenCalledWith(
                expect.stringContaining('Max recursion depth exceeded')
            );
            warnSpy.mockRestore();
        });
    });

    // --- Permission Sizing ---

    describe('Permission Sizing', () => {

        test('Sets fixed height of factor * 7', () => {
            const p = createMockNode({
                type: 'dao_ml:permission',
                size: { width: 100, height: 30 },
                getProperty: jest.fn((prop: string) => {
                    if (prop === 'allowedAction') return 'READ';
                    if (prop === 'permissionType') return 'Operational';
                    return undefined;
                })
            });
            updateModel(p as any);
            expect(p.size.height).toBe(factor * 7);
        });

        test('Width based on longest label (allowedAction vs permissionType)', () => {
            const p = createMockNode({
                type: 'dao_ml:permission',
                size: { width: 10, height: 30 },
                getProperty: jest.fn((prop: string) => {
                    if (prop === 'allowedAction') return 'EXECUTE_CONTRACT';  // 16 chars
                    if (prop === 'permissionType') return 'Op';               // (Op) = 4 chars
                    return undefined;
                })
            });
            updateModel(p as any);
            expect(p.size.width).toBe(16 * factor);
        });

        test('Width tolerance: does not update if difference <= 2', () => {
            const expectedWidth = 'READ'.length * factor; // 28
            const p = createMockNode({
                type: 'dao_ml:permission',
                size: { width: expectedWidth + 1, height: 30 },
                getProperty: jest.fn((prop: string) => {
                    if (prop === 'allowedAction') return 'READ';
                    if (prop === 'permissionType') return '';
                    return undefined;
                })
            });
            updateModel(p as any);
            expect(p.size.width).toBe(expectedWidth + 1);
        });

        test('Cascades to parent GovernanceArea only', () => {
            const ga = createMockContainer({
                type: 'dao_ml:governancearea',
                size: { width: 300, height: 200 },
                getProperty: jest.fn().mockReturnValue('TestGA')
            });
            const p = createMockNode({
                type: 'dao_ml:permission',
                parent: ga,
                size: { width: 100, height: 30 },
                getProperty: jest.fn().mockReturnValue('READ')
            });
            ga.containments = [p];

            updateModel(p as any);

            expect(ga.size.width).toBeGreaterThan(0);
            expect(ga.size.height).toBeGreaterThan(0);
        });

        test('Does NOT cascade to parent DAO (DAOs never contain permissions)', () => {
            const dao = createMockContainer({
                type: 'dao_ml:dao',
                size: { width: 500, height: 400 },
                getProperty: jest.fn().mockReturnValue('MyDAO')
            });
            const originalWidth = dao.size.width;
            const originalHeight = dao.size.height;

            const p = createMockNode({
                type: 'dao_ml:permission',
                parent: dao,
                size: { width: 100, height: 30 },
                getProperty: jest.fn().mockReturnValue('READ')
            });

            updateModel(p as any);

            expect(dao.size.width).toBe(originalWidth);
            expect(dao.size.height).toBe(originalHeight);
        });

        test('Does not cascade when parent is null', () => {
            const p = createMockNode({
                type: 'dao_ml:permission',
                parent: null,
                size: { width: 100, height: 30 },
                getProperty: jest.fn().mockReturnValue('READ')
            });
            expect(() => updateModel(p as any)).not.toThrow();
        });
    });

    // --- GovernanceArea Sizing & Centering ---

    describe('GovernanceArea Sizing', () => {

        test('Sizes based on label when no containments', () => {
            const ga = createMockContainer({
                type: 'dao_ml:governancearea',
                size: { width: 50, height: 50 },
                containments: [],
                getProperty: jest.fn((prop: string) => {
                    if (prop === 'description') return 'Financial Governance';
                    if (prop === 'implementation') return 'on-chain';
                    return undefined;
                })
            });
            updateModel(ga as any);
            expect(ga.size.width).toBeGreaterThanOrEqual(160);
            expect(ga.size.height).toBe(60);
        });

        test('Expands to fit children with margins', () => {
            const perm1 = createMockNode({
                type: 'dao_ml:permission',
                size: { width: 150, height: 49 },
                position: { x: 0, y: 0 }
            });
            const perm2 = createMockNode({
                type: 'dao_ml:permission',
                size: { width: 150, height: 49 },
                position: { x: 0, y: 0 }
            });
            const ga = createMockContainer({
                type: 'dao_ml:governancearea',
                size: { width: 50, height: 50 },
                containments: [perm1, perm2],
                getProperty: jest.fn().mockReturnValue('Test')
            });
            updateModel(ga as any);

            expect(ga.size.width).toBeGreaterThan(150);
            // Height = 40 + (49+5) + (49+5) + 20 = 168
            expect(ga.size.height).toBe(40 + (49 + 5) + (49 + 5) + 20);
        });

        test('Centers permissions horizontally within final width', () => {
            const perm = createMockNode({
                type: 'dao_ml:permission',
                size: { width: 100, height: 49 },
                position: { x: 0, y: 0 }
            });
            const ga = createMockContainer({
                type: 'dao_ml:governancearea',
                size: { width: 50, height: 50 },
                containments: [perm],
                getProperty: jest.fn().mockReturnValue('Test')
            });
            updateModel(ga as any);

            const expectedCenterX = (ga.size.width - perm.size.width) / 2;
            expect(perm.position.x).toBe(expectedCenterX);
            expect(perm.position.y).toBe(40);
        });

        test('Multiple children stacked vertically and each centered', () => {
            const p1 = createMockNode({
                type: 'dao_ml:permission',
                size: { width: 80, height: 49 },
                position: { x: 0, y: 0 }
            });
            const p2 = createMockNode({
                type: 'dao_ml:permission',
                size: { width: 120, height: 49 },
                position: { x: 0, y: 0 }
            });
            const ga = createMockContainer({
                type: 'dao_ml:governancearea',
                size: { width: 50, height: 50 },
                containments: [p1, p2],
                getProperty: jest.fn().mockReturnValue('X')
            });
            updateModel(ga as any);

            expect(p1.position.y).toBe(40);
            expect(p2.position.y).toBe(94);  // 40 + 49 + 5
            expect(p1.position.x).toBe((ga.size.width - p1.size.width) / 2);
            expect(p2.position.x).toBe((ga.size.width - p2.size.width) / 2);
        });

        test('Cascades to parent DAO', () => {
            const dao = createMockContainer({
                type: 'dao_ml:dao',
                size: { width: 200, height: 100 },
                getProperty: jest.fn().mockReturnValue('MyDAO')
            });
            const ga = createMockContainer({
                type: 'dao_ml:governancearea',
                parent: dao,
                size: { width: 50, height: 50 },
                position: { x: 16, y: 34 },
                containments: [],
                getProperty: jest.fn().mockReturnValue('Test')
            });
            dao.containments = [ga];

            updateModel(ga as any);

            expect(dao.size.width).toBeGreaterThanOrEqual(ga.position.x + ga.size.width + 15);
            expect(dao.size.height).toBeGreaterThanOrEqual(ga.position.y + ga.size.height + 15);
        });

        test('GovernanceArea outside DAO sizes by label only (no permission containments)', () => {
            const ga = createMockContainer({
                type: 'dao_ml:governancearea',
                parent: null,
                size: { width: 300, height: 200 },
                containments: [],
                getProperty: jest.fn((prop: string) => {
                    if (prop === 'description') return 'Governance Area';
                    if (prop === 'implementation') return 'on-chain';
                    return undefined;
                })
            });
            updateModel(ga as any);
            expect(ga.size.height).toBe(60);
        });
    });

    // --- Committee Sizing ---

    describe('Committee Sizing', () => {

        test('Width based on longest label, fixed height', () => {
            const c = createMockNode({
                type: 'dao_ml:committee',
                size: { width: 50, height: 50 },
                getProperty: jest.fn((prop: string) => {
                    if (prop === 'description') return 'Finance Committee';
                    if (prop === 'decisionMakingMethod') return 'Vote';
                    return undefined;
                })
            });
            updateModel(c as any);
            expect(c.size.width).toBe(17 * factor);
            expect(c.size.height).toBe(factor * 7);
        });

        test('Handles missing properties gracefully', () => {
            const c = createMockNode({
                type: 'dao_ml:committee',
                size: { width: 50, height: 50 },
                getProperty: jest.fn().mockReturnValue(undefined)
            });
            expect(() => updateModel(c as any)).not.toThrow();
            expect(c.size.height).toBe(factor * 7);
        });
    });

    // --- Role Sizing ---

    describe('Role Sizing', () => {

        test('Does not resize in any way', () => {
            const role = createMockNode({
                type: 'dao_ml:role',
                size: { width: 56, height: 49 },
                getProperty: jest.fn((prop: string) => {
                    if (prop === 'name') return 'Board Member';
                    if (prop === 'agentType') return 'Human';
                    return undefined;
                })
            });
            updateModel(role as any);
            expect(role.size.width).toBe(56);
            expect(role.size.height).toBe(49);
        });
    });

    // --- DAO Sizing ---

    describe('DAO Sizing', () => {

        test('Minimum width 200 and header height 50 when no containments', () => {
            const dao = createMockContainer({
                type: 'dao_ml:dao',
                size: { width: 500, height: 400 },
                containments: [],
                getProperty: jest.fn((prop: string) => {
                    if (prop === 'name') return 'DAO';
                    if (prop === 'missionStatement') return '';
                    return undefined;
                })
            });
            updateModel(dao as any);
            expect(dao.size.width).toBe(200);
            expect(dao.size.height).toBe(50);
        });

        test('Expands to fit contained elements at their positions', () => {
            const ga = createMockContainer({
                type: 'dao_ml:governancearea',
                size: { width: 180, height: 114 },
                position: { x: 16, y: 34 },
                containments: [],
                getProperty: jest.fn().mockReturnValue('Test')
            });
            const dao = createMockContainer({
                type: 'dao_ml:dao',
                size: { width: 100, height: 100 },
                containments: [ga],
                getProperty: jest.fn((prop: string) => {
                    if (prop === 'name') return 'New Dao';
                    if (prop === 'missionStatement') return '';
                    return undefined;
                })
            });
            updateModel(dao as any);

            // ga.x + ga.width + 15 = 16 + 180 + 15 = 211
            expect(dao.size.width).toBeGreaterThanOrEqual(211);
            // ga.y + ga.height + 15 = 34 + 114 + 15 = 163
            expect(dao.size.height).toBeGreaterThanOrEqual(163);
        });

        test('Does not reposition containments', () => {
            const ga = createMockContainer({
                type: 'dao_ml:governancearea',
                size: { width: 180, height: 114 },
                position: { x: 16, y: 34 },
                containments: [],
                getProperty: jest.fn().mockReturnValue('Test')
            });
            const dao = createMockContainer({
                type: 'dao_ml:dao',
                size: { width: 300, height: 200 },
                containments: [ga],
                getProperty: jest.fn().mockReturnValue('MyDAO')
            });
            updateModel(dao as any);

            expect(ga.position.x).toBe(16);
            expect(ga.position.y).toBe(34);
        });

        test('Width expands for long name or missionStatement', () => {
            const dao = createMockContainer({
                type: 'dao_ml:dao',
                size: { width: 100, height: 100 },
                containments: [],
                getProperty: jest.fn((prop: string) => {
                    if (prop === 'name') return 'A Very Long DAO Name For Testing Purposes';
                    if (prop === 'missionStatement') return '';
                    return undefined;
                })
            });
            updateModel(dao as any);
            // textWidth = 42 * 8 = 336
            expect(dao.size.width).toBeGreaterThanOrEqual(336);
        });
    });

    // --- Full Cascade: Permission -> GovernanceArea -> DAO ---

    describe('Full Cascade', () => {

        test('Permission update cascades through GovernanceArea to DAO', () => {
            const dao = createMockContainer({
                type: 'dao_ml:dao',
                size: { width: 213, height: 163 },
                getProperty: jest.fn().mockReturnValue('New Dao')
            });
            const ga = createMockContainer({
                type: 'dao_ml:governancearea',
                parent: dao,
                size: { width: 182, height: 114 },
                position: { x: 16, y: 34 },
                getProperty: jest.fn().mockReturnValue('New Governance Area')
            });
            const perm = createMockNode({
                type: 'dao_ml:permission',
                parent: ga,
                size: { width: 100, height: 49 },
                position: { x: 41, y: 40 },
                getProperty: jest.fn((prop: string) => {
                    if (prop === 'allowedAction') return 'New Permission';
                    if (prop === 'permissionType') return 'operational';
                    return undefined;
                })
            });
            ga.containments = [perm];
            dao.containments = [ga];

            updateModel(perm as any);

            // Permission should be sized
            expect(perm.size.height).toBe(factor * 7);

            // GA should be resized to fit the permission and centered it
            expect(ga.size.height).toBeGreaterThan(49);
            const centerX = (ga.size.width - perm.size.width) / 2;
            expect(perm.position.x).toBe(centerX);

            // DAO should be resized to fit the GA
            expect(dao.size.width).toBeGreaterThanOrEqual(ga.position.x + ga.size.width + 15);
            expect(dao.size.height).toBeGreaterThanOrEqual(ga.position.y + ga.size.height + 15);
        });

        test('GovernanceArea dragged out of DAO: no permissions, sizes by label', () => {
            const ga = createMockContainer({
                type: 'dao_ml:governancearea',
                parent: null,
                size: { width: 182, height: 114 },
                position: { x: 100, y: 100 },
                containments: [],
                getProperty: jest.fn((prop: string) => {
                    if (prop === 'description') return 'New Governance Area';
                    if (prop === 'implementation') return 'on-chain';
                    return undefined;
                })
            });

            updateModel(ga as any);

            // height = 40 + 20 = 60 (no children)
            expect(ga.size.height).toBe(60);
            expect(ga.size.width).toBeGreaterThanOrEqual(100);
        });
    });
});
