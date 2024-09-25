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
import { cellValues, Choice, Optional, isGhost, optionalValue } from '@cinco-glsp/cinco-glsp-api';

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

export function mergeLazyConflict<T = any>(value: T): MergeResult<T> {
    return { value, newEagerConflicts: false, newLazyConflicts: true };
}

export function mapMergeResult<A, B>({ value, newEagerConflicts, newLazyConflicts }: MergeResult<A>, f: (a: A) => B): MergeResult<B> {
    return {
        value: f(value),
        newEagerConflicts,
        newLazyConflicts
    };
}

export type Merger<T = any> = (versions: Versions<T>) => MergeResult<T>;

export function optionalMerger(valueMerger: Merger<any>): Merger<Optional<any>> {
    return ({ ancestor, versionA, versionB }) => {
        // Compare optional-merging.fods

        // no conflict
        if (jsonEqual(ancestor, versionA)) {
            return {
                value: versionB,
                newEagerConflicts: false,
                newLazyConflicts: false
            };
        }

        if (jsonEqual(ancestor, versionB)) {
            return {
                value: versionA,
                newEagerConflicts: false,
                newLazyConflicts: false
            };
        }

        // both deleted
        if (versionA === undefined && versionB === undefined) {
            return {
                value: undefined,
                newEagerConflicts: false,
                newLazyConflicts: false
            };
        }

        // both defined
        if (versionA !== undefined && !isGhost(versionA) && versionB !== undefined && !isGhost(versionB)) {
            return valueMerger({ ancestor: optionalValue(ancestor), versionA, versionB });
        }

        // edit/delete
        if (ancestor !== undefined) {
            if (versionA === undefined && versionB !== undefined) {
                return {
                    value: {
                        tag: 'ghost',
                        value: optionalValue(versionB)
                    },
                    newEagerConflicts: false,
                    newLazyConflicts: true
                };
            }

            if (versionB === undefined && versionA !== undefined) {
                return {
                    value: {
                        tag: 'ghost',
                        value: optionalValue(versionA)
                    },
                    newEagerConflicts: false,
                    newLazyConflicts: true
                };
            }
        }

        const valueMerge = valueMerger({
            ancestor: optionalValue(ancestor),
            versionA: optionalValue(versionA),
            versionB: optionalValue(versionB)
        });

        // ghosts
        return {
            value: {
                tag: 'ghost',
                value: valueMerge.value
            },
            newEagerConflicts: valueMerge.newEagerConflicts,
            newLazyConflicts: true
        };
    };
}

export function recordMerger(mergers: Record<string, Merger>): Merger {
    return objectMerger(key => mergers[key] ?? eagerMerger());
}

export function sequenceMergeResultsObject(mergeResults: Record<string, MergeResult>): MergeResult {
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

export function entityListMerger(merger: Merger): Merger {
    return ({ ancestor, versionA, versionB }) =>
        mapMergeResult(
            mapMerger(merger)({
                ancestor: mapFromEntityArray(ancestor),
                versionA: mapFromEntityArray(versionA),
                versionB: mapFromEntityArray(versionB)
            }),
            entityArrayFromMap
        );
}

export function mapMerger(merger: Merger): Merger {
    return objectMerger(_ => optionalMerger(merger));
}

export function objectMerger(mergers: (key: string) => Merger): Merger {
    return ({ ancestor, versionA, versionB }) => {
        const keys = new Set<string>(Object.keys(ancestor).concat(Object.keys(versionA).concat(Object.keys(versionB))));
        const mergeResults: Record<string, MergeResult> = {};
        for (const key of keys) {
            const merger = mergers(key);
            const entityMerge = merger({
                ancestor: ancestor[key],
                versionA: versionA[key],
                versionB: versionB[key]
            });
            if (entityMerge.value !== undefined) {
                mergeResults[key] = entityMerge;
            }
        }
        return sequenceMergeResultsObject(mergeResults);
    };
}

export function eagerMerger(): Merger {
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
                versions: {
                    ancestor: strigifyUndefined(ancestor),
                    versionA: strigifyUndefined(versionA),
                    versionB: strigifyUndefined(versionB)
                }
            };
            return {
                value: conflict,
                newEagerConflicts: true,
                newLazyConflicts: false
            };
        }
    };
}

function strigifyUndefined(value: any): any {
    return value !== undefined ? value : 'undefined';
}

export function cellMerger(): Merger {
    return ({ ancestor, versionA, versionB }) => {
        const ancestorSet = new Set(cellValues(ancestor));
        const versionASet = new Set(cellValues(versionA));
        const versionBSet = new Set(cellValues(versionB));
        const survivorSet = ancestorSet.intersection(versionASet).intersection(versionBSet);
        const offspringSet = versionASet.union(versionBSet).difference(ancestorSet);
        const mergedSet = survivorSet.union(offspringSet);
        return {
            value: mergedSet.size === 1 ? [...mergedSet][0] : ({ tag: 'choice', options: [...mergedSet].sort() } satisfies Choice<any>),
            newEagerConflicts: false,
            newLazyConflicts: mergedSet.size > 1 && mergedSet.difference(ancestorSet).size > 0
        };
    };
}
