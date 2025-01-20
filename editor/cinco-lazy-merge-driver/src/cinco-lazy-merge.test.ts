/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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
import { describe, test, expect } from '@jest/globals';
import { spawn } from 'child_process';
import { readdirSync } from 'fs';
import { readFile, mkdtemp, unlink, rmdir } from 'fs/promises';
import * as path from 'path';

type ExampleConfig = Readonly<{
    eager: boolean;
    arbitrarily: boolean;
}>;

describe('examples', () => {
    const examplesDirectory = `${__dirname}/../cinco-lazy-merge-examples`;
    const examples = readdirSync(examplesDirectory, { withFileTypes: true })
        .filter(entry => entry.isDirectory())
        .map(entry => entry.name);
    for (const example of examples) {
        test(example, async () => {
            const exampleDirectory = path.join(examplesDirectory, example);
            const config: ExampleConfig = JSON.parse(await readFile(path.join(exampleDirectory, 'config.json'), 'utf-8'));
            const tempDirectory = await mkdtemp(`/tmp/cinco-lazy-merge-test-example-${example}-`);
            const outFile = path.join(tempDirectory, 'merged.flowgraph');
            const process = spawn(
                'node',
                [
                    path.join(__dirname, '..', 'lib', 'cinco-lazy-merge.js'),
                    path.join(exampleDirectory, 'ancestor.flowgraph'),
                    path.join(exampleDirectory, 'version-a.flowgraph'),
                    path.join(exampleDirectory, 'version-b.flowgraph'),
                    outFile
                ]
                    .concat(config.eager ? ['--fail-merge-on-lazy-conflicts'] : [])
                    .concat(config.arbitrarily ? ['--merge-unknown-cells-arbitrarily'] : [])
            );
            const expectedExitCode = JSON.parse(await readFile(path.join(exampleDirectory, 'exit-code.json'), 'utf-8'));
            const expectedMerge = await readFile(path.join(exampleDirectory, 'merged.flowgraph'), 'utf-8');

            const exitCode = await new Promise((resolve, reject) => {
                process.on('error', error => reject(error));

                process.on('exit', (code, signal) => {
                    // eslint-disable-next-line no-null/no-null
                    if (code !== null) {
                        resolve(code);
                    } else {
                        reject(new Error(`Exit code is null, signal is ${signal}.`));
                    }
                });
            });

            expect(exitCode).toBe(expectedExitCode);
            expect(await readFile(outFile, 'utf-8')).toBe(expectedMerge);

            await unlink(outFile);
            await rmdir(tempDirectory);
        });
    }
});
