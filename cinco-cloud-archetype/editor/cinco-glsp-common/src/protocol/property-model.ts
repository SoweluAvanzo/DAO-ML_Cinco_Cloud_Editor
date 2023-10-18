/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud and others.
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

import { Attribute, Enum, UserDefinedType, getAttribute, getCustomType } from '../meta-specification';

export interface ModelElementIndex {
    [type: string]: LabeledModelElementReference[];
}

export interface LabeledModelElementReference {
    id: string;
    elementTypeId: string;
    name: string;
    label: string;
}

export type PrimitivePropertyType = 'string' | 'number' | 'boolean';

export function isPrimitivePropertyType(type: PropertyType): type is PrimitivePropertyType {
    switch (type) {
        case 'string':
        case 'number':
        case 'boolean': {
            return true;
        }
        default: {
            return false;
        }
    }
}

export type PropertyType = PrimitivePropertyType | string;

export function getDefaultValue(elementTypeId: string, attributeName: string): any {
    const definition = getAttribute(elementTypeId, attributeName);
    return !definition ? undefined : definition.defaultValue ?? getFallbackDefaultValue(definition.type);
}

export function getFallbackDefaultValue(type: string): any {
    switch (type) {
        case 'string':
            return '';
        case 'number':
            return 0;
        case 'boolean': {
            return false;
        }
        default: {
            const typeDefinition = getCustomType(type);
            if (Enum.is(typeDefinition)) {
                return typeDefinition.literals[0];
            } else if (UserDefinedType.is(typeDefinition)) {
                return {};
            } else {
                return {};
            }
        }
    }
}

export function isListAttribute(upperBound?: number): boolean {
    return (upperBound === undefined || upperBound < 0 ? Infinity : upperBound) > 1;
}

export function isList(attribute: Attribute): boolean {
    const bounds = attribute.bounds ?? { lowerBound: 1.0, upperBound: 1.0 };
    return isListAttribute(bounds.upperBound);
}

export type PropertyViewMessage = EditProperty;

export interface EditProperty {
    kind: 'editProperty';
    modelElementId: string;
    pointer: ObjectPointer;
    name: string;
    change: PropertyChange;
}

export type ObjectPointer = { attribute: string; index?: number }[];

export type PropertyChange = AddValue | AssignValue | DeleteValue;

export interface AddValue {
    kind: 'addValue';
    isList: boolean;
    defaultValue: any;
}

export interface AssignValue {
    kind: 'assignValue';
    value: any;
    index?: number;
}

export interface DeleteValue {
    kind: 'deleteValue';
    index?: number;
}
