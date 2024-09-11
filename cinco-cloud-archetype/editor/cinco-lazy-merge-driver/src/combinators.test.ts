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
import { eagerCellMerger, lazyCellMerger, lazyEntityListMerger, lazyMapMerger, mapMergeResult, recordMerger } from './combinators';

describe('mapMergeResult', () => {
    expect(mapMergeResult({ value: 2, newEagerConflicts: false, newLazyConflicts: true }, n => n + 2)).toStrictEqual({
        value: 4,
        newEagerConflicts: false,
        newLazyConflicts: true
    });
});

describe('recordMerger', () => {
    test('merge record of cells', () => {
        expect(
            recordMerger({ x: lazyCellMerger(), y: lazyCellMerger() })({
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
    test('unknown key in ancestor', () => {
        expect(() =>
            recordMerger({ x: lazyCellMerger() })({
                ancestor: { x: 'foo', y: 'doo' },
                versionA: { x: 'bar' },
                versionB: { x: 'baz' }
            })
        ).toThrow(new Error('Key y has no merger defined.'));
    });
    test('unknown key in version A', () => {
        expect(() =>
            recordMerger({ x: lazyCellMerger() })({
                ancestor: { x: 'foo' },
                versionA: { x: 'bar', y: 'doo' },
                versionB: { x: 'baz' }
            })
        ).toThrow(new Error('Key y has no merger defined.'));
    });
    test('unknown key in version B', () => {
        expect(() =>
            recordMerger({ x: lazyCellMerger() })({
                ancestor: { x: 'foo' },
                versionA: { x: 'bar' },
                versionB: { x: 'baz', y: 'doo' }
            })
        ).toThrow(new Error('Key y has no merger defined.'));
    });
    test('ghost in ancestor and A', () => {
        expect(
            recordMerger({ x: lazyCellMerger() })({
                ancestor: { x: 'foo', ghost: true },
                versionA: { x: 'bar', ghost: true },
                versionB: { x: 'foo' }
            })
        ).toStrictEqual({ value: { x: 'bar' }, newEagerConflicts: false, newLazyConflicts: false });
    });
    test('ghost in A', () => {
        expect(
            recordMerger({ x: lazyCellMerger() })({
                ancestor: { x: 'foo' },
                versionA: { x: 'bar', ghost: true },
                versionB: { x: 'foo' }
            })
        ).toStrictEqual({ value: { x: 'bar', ghost: true }, newEagerConflicts: false, newLazyConflicts: false });
    });
    test('ghost in B', () => {
        expect(
            recordMerger({ x: lazyCellMerger() })({
                ancestor: { x: 'foo' },
                versionA: { x: 'bar' },
                versionB: { x: 'foo', ghost: true }
            })
        ).toStrictEqual({ value: { x: 'bar', ghost: true }, newEagerConflicts: false, newLazyConflicts: false });
    });
    test('ghost in all', () => {
        expect(
            recordMerger({ x: lazyCellMerger() })({
                ancestor: { x: 'foo', ghost: true },
                versionA: { x: 'bar', ghost: true },
                versionB: { x: 'foo', ghost: true }
            })
        ).toStrictEqual({ value: { x: 'bar', ghost: true }, newEagerConflicts: false, newLazyConflicts: false });
    });
});

describe('lazyEntityMapMerger', () => {
    test('update and addition', () => {
        expect(
            lazyEntityListMerger(recordMerger({ value: lazyCellMerger() }))({
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

describe('lazyMapMerger', () => {
    test('updates', () => {
        expect(
            lazyMapMerger(lazyCellMerger())({
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
            lazyMapMerger(lazyCellMerger())({
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
            lazyMapMerger(lazyCellMerger())({
                ancestor: { x: 'foo' },
                versionA: {},
                versionB: { x: 'foo' }
            })
        ).toStrictEqual({ value: {}, newEagerConflicts: false, newLazyConflicts: false });
    });
    test('deleted in version B', () => {
        expect(
            lazyMapMerger(lazyCellMerger())({
                ancestor: { x: 'foo' },
                versionA: { x: 'foo' },
                versionB: {}
            })
        ).toStrictEqual({ value: {}, newEagerConflicts: false, newLazyConflicts: false });
    });
    test('record deleted', () => {
        expect(
            lazyMapMerger(recordMerger({ y: lazyCellMerger() }))({
                ancestor: { x: { y: 'foo' } },
                versionA: { x: { y: 'foo' } },
                versionB: {}
            })
        ).toStrictEqual({ value: {}, newEagerConflicts: false, newLazyConflicts: false });
    });
    test('edited in A, deleted in B', () => {
        expect(
            lazyMapMerger(recordMerger({ y: lazyCellMerger() }))({
                ancestor: { x: { y: 'foo' } },
                versionA: { x: { y: 'bar' } },
                versionB: {}
            })
        ).toStrictEqual({ value: { x: { y: 'bar', ghost: true } }, newEagerConflicts: false, newLazyConflicts: true });
    });
    test('deleted in A, edited in B', () => {
        expect(
            lazyMapMerger(recordMerger({ y: lazyCellMerger() }))({
                ancestor: { x: { y: 'foo' } },
                versionA: {},
                versionB: { x: { y: 'bar' } }
            })
        ).toStrictEqual({ value: { x: { y: 'bar', ghost: true } }, newEagerConflicts: false, newLazyConflicts: true });
    });
});

describe('eagerCellMerger', () => {
    test('unchanged value', () => {
        expect(eagerCellMerger()({ ancestor: 'foo', versionA: 'foo', versionB: 'foo' })).toStrictEqual({
            value: 'foo',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('a changed', () => {
        expect(eagerCellMerger()({ ancestor: 'foo', versionA: 'bar', versionB: 'foo' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('b changed', () => {
        expect(eagerCellMerger()({ ancestor: 'foo', versionA: 'foo', versionB: 'bar' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('changed to the same value', () => {
        expect(eagerCellMerger()({ ancestor: 'foo', versionA: 'bar', versionB: 'bar' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('changed to different values', () => {
        expect(eagerCellMerger()({ ancestor: 'foo', versionA: 'bar', versionB: 'baz' })).toStrictEqual({
            value: {
                tag: 'eager-merge-conflict',
                versions: { ancestor: 'foo', versionA: 'bar', versionB: 'baz' }
            },
            newEagerConflicts: true,
            newLazyConflicts: false
        });
    });
});

describe('lazyCellMerger', () => {
    test('unchanged value', () => {
        expect(lazyCellMerger()({ ancestor: 'foo', versionA: 'foo', versionB: 'foo' })).toStrictEqual({
            value: 'foo',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('a changed', () => {
        expect(lazyCellMerger()({ ancestor: 'foo', versionA: 'bar', versionB: 'foo' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('b changed', () => {
        expect(lazyCellMerger()({ ancestor: 'foo', versionA: 'foo', versionB: 'bar' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('changed to the same value', () => {
        expect(lazyCellMerger()({ ancestor: 'foo', versionA: 'bar', versionB: 'bar' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('changed to different values', () => {
        expect(lazyCellMerger()({ ancestor: 'foo', versionA: 'bar', versionB: 'baz' })).toStrictEqual({
            value: {
                tag: 'choice',
                options: ['bar', 'baz']
            },
            newEagerConflicts: false,
            newLazyConflicts: true
        });
    });
    test('a changed, b option added', () => {
        expect(lazyCellMerger()({ ancestor: 'foo', versionA: { tag: 'choice', options: ['foo', 'bar'] }, versionB: 'baz' })).toStrictEqual({
            value: {
                tag: 'choice',
                options: ['bar', 'baz']
            },
            newEagerConflicts: false,
            newLazyConflicts: true
        });
    });
});
