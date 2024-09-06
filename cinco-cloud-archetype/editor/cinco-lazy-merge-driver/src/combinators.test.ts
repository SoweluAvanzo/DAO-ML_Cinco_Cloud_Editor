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
import { mergeEager, mergeRecord } from './combinators';
import { mergeAssignments } from './assignments';

describe('mergeRecord', () => {
    test('merge record of assignments', () => {
        expect(
            mergeRecord({ x: mergeAssignments(), y: mergeAssignments() })(
                { x: { a: ['foo'] }, y: { b: ['zoo'] } },
                { x: { c: ['bar'] }, y: { d: ['dar'] } },
                { x: { e: ['baz'] }, y: { f: ['daz'] } }
            )
        ).toStrictEqual({
            x: { c: ['bar'], e: ['baz'] },
            y: { d: ['dar'], f: ['daz'] }
        });
    });
});

describe('mergeEager', () => {
    test('unchanged value', () => {
        expect(mergeEager('foo', 'foo', 'foo')).toBe('foo');
    });
    test('changed to the same value', () => {
        expect(mergeEager('foo', 'bar', 'bar')).toBe('bar');
    });
    test('changed to different values', () => {
        expect(mergeEager('foo', 'bar', 'baz')).toStrictEqual({
            tag: 'eager-merge-conflict',
            ancestor: 'foo',
            versionA: 'bar',
            versionB: 'baz'
        });
    });
});
