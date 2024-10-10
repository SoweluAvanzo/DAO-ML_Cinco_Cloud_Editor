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
import {
    eagerMerger,
    cellMerger,
    entityListMerger,
    mapMerger,
    mapMergeResult,
    recordMerger,
    optionalMerger,
    MergeResult,
    mergeOk,
    mergeLazyConflict,
    recursiveMerger,
    arbitraryMerger,
    defaultMerger,
    defaultContext
} from './combinators';

describe('mapMergeResult', () => {
    expect(mapMergeResult({ value: 2, newEagerConflicts: false, newLazyConflicts: true }, n => n + 2)).toStrictEqual({
        value: 4,
        newEagerConflicts: false,
        newLazyConflicts: true
    });
});

describe('optionalMerger', () => {
    const scenarios: { ancestor: any; versionA: any; versionB: any; result: MergeResult<any> }[] = [
        {
            ancestor: undefined,
            versionA: undefined,
            versionB: undefined,
            result: mergeOk(undefined)
        },
        {
            ancestor: undefined,
            versionA: undefined,
            versionB: { tag: 'ghost', value: 'baz' },
            result: mergeOk({ tag: 'ghost', value: 'baz' })
        },
        {
            ancestor: undefined,
            versionA: undefined,
            versionB: 'bar',
            result: mergeOk('bar')
        },
        {
            ancestor: undefined,
            versionA: { tag: 'ghost', value: 'bar' },
            versionB: undefined,
            result: mergeOk({ tag: 'ghost', value: 'bar' })
        },
        {
            ancestor: undefined,
            versionA: { tag: 'ghost', value: 'bar' },
            versionB: { tag: 'ghost', value: 'baz' },
            result: mergeLazyConflict({ tag: 'ghost', value: { tag: 'choice', options: ['bar', 'baz'] } })
        },
        {
            ancestor: undefined,
            versionA: { tag: 'ghost', value: 'bar' },
            versionB: 'baz',
            result: mergeLazyConflict({ tag: 'ghost', value: { tag: 'choice', options: ['bar', 'baz'] } })
        },
        { ancestor: undefined, versionA: 'bar', versionB: undefined, result: mergeOk('bar') },
        {
            ancestor: undefined,
            versionA: 'bar',
            versionB: { tag: 'ghost', value: 'baz' },
            result: mergeLazyConflict({ tag: 'ghost', value: { tag: 'choice', options: ['bar', 'baz'] } })
        },
        { ancestor: undefined, versionA: 'foo', versionB: 'bar', result: mergeLazyConflict({ tag: 'choice', options: ['bar', 'foo'] }) },
        {
            ancestor: { tag: 'ghost', value: 'foo' },
            versionA: undefined,
            versionB: undefined,
            result: mergeOk(undefined)
        },
        {
            ancestor: { tag: 'ghost', value: 'foo' },
            versionA: undefined,
            versionB: { tag: 'ghost', value: 'baz' },
            result: mergeLazyConflict({ tag: 'ghost', value: 'baz' })
        },
        {
            ancestor: { tag: 'ghost', value: 'foo' },
            versionA: undefined,
            versionB: 'baz',
            result: mergeLazyConflict({ tag: 'ghost', value: 'baz' })
        },
        {
            ancestor: { tag: 'ghost', value: 'foo' },
            versionA: { tag: 'ghost', value: 'bar' },
            versionB: undefined,
            result: mergeLazyConflict({ tag: 'ghost', value: 'bar' })
        },
        {
            ancestor: { tag: 'ghost', value: 'foo' },
            versionA: { tag: 'ghost', value: 'bar' },
            versionB: { tag: 'ghost', value: 'baz' },
            result: mergeLazyConflict({ tag: 'ghost', value: { tag: 'choice', options: ['bar', 'baz'] } })
        },
        {
            ancestor: { tag: 'ghost', value: 'foo' },
            versionA: { tag: 'ghost', value: 'bar' },
            versionB: 'baz',
            result: mergeLazyConflict({ tag: 'ghost', value: { tag: 'choice', options: ['bar', 'baz'] } })
        },
        {
            ancestor: { tag: 'ghost', value: 'foo' },
            versionA: 'bar',
            versionB: undefined,
            result: mergeLazyConflict({ tag: 'ghost', value: 'bar' })
        },
        {
            ancestor: { tag: 'ghost', value: 'foo' },
            versionA: 'bar',
            versionB: { tag: 'ghost', value: 'baz' },
            result: mergeLazyConflict({ tag: 'ghost', value: { tag: 'choice', options: ['bar', 'baz'] } })
        },
        {
            ancestor: { tag: 'ghost', value: 'foo' },
            versionA: 'bar',
            versionB: 'baz',
            result: mergeLazyConflict({ tag: 'choice', options: ['bar', 'baz'] })
        },
        {
            ancestor: 'foo',
            versionA: undefined,
            versionB: undefined,
            result: mergeOk(undefined)
        },
        {
            ancestor: 'foo',
            versionA: undefined,
            versionB: { tag: 'ghost', value: 'baz' },
            result: mergeLazyConflict({ tag: 'ghost', value: 'baz' })
        },
        {
            ancestor: 'foo',
            versionA: undefined,
            versionB: 'baz',
            result: mergeLazyConflict({ tag: 'ghost', value: 'baz' })
        },
        {
            ancestor: 'foo',
            versionA: { tag: 'ghost', value: 'bar' },
            versionB: undefined,
            result: mergeLazyConflict({ tag: 'ghost', value: 'bar' })
        },
        {
            ancestor: 'foo',
            versionA: { tag: 'ghost', value: 'bar' },
            versionB: { tag: 'ghost', value: 'baz' },
            result: mergeLazyConflict({ tag: 'ghost', value: { tag: 'choice', options: ['bar', 'baz'] } })
        },
        {
            ancestor: 'foo',
            versionA: { tag: 'ghost', value: 'bar' },
            versionB: 'baz',
            result: mergeLazyConflict({ tag: 'ghost', value: { tag: 'choice', options: ['bar', 'baz'] } })
        },
        {
            ancestor: 'foo',
            versionA: 'bar',
            versionB: undefined,
            result: mergeLazyConflict({ tag: 'ghost', value: 'bar' })
        },
        {
            ancestor: 'foo',
            versionA: 'bar',
            versionB: { tag: 'ghost', value: 'baz' },
            result: mergeLazyConflict({ tag: 'ghost', value: { tag: 'choice', options: ['bar', 'baz'] } })
        },
        {
            ancestor: 'foo',
            versionA: 'bar',
            versionB: 'baz',
            result: mergeLazyConflict({ tag: 'choice', options: ['bar', 'baz'] })
        }
    ];
    for (const [i, { ancestor, versionA, versionB, result }] of scenarios.entries()) {
        test(`optional merging scenario ${i + 1}`, () => {
            expect(optionalMerger(cellMerger())(defaultContext, { ancestor, versionA, versionB })).toStrictEqual(result);
        });
    }
    test('ghost of equal value anomaly', () => {
        expect(
            optionalMerger(cellMerger())(defaultContext, {
                ancestor: { tag: 'ghost', value: 'foo' },
                versionA: 'foo',
                versionB: 'baz'
            })
        ).toStrictEqual(mergeOk('baz'));
        // Is this actually what we want? A's decision for 'foo' is lost.
    });
});

