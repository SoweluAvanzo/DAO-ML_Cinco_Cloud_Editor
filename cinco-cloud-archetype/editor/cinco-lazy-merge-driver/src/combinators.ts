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

import { jsonEqual, mapRecord } from './json-utilities';

type Conflict = Readonly<{
    tag: 'eager-merge-conflict';
    ancestor: any;
    versionA: any;
    versionB: any;
}>;

export interface MergeResult {
    value: any;
    newEagerConflicts: boolean;
    newLazyConflicts: boolean;
}

export type Merger = (ancestor: any, versionA: any, versionB: any) => MergeResult;

export function mergeRecord(mergers: Record<string, Merger>): Merger {
    return (ancestor, versionA, versionB) => {
        validateNoUnknownKeysForRecordMerger(mergers, ancestor);
        validateNoUnknownKeysForRecordMerger(mergers, versionA);
        validateNoUnknownKeysForRecordMerger(mergers, versionB);
        return traverseMergeResultsMap(mergers, (merger, key) => merger(ancestor[key], versionA[key], versionB[key]));
    };
}

export function traverseMergeResultsMap(inputMap: Record<string, any>, f: (value: any, key: string) => MergeResult): MergeResult {
    const mergeResults = mapRecord(inputMap, f);
    return {
        value: mapRecord(mergeResults, ({ value }) => value),
        newEagerConflicts: Object.values(mergeResults)
            .map(({ newEagerConflicts }) => newEagerConflicts)
            .some(x => x),
        newLazyConflicts: Object.values(mergeResults)
            .map(({ newLazyConflicts }) => newLazyConflicts)
            .some(x => x)
    };
}

function validateNoUnknownKeysForRecordMerger(mergers: Record<string, Merger>, record: Record<string, any>): void {
    for (const key of Object.keys(record)) {
        if (!(key in mergers)) {
            throw new Error(`Key ${key} has no merger defined.`);
        }
    }
}

export function mergeEager(): Merger {
    return (ancestor, versionA, versionB) => {
        if (jsonEqual(versionA, versionB)) {
            return {
                value: versionA,
                newEagerConflicts: false,
                newLazyConflicts: false
            };
        } else {
            const conflict: Conflict = {
                tag: 'eager-merge-conflict',
                ancestor,
                versionA,
                versionB
            };
            return {
                value: conflict,
                newEagerConflicts: true,
                newLazyConflicts: false
            };
        }
    };
}
