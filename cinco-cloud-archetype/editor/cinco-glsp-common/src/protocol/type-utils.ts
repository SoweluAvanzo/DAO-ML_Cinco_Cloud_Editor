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

/**
 * IMPORTED FROM: @eclipse-glsp/___/protocol/src/utils/type-utils.ts
 *
 * ( Needed, because type-utils is present in two packages: 'server-node' and 'client',
 * where the both can only either be used in frontend or backend.
 * This package will be used in both, frontend and backend. )
 */

export type AnyObject = Record<PropertyKey, unknown>;
export namespace AnyObject {
    /**
     * Type guard to check wether a given object is of type {@link AnyObject}.
     * @param object The object to check.
     * @returns The given object as {@link AnyObject} or `false`.
     */
    export function is(object: unknown): object is AnyObject {
        // eslint-disable-next-line no-null/no-null
        return object !== null && typeof object === 'object';
    }
}
export function hasStringProp(object: AnyObject, propertyKey: string): boolean {
    return propertyKey in object && typeof object[propertyKey] === 'string';
}
export function hasObjectProp(object: AnyObject, propertyKey: string): boolean {
    return propertyKey in object && AnyObject.is(object[propertyKey]);
}
export function hasArrayProp(object: AnyObject, propertyKey: string): boolean {
    return propertyKey in object && Array.isArray(object[propertyKey]);
}
export function hasNumberProp(object: AnyObject, propertyKey: string): boolean {
    return propertyKey in object && typeof object[propertyKey] === 'number';
}
export function hasBooleanProp(object: AnyObject, propertyKey: string): boolean {
    return propertyKey in object && typeof object[propertyKey] === 'boolean';
}
