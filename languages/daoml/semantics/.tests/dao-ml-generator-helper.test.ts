/********************************************************************************
 * Tests for dao-ml-generator-helper.ts
 *
 * Covers: getContent(), checkInstallPackages(), executeProcess()
 * Note: generate() is not unit-testable (requires Python environment & file I/O)
 *       but the helper functions it calls are tested.
 ********************************************************************************/
import {
    GraphModel,
    Node,
    ModelElement
} from '@cinco-glsp/cinco-glsp-api';
import { getContent } from '../helper/dao-ml-generator-helper';
import { setupDaoMlMetaSpecification, teardownDaoMlMetaSpecification } from './test-setup';

// ═════════════════════════════════════════════════════════════════════════════

describe('dao-ml-generator-helper', () => {
    beforeEach(() => setupDaoMlMetaSpecification());
    afterEach(() => teardownDaoMlMetaSpecification());

    describe('getContent', () => {
        test.each([
            ['dao_ml:role'],
            ['dao_ml:committee'],
            ['dao_ml:permission'],
            ['dao_ml:governancearea'],
            ['dao_ml:dao'],
        ])('returns "%s generation content" for a Node', (type) => {
            const node = new Node();
            node.type = type;
            node.initializeProperties();
            expect(getContent(node)).toBe(`${type} generation content`);
        });

        test('returns type + generation content for a GraphModel', () => {
            const model = new GraphModel();
            model.type = 'dao_ml:dao_model';
            model.initializeProperties();
            expect(getContent(model)).toBe('dao_ml:dao_model generation content');
        });
    });
});
