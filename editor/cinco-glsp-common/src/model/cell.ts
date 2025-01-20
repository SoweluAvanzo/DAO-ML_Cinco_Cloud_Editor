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

import { JsonAny } from '@eclipse-glsp/server';

export type Optional<T> = undefined | Deletable<T>;

export type Deletable<T> = T | Ghost<T>;

export type Ghost<T> = Readonly<{
    tag: 'ghost';
    value: T;
}>;

export function isGhost<T>(deletable: Deletable<T>): deletable is Ghost<T>;
export function isGhost<T>(optional: Optional<T>): optional is Ghost<T> {
    // eslint-disable-next-line no-null/no-null
    return typeof optional === 'object' && optional !== null && 'tag' in optional && optional.tag === 'ghost';
}

export function deletableValue<T>(deletable: Deletable<T>): T {
    return isGhost(deletable) ? deletable.value : deletable;
}

export function optionalValue<T>(optional: Optional<T>): T | undefined {
    return isGhost(optional) ? optional.value : optional;
}

export function mapDeletable<A, B>(deletable: Deletable<A>, f: (a: A) => B): Deletable<B> {
    return isGhost(deletable) ? { tag: 'ghost', value: f(deletable.value) } : f(deletable);
}

export type Cell<T> = T | Choice<T>;

export type Choice<T> = Readonly<{
    tag: 'choice';
    options: ReadonlyArray<T>;
}>;

type CellMatcher<T, R> = Readonly<{
    single: (x: T) => R;
    choice: (options: ReadonlyArray<T>) => R;
}>;

export function isChoice<T>(cell: Cell<T>): cell is Choice<T> {
    // eslint-disable-next-line no-null/no-null
    return typeof cell === 'object' && cell !== null && 'tag' in cell && cell.tag === 'choice';
}

export function matchCell<T, R>(cell: Cell<T>, { single, choice }: CellMatcher<T, R>): R {
    if (isChoice(cell)) {
        return choice(cell.options);
    } else {
        return single(cell);
    }
}

export function cellValues<T>(cell: Cell<T>): ReadonlyArray<T> {
    return matchCell(cell, {
        single: value => [value],
        choice: options => options
    });
}

export function mapCell<A, B>(cell: Cell<A>, f: (a: A) => B): Cell<B> {
    return matchCell<A, Cell<B>>(cell, {
        single: value => f(value),
        choice: options => ({ tag: 'choice', options: options.map(f) })
    });
}

export function filterOptions<T>(cell: Cell<T>, f: (x: T) => boolean): Cell<T> {
    const filtered = cellValues(cell).filter(f);
    return filtered.length === 1 ? filtered[0] : { tag: 'choice', options: filtered };
}

export function isConflictFree(value: JsonAny | undefined): boolean {
    switch (typeof value) {
        case 'function':
        case 'symbol':
            throw new TypeError(`Expected JSON data, got ${typeof value}.`);
    }

    if (isChoice<any>(value) || isGhost<any>(value)) {
        return false;
    }

    if (Array.isArray(value)) {
        return value.every(isConflictFree);
    }

    // eslint-disable-next-line no-null/no-null
    if (typeof value === 'object' && value !== null) {
        for (const key in value) {
            if (!isConflictFree(value[key])) {
                return false;
            }
        }
        return true;
    }

    // Primitive types
    return true;
}
