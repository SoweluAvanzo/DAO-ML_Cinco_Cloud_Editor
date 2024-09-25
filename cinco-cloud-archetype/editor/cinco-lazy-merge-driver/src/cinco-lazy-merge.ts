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
import { argv, exit } from 'process';
import { readFileSync, writeFileSync } from 'fs';
import { graphMerger } from './graph-merger';

console.log('ancestor', argv[2]);
console.log('version a', argv[3]);
console.log('version b', argv[4]);
console.log('conflict marker length', argv[5]);

const ancestorInput = readFileSync(argv[2], 'utf-8');
const versionAInput = readFileSync(argv[3], 'utf-8');
const versionBInput = readFileSync(argv[4], 'utf-8');

let output: string;
let exitCode: number;

try {
    const ancestor = JSON.parse(ancestorInput);
    const versionA = JSON.parse(versionAInput);
    const versionB = JSON.parse(versionBInput);

    const merger = graphMerger();
    const result = merger({ ancestor, versionA, versionB });

    output = JSON.stringify(result.value, undefined, 2);
    exitCode = result.newEagerConflicts ? 1 : 0;
} catch (error) {
    const markerSize = Number(argv[5]);
    output =
        'Unable to merge files:\n' +
        `${error}\n` +
        `${'<'.repeat(markerSize)}\n` +
        `${versionAInput}\n` +
        `${'='.repeat(markerSize)}\n` +
        `${versionBInput}\n` +
        `${'>'.repeat(markerSize)}\n`;
    exitCode = 2;
}

writeFileSync(argv[6], output);
exit(exitCode);
