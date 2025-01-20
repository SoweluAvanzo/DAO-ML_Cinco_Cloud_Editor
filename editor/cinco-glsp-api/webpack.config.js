/********************************************************************************
 * Copyright (c) 2022 Cinco Cloud.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
const path = require('path');
const CircularDependencyPlugin = require('circular-dependency-plugin');
const appRoot = path.resolve(__dirname, 'bundle');
const buildRoot = path.resolve(__dirname, 'lib');

module.exports = {
    entry: [path.resolve(buildRoot, 'index')],
    output: {
        filename: 'cinco-glsp-api-packed.js',
        path: appRoot
    },
    mode: 'development',
    devtool: 'inline-source-map',
    resolve: {
        extensions: ['.ts', '.tsx', '.js']
    },
    target: 'node',
    module: {
        rules: [
            {
                test: /\.jsx?$/,
                exclude: [/(node_modules)/],
                use: ['source-map-loader'],
                enforce: 'pre'
            },
            {
                test: /\.(tsx?)$/,
                use: ['ts-loader'],
                exclude: /node_modules|\.d\.ts$/
            },
            {
                test: /\.(map|d\.ts|tsbuildinfo)$/,
                use: 'ignore-loader'
            }
        ]
    },
    plugins: [
        new CircularDependencyPlugin({
            exclude: /(node_modules)\/./,
            failOnError: false
        })
    ],
    ignoreWarnings: [/Failed to parse source map/, /Can't resolve .* in '.*ws\/lib'/]
};
