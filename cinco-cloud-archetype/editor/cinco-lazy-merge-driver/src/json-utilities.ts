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

export function jsonEqual(a: any, b: any): boolean {
    const jsonTypeA = jsonType(a);
    const jsonTypeB = jsonType(b);
    if (jsonTypeA !== jsonTypeB) {
        return false;
    }
    switch (jsonTypeA) {
        case 'undefined':
        case 'null':
        case 'boolean':
        case 'number':
        case 'string':
            return a === b;
        case 'array':
            return a.length === b.length && (a as any[]).every((value, index) => jsonEqual(value, b[index]));
        case 'record':
            return jsonEqual(Object.entries(a), Object.entries(b));
    }
}

function jsonType(value: any): 'undefined' | 'null' | 'boolean' | 'number' | 'string' | 'array' | 'record' {
    switch (typeof value) {
        case 'undefined':
            return 'undefined';
        case 'boolean':
            return 'boolean';
        case 'number':
            return 'number';
        case 'string':
            return 'string';
        case 'object':
            // eslint-disable-next-line no-null/no-null
            if (value === null) {
                return 'null';
            } else if (Array.isArray(value)) {
                return 'array';
            } else {
                // Actually checking if it's a record seems impossible.
                return 'record';
            }
        default:
            throw new TypeError(`${typeof value} is not a JSON value.`);
    }
}

export function mapMap<A, B>(record: Record<string, A>, f: (value: A, key: string) => B): Record<string, B> {
    return Object.fromEntries(Object.entries(record).map(([key, value]) => [key, f(value, key)]));
}

export function mapFromEntityArray(entities: ReadonlyArray<any>): Record<string, any> {
    const result: any = {};
    for (const entity of entities) {
        const { id } = entity;
        if (id === undefined) {
            throw new TypeError('Entity has no id field.');
        }
        if (id in result) {
            throw new Error(`Duplicate ID ${id}.`);
        }
        const entityCopy = { ...entity };
        delete entityCopy.id;
        result[id] = entityCopy;
    }
    return result;
}

export function entityArrayFromMap(map: Record<string, any>): ReadonlyArray<any> {
    return Object.entries(map).map(([id, entity]) => ({ id, ...entity }));
}
