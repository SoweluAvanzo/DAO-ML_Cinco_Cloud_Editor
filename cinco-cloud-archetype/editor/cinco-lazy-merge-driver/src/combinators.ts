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

import 'core-js/actual/set';
import { jsonEqual, mapMap, mapFromEntityArray, entityArrayFromMap } from './json-utilities';
import { cellValues } from '@cinco-glsp/cinco-glsp-api';

type Versions<T = any> = Readonly<{
    ancestor: T;
    versionA: T;
    versionB: T;
}>;

type EagerMergeConflict<T = any> = Readonly<{
    tag: 'eager-merge-conflict';
    versions: Versions<T>;
}>;

export type MergeResult<T = any> = Readonly<{
    value: T;
    newEagerConflicts: boolean;
    newLazyConflicts: boolean;
}>;

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

export function recordMerger(mergers: Record<string, Merger>): Merger {
    return ({ ancestor, versionA, versionB }) => {
        validateNoUnknownKeysForRecordMerger(mergers, ancestor);
        validateNoUnknownKeysForRecordMerger(mergers, versionA);
        validateNoUnknownKeysForRecordMerger(mergers, versionB);
        const ghost = (ancestor.ghost && versionA.ghost && versionB.ghost) || (!ancestor.ghost && (versionA.ghost || versionB.ghost));
        return sequenceMergeResultsMap(
            mapMap(mergers, (merger, key) =>
                merger({
                    ancestor: ancestor[key],
                    versionA: versionA[key],
                    versionB: versionB[key]
                })
            ),
            ghost ? { ghost: true } : {}
        );
    };
}

export function sequenceMergeResultsMap(mergeResults: Record<string, MergeResult>, addition: Record<string, any> = {}): MergeResult {
    return {
        value: { ...mapMap(mergeResults, ({ value }) => value), ...addition },
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
        if (key !== 'ghost' && !(key in mergers)) {
            throw new Error(`Key ${key} has no merger defined.`);
        }
    }
}

export function lazyEntityListMerger(merger: Merger): Merger {
    return ({ ancestor, versionA, versionB }) =>
        mapMergeResult(
            lazyMapMerger(merger)({
                ancestor: mapFromEntityArray(ancestor),
                versionA: mapFromEntityArray(versionA),
                versionB: mapFromEntityArray(versionB)
            }),
            entityArrayFromMap
        );
}

export function lazyMapMerger(merger: Merger): Merger {
    return ({ ancestor, versionA, versionB }) => {
        const keys = new Set<string>(Object.keys(ancestor).concat(Object.keys(versionA).concat(Object.keys(versionB))));
        const mergeResults: Record<string, MergeResult> = {};
        for (const key of keys) {
            if (key in ancestor) {
                if (!(key in versionA) && !(key in versionB)) {
                    // Omit
                } else if (key in versionA && !(key in versionB)) {
                    if (jsonEqual(ancestor[key], versionA[key])) {
                        // Omit
                    } else {
                        mergeResults[key] = { value: { ...versionA[key], ghost: true }, newEagerConflicts: false, newLazyConflicts: true };
                    }
                } else if (!(key in versionA) && key in versionB) {
                    if (jsonEqual(ancestor[key], versionB[key])) {
                        // Omit
                    } else {
                        mergeResults[key] = { value: { ...versionB[key], ghost: true }, newEagerConflicts: false, newLazyConflicts: true };
                    }
                } else {
                    mergeResults[key] = merger({
                        ancestor: ancestor[key],
                        versionA: versionA[key],
                        versionB: versionB[key]
                    });
                }
            } else {
                if (key in versionA && key in versionB) {
                    // TODO Implement this
                    throw new Error(`Entity with key ${key} added in both versions without ancestor.`);
                } else if (key in versionA) {
                    mergeResults[key] = mergeOk(versionA[key]);
                } else {
                    /* istanbul ignore else */
                    if (key in versionB) {
                        mergeResults[key] = mergeOk(versionB[key]);
                    } else {
                        throw new Error(`Impossible state: Key ${key} neither in ancestor, nor in versionA, nor in versionB.`);
                    }
                }
            }
        }
        return sequenceMergeResultsMap(mergeResults);
    };
}

export function eagerCellMerger(): Merger {
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

export function lazyCellMerger(): Merger {
    return ({ ancestor, versionA, versionB }) => {
        const ancestorSet = new Set(cellValues(ancestor));
        const versionASet = new Set(cellValues(versionA));
        const versionBSet = new Set(cellValues(versionB));
        const survivorSet = ancestorSet.intersection(versionASet).intersection(versionBSet);
        const offspringSet = versionASet.union(versionBSet).difference(ancestorSet);
        const mergedSet = survivorSet.union(offspringSet);
        return {
            value: mergedSet.size === 1 ? [...mergedSet][0] : { tag: 'choice', options: [...mergedSet].sort() },
            newEagerConflicts: false,
            newLazyConflicts: mergedSet.size > 1 && mergedSet.difference(ancestorSet).size > 0
        };
    };
}
