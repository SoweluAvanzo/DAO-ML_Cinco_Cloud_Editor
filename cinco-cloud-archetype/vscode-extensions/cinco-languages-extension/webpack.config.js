const path = require('path');

const commonConfig = {
    target: 'node',
    mode: 'none',
    devtool: 'nosources-source-map',
    externals: {
        vscode: 'commonjs vscode' // the vscode-module is created on-the-fly and must be excluded
    },
    resolve: {
        extensions: ['.ts', '.js']
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                enforce: 'pre',
                loader: 'source-map-loader',
                exclude: /vscode/
            },
            {
                test: /\.ts$/,
                exclude: /node_modules/,
                use: [
                    {
                        loader: 'ts-loader'
                    }
                ]
            }
        ]
    }
}
const mglConfig = {
    ...commonConfig,
    entry: './src/mgl/language-server/main.ts', // the entry point of the language server
    output: {
        path: path.resolve(__dirname, 'out', 'mgl', 'language-server'),
        filename: 'main.js',
        libraryTarget: 'commonjs2',
        devtoolModuleFilenameTemplate: '../../[resource-path]',
        clean: true
    }
};
const mslConfig = {
    ...commonConfig,
    entry: './src/msl/language-server/main.ts', // the entry point of the language server
    output: {
        path: path.resolve(__dirname, 'out', 'msl', 'language-server'),
        filename: 'main.js',
        libraryTarget: 'commonjs2',
        devtoolModuleFilenameTemplate: '../../[resource-path]',
        clean: true
    }
};
const cpdConfig = {
    ...commonConfig,
    entry: './src/cpd/language-server/main.ts', // the entry point of the language server
    output: {
        path: path.resolve(__dirname, 'out', 'cpd', 'language-server'),
        filename: 'main.js',
        libraryTarget: 'commonjs2',
        devtoolModuleFilenameTemplate: '../../[resource-path]',
        clean: true
    }
};
const vscodeConfig = {
    ...commonConfig,
    entry: './src/extension.ts', // the entry point of this extension
    output: {
        path: path.resolve(__dirname, 'out'),
        filename: 'extension.js',
        libraryTarget: 'commonjs2',
        devtoolModuleFilenameTemplate: '../[resource-path]'
    }
};
module.exports = [mglConfig, mslConfig, cpdConfig, vscodeConfig];