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

import { jsonEqual, mapMap, mapFromEntityArray } from './json-utilities';

type Versions<T = any> = Readonly<{
    ancestor: T | undefined;
    versionA: T;
    versionB: T;
}>;

type EagerMergeConflict<T = any> = Readonly<{
    tag: 'eager-merge-conflict';
    versions: Versions<T>;
}>;

export interface MergeResult<T = any> {
    value: T;
    newEagerConflicts: boolean;
    newLazyConflicts: boolean;
}

export function mergeOk<T = any>(value: T): MergeResult<T> {
    return { value, newEagerConflicts: false, newLazyConflicts: false };
}

export function mapMergeResult<A, B>({ value, newEagerConflicts, newLazyConflicts }: MergeResult<A>, f: (a: A) => B): MergeResult<B> {
    return {
        value: f(value),
        newEagerConflicts,
        newLazyConflicts
    };
}

export type Merger = (versions: Versions) => MergeResult;

export function mergeRecord(mergers: Record<string, Merger>): Merger {
    return ({ ancestor, versionA, versionB }) => {
        ancestor !== undefined && validateNoUnknownKeysForRecordMerger(mergers, ancestor);
        validateNoUnknownKeysForRecordMerger(mergers, versionA);
        validateNoUnknownKeysForRecordMerger(mergers, versionB);
        return sequenceMergeResultsMap(
            mapMap(mergers, (merger, key) =>
                merger({
                    ancestor: (ancestor ?? {})[key],
                    versionA: versionA[key],
                    versionB: versionB[key]
                })
            )
        );
    };
}

export function sequenceMergeResultsMap(mergeResults: Record<string, MergeResult>): MergeResult {
    return {
        value: mapMap(mergeResults, ({ value }) => value),
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

export function lazyMergeEntityList(merger: Merger): Merger {
    return ({ ancestor, versionA, versionB }) =>
        mapMergeResult(
            lazyMergeMap(merger)({
                ancestor: mapFromEntityArray(ancestor ?? []),
                versionA: mapFromEntityArray(versionA),
                versionB: mapFromEntityArray(versionB)
            }),
            Object.values
        );
}

export function lazyMergeMap(merger: Merger): Merger {
    return ({ ancestor, versionA, versionB }) => {
        if (ancestor !== undefined) {
            for (const ancestorKey of Object.keys(ancestor)) {
                // Entries may never be removed from a map, to keep being able to merge old branches.
                if (!(ancestor in versionA) || !(ancestorKey in versionB)) {
                    throw new Error(`Entity with key ${ancestorKey} has been removed from map.`);
                }
            }
        }
        const keys = new Set<string>();
        Object.keys(versionA).forEach(key => keys.add(key));
        Object.keys(versionB).forEach(key => keys.add(key));
        return sequenceMergeResultsMap(
            Object.fromEntries(
                [...keys].map(key => {
                    if (key in versionA && key in versionB) {
                        return [
                            key,
                            merger({
                                ancestor: (ancestor ?? {})[key],
                                versionA: versionA[key],
                                versionB: versionB[key]
                            })
                        ];
                    } else if (key in versionA) {
                        return [key, mergeOk(versionA[key])];
                    } else if (key in versionB) {
                        return [key, mergeOk(versionB[key])];
                    } else {
                        throw new Error(`Impossible state: Key ${key} neither in versionA nor in versionB.`);
                    }
                })
            )
        );
    };
}

export function eagerMergeCell(): Merger {
    return ({ ancestor, versionA, versionB }) => {
        if (jsonEqual(versionA, versionB)) {
            return mergeOk(versionA);
        } else if (jsonEqual(versionB, ancestor)) {
            return mergeOk(versionA);
        } else if (jsonEqual(versionA, ancestor)) {
            return mergeOk(versionB);
        } else {
            const conflict: EagerMergeConflict = {
                tag: 'eager-merge-conflict',
                versions: { ancestor, versionA, versionB }
            };
            return {
                value: conflict,
                newEagerConflicts: true,
                newLazyConflicts: false
            };
        }
    };
}
