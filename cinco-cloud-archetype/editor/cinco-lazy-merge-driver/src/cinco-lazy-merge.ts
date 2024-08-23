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

console.log('ancestor', argv[2]);
console.log('version a', argv[3]);
console.log('version b', argv[4]);

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

function assignmentsUnion<T extends Sortable>(...assignmentsList: Assignments<T>[]): Assignments<T> {
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

function assignmentsIntersection<T extends Sortable>(
    firstAssignments: Assignments<T>,
    ...remainingAssignmentsList: Assignments<T>[]
): Assignments<T> {
    const intersection: Assignments<T> = { ...firstAssignments };
    for (const assignments of remainingAssignmentsList) {
        const assignemtsKeys = Object.keys(assignments);
        for (const [key, value] of Object.entries(intersection)) {
            if (!(key in assignemtsKeys)) {
                delete intersection[key];
            } else {
                assertEqualAssignmentValues(key, value, assignments[key]);
            }
        }
    }
    return intersection;
}

function assignmentsDifference<T extends Sortable>(a: Assignments<T>, b: Assignments<T>): Assignments<T> {
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
    if (!equal(valueA, valueB)) {
        throw new Error(`Assignment ${key} has at least two different values, ${valueA} and ${valueB}.`);
    }
}

export function equal(a: any, b: any): boolean {
    if (typeof a !== typeof b) {
        throw new TypeError(`Equality between ${typeof a} and ${typeof b} is undefined.`);
    }
    switch (typeof a) {
        case 'undefined':
        case 'boolean':
        case 'number':
        case 'bigint':
        case 'string':
        case 'symbol':
            return a === b;
        case 'object':
            // eslint-disable-next-line no-null/no-null
            if (a === null && b === null) {
                return true;
            } else if (Array.isArray(a) && Array.isArray(b)) {
                return a.length === b.length && a.every((value, index) => equal(value, b[index]));
            } else {
                return equal(Object.entries(a), Object.entries(b));
            }
        case 'function':
            throw new TypeError('Function equality is undefined.');
    }
}
