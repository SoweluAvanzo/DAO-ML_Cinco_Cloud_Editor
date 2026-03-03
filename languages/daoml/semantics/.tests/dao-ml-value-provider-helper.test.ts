/********************************************************************************
 * Tests for dao-ml-value-provider-helper.ts
 *
 * Covers: updateValue() — size/position recalculation for Permission,
 *         GovernanceArea, and Committee nodes.
 ********************************************************************************/
import {
    GraphModel,
    Node,
    Container,
    ModelElement
} from '@cinco-glsp/cinco-glsp-api';
import { updateValue } from '../helper/dao-ml-value-provider-helper';
import { setupDaoMlMetaSpecification, teardownDaoMlMetaSpecification } from './test-setup';

// ─── Helpers ─────────────────────────────────────────────────────────────────

function makePermission(allowedAction: string, permissionType: string): Node {
    const node = new Node();
    node.type = 'dao_ml:permission';
    node.initializeProperties();
    node.setProperty('allowedAction', allowedAction);
    node.setProperty('permissionType', permissionType);
    node.size = { width: 100, height: 49 };
    node.position = { x: 0, y: 0 };
    return node;
}

function makeGovernanceArea(description: string, implementation: string): Container {
    const container = new Container();
    container.type = 'dao_ml:governancearea';
    container.initializeProperties();
    container.setProperty('description', description);
    container.setProperty('implementation', implementation);
    container.size = { width: 200, height: 150 };
    container.position = { x: 0, y: 0 };
    return container;
}

function makeCommittee(description: string, decisionMakingMethod: string): Node {
    const node = new Node();
    node.type = 'dao_ml:committee';
    node.initializeProperties();
    node.setProperty('description', description);
    node.setProperty('decisionMakingMethod', decisionMakingMethod);
    node.size = { width: 112, height: 49 };
    node.position = { x: 0, y: 0 };
    return node;
}

// ═════════════════════════════════════════════════════════════════════════════

describe('dao-ml-value-provider-helper', () => {
    beforeEach(() => setupDaoMlMetaSpecification());
    afterEach(() => teardownDaoMlMetaSpecification());

    describe('updateValue - Permission', () => {
        test('adjusts permission width to text width', () => {
            const perm = makePermission('Vote on Proposal', 'Operational');
            const origWidth = perm.size.width;

            updateValue(perm, 'changeProperty', false);

            // Width should be recalculated based on label length
            expect(perm.size.width).toBe(
                Math.max('Vote on Proposal'.length, `(Operational)`.length) * 7
            );
        });

        test('sets permission height to factor * 7', () => {
            const perm = makePermission('X', 'Operational');
            updateValue(perm, 'changeProperty', false);

            expect(perm.size.height).toBe(49); // 7 * 7
        });

        test('does not adjust when reason is changeContainer', () => {
            const perm = makePermission('Vote', 'Operational');
            const origWidth = perm.size.width;
            const origHeight = perm.size.height;

            updateValue(perm, 'changeContainer', false);

            // Should NOT adjust when reason is changeContainer
            expect(perm.size.width).toBe(origWidth);
            expect(perm.size.height).toBe(origHeight);
        });

        test('uses longer of label1 and label2 for width', () => {
            const perm = makePermission('A', 'Operational');
            updateValue(perm, 'changeProperty', false);

            // label1 = 'A' (len 1), label2 = '(Operational)' (len 13)
            // label2 is longer, so width = 13 * 7 = 91
            expect(perm.size.width).toBe(13 * 7);
        });
    });

    describe('updateValue - GovernanceArea', () => {
        test('adjusts governance area size based on containments', () => {
            const govArea = makeGovernanceArea('Treasury Management', 'OnChain');
            const perm1 = makePermission('Vote', 'Operational');
            const perm2 = makePermission('Manage', 'Strategic');
            perm1.size = { width: 91, height: 49 };
            perm2.size = { width: 91, height: 49 };
            perm1.position = { x: 0, y: 0 };
            perm2.position = { x: 0, y: 0 };

            govArea.containments.push(perm1);
            govArea.containments.push(perm2);

            updateValue(govArea, 'changeProperty', false);

            // Width should account for containments
            expect(govArea.size.width).toBeGreaterThan(0);
            // Height should account for stacked containments
            expect(govArea.size.height).toBeGreaterThan(49);
        });

        test('repositions containments vertically', () => {
            const govArea = makeGovernanceArea('Data', 'OnChain');
            const perm1 = makePermission('Vote', 'Operational');
            const perm2 = makePermission('Manage', 'Strategic');
            perm1.size = { width: 91, height: 49 };
            perm2.size = { width: 91, height: 49 };
            perm1.position = { x: 0, y: 0 };
            perm2.position = { x: 0, y: 0 };

            govArea.containments.push(perm1);
            govArea.containments.push(perm2);

            updateValue(govArea, 'changeProperty', false);

            // First containment starts at y=40 (top margin)
            expect(perm1.position.y).toBe(40);
            // Second containment below first (40 + 49 + 5 = 94)
            expect(perm2.position.y).toBe(94);
        });

        test('handles governance area with no containments', () => {
            const govArea = makeGovernanceArea('Empty Area', 'Hybrid');
            updateValue(govArea, 'changeProperty', false);

            expect(govArea.size.width).toBeGreaterThan(0);
            expect(govArea.size.height).toBeGreaterThan(0);
        });
    });

    describe('updateValue - Committee', () => {
        test('adjusts committee width based on text', () => {
            const committee = makeCommittee('Governance Board', 'SimpleMajority');
            updateValue(committee, 'changeProperty', false);

            const label1 = 'Governance Board';
            const label2 = '(SimpleMajority)';
            const expectedWidth = Math.max(label1.length, label2.length) * 7;
            expect(committee.size.width).toBe(expectedWidth);
        });

        test('sets committee height to factor * 7', () => {
            const committee = makeCommittee('Board', 'LazyConsensus');
            updateValue(committee, 'changeProperty', false);

            expect(committee.size.height).toBe(49);
        });
    });

    describe('updateValue - non-Node types', () => {
        test('does nothing for GraphModel', () => {
            const model = new GraphModel();
            model.type = 'dao_ml:dao_model';
            model.initializeProperties();

            // Should not throw
            expect(() => updateValue(model as any, 'changeProperty', false)).not.toThrow();
        });
    });
});
