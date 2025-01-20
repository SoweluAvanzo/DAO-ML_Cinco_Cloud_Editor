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
import { readdirSync } from 'fs';
import { readFile } from 'fs/promises';
import { graphMerger } from './graph-merger';
import { defaultContext } from './combinators';
import * as path from 'path';

describe('examples', () => {
    const examplesDirectory = `${__dirname}/../graph-merger-examples`;
    const examples = readdirSync(examplesDirectory, { withFileTypes: true })
        .filter(entry => entry.isDirectory())
        .map(entry => entry.name);
    for (const example of examples) {
        test(example, async () => {
            const exampleDirectory = path.join(examplesDirectory, example);
            const ancestor = JSON.parse(await readFile(path.join(exampleDirectory, 'ancestor.flowgraph'), 'utf-8'));
            const versionA = JSON.parse(await readFile(path.join(exampleDirectory, 'version-a.flowgraph'), 'utf-8'));
            const versionB = JSON.parse(await readFile(path.join(exampleDirectory, 'version-b.flowgraph'), 'utf-8'));

            const merger = graphMerger();
            const { value, newEagerConflicts, newLazyConflicts } = merger(defaultContext, { ancestor, versionA, versionB });

            expect(value).toStrictEqual(JSON.parse(await readFile(path.join(exampleDirectory, 'merged.flowgraph'), 'utf-8')));
            expect({ newEagerConflicts, newLazyConflicts }).toStrictEqual(
                JSON.parse(await readFile(path.join(exampleDirectory, 'conflicts.json'), 'utf-8'))
            );
        });
    }
});
