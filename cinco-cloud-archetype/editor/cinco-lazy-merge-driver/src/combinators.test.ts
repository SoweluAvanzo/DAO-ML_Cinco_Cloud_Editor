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
import { eagerMergeCell, lazyMergeEntityList, lazyMergeMap, mapMergeResult, mergeRecord } from './combinators';
import { lazyMergeCell } from './assignments';

describe('mapMergeResult', () => {
    expect(mapMergeResult({ value: 2, newEagerConflicts: false, newLazyConflicts: true }, n => n + 2)).toStrictEqual({
        value: 4,
        newEagerConflicts: false,
        newLazyConflicts: true
    });
});

describe('mergeRecord', () => {
    test('merge record of assignments', () => {
        expect(
            mergeRecord({ x: lazyMergeCell(), y: lazyMergeCell() })({
                ancestor: { x: { a: ['foo'] }, y: { b: ['doo'] } },
                versionA: { x: { c: ['bar'] }, y: { d: ['dar'] } },
                versionB: { x: { e: ['baz'] }, y: { f: ['daz'] } }
            })
        ).toStrictEqual({
            value: {
                x: { c: ['bar'], e: ['baz'] },
                y: { d: ['dar'], f: ['daz'] }
            },
            newEagerConflicts: false,
            newLazyConflicts: true
        });
    });
    test('unknown key in ancestor', () => {
        expect(() =>
            mergeRecord({ x: lazyMergeCell() })({
                ancestor: { x: { a: ['foo'] }, y: { b: ['doo'] } },
                versionA: { x: { c: ['bar'] } },
                versionB: { x: { d: ['baz'] } }
            })
        ).toThrow(new Error('Key y has no merger defined.'));
    });
    test('unknown key in version A', () => {
        expect(() =>
            mergeRecord({ x: lazyMergeCell() })({
                ancestor: { x: { a: ['foo'] } },
                versionA: { x: { b: ['bar'] }, y: { c: ['doo'] } },
                versionB: { x: { d: ['baz'] } }
            })
        ).toThrow(new Error('Key y has no merger defined.'));
    });
    test('unknown key in version B', () => {
        expect(() =>
            mergeRecord({ x: lazyMergeCell() })({
                ancestor: { x: { a: ['foo'] } },
                versionA: { x: { b: ['bar'] } },
                versionB: { x: { c: ['baz'] }, y: { d: ['doo'] } }
            })
        ).toThrow(new Error('Key y has no merger defined.'));
    });
});

describe('lazyMergeEntityMap', () => {
    test('update and addition', () => {
        expect(
            lazyMergeEntityList(mergeRecord({ value: lazyMergeCell() }))({
                ancestor: [{ id: 'x', value: { a: ['foo'] } }],
                versionA: [
                    { id: 'x', value: { b: ['bar'] } },
                    { id: 'y', value: { c: ['dar'] } }
                ],
                versionB: [
                    { id: 'x', value: { d: ['baz'] } },
                    { id: 'z', value: { e: ['faz'] } }
                ]
            })
        ).toStrictEqual({
            value: [
                { id: 'x', value: { b: ['bar'], d: ['baz'] } },
                { id: 'y', value: { c: ['dar'] } },
                { id: 'z', value: { e: ['faz'] } }
            ],
            newEagerConflicts: false,
            newLazyConflicts: true
        });
    });
});

describe('lazyMergeMap', () => {
    test('updates', () => {
        expect(
            lazyMergeMap(lazyMergeCell())({
                ancestor: { x: { a: ['foo'] }, y: { b: ['doo'] } },
                versionA: { x: { c: ['bar'] }, y: { d: ['dar'] } },
                versionB: { x: { e: ['baz'] }, y: { f: ['daz'] } }
            })
        ).toStrictEqual({
            value: {
                x: { c: ['bar'], e: ['baz'] },
                y: { d: ['dar'], f: ['daz'] }
            },
            newEagerConflicts: false,
            newLazyConflicts: true
        });
    });
    test('additions', () => {
        expect(
            lazyMergeMap(lazyMergeCell())({
                ancestor: {},
                versionA: { x: { a: ['bar'] } },
                versionB: { y: { b: ['daz'] } }
            })
        ).toStrictEqual({
            value: {
                x: { a: ['bar'] },
                y: { b: ['daz'] }
            },
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('key removed in version A', () => {
        expect(() =>
            lazyMergeMap(lazyMergeCell())({
                ancestor: { x: { a: ['foo'] } },
                versionA: {},
                versionB: { x: { a: ['foo'] } }
            })
        ).toThrow('Key x has been removed from map.');
    });
    test('key removed in version B', () => {
        expect(() =>
            lazyMergeMap(lazyMergeCell())({
                ancestor: { x: { a: ['foo'] } },
                versionA: { x: { a: ['foo'] } },
                versionB: {}
            })
        ).toThrow('Key x has been removed from map.');
    });
});

describe('eagerMergeCell', () => {
    test('unchanged value', () => {
        expect(eagerMergeCell()({ ancestor: 'foo', versionA: 'foo', versionB: 'foo' })).toStrictEqual({
            value: 'foo',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('a changed', () => {
        expect(eagerMergeCell()({ ancestor: 'foo', versionA: 'bar', versionB: 'foo' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('b changed', () => {
        expect(eagerMergeCell()({ ancestor: 'foo', versionA: 'foo', versionB: 'bar' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('changed to the same value', () => {
        expect(eagerMergeCell()({ ancestor: 'foo', versionA: 'bar', versionB: 'bar' })).toStrictEqual({
            value: 'bar',
            newEagerConflicts: false,
            newLazyConflicts: false
        });
    });
    test('changed to different values', () => {
        expect(eagerMergeCell()({ ancestor: 'foo', versionA: 'bar', versionB: 'baz' })).toStrictEqual({
            value: {
                tag: 'eager-merge-conflict',
                versions: { ancestor: 'foo', versionA: 'bar', versionB: 'baz' }
            },
            newEagerConflicts: true,
            newLazyConflicts: false
        });
    });
});
