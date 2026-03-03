/********************************************************************************
 * Test setup helper for DAO-ML tests.
 * Registers the DAO-ML MetaSpecification so that GraphModel, Node, Edge,
 * Container type guards and property resolution work correctly in tests.
 *
 * Also exports shared fixtures, decode/encode helpers, and comparison utilities
 * used across multiple test files.
 ********************************************************************************/
import * as fs from 'fs';
import * as path from 'path';
import {
    GraphModel,
    Node,
    Edge,
    Container,
    ModelElement
} from '@cinco-glsp/cinco-glsp-api';
import { MetaSpecification, CompositionSpecification, Enum } from '@cinco-glsp/cinco-glsp-common';
import { decodeSwitch, encodeSwitch } from '../helper/dao-ml-codec-helper';

// ─── Shared fixtures ─────────────────────────────────────────────────────────
const FIXTURES_DIR = path.resolve(__dirname, 'fixtures');

export const MINIMAL_DAO = fs.readFileSync(path.join(FIXTURES_DIR, 'minimal-test.dao'), 'utf-8');
export const EDGE_CASES_DAO = fs.readFileSync(path.join(FIXTURES_DIR, 'edge-cases.dao'), 'utf-8');
export const EMPTY_DAO = fs.readFileSync(path.join(FIXTURES_DIR, 'empty-dao.dao'), 'utf-8');
export const COMPLEX_DAO = fs.readFileSync(path.join(FIXTURES_DIR, 'complex-full-feature.dao'), 'utf-8');
export const WIRE_DAO = fs.readFileSync(path.resolve(FIXTURES_DIR, '../../../WIRE.dao'), 'utf-8');
export const TRAVELHIVE_DAO = fs.readFileSync(path.resolve(FIXTURES_DIR, '../../../Travelhive_final_model.dao'), 'utf-8');

// ─── Shared helpers ──────────────────────────────────────────────────────────
export const noop = (_msg: any): void => { };

/** Decode DAO XML content into a GraphModel. */
export function decodeFull(content: string): GraphModel {
    const model = new GraphModel();
    const result = decodeSwitch('DAO-ML_diagram', content, model, {
        edge_references: [],
        containment_references: [],
        log: noop
    });
    return result as GraphModel;
}

/** Encode a GraphModel back to DAO XML. */
export function encodeFull(model: GraphModel): string {
    return encodeSwitch(model, model)!;
}

// ─── Bisimilarity comparison utilities ───────────────────────────────────────

/** Collect all model elements in a deterministic order for comparison. */
export function collectElements(model: GraphModel): {
    type: string;
    id: string;
    properties: Record<string, any>;
}[] {
    const result: { type: string; id: string; properties: Record<string, any> }[] = [];

    function visit(elem: ModelElement): void {
        const props: Record<string, any> = {};
        try {
            for (const def of elem.propertyDefinitions) {
                try {
                    props[def.name] = elem.getProperty(def.name);
                } catch { /* skip unresolvable */ }
            }
        } catch { /* no property definitions */ }

        result.push({ type: elem.type, id: elem.id, properties: props });

        if (Container.is(elem)) {
            for (const child of (elem as Container).containments as Node[]) {
                visit(child);
            }
        }
    }

    visit(model);
    return result;
}

/** Collect edges in a normalized form for comparison. */
export function collectEdges(model: GraphModel): {
    type: string;
    sourceID: string;
    targetID: string;
}[] {
    return (model.edges as Edge[])
        .map(e => ({
            type: e.type,
            sourceID: e.sourceID as string,
            targetID: e.targetID as string
        }))
        .sort((a, b) => {
            const cmp = a.type.localeCompare(b.type);
            if (cmp !== 0) return cmp;
            const cmp2 = (a.sourceID ?? '').localeCompare(b.sourceID ?? '');
            if (cmp2 !== 0) return cmp2;
            return (a.targetID ?? '').localeCompare(b.targetID ?? '');
        });
}

/**
 * Assert that two GraphModels are bisimilar (structurally equivalent).
 * Checks element count, types, properties, edge count, and edge structure.
 */
