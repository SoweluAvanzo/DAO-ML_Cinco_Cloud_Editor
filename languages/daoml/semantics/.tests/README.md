# DAO-ML Helper Tests

Jest test suite for the DAO-ML codec, value provider, and generator helper files.

## Setup

```bash
cd workspace/languages/daoml/semantics/.tests
npm install
```

## Running Tests

```bash
npm test                # run all tests
npm run test:coverage   # run with coverage report
```

## Known Issue: Local Package References

The `package.json` dependencies reference local workspace packages via relative `file:` paths:

```json
"dependencies": {
    "@cinco-glsp/cinco-glsp-api": "file:../../../../../cinco-glsp-api",
    "@cinco-glsp/cinco-glsp-common": "file:../../../../../cinco-glsp-common",
}
```

**This needs to be fixed and adapted** when the test folder moves to a different location or when the project structure changes. These paths are relative to the `.tests/` folder and assume the monorepo layout with `cinco-glsp-api/` and `cinco-glsp-common/` at the workspace root. If the folder hierarchy changes, update these paths accordingly or switch to a proper workspace/package linking mechanism.

## Structure

```
.tests/
├── package.json                          # isolated dependencies
├── jest.config.js                        # Jest configuration
├── tsconfig.json                         # TypeScript config for tests
├── test-setup.ts                         # MetaSpecification setup (DAO-ML types, enums)
├── dao-ml-codec-helper.test.ts           # Tests for codec encode/decode
├── bisimilarity.test.ts                  # Round-trip (decode→encode→decode) tests
├── dao-ml-value-provider-helper.test.ts  # Tests for size/position value provider
├── dao-ml-generator-helper.test.ts       # Tests for generator helper
└── fixtures/
    ├── minimal-test.dao                  # Small model: 1 DAO, basic elements
    ├── edge-cases.dao                    # All enum values, federation/aggregation
    ├── empty-dao.dao                     # Empty DAO with no children
    └── complex-full-feature.dao          # Comprehensive model exercising all MGL features
```

Additional real-world fixtures are loaded from `../../WIRE.dao` and `../../Travelhive_final_model.dao`.
