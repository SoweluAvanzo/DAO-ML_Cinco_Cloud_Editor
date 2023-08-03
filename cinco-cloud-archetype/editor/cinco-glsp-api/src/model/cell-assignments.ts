/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud.
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
import * as crypto from 'crypto';

export type Sortable = string | number | boolean;

export type CellAssignments<T extends Sortable> = Record<string, T[]>;

export function assignValues<T extends Sortable>(
    values: T[]
): CellAssignments<T> {
    const sortedValues = [...values];
    sortedValues.sort();
    return {[crypto.randomUUID()]: sortedValues};
}

export function assignValue<T extends Sortable>(value: T): CellAssignments<T> {
    return assignValues([value]);
}

export function cellValues<T extends Sortable>(
    cellAssignments: CellAssignments<T>
): T[] {
    const values = Object.values(cellAssignments).flat();
    values.sort();
    return removeDuplicatesFromSortedArray(values);
}

function removeDuplicatesFromSortedArray<T>(array: T[]): T[] {
    return array.filter(
        (value, index) => index === 0 || array[index - 1] !== value
    );
}
