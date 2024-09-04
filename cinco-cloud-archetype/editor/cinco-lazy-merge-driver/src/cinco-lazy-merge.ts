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
import { Assignments, Sortable } from '@cinco-glsp/cinco-glsp-api';
import { argv } from 'process';

if (require.main === module) {
    console.log('ancestor', argv[2]);
    console.log('version a', argv[3]);
    console.log('version b', argv[4]);
}

export function mergeAssignments<T extends Sortable>(
    ancestor: Assignments<T>,
    versionA: Assignments<T>,
    versionB: Assignments<T>
): Assignments<T> {
    return assignmentsUnion(
        // Ancestor assignments not killed
        assignmentsIntersection(ancestor, versionA, versionB),
        // Newly brought in assignments
        assignmentsDifference(assignmentsUnion(versionA, versionB), ancestor)
    );
}

export function assignmentsUnion<T extends Sortable>(...assignmentsList: Assignments<T>[]): Assignments<T> {
    const union: Assignments<T> = {};
    for (const assignments of assignmentsList) {
        for (const [key, value] of Object.entries(assignments)) {
            if (!(key in union)) {
                union[key] = value;
            } else {
                assertEqualAssignmentValues(key, union[key], value);
            }
        }
    }
    return union;
}

export function assignmentsIntersection<T extends Sortable>(
    firstAssignments: Assignments<T>,
    ...remainingAssignmentsList: Assignments<T>[]
): Assignments<T> {
    const intersection: Assignments<T> = { ...firstAssignments };
    for (const assignments of remainingAssignmentsList) {
        const assignemtsKeys = Object.keys(assignments);
        for (const [key, value] of Object.entries(intersection)) {
            if (!assignemtsKeys.includes(key)) {
                delete intersection[key];
            } else {
                assertEqualAssignmentValues(key, value, assignments[key]);
            }
        }
    }
    return intersection;
}

export function assignmentsDifference<T extends Sortable>(a: Assignments<T>, b: Assignments<T>): Assignments<T> {
    const difference = { ...a };
    for (const [key, value] of Object.entries(b)) {
        if (key in difference) {
            assertEqualAssignmentValues(key, difference[key], value);
            delete difference[key];
        }
    }
    return difference;
}

/**
 * The values of assignments must not change over time. New values require new assignments.
 */
function assertEqualAssignmentValues(key: string, valueA: any, valueB: any): void {
    if (!jsonEqual(valueA, valueB)) {
        throw new Error(`Assignment ${key} has at least two different values, ${valueA} and ${valueB}.`);
    }
}

export function jsonEqual(a: any, b: any): boolean {
    const jsonTypeA = jsonType(a);
    const jsonTypeB = jsonType(b);
    if (jsonTypeA !== jsonTypeB) {
        throw new TypeError(`Equality between ${jsonTypeA} and ${jsonTypeB} is undefined.`);
    }
    switch (jsonTypeA) {
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

function jsonType(value: any): 'null' | 'boolean' | 'number' | 'string' | 'array' | 'record' {
    switch (typeof value) {
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
