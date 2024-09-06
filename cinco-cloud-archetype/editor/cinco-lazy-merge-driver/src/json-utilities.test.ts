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
import { describe, test, expect } from '@jest/globals';

describe('jsonValuesEqual', () => {
    test('different types', () => {
        expect(() => jsonEqual(2, 'two')).toThrow(new TypeError('Equality between number and string is undefined.'));
    });
    test('different types but equal typeof', () => {
        // eslint-disable-next-line no-null/no-null
        expect(() => jsonEqual(null, [])).toThrow(new TypeError('Equality between null and array is undefined.'));
    });
    test('null', () => {
        // eslint-disable-next-line no-null/no-null
        expect(jsonEqual(null, null)).toBe(true);
    });
    test('same booleans', () => {
        expect(jsonEqual(true, true)).toBe(true);
    });
    test('different booleans', () => {
        expect(jsonEqual(false, true)).toBe(false);
    });
    test('same integers', () => {
        expect(jsonEqual(4, 4)).toBe(true);
    });
    test('different integers', () => {
        expect(jsonEqual(2, 4)).toBe(false);
    });
    test('same floats', () => {
        expect(jsonEqual(0.1, 0.1)).toBe(true);
    });
    test('different floats', () => {
        expect(jsonEqual(0.1, 0.2)).toBe(false);
    });
    test('same strings', () => {
        expect(jsonEqual('foo', 'foo')).toBe(true);
    });
    test('different strings', () => {
        expect(jsonEqual('foo', 'bar')).toBe(false);
    });
    test('cannot compare functions', () => {
        expect(() =>
            jsonEqual(
                () => {},
                () => {}
            )
        ).toThrow('function is not a JSON value.');
    });
});

describe('mapRecord', () => {
    test('map empty record', () => {
        expect(mapMap({}, () => {})).toStrictEqual({});
    });
    test('map several values', () => {
        expect(mapMap({ a: 1, b: 2, c: 3 }, n => n + 2)).toStrictEqual({ a: 3, b: 4, c: 5 });
    });
});

describe('recordFromEntityArray', () => {
    test('empty entity list', () => {
        expect(mapFromEntityArray([])).toStrictEqual({});
    });
    test('several entities', () => {
        expect(
            mapFromEntityArray([
                { id: 'x', a: 1 },
                { id: 'y', a: 2 }
            ])
        ).toStrictEqual({ x: { id: 'x', a: 1 }, y: { id: 'y', a: 2 } });
    });
    test('missing id field', () => {
        expect(() => mapFromEntityArray([{ foo: 'bar' }])).toThrow(new TypeError('Entity has no id field.'));
    });
    test('duplicate ids', () => {
        expect(() => mapFromEntityArray([{ id: 'x' }, { id: 'x' }])).toThrow('Duplicate ID x.');
    });
});
