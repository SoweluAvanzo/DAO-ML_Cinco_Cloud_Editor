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

export type Sortable = string | number | boolean;

export type Optional<T> = undefined | T | Ghost<T>;

export type Ghost<T> = Readonly<{
    tag: 'ghost';
    value: T;
}>;

export function isGhost<T>(optional: Optional<T>): optional is Ghost<T> {
    // eslint-disable-next-line no-null/no-null
    return typeof optional === 'object' && optional !== null && 'tag' in optional && optional.tag === 'ghost';
}

export function optionalValue<T>(optional: Optional<T>): T | undefined {
    return isGhost(optional) ? optional.value : optional;
}

export type Cell<T extends Sortable> = T | Choice<T>;

export type Choice<T extends Sortable> = Readonly<{
    tag: 'choice';
    options: ReadonlyArray<T>;
}>;

type CellMatcher<T extends Sortable, R> = Readonly<{
    single: (x: T) => R;
    choice: (options: ReadonlyArray<T>) => R;
}>;

export function isChoice<T extends Sortable>(cell: Cell<T>): cell is Choice<T> {
    return typeof cell === 'object' && cell.tag === 'choice';
}

export function matchCell<T extends Sortable, R>(cell: Cell<T>, { single, choice }: CellMatcher<T, R>): R {
    if (isChoice(cell)) {
        return choice(cell.options);
    } else {
        return single(cell);
    }
}

export function cellValues<T extends Sortable>(cell: Cell<T>): ReadonlyArray<T> {
    return matchCell(cell, {
        single: value => [value],
        choice: options => options
    });
}
