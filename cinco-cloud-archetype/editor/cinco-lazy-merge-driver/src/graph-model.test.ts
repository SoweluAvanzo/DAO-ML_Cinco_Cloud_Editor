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
import { readdirSync, readFileSync } from 'fs';
import { graphMerger } from './graph-model';

describe('examples', () => {
    const examples = readdirSync('examples', { withFileTypes: true })
        .filter(entry => entry.isDirectory())
        .map(entry => entry.name);
    for (const example of examples) {
        test(example, () => {
            const ancestor = JSON.parse(readFileSync(`examples/${example}/ancestor.flowgraph`, 'utf-8'));
            const versionA = JSON.parse(readFileSync(`examples/${example}/version-a.flowgraph`, 'utf-8'));
            const versionB = JSON.parse(readFileSync(`examples/${example}/version-b.flowgraph`, 'utf-8'));

            const merger = graphMerger();
            const result = merger({ ancestor, versionA, versionB });

            expect(result.value).toStrictEqual(JSON.parse(readFileSync(`examples/${example}/merged.flowgraph`, 'utf-8')));
            expect(result.newEagerConflicts ? 1 : 0).toBe(Number(readFileSync(`examples/${example}/exit-code.txt`, 'utf-8')));
        });
    }
});
