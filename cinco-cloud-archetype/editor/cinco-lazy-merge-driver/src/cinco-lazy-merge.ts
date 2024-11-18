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
import { command, flag, number, option, positional, run, string } from 'cmd-ts';
import { GraphModelStorage } from '@cinco-glsp/cinco-glsp-api';

const app = command({
    name: 'cinco-lazy-merge',
    args: {
        ancestorFile: positional({ type: string, displayName: 'ancestor-file' }),
        versionAFile: positional({ type: string, displayName: 'version-a-file' }),
        versionBFile: positional({ type: string, displayName: 'version-b-file' }),
        outputFile: positional({ type: string, displayName: 'output-file' }),
        conflictMarkerSize: option({ type: number, long: 'conflict-marker-size', defaultValue: () => 7 }),
        failMergeOnLazyConflicts: flag({ long: 'fail-merge-on-lazy-conflicts' }),
        mergeUnknownCellsArbitrarily: flag({ long: 'merge-unknown-cells-arbitrarily' })
    },
    handler: ({
        ancestorFile,
        versionAFile,
        versionBFile,
        outputFile,
        conflictMarkerSize,
        failMergeOnLazyConflicts,
        mergeUnknownCellsArbitrarily
    }) => {
        const ancestorInput = readFileSync(ancestorFile, 'utf-8');
        const versionAInput = readFileSync(versionAFile, 'utf-8');
        const versionBInput = readFileSync(versionBFile, 'utf-8');

        let output: string;
        let exitCode: number;

        try {
            const ancestor = JSON.parse(ancestorInput);
            const versionA = JSON.parse(versionAInput);
            const versionB = JSON.parse(versionBInput);

            const merger = graphMerger();
            const { value, newEagerConflicts, newLazyConflicts } = merger(
                { mergeUnknownCellsArbitrarily },
                { ancestor, versionA, versionB }
            );

            output = GraphModelStorage.stringifyGraphModel(value);
            exitCode = newEagerConflicts || (failMergeOnLazyConflicts && newLazyConflicts) ? 1 : 0;
        } catch (error) {
            output =
                'Unable to merge files:\n' +
                `${error}\n` +
                `${'<'.repeat(conflictMarkerSize)}\n` +
                assureEndsWithNewline(versionAInput) +
                `${'='.repeat(conflictMarkerSize)}\n` +
                assureEndsWithNewline(ancestorInput) +
                `${'='.repeat(conflictMarkerSize)}\n` +
                assureEndsWithNewline(versionBInput) +
                `${'>'.repeat(conflictMarkerSize)}\n`;
            exitCode = 2;
        }

        writeFileSync(outputFile, output);
        exit(exitCode);
    }
});

function assureEndsWithNewline(value: string): string {
    return value.endsWith('\n') ? value : `${value}\n`;
}

run(app, argv.slice(2));
