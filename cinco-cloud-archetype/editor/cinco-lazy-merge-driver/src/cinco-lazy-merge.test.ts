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
import { equal } from './cinco-lazy-merge';
import { describe, test, expect } from '@jest/globals';

describe('jsonValuesEqual', () => {
    test('different types', () => {
        expect(() => equal(2, 'two')).toThrow(new TypeError('Equality between number and string is undefined.'));
    });
    test('undefined', () => {
        // eslint-disable-next-line no-null/no-null
        expect(equal(undefined, undefined)).toBe(true);
    });
    test('null', () => {
        // eslint-disable-next-line no-null/no-null
        expect(equal(null, null)).toBe(true);
    });
    test('same booleans', () => {
        expect(equal(true, true)).toBe(true);
    });
    test('different booleans', () => {
        expect(equal(false, true)).toBe(false);
    });
    test('same integers', () => {
        expect(equal(4, 4)).toBe(true);
    });
    test('different integers', () => {
        expect(equal(2, 4)).toBe(false);
    });
    test('same floats', () => {
        expect(equal(0.1, 0.1)).toBe(true);
    });
    test('different floats', () => {
        expect(equal(0.1, 0.2)).toBe(false);
    });
    test('same bigints', () => {
        expect(equal(BigInt('9007199254740992'), BigInt('9007199254740992'))).toBe(true);
    });
    test('different bigints', () => {
        expect(equal(BigInt('9007199254740992'), BigInt('9007199254740993'))).toBe(false);
    });
    test('same strings', () => {
        expect(equal('foo', 'foo')).toBe(true);
    });
    test('different strings', () => {
        expect(equal('foo', 'bar')).toBe(false);
    });
    test('same symbols', () => {
        const symbol = Symbol();
        expect(equal(symbol, symbol)).toBe(true);
    });
    test('different symbols', () => {
        expect(equal(Symbol(), Symbol())).toBe(false);
    });
});
