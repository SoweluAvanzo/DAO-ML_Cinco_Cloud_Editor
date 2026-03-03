/** @type {import('ts-jest').JestConfigWithTsJest} **/
const path = require('path');

module.exports = {
    testEnvironment: 'node',
    transform: {
        '^.+\\.tsx?$': ['ts-jest', {
            tsconfig: path.resolve(__dirname, 'tsconfig.json')
        }]
    },
    testPathIgnorePatterns: ['node_modules', 'lib'],
    rootDir: path.resolve(__dirname, '..'),
    roots: [path.resolve(__dirname)],

    collectCoverageFrom: [
        'helper/dao-ml-codec-helper.ts',
        'helper/dao-ml-generator-helper.ts',
        'helper/dao-ml-value-provider-helper.ts'
    ]
};
