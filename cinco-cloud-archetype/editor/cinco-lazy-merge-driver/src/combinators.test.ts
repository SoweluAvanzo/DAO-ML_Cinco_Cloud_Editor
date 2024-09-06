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
import { eagerMergeCell, mergeRecord } from './combinators';
import { lazyMergeCell } from './assignments';

describe('mergeRecord', () => {
    test('merge record of assignments', () => {
        expect(
            mergeRecord({ x: lazyMergeCell(), y: lazyMergeCell() })({
                ancestor: { x: { a: ['foo'] }, y: { b: ['zoo'] } },
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
});

describe('eagerMergeCell', () => {
    test('unchanged value', () => {
        expect(eagerMergeCell()({ ancestor: 'foo', versionA: 'foo', versionB: 'foo' })).toStrictEqual({
            value: 'foo',
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