describe('recordMerger', () => {
    test('merge record of cells', () => {
        expect(
            recordMerger({ x: cellMerger(), y: cellMerger() })(defaultContext, {
                ancestor: { x: 'foo', y: 'doo' },
                versionA: { x: 'bar', y: 'dar' },
                versionB: { x: 'baz', y: 'daz' }
            })
        ).toStrictEqual({
            value: {
                x: { tag: 'choice', options: ['bar', 'baz'] },
                y: { tag: 'choice', options: ['dar', 'daz'] }
            },
            newEagerConflicts: false,
            newLazyConflicts: true
        });
    });
    test('merge unknown keys eagerly', () => {
        expect(
            recordMerger({})(
                { mergeUnknownCellsArbitrarily: false },
                {
                    ancestor: { x: 'foo' },
                    versionA: { x: 'bar' },
                    versionB: { x: 'baz' }
                }
            )
        ).toStrictEqual({
            value: {
                x: {
                    tag: 'eager-merge-conflict',
                    versions: {
                        ancestor: 'foo',
                        versionA: 'bar',
                        versionB: 'baz'
                    }
                }
            },
            newEagerConflicts: true,
            newLazyConflicts: false
        });
    });
    test('merge unknown keys arbitrarily', () => {
        expect(
            recordMerger({})(
                { mergeUnknownCellsArbitrarily: true },
                {
                    ancestor: { x: 'foo' },
                    versionA: { x: 'bar' },
                    versionB: { x: 'baz' }
                }
            )
        ).toStrictEqual({
            value: {
                x: 'baz'
            },
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('optional merging for missing keys', () => {
        expect(
            recordMerger({ x: cellMerger() })(defaultContext, {
                ancestor: { x: 'foo' },
                versionA: { x: 'bar' },
                versionB: {}
            })
        ).toStrictEqual({
            value: {
                x: {
                    tag: 'ghost',
                    value: 'bar'
                }
            },
            newEagerConflicts: false,
            newLazyConflicts: true
        });
    });
});

describe('entityMapMerger', () => {
    test('update and addition', () => {
        expect(
            entityListMerger(recordMerger({ value: cellMerger() }))(defaultContext, {
                ancestor: [{ id: 'x', value: 'foo' }],
                versionA: [
                    { id: 'x', value: 'bar' },
                    { id: 'y', value: 'dar' }
                ],
                versionB: [
                    { id: 'x', value: 'baz' },
                    { id: 'z', value: 'faz' }
                ]
            })
        ).toStrictEqual({
            value: [
                { id: 'x', value: { tag: 'choice', options: ['bar', 'baz'] } },
                { id: 'y', value: 'dar' },
                { id: 'z', value: 'faz' }
            ],
            newEagerConflicts: false,
            newLazyConflicts: true
        });
    });
});

describe('mapMerger', () => {
    test('updates', () => {
        expect(
            mapMerger(cellMerger())(defaultContext, {
                ancestor: { x: 'foo', y: 'doo' },
                versionA: { x: 'bar', y: 'dar' },
                versionB: { x: 'baz', y: 'daz' }
            })
        ).toStrictEqual({
            value: {
                x: { tag: 'choice', options: ['bar', 'baz'] },
                y: { tag: 'choice', options: ['dar', 'daz'] }
            },
            newEagerConflicts: false,
            newLazyConflicts: true
        });
    });
    test('additions', () => {
        expect(
            mapMerger(cellMerger())(defaultContext, {
                ancestor: {},
                versionA: { x: 'bar' },
                versionB: { y: 'daz' }
            })
        ).toStrictEqual({
            value: {
                x: 'bar',
                y: 'daz'
            },
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('deleted in version A', () => {
        expect(
            mapMerger(cellMerger())(defaultContext, {
                ancestor: { x: 'foo' },
                versionA: {},
                versionB: { x: 'foo' }
            })
        ).toStrictEqual({ value: {}, newEagerConflicts: false, newLazyConflicts: false });
    });
    test('deleted in version B', () => {
        expect(
            mapMerger(cellMerger())(defaultContext, {
                ancestor: { x: 'foo' },
                versionA: { x: 'foo' },
                versionB: {}
            })
        ).toStrictEqual({ value: {}, newEagerConflicts: false, newLazyConflicts: false });
    });
    test('record deleted', () => {
        expect(
            mapMerger(recordMerger({ y: cellMerger() }))(defaultContext, {
                ancestor: { x: { y: 'foo' } },
                versionA: { x: { y: 'foo' } },
                versionB: {}
            })
        ).toStrictEqual({ value: {}, newEagerConflicts: false, newLazyConflicts: false });
    });
    test('edited in A, deleted in B', () => {
        expect(
            mapMerger(cellMerger())(defaultContext, {
                ancestor: { x: 'foo' },
                versionA: { x: 'bar' },
                versionB: {}
            })
        ).toStrictEqual({ value: { x: { tag: 'ghost', value: 'bar' } }, newEagerConflicts: false, newLazyConflicts: true });
    });
    test('deleted in A, edited in B', () => {
        expect(
            mapMerger(cellMerger())(defaultContext, {
                ancestor: { x: 'foo' },
                versionA: {},
                versionB: { x: 'bar' }
            })
        ).toStrictEqual({ value: { x: { tag: 'ghost', value: 'bar' } }, newEagerConflicts: false, newLazyConflicts: true });
    });
});

describe('eagerMerger', () => {
    test('unchanged value', () => {
        expect(eagerMerger()(defaultContext, { ancestor: 'foo', versionA: 'foo', versionB: 'foo' })).toStrictEqual({
            value: 'foo',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('a changed', () => {
        expect(eagerMerger()(defaultContext, { ancestor: 'foo', versionA: 'bar', versionB: 'foo' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('b changed', () => {
        expect(eagerMerger()(defaultContext, { ancestor: 'foo', versionA: 'foo', versionB: 'bar' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('changed to the same value', () => {
        expect(eagerMerger()(defaultContext, { ancestor: 'foo', versionA: 'bar', versionB: 'bar' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('changed to different values', () => {
        expect(eagerMerger()(defaultContext, { ancestor: 'foo', versionA: 'bar', versionB: 'baz' })).toStrictEqual({
            value: {
                tag: 'eager-merge-conflict',
                versions: { ancestor: 'foo', versionA: 'bar', versionB: 'baz' }
            },
            newEagerConflicts: true,
            newLazyConflicts: false
        });
    });
});

describe('cellMerger', () => {
    test('unchanged value', () => {
        expect(cellMerger()(defaultContext, { ancestor: 'foo', versionA: 'foo', versionB: 'foo' })).toStrictEqual({
            value: 'foo',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('a changed', () => {
        expect(cellMerger()(defaultContext, { ancestor: 'foo', versionA: 'bar', versionB: 'foo' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('b changed', () => {
        expect(cellMerger()(defaultContext, { ancestor: 'foo', versionA: 'foo', versionB: 'bar' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('changed to the same value', () => {
        expect(cellMerger()(defaultContext, { ancestor: 'foo', versionA: 'bar', versionB: 'bar' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('changed to different values', () => {
        expect(cellMerger()(defaultContext, { ancestor: 'foo', versionA: 'bar', versionB: 'baz' })).toStrictEqual({
            value: {
                tag: 'choice',
                options: ['bar', 'baz']
            },
            newEagerConflicts: false,
            newLazyConflicts: true
        });
    });
    test('a changed, b option added', () => {
        expect(
            cellMerger()(defaultContext, { ancestor: 'foo', versionA: { tag: 'choice', options: ['foo', 'bar'] }, versionB: 'baz' })
        ).toStrictEqual({
            value: {
                tag: 'choice',
                options: ['bar', 'baz']
            },
            newEagerConflicts: false,
            newLazyConflicts: true
        });
    });
    test('sort options', () => {
        expect(cellMerger()(defaultContext, { ancestor: 'foo', versionA: 'bar', versionB: 'baz' })).toStrictEqual({
            value: {
                tag: 'choice',
                options: ['bar', 'baz']
            },
            newEagerConflicts: false,
            newLazyConflicts: true
        });
        expect(cellMerger()(defaultContext, { ancestor: 'foo', versionA: 'baz', versionB: 'bar' })).toStrictEqual({
            value: {
                tag: 'choice',
                options: ['bar', 'baz']
            },
            newEagerConflicts: false,
            newLazyConflicts: true
        });
    });
});

describe('recursiveMerger', () => {
    test('merge recursive record', () => {
        const merger = recordMerger({
            x: cellMerger(),
            child: recursiveMerger(() => merger)
        });
        expect(
            merger(defaultContext, {
                ancestor: {
                    x: 'foo',
                    child: {
                        x: 'doo'
                    }
                },
                versionA: {
                    x: 'bar',
                    child: {
                        x: 'dar'
                    }
                },
                versionB: {
                    x: 'baz',
                    child: {
                        x: 'daz'
                    }
                }
            })
        ).toStrictEqual({
            value: {
                x: {
                    tag: 'choice',
                    options: ['bar', 'baz']
                },
                child: {
                    x: {
                        tag: 'choice',
                        options: ['dar', 'daz']
                    }
                }
            },
            newEagerConflicts: false,
            newLazyConflicts: true
        });
    });
});

describe('arbitraryMerger', () => {
    test('select first', () => {
        expect(arbitraryMerger()(defaultContext, { ancestor: 'coo', versionA: 'car', versionB: 'caz' })).toStrictEqual({
            value: 'car',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('select second', () => {
        expect(arbitraryMerger()(defaultContext, { ancestor: 'foo', versionA: 'bar', versionB: 'baz' })).toStrictEqual({
            value: 'baz',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
});

describe('defaultMerger', () => {
    test('conflict', () => {
        expect(defaultMerger('bam')(defaultContext, { ancestor: 'foo', versionA: 'bar', versionB: 'baz' })).toStrictEqual({
            value: 'bam',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
});
