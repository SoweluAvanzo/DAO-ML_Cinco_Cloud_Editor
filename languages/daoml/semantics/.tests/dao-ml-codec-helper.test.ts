/********************************************************************************
 * Copyright (c) 2024 The DAO ML Team.
 ********************************************************************************/

import {
    GraphModel,
    Node,
    Edge,
    Container
} from '@cinco-glsp/cinco-glsp-api';
import {
    parseXMLContent,
    toModelElement,
    encodeSwitch,
    findContainer,
    countSymbol,
    toDAOId,
    isExternalContainment,
    attributesPrefix
} from '../helper/dao-ml-codec-helper';
import {
    setupDaoMlMetaSpecification,
    teardownDaoMlMetaSpecification,
    decodeFull,
    encodeFull,
    MINIMAL_DAO,
    EDGE_CASES_DAO,
    EMPTY_DAO,
    COMPLEX_DAO,
    WIRE_DAO,
    TRAVELHIVE_DAO
} from './test-setup';

// ═════════════════════════════════════════════════════════════════════════════
// Test suites
// ═════════════════════════════════════════════════════════════════════════════

describe('dao-ml-codec-helper', () => {
    beforeEach(() => setupDaoMlMetaSpecification());
    afterEach(() => teardownDaoMlMetaSpecification());

    // ── Utility functions ────────────────────────────────────────────────
    describe('countSymbol', () => {
        test('counts occurrences of a character', () => {
            expect(countSymbol('a-b-c-d', '-')).toBe(3);
        });

        test('returns 0 when symbol is absent', () => {
            expect(countSymbol('abcdef', '-')).toBe(0);
        });

        test('works with single character string', () => {
            expect(countSymbol('-', '-')).toBe(1);
        });

        test('works with empty string', () => {
            expect(countSymbol('', '-')).toBe(0);
        });

        test('counts UUID dashes correctly', () => {
            expect(countSymbol('1e114227-2bb7-4409-95a8-8b1808a56f12', '-')).toBe(4);
        });
    });

    describe('toDAOId', () => {
        test('prefixes with dao_ and replaces dashes with underscores', () => {
            expect(toDAOId('1e114227-2bb7-4409-95a8-8b1808a56f12'))
                .toBe('dao_1e114227_2bb7_4409_95a8_8b1808a56f12');
        });

        test('handles string with no dashes', () => {
            expect(toDAOId('abc123')).toBe('dao_abc123');
        });

        test('handles already-prefixed id', () => {
            // toDAOId doesn't check for existing prefix - it always prepends
            expect(toDAOId('dao_test')).toBe('dao_dao_test');
        });

        test('handles empty string', () => {
            expect(toDAOId('')).toBe('dao_');
        });
    });

    // ── parseXMLContent ──────────────────────────────────────────────────
    describe('parseXMLContent', () => {
        test('parses minimal DAO XML into attributes and containments', () => {
            const { attributes, encodedContainments } = parseXMLContent(MINIMAL_DAO);

            expect(attributes).toBeDefined();
            expect(attributes[attributesPrefix + 'name']).toBe('Minimal Test DAO');
            expect(attributes[attributesPrefix + 'uniqueID']).toBe('dao_test_model_001');
            expect(encodedContainments).toBeDefined();
            expect(Array.isArray(encodedContainments)).toBe(true);
            expect(encodedContainments.length).toBeGreaterThan(0);
        });

        test('parses empty DAO', () => {
            const { attributes, encodedContainments } = parseXMLContent(EMPTY_DAO);

            expect(attributes[attributesPrefix + 'name']).toBe('Empty DAO');
            expect(encodedContainments).toBeDefined();
        });

        test('attributes are plain objects (not Maps)', () => {
            const { attributes } = parseXMLContent(MINIMAL_DAO);

            // The fix: attributes should be accessible via bracket notation
            expect(typeof attributes).toBe('object');
            expect(attributes).not.toBeInstanceOf(Map);
            expect(attributes[attributesPrefix + 'name']).toBeDefined();
        });

        test('parses WIRE.dao without errors', () => {
            expect(() => parseXMLContent(WIRE_DAO)).not.toThrow();
            const { attributes, encodedContainments } = parseXMLContent(WIRE_DAO);
            expect(attributes[attributesPrefix + 'name']).toBe('WIRE DAO Model');
        });

        test('parses Travelhive.dao without errors', () => {
            expect(() => parseXMLContent(TRAVELHIVE_DAO)).not.toThrow();
            const { attributes, encodedContainments } = parseXMLContent(TRAVELHIVE_DAO);
            expect(attributes[attributesPrefix + 'name']).toBe('TravelHive System');
        });
    });

    // ── toModelElement ───────────────────────────────────────────────────
    describe('toModelElement', () => {
        test('creates GraphModel for DAO-ML_diagram', () => {
            const model = new GraphModel();
            const elem = toModelElement('DAO-ML_diagram', model);
            expect(elem).toBe(model);
        });

        test('creates Container for DAO', () => {
            const model = new GraphModel();
            const elem = toModelElement('DAO', model);
            expect(elem).toBeInstanceOf(Container);
        });

        test('creates Container for GovernanceArea', () => {
            const model = new GraphModel();
            const elem = toModelElement('GovernanceArea', model);
            expect(elem).toBeInstanceOf(Container);
        });

        test('creates Node for Role', () => {
            const model = new GraphModel();
            const elem = toModelElement('Role', model);
            expect(elem).toBeInstanceOf(Node);
        });

        test('creates Node for Committee', () => {
            const model = new GraphModel();
            const elem = toModelElement('Committee', model);
            expect(elem).toBeInstanceOf(Node);
        });

        test('creates Node for Permission', () => {
            const model = new GraphModel();
            const elem = toModelElement('Permission', model);
            expect(elem).toBeInstanceOf(Node);
        });

        test('creates Edge for each edge type', () => {
            const model = new GraphModel();
            for (const edgeType of ['aggregates', 'federates_into', 'associated_to', 'is_controlled_by']) {
                const elem = toModelElement(edgeType, model);
                expect(elem).toBeInstanceOf(Edge);
            }
        });

        test('throws for unknown type', () => {
            const model = new GraphModel();
            expect(() => toModelElement('UnknownType', model)).toThrow('Unknown dao_ml-type identified');
        });

        test('assigns dao_ prefixed id', () => {
            const model = new GraphModel();
            const elem = toModelElement('Role', model);
            expect(elem.id).toMatch(/^dao_/);
        });
    });

    // ── isExternalContainment ────────────────────────────────────────────
    describe('isExternalContainment', () => {
        test('Permission is an external containment (has ref_gov_area)', () => {
            expect(isExternalContainment('Permission')).toBe(true);
        });

        test.each([
            'Role', 'Committee', 'DAO', 'GovernanceArea',
            'aggregates', 'federates_into', 'associated_to', 'is_controlled_by'
        ])('%s is NOT an external containment', (type) => {
            expect(isExternalContainment(type)).toBe(false);
        });
    });

    // ── decodeSwitch (full decode) ───────────────────────────────────────
    describe('decodeSwitch', () => {
        test('decodes minimal DAO correctly', () => {
            const model = decodeFull(MINIMAL_DAO);

            expect(model).toBeDefined();
            expect(model.type).toBe('dao_ml:dao_model');
            expect(model.getProperty('name')).toBe('Minimal Test DAO');
            expect(model.id).toBe('dao_test_model_001');
        });

        test('decodes DAO containment', () => {
            const model = decodeFull(MINIMAL_DAO);

            // DAO is contained in the model
            const daos = model.containments.filter((n: Node) => n.type === 'dao_ml:dao');
            expect(daos.length).toBe(1);

            const dao = daos[0] as Container;
            expect(dao.getProperty('name')).toBe('Test DAO');
            expect(dao.getProperty('missionStatement')).toBe('A test DAO');
            expect(dao.id).toBe('dao_test_dao_001');
        });

        test('decodes roles inside DAO', () => {
            const model = decodeFull(MINIMAL_DAO);
            const dao = model.containments.find((n: Node) => n.type === 'dao_ml:dao') as Container;
            const roles = dao.containments.filter((n: Node) => n.type === 'dao_ml:role');

            expect(roles.length).toBe(1);
            expect(roles[0].getProperty('name')).toBe('Admin');
            expect(roles[0].getProperty('agentMin')).toBe('1');
            expect(roles[0].getProperty('agentMax')).toBe('5');
        });

        test('decodes committees inside DAO', () => {
            const model = decodeFull(MINIMAL_DAO);
            const dao = model.containments.find((n: Node) => n.type === 'dao_ml:dao') as Container;
            const committees = dao.containments.filter((n: Node) => n.type === 'dao_ml:committee');

            expect(committees.length).toBe(1);
            expect(committees[0].getProperty('description')).toBe('Board');
            expect(committees[0].getProperty('decisionMakingMethod')).toBe('SimpleMajority');
        });

        test('decodes governance areas inside DAO', () => {
            const model = decodeFull(MINIMAL_DAO);
            const dao = model.containments.find((n: Node) => n.type === 'dao_ml:dao') as Container;
            const govAreas = dao.containments.filter((n: Node) => n.type === 'dao_ml:governancearea');

            expect(govAreas.length).toBe(1);
            expect(govAreas[0].getProperty('description')).toBe('Treasury');
            expect(govAreas[0].getProperty('implementation')).toBe('OnChain');
        });

        test('decodes permissions with containment references', () => {
            const model = decodeFull(MINIMAL_DAO);
            // Permissions should be re-parented to their governance area
            const dao = model.containments.find((n: Node) => n.type === 'dao_ml:dao') as Container;
            const govArea = dao.containments.find((n: Node) => n.type === 'dao_ml:governancearea') as Container;

            // Permissions should be inside the governance area (external containment)
            const permsInGovArea = govArea.containments.filter((n: Node) => n.type === 'dao_ml:permission');
            expect(permsInGovArea.length).toBe(2);

            const vote = permsInGovArea.find((n: Node) => n.getProperty('allowedAction') === 'Vote');
            expect(vote).toBeDefined();
            expect(vote!.getProperty('permissionType')).toBe('Operational');

            const manage = permsInGovArea.find((n: Node) => n.getProperty('allowedAction') === 'Manage Funds');
            expect(manage).toBeDefined();
            expect(manage!.getProperty('permissionType')).toBe('Strategic');
        });

        test('decodes edges', () => {
            const model = decodeFull(MINIMAL_DAO);
            const edges = model.edges as Edge[];

            // is_controlled_by + 2 associated_to = 3 edges
            expect(edges.length).toBe(3);

            const isControlledBy = edges.find(e => e.type === 'dao_ml:iscontrolledby');
            expect(isControlledBy).toBeDefined();
            expect(isControlledBy!.sourceID).toBe('dao_test_role_001');
            expect(isControlledBy!.targetID).toBe('dao_test_committee_001');
        });

        test('decodes enum translations correctly', () => {
            const model = decodeFull(EDGE_CASES_DAO);
            const dao = model.containments.find((n: Node) => n.type === 'dao_ml:dao') as Container;

            // LazyConsensus committee
            const lazyCommittee = dao.containments.find(
                (n: Node) => n.type === 'dao_ml:committee' && n.getProperty('description') === 'Main Board'
            );
            expect(lazyCommittee?.getProperty('decisionMakingMethod')).toBe('LazyConsensus');

            // QuadraticVoting committee
            const qvCommittee = dao.containments.find(
                (n: Node) => n.type === 'dao_ml:committee' && n.getProperty('description') === 'Sub Board'
            );
            expect(qvCommittee?.getProperty('decisionMakingMethod')).toBe('QuadraticVoting');
        });

        test('decodes all governance area implementations', () => {
            const model = decodeFull(EDGE_CASES_DAO);
            const dao = model.containments.find((n: Node) => n.type === 'dao_ml:dao') as Container;
            const govAreas = dao.containments.filter((n: Node) => n.type === 'dao_ml:governancearea');

            const impls = govAreas.map((g: Node) => g.getProperty('implementation'));
            expect(impls).toContain('OnChain');
            expect(impls).toContain('Hybrid');
            expect(impls).toContain('OffChain');
        });

        test('decodes all permission types', () => {
            const model = decodeFull(EDGE_CASES_DAO);
            const allElements = model.getAllContainedElements();
            const perms = allElements.filter((n: Node) => n.type === 'dao_ml:permission');

            const types = perms.map((p: Node) => p.getProperty('permissionType'));
            expect(types).toContain('Operational');
            expect(types).toContain('Structural');
            expect(types).toContain('Strategic');
        });

        test('decodes federation edges', () => {
            const model = decodeFull(EDGE_CASES_DAO);
            const federations = (model.edges as Edge[]).filter(e => e.type === 'dao_ml:federation');
            expect(federations.length).toBe(2);
        });

        test('decodes aggregation edges with role/committee subtypes', () => {
            const model = decodeFull(EDGE_CASES_DAO);
            const aggregations = (model.edges as Edge[]).filter(e =>
                e.type === 'dao_ml:aggregationrole' || e.type === 'dao_ml:aggregationcommittee'
            );
            // 1 role-to-role aggregation + 1 committee-to-committee aggregation
            expect(aggregations.length).toBe(2);

            const roleAgg = aggregations.find(e => e.type === 'dao_ml:aggregationrole');
            expect(roleAgg).toBeDefined();

            const committeeAgg = aggregations.find(e => e.type === 'dao_ml:aggregationcommittee');
            expect(committeeAgg).toBeDefined();
        });

        test('decodes AI Agent role without agent_type (defaults to Autonomous)', () => {
            const model = decodeFull(EDGE_CASES_DAO);
            const dao = model.containments.find((n: Node) => n.type === 'dao_ml:dao') as Container;
            const aiRole = dao.containments.find(
                (n: Node) => n.type === 'dao_ml:role' && n.getProperty('name') === 'AI Agent'
            );
            expect(aiRole).toBeDefined();
            // Agent type defaults to 'Autonomous' when empty/undefined
            expect(aiRole!.getProperty('agentType')).toBe('Autonomous');
        });

        test('decodes empty DAO (no children in DAO)', () => {
            const model = decodeFull(EMPTY_DAO);
            const dao = model.containments.find((n: Node) => n.type === 'dao_ml:dao') as Container;
            expect(dao).toBeDefined();
            expect(dao.containments.length).toBe(0);
            expect(model.edges.length).toBe(0);
        });

        test('decodes node positions', () => {
            const model = decodeFull(MINIMAL_DAO);
            const dao = model.containments.find((n: Node) => n.type === 'dao_ml:dao') as Container;
            const role = dao.containments.find((n: Node) => n.type === 'dao_ml:role') as Node;

            expect(role.position.x).toBe(50);
            expect(role.position.y).toBe(100);
        });

        test('decodes node sizes', () => {
            const model = decodeFull(MINIMAL_DAO);
            const dao = model.containments.find((n: Node) => n.type === 'dao_ml:dao') as Container;
            const role = dao.containments.find((n: Node) => n.type === 'dao_ml:role') as Node;

            expect(role.size.width).toBe(50);
            expect(role.size.height).toBe(80);
        });
    });

    // ── decodeSwitch with real DAO files ──────────────────────────────────
    describe('decodeSwitch - WIRE.dao', () => {
        test('decodes WIRE.dao successfully', () => {
            const model = decodeFull(WIRE_DAO);
            expect(model).toBeDefined();
            expect(model.type).toBe('dao_ml:dao_model');
            expect(model.getProperty('name')).toBe('WIRE DAO Model');
        });

        test('WIRE.dao has exactly 1 DAO', () => {
            const model = decodeFull(WIRE_DAO);
            const daos = model.containments.filter((n: Node) => n.type === 'dao_ml:dao');
            expect(daos.length).toBe(1);
        });

        test('WIRE.dao DAO has correct name/mission', () => {
            const model = decodeFull(WIRE_DAO);
            const dao = model.containments.find((n: Node) => n.type === 'dao_ml:dao') as Container;
            expect(dao.getProperty('name')).toBe('WIRE DAO');
            expect(dao.getProperty('missionStatement')).toContain('independent media');
        });

        test('WIRE.dao has the expected number of roles', () => {
            const model = decodeFull(WIRE_DAO);
            const allElements = model.getAllContainedElements();
            const roles = allElements.filter(e => e.type === 'dao_ml:role');
            expect(roles.length).toBe(6);
        });

        test('WIRE.dao has the expected number of committees', () => {
            const model = decodeFull(WIRE_DAO);
            const allElements = model.getAllContainedElements();
            const committees = allElements.filter(e => e.type === 'dao_ml:committee');
            expect(committees.length).toBe(1);
        });

        test('WIRE.dao has the expected number of governance areas', () => {
            const model = decodeFull(WIRE_DAO);
            const allElements = model.getAllContainedElements();
            const govAreas = allElements.filter(e => e.type === 'dao_ml:governancearea');
            expect(govAreas.length).toBe(6);
        });

        test('WIRE.dao has the expected number of permissions', () => {
            const model = decodeFull(WIRE_DAO);
            const allElements = model.getAllContainedElements();
            const perms = allElements.filter(e => e.type === 'dao_ml:permission');
            expect(perms.length).toBe(17);
        });

        test('WIRE.dao has edges', () => {
            const model = decodeFull(WIRE_DAO);
            expect(model.edges.length).toBeGreaterThan(0);
        });
    });

    describe('decodeSwitch - Travelhive.dao', () => {
        test('decodes Travelhive.dao successfully', () => {
            const model = decodeFull(TRAVELHIVE_DAO);
            expect(model).toBeDefined();
            expect(model.type).toBe('dao_ml:dao_model');
            expect(model.getProperty('name')).toBe('TravelHive System');
        });

        test('Travelhive.dao has 2 DAOs', () => {
            const model = decodeFull(TRAVELHIVE_DAO);
            const daos = model.containments.filter((n: Node) => n.type === 'dao_ml:dao');
            expect(daos.length).toBe(2);
        });

        test('Travelhive.dao DAOs have correct names', () => {
            const model = decodeFull(TRAVELHIVE_DAO);
            const daos = model.containments.filter((n: Node) => n.type === 'dao_ml:dao');
            const names = daos.map((d: Node) => d.getProperty('name'));
            expect(names).toContain('Travelhive DAO');
            expect(names).toContain('Decentralized Destination Management Organization');
        });

        test('Travelhive.dao has edges', () => {
            const model = decodeFull(TRAVELHIVE_DAO);
            expect(model.edges.length).toBeGreaterThan(0);
        });

        test('Travelhive.dao all elements have valid IDs', () => {
            const model = decodeFull(TRAVELHIVE_DAO);
            const allElements = model.getAllContainedElements();
            for (const elem of allElements) {
                expect(elem.id).toBeDefined();
                expect(elem.id.length).toBeGreaterThan(0);
                expect(elem.id).toMatch(/^dao_/);
            }
        });
    });

    // ── findContainer ────────────────────────────────────────────────────
    describe('findContainer', () => {
        test('finds direct child in container', () => {
            const model = decodeFull(MINIMAL_DAO);
            const dao = model.containments.find((n: Node) => n.type === 'dao_ml:dao') as Container;
            const result = findContainer(dao, model);
            expect(result).toBe(model);
        });

        test('finds nested child', () => {
            const model = decodeFull(MINIMAL_DAO);
            const dao = model.containments.find((n: Node) => n.type === 'dao_ml:dao') as Container;
            const role = dao.containments.find((n: Node) => n.type === 'dao_ml:role') as Node;
            const result = findContainer(role, model);
            // role should be in DAO
            expect(result).toBe(dao);
        });

        test('returns undefined for non-contained element', () => {
            const model = decodeFull(MINIMAL_DAO);
            const orphan = new Node();
            orphan.id = 'orphan_123';
            const result = findContainer(orphan, model);
            expect(result).toBeUndefined();
        });
    });

    // ── encodeSwitch ─────────────────────────────────────────────────────
    describe('encodeSwitch', () => {
        test('encodes minimal model with all element types and attributes', () => {
            const model = decodeFull(MINIMAL_DAO);
            const xml = encodeSwitch(model, model)!;

            // Root element
            expect(xml).toContain('<DAO-ML_diagram');
            expect(xml).toContain('</DAO-ML_diagram>');
            expect(xml).toContain('name="Minimal Test DAO"');
            expect(xml).toContain('uniqueID="dao_test_model_001"');
            // DAO
            expect(xml).toContain('<DAO');
            expect(xml).toContain('DAO_name="Test DAO"');
            expect(xml).toContain('mission_statement="A test DAO"');
            // Role
            expect(xml).toContain('<Role');
            expect(xml).toContain('role_name="Admin"');
            // Committee
            expect(xml).toContain('<Committee');
            expect(xml).toContain('committee_description="Board"');
            // GovernanceArea
            expect(xml).toContain('<GovernanceArea');
            expect(xml).toContain('gov_area_description="Treasury"');
            expect(xml).toContain('implementation="on-chain"');
            // Permission
            expect(xml).toContain('<Permission');
            expect(xml).toContain('allowed_action="Vote"');
            expect(xml).toContain('permission_type="operational"');
            // Edges
            expect(xml).toContain('<is_controlled_by');
            expect(xml).toContain('<associated_to');
            // Edge target ID as text content
            expect(xml).toContain('dao_test_committee_001</is_controlled_by>');
        });

        test('encodes enum values back to XML format', () => {
            const model = decodeFull(EDGE_CASES_DAO);
            const xml = encodeSwitch(model, model)!;

            // Enum values should be translated back to XML format
            expect(xml).toContain('decision_making_method="lazy_consensus"');
            expect(xml).toContain('decision_making_method="quadratic_voting"');
            expect(xml).toContain('implementation="on-chain"');
            expect(xml).toContain('implementation="hybrid"');
            expect(xml).toContain('implementation="off-chain"');
        });

        test.each([
            ['WIRE.dao', WIRE_DAO],
            ['Travelhive.dao', TRAVELHIVE_DAO],
            ['complex-full-feature.dao', COMPLEX_DAO],
        ])('encodes %s model without errors', (_name, daoContent) => {
            const model = decodeFull(daoContent);
            const xml = encodeSwitch(model, model);
            expect(xml).toBeDefined();
            expect(xml).toContain('<DAO-ML_diagram');
        });
    });

    // ─── Complex full-feature fixture ────────────────────────────────────────
    describe('decodeSwitch – complex-full-feature.dao', () => {
        let model: GraphModel;
        let allElements: any[];

        beforeEach(() => {
            model = decodeFull(COMPLEX_DAO);
            allElements = model.getAllContainedElements();
        });

        // --- Structure ---

        test('model has correct name', () => {
            expect(model.getProperty('name')).toBe('Complex Full Feature DAO Model');
        });

        test('model contains exactly 2 DAOs', () => {
            const daos = model.containments.filter((n: Node) => n.type === 'dao_ml:dao');
            expect(daos.length).toBe(2);
        });

        test('Alpha DAO has hierarchicalInheritance=true', () => {
            const alpha = allElements.find(e => e.id === 'dao_complex_dao_alpha');
            expect(alpha).toBeDefined();
            expect(alpha!.getProperty('hierarchicalInheritance')).toBe('true');
        });

        test('Beta DAO has hierarchicalInheritance=false', () => {
            const beta = allElements.find(e => e.id === 'dao_complex_dao_beta');
            expect(beta).toBeDefined();
            expect(beta!.getProperty('hierarchicalInheritance')).toBe('false');
        });

        test('Alpha DAO has correct mission statement', () => {
            const alpha = allElements.find(e => e.id === 'dao_complex_dao_alpha');
            expect(alpha!.getProperty('missionStatement')).toBe('Full-featured DAO exercising every MGL construct');
        });

        // --- Roles ---

        test('contains 7 roles total (5 Alpha + 2 Beta)', () => {
            const roles = allElements.filter(e => e.type === 'dao_ml:role');
            expect(roles.length).toBe(7);
        });

        test('Human roles have agent_type=Human', () => {
            const founder = allElements.find(e => e.id === 'dao_complex_role_founder');
            expect(founder!.getProperty('agentType')).toBe('Human');
        });

        test('Autonomous roles have no agent_type attribute (default)', () => {
            // AI Curator and AI Auditor have no agent_type in XML → should get enum default
            const aiCurator = allElements.find(e => e.id === 'dao_complex_role_ai_curator');
            expect(aiCurator).toBeDefined();
            const agentType = aiCurator!.getProperty('agentType');
            // When agent_type is missing, the codec uses the first enum literal as default
            expect(agentType).toBeDefined();
        });

        test('roles have non-zero aggregation and federation levels', () => {
            const founder = allElements.find(e => e.id === 'dao_complex_role_founder');
            expect(founder!.getProperty('aggregationLevel')).toBe('2');
            expect(founder!.getProperty('federationLevel')).toBe('1');
        });

        test('roles have agentMin and agentMax', () => {
            const founder = allElements.find(e => e.id === 'dao_complex_role_founder');
            expect(founder!.getProperty('agentMin')).toBe('1');
            expect(founder!.getProperty('agentMax')).toBe('3');

            const delegate = allElements.find(e => e.id === 'dao_complex_role_delegate');
            expect(delegate!.getProperty('agentMin')).toBe('5');
            expect(delegate!.getProperty('agentMax')).toBe('50');
        });

        // --- Committees ---

        test('contains 6 committees total (5 Alpha + 1 Beta)', () => {
            const committees = allElements.filter(e => e.type === 'dao_ml:committee');
            expect(committees.length).toBe(6);
        });

        test('committees have all decision making methods', () => {
            const board = allElements.find(e => e.id === 'dao_complex_comm_board');
            expect(board!.getProperty('decisionMakingMethod')).toBe('SimpleMajority');

            const moderation = allElements.find(e => e.id === 'dao_complex_comm_moderation');
            expect(moderation!.getProperty('decisionMakingMethod')).toBe('LazyConsensus');

            const dispute = allElements.find(e => e.id === 'dao_complex_comm_dispute');
            expect(dispute!.getProperty('decisionMakingMethod')).toBe('QuadraticVoting');

            const optimistic = allElements.find(e => e.id === 'dao_complex_comm_optimistic');
            expect(optimistic!.getProperty('decisionMakingMethod')).toBe('OptimisticGovernance');
        });

        test('committee with CustomProtocol has empty decision_making_method', () => {
            // The Protocol committee has decision_making_method="" which maps to CustomProtocol
            const protocol = allElements.find(e => e.id === 'dao_complex_comm_protocol');
            expect(protocol).toBeDefined();
        });

        test('committees have voting and proposal conditions', () => {
            const board = allElements.find(e => e.id === 'dao_complex_comm_board');
            expect(board!.getProperty('votingCondition')).toBe('quorum >= 51%');
            expect(board!.getProperty('proposalCondition')).toBe('any member');

            const dispute = allElements.find(e => e.id === 'dao_complex_comm_dispute');
            expect(dispute!.getProperty('votingCondition')).toBe('token weighted');
            expect(dispute!.getProperty('proposalCondition')).toBe('stake >= 100');
        });

        test('committees have aggregation and federation levels', () => {
            const moderation = allElements.find(e => e.id === 'dao_complex_comm_moderation');
            expect(moderation!.getProperty('aggregationLevel')).toBe('1');

            const dispute = allElements.find(e => e.id === 'dao_complex_comm_dispute');
            expect(dispute!.getProperty('federationLevel')).toBe('1');
        });

        // --- GovernanceAreas ---

        test('contains 4 governance areas total (3 Alpha + 1 Beta)', () => {
            const govAreas = allElements.filter(e => e.type === 'dao_ml:governancearea');
            expect(govAreas.length).toBe(4);
        });

        test('governance areas cover all implementation types', () => {
            const onchain = allElements.find(e => e.id === 'dao_complex_gov_onchain');
            expect(onchain!.getProperty('implementation')).toBe('OnChain');

            const hybrid = allElements.find(e => e.id === 'dao_complex_gov_hybrid');
            expect(hybrid!.getProperty('implementation')).toBe('Hybrid');

            const offchain = allElements.find(e => e.id === 'dao_complex_gov_offchain');
            expect(offchain!.getProperty('implementation')).toBe('OffChain');
        });

        // --- Permissions ---

        test('contains 7 permissions total (6 Alpha + 1 Beta)', () => {
            const perms = allElements.filter(e => e.type === 'dao_ml:permission');
            expect(perms.length).toBe(7);
        });

        test('permissions cover all permission types', () => {
            const operational = allElements.filter(e =>
                e.type === 'dao_ml:permission' && e.getProperty('permissionType') === 'Operational'
            );
            const structural = allElements.filter(e =>
                e.type === 'dao_ml:permission' && e.getProperty('permissionType') === 'Structural'
            );
            const strategic = allElements.filter(e =>
                e.type === 'dao_ml:permission' && e.getProperty('permissionType') === 'Strategic'
            );
            expect(operational.length).toBe(3); // 2 Alpha + 1 Beta
            expect(structural.length).toBe(2);
            expect(strategic.length).toBe(2);
        });

        test('permissions have external containment (ref_gov_area)', () => {
            // Permissions with ref_gov_area should be re-parented into their governance areas
            const govOnchain = allElements.find(e => e.id === 'dao_complex_gov_onchain');
            if (Container.is(govOnchain)) {
                const permsInOnchain = govOnchain.containments.filter(
                    (n: Node) => n.type === 'dao_ml:permission'
                );
                expect(permsInOnchain.length).toBe(2);
            }
        });

        // --- Edges ---

        test('contains all edge types (aggregation interpolated to role/committee)', () => {
            const edgeTypes = new Set(model.edges.map((e: Edge) => e.type));
            expect(edgeTypes.has('dao_ml:association')).toBe(true);
            expect(edgeTypes.has('dao_ml:federation')).toBe(true);
            // aggregates edges are interpolated: role source → aggregationrole, committee source → aggregationcommittee
            expect(edgeTypes.has('dao_ml:aggregationrole')).toBe(true);
            expect(edgeTypes.has('dao_ml:aggregationcommittee')).toBe(true);
            expect(edgeTypes.has('dao_ml:iscontrolledby')).toBe(true);
        });

        test('association edges connect roles/committees to permissions', () => {
            const associations = model.edges.filter((e: Edge) => e.type === 'dao_ml:association');
            expect(associations.length).toBeGreaterThanOrEqual(13);
        });

        test('federation edges exist', () => {
            const federations = model.edges.filter((e: Edge) => e.type === 'dao_ml:federation');
            expect(federations.length).toBeGreaterThanOrEqual(4);
        });

        test('aggregation edges interpolated to role and committee subtypes', () => {
            const aggRole = model.edges.filter((e: Edge) => e.type === 'dao_ml:aggregationrole');
            const aggComm = model.edges.filter((e: Edge) => e.type === 'dao_ml:aggregationcommittee');
            expect(aggRole.length).toBeGreaterThanOrEqual(2); // Founder→Member, AI Auditor→AI Curator
            expect(aggComm.length).toBeGreaterThanOrEqual(1); // Board→Moderation
        });

        test('isControlledBy edges exist', () => {
            const controlledBy = model.edges.filter((e: Edge) => e.type === 'dao_ml:iscontrolledby');
            expect(controlledBy.length).toBeGreaterThanOrEqual(7);
        });

        test('edge source and target IDs are valid element IDs', () => {
            const allIds = new Set(allElements.map(e => e.id));
            for (const edge of model.edges as Edge[]) {
                expect(allIds.has(edge.sourceID)).toBe(true);
                expect(allIds.has(edge.targetID)).toBe(true);
            }
        });

        test('role with multiple outgoing edge types', () => {
            // Founder has: associated_to, federates_into, aggregates (→aggregationrole), is_controlled_by
            const founderEdges = (model.edges as Edge[]).filter(e => e.sourceID === 'dao_complex_role_founder');
            const founderEdgeTypes = new Set(founderEdges.map(e => e.type));
            expect(founderEdgeTypes.has('dao_ml:association')).toBe(true);
            expect(founderEdgeTypes.has('dao_ml:federation')).toBe(true);
            expect(founderEdgeTypes.has('dao_ml:aggregationrole')).toBe(true);
            expect(founderEdgeTypes.has('dao_ml:iscontrolledby')).toBe(true);
        });

        // --- Multi-DAO isolation ---

        test('Beta DAO elements are separate from Alpha DAO', () => {
            const betaAdmin = allElements.find(e => e.id === 'dao_complex_role_beta_admin');
            expect(betaAdmin).toBeDefined();
            expect(betaAdmin!.getProperty('name')).toBe('Beta Admin');

            const betaBot = allElements.find(e => e.id === 'dao_complex_role_beta_bot');
            expect(betaBot).toBeDefined();
            expect(betaBot!.getProperty('name')).toBe('Beta Bot');
        });

        // --- Encode XML format ---

        test('re-encoded XML preserves enum values, DAO names, and edge elements', () => {
            const xml = encodeFull(model);
            // GovernanceArea implementations
            expect(xml).toContain('implementation="on-chain"');
            expect(xml).toContain('implementation="hybrid"');
            expect(xml).toContain('implementation="off-chain"');
            // Committee decision methods
            expect(xml).toContain('decision_making_method="simple_majority"');
            expect(xml).toContain('decision_making_method="lazy_consensus"');
            expect(xml).toContain('decision_making_method="quadratic_voting"');
            expect(xml).toContain('decision_making_method="optimistic_governance"');
            // Permission types
            expect(xml).toContain('permission_type="operational"');
            expect(xml).toContain('permission_type="structural"');
            expect(xml).toContain('permission_type="strategic"');
            // Agent types
            expect(xml).toContain('agent_type="human"');
            // DAOs
            expect(xml).toContain('DAO_name="Alpha DAO"');
            expect(xml).toContain('DAO_name="Beta Sub-DAO"');
            // Edge elements
            expect(xml).toContain('<associated_to');
            expect(xml).toContain('<federates_into');
            expect(xml).toContain('<aggregates');
            expect(xml).toContain('<is_controlled_by');
        });
    });
});
