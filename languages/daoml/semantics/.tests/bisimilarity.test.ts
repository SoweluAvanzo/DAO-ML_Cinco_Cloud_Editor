/********************************************************************************
 * Bisimilarity tests for DAO-ML codec.
 *
 * Verifies that DAO-ML XML files can be decoded into GraphModels and then
 * re-encoded back to XML such that decoding the re-encoded XML produces an
 * equivalent GraphModel. This establishes full codec round-trip correctness.
 *
 * All property, element, and edge comparisons are handled by assertBisimilar
 * in test-setup.ts \u2014 no per-fixture sub-tests needed.
 ********************************************************************************/
import {
    setupDaoMlMetaSpecification,
    teardownDaoMlMetaSpecification,
    decodeFull,
    encodeFull,
    assertBisimilar,
    MINIMAL_DAO,
    EDGE_CASES_DAO,
    EMPTY_DAO,
    COMPLEX_DAO,
    WIRE_DAO,
    TRAVELHIVE_DAO
} from './test-setup';

// \u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550
// Bisimilarity test suites
// \u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550

describe('Bisimilarity: DAO XML \u2194 GraphModel round-trip', () => {
    beforeEach(() => setupDaoMlMetaSpecification());
    afterEach(() => teardownDaoMlMetaSpecification());

    test.each([
        ['minimal-test.dao', MINIMAL_DAO],
        ['edge-cases.dao', EDGE_CASES_DAO],
        ['empty-dao.dao', EMPTY_DAO],
        ['WIRE.dao', WIRE_DAO],
        ['Travelhive_final_model.dao', TRAVELHIVE_DAO],
        ['complex-full-feature.dao', COMPLEX_DAO],
    ])('%s: decode \u2192 encode \u2192 decode produces bisimilar GraphModel', (_name, content) => {
        const original = decodeFull(content);
        const encoded = encodeFull(original);
        const roundTripped = decodeFull(encoded);

        assertBisimilar(original, roundTripped);
    });
});
