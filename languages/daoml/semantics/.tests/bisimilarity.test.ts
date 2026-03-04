/********************************************************************************
 * Copyright (c) 2024 The DAO ML Team.
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
    ])('%s: decode -> encode -> decode produces bisimilar GraphModel', (_name, content) => {
        const original = decodeFull(content);
        const encoded = encodeFull(original);
        const roundTripped = decodeFull(encoded);

        assertBisimilar(original, roundTripped);
    });
});