export function assertBisimilar(original: GraphModel, roundTripped: GraphModel): void {
    const origElements = collectElements(original);
    const rtElements = collectElements(roundTripped);

    expect(rtElements.length).toBe(origElements.length);

    for (const origElem of origElements) {
        const match = rtElements.find(e => e.id === origElem.id);
        expect(match).toBeDefined();
        if (match) {
            expect(match.type).toBe(origElem.type);
            for (const [key, value] of Object.entries(origElem.properties)) {
                expect(match.properties[key]).toBe(value);
            }
        }
    }

    const origEdges = collectEdges(original);
    const rtEdges = collectEdges(roundTripped);
    expect(rtEdges.length).toBe(origEdges.length);

    for (let i = 0; i < origEdges.length; i++) {
        expect(rtEdges[i].type).toBe(origEdges[i].type);
        expect(rtEdges[i].sourceID).toBe(origEdges[i].sourceID);
        expect(rtEdges[i].targetID).toBe(origEdges[i].targetID);
    }
}

/**
 * Full DAO-ML MetaSpecification derived from DAO_ML.mgl.
 * Type IDs use the 'dao_ml:' prefix as used by the codec helper's
 * ELEMENT_TYPE_ID_TRANSLATOR (the cinco-side type IDs).
 */
export const DAOML_SPEC: CompositionSpecification = {
    graphTypes: [
        {
            elementTypeId: 'dao_ml:dao_model',
            label: 'DAO_Model',
            diagramExtension: 'dao',
            containments: [
                {
                    lowerBound: 0, upperBound: -1,
                    elements: ['dao_ml:dao', 'dao_ml:role', 'dao_ml:committee', 'dao_ml:governancearea', 'dao_ml:permission']
                }
            ],
            attributes: [
                { name: 'name', type: 'string', defaultValue: '' },
                { name: 'optimizedTranslation', type: 'boolean', defaultValue: 'True' }
            ]
        }
    ],
    nodeTypes: [
        {
            elementTypeId: 'dao_ml:dao',
            label: 'DAO',
            reparentable: false,
            width: 500,
            height: 400,
            containments: [
                {
                    lowerBound: 0, upperBound: -1,
                    elements: ['dao_ml:role', 'dao_ml:committee', 'dao_ml:governancearea', 'dao_ml:permission']
                }
            ],
            attributes: [
                { name: 'name', type: 'string', defaultValue: 'New Dao' },
                { name: 'missionStatement', type: 'string', defaultValue: '' },
                { name: 'hierarchicalInheritance', type: 'boolean', defaultValue: 'false' }
            ]
        },
        {
            elementTypeId: 'dao_ml:governancearea',
            label: 'GovernanceArea',
            reparentable: false,
            width: 200,
            height: 150,
            containments: [
                { lowerBound: 0, upperBound: -1, elements: ['dao_ml:permission'] }
            ],
            attributes: [
                { name: 'description', type: 'string', defaultValue: 'New Governance Area' },
                { name: 'implementation', type: 'dao_ml:GovernanceAreaImplementation', defaultValue: 'OnChain' }
            ]
        },
        {
            elementTypeId: 'dao_ml:role',
            label: 'Role',
            reparentable: false,
            width: 50,
            height: 80,
            incomingEdges: [
                { lowerBound: 0, upperBound: -1, elements: ['dao_ml:aggregationrole', 'dao_ml:iscontrolledby'] }
            ],
            outgoingEdges: [
                {
                    lowerBound: 0, upperBound: -1,
                    elements: ['dao_ml:association', 'dao_ml:federation', 'dao_ml:aggregationrole', 'dao_ml:iscontrolledby']
                }
            ],
            attributes: [
                { name: 'name', type: 'string', defaultValue: 'New Role' },
                { name: 'agentType', type: 'dao_ml:AgentType', defaultValue: 'Human' },
                { name: 'aggregationLevel', type: 'number', defaultValue: '0' },
                { name: 'federationLevel', type: 'number', defaultValue: '0' },
                { name: 'assignmentMethod', type: 'string', defaultValue: '' },
                { name: 'agentMin', type: 'number', defaultValue: '0' },
                { name: 'agentMax', type: 'number', defaultValue: '0' }
            ]
        },
        {
            elementTypeId: 'dao_ml:committee',
            label: 'Committee',
            reparentable: false,
            width: 112,
            height: 49,
            incomingEdges: [
                { lowerBound: 0, upperBound: -1, elements: ['dao_ml:federation', 'dao_ml:aggregationcommittee', 'dao_ml:iscontrolledby'] }
            ],
            outgoingEdges: [
                {
                    lowerBound: 0, upperBound: -1,
                    elements: ['dao_ml:association', 'dao_ml:federation', 'dao_ml:aggregationcommittee', 'dao_ml:iscontrolledby']
                }
            ],
            attributes: [
                { name: 'description', type: 'string', defaultValue: 'New Committee' },
                { name: 'aggregationLevel', type: 'number', defaultValue: '0' },
                { name: 'federationLevel', type: 'number', defaultValue: '0' },
                { name: 'decisionMakingMethod', type: 'dao_ml:CommitteeDecisionMakingMethod', defaultValue: 'SimpleMajority' },
                { name: 'customDecisionMakingMethod', type: 'string', defaultValue: '' },
                { name: 'votingCondition', type: 'string', defaultValue: '' },
                { name: 'proposalCondition', type: 'string', defaultValue: '' }
            ]
        },
        {
            elementTypeId: 'dao_ml:permission',
            label: 'Permission',
            reparentable: false,
            width: 91,
            height: 49,
            incomingEdges: [
                { lowerBound: 0, upperBound: -1, elements: ['dao_ml:association'] }
            ],
            outgoingEdges: [],
            attributes: [
                { name: 'allowedAction', type: 'string', defaultValue: 'New Permission' },
                { name: 'permissionType', type: 'dao_ml:PermissionType', defaultValue: 'Operational' }
            ]
        }
    ],
    edgeTypes: [
        { elementTypeId: 'dao_ml:association', label: 'Association', routable: true, attributes: [] },
        { elementTypeId: 'dao_ml:federation', label: 'Federation', routable: true, attributes: [] },
        { elementTypeId: 'dao_ml:aggregation', label: 'Aggregation', routable: true, attributes: [] },
        { elementTypeId: 'dao_ml:aggregationcommittee', label: 'AggregationCommittee', routable: true, superTypes: ['dao_ml:aggregation'], attributes: [] },
        { elementTypeId: 'dao_ml:aggregationrole', label: 'AggregationRole', routable: true, superTypes: ['dao_ml:aggregation'], attributes: [] },
        { elementTypeId: 'dao_ml:iscontrolledby', label: 'IsControlledBy', routable: true, attributes: [] }
    ],
    customTypes: [
        { elementTypeId: 'dao_ml:AgentType', label: 'AgentType', literals: ['Human', 'Autonomous'] } as Enum,
        { elementTypeId: 'dao_ml:GovernanceAreaImplementation', label: 'GovernanceAreaImplementation', literals: ['OnChain', 'Hybrid', 'OffChain'] } as Enum,
        { elementTypeId: 'dao_ml:CommitteeDecisionMakingMethod', label: 'CommitteeDecisionMakingMethod', literals: ['CustomProtocol', 'SimpleMajority', 'LazyConsensus', 'QuadraticVoting', 'OptimisticGovernance'] } as Enum,
        { elementTypeId: 'dao_ml:PermissionType', label: 'PermissionType', literals: ['Operational', 'Structural', 'Strategic'] } as Enum
    ]
};

/** Abstract elements spec (abstract edge Aggregation) */
export const DAOML_ABSTRACT_SPEC: CompositionSpecification = {
    edgeTypes: [
        { elementTypeId: 'dao_ml:aggregation', label: 'Aggregation', routable: true, attributes: [] }
    ]
};

/**
 * Set up the DAO-ML MetaSpecification for testing.
 * Call in beforeEach().
 */
export function setupDaoMlMetaSpecification(): void {
    MetaSpecification.clear();
    MetaSpecification.merge(DAOML_SPEC, DAOML_ABSTRACT_SPEC);
}

/**
 * Tear down the MetaSpecification after testing.
 * Call in afterEach().
 */
export function teardownDaoMlMetaSpecification(): void {
    MetaSpecification.clear();
}
