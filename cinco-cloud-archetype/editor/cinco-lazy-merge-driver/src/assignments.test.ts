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
import { assignmentsDifference, assignmentsIntersection, assignmentsUnion } from './assignments';
import { describe, test, expect } from '@jest/globals';

// TODO: Test lazyMergeCell

describe('assignmentsUnion', () => {
    test('disjoint assignment sets', () => {
        expect(assignmentsUnion({ a: ['foo'] }, { b: ['bar'] })).toStrictEqual({ a: ['foo'], b: ['bar'] });
    });
    test('overlapping assignment sets', () => {
        expect(assignmentsUnion({ a: ['foo'], b: ['bar'] }, { a: ['foo'], c: ['baz'] })).toStrictEqual({
            a: ['foo'],
            b: ['bar'],
            c: ['baz']
        });
    });
    test('mutated assignment sets', () => {
        expect(() => assignmentsUnion({ a: ['foo'] }, { a: ['bar'] })).toThrow(
            new Error('Assignment a has at least two different values, foo and bar.')
        );
    });
});

describe('assignmentsIntersection', () => {
    test('disjoint assignment sets', () => {
        expect(assignmentsIntersection({ a: ['foo'] }, { b: ['bar'] })).toStrictEqual({});
    });
    test('overlapping assignment sets', () => {
        expect(assignmentsIntersection({ a: ['foo'], b: ['bar'] }, { a: ['foo'], c: ['baz'] })).toStrictEqual({
            a: ['foo']
        });
    });
    test('mutated assignment sets', () => {
        expect(() => assignmentsIntersection({ a: ['foo'] }, { a: ['bar'] })).toThrow(
            new Error('Assignment a has at least two different values, foo and bar.')
        );
    });
});

describe('assignmentsDifference', () => {
    test('disjoint assignment sets', () => {
        expect(assignmentsDifference({ a: ['foo'] }, { b: ['bar'] })).toStrictEqual({ a: ['foo'] });
    });
    test('overlapping assignment sets', () => {
        expect(assignmentsDifference({ a: ['foo'], b: ['bar'] }, { a: ['foo'], c: ['baz'] })).toStrictEqual({
            b: ['bar']
        });
    });
    test('mutated assignment sets', () => {
        expect(() => assignmentsDifference({ a: ['foo'] }, { a: ['bar'] })).toThrow(
            new Error('Assignment a has at least two different values, foo and bar.')
        );
    });
});
