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

import { Annotation, Attribute, Enum, UserDefinedType, getAttribute, getCustomType, getNonAbstractTypeOptions } from '../meta-specification';

export interface ModelElementIndex {
    [type: string]: LabeledModelElementReference[];
}

export interface LabeledModelElementReference {
    id: string;
    elementTypeId: string;
    name: string;
    label: string;
}

export type PrimitivePropertyType = 'string' | 'number' | 'boolean' | 'date' | 'Date' | 'color' | 'Color' | 'file' | 'File';

export function isPrimitivePropertyType(type: PropertyType): type is PrimitivePropertyType {
    switch (type) {
        case 'string':
        case 'number':
        case 'boolean':
        case 'date':
        case 'Date':
        case 'file':
        case 'File':
        case 'color':
        case 'Color': {
            return true;
        }
        default: {
            return false;
        }
    }
}

export type PropertyType = PrimitivePropertyType | string;

export function isMultiline(type: string, annotations: Annotation[] = []): boolean {
    return type === 'string' && annotations.filter(a => a.name === 'multiline').length > 0;
}

export function isDate(type: string, annotations: Annotation[] = []): boolean {
    return isType('date', type, annotations);
}

export function isFile(type: string, annotations: Annotation[] = []): boolean {
    return isType('file', type, annotations);
}

export function isColor(type: string, annotations: Annotation[] = []): boolean {
    return isType('color', type, annotations);
}

function isType(target: string, type: string, annotations: Annotation[] = []): boolean {
    return target === type.toLowerCase() || annotations.filter(a => a.name === target).length > 0;
}

export function getColorStyle(annotations: Annotation[]): string | undefined {
    const colorAnnotations = (annotations ?? []).filter(a => a.name === 'color');
    if (colorAnnotations.length > 0) {
        const colorAnnotationValues = colorAnnotations.map(a => a.values).flat();
        const colorAnnotationValue = colorAnnotationValues.at(colorAnnotationValues.length - 1);
        return colorAnnotationValue;
    }
    return undefined;
}

export function parseColorValueToHex(value: string, annotations: Annotation[] = []): string {
    const colorStyle = getColorStyle(annotations);
    if (value.startsWith('#') && colorStyle !== 'hex') {
        // if for some reason, wrong style is persistet, correct it
        value = parseColorValueFromHex(value, annotations);
    }
    if (colorStyle) {
        // parse type
        switch (colorStyle) {
            case 'hex':
                return value;
            case 'rgb': {
                return rgbaToHex(value);
            }
            case 'rgba': {
                return rgbaToHex(value, false); // TODO: alpha is currently not supported by the HTML-input-element of type color
            }
        }
    }
    return value;
}

export function parseColorValueFromHex(value: string, annotations: Annotation[] = []): string {
    const colorStyle = getColorStyle(annotations);
    if (colorStyle) {
        // parse type
        switch (colorStyle) {
            case 'hex':
                return value;
            case 'rgb': {
                return hexToRGBA(value);
            }
            case 'rgba': {
                return hexToRGBA(value);
            }
        }
    }
    return value;
}

function hexToRGBA(hex: string): string {
    hex = hex.startsWith('#') ? hex.slice(1) : hex;
    if (hex.length === 3) {
        hex = Array.from(hex).reduce((str, x) => str + x + x, '');
    }
    const values = hex
        .split(/([a-zA-Z0-9]{2,2})/)
        .filter(Boolean)
        .map(x => parseInt(x, 16));
    return `${values.join(', ')}`;
}

function rgbaToHex(rgba: string, forceRemoveAlpha = false): string {
    return (
        '#' +
        rgba
            .replace(/^rgba?\(|\s+|\)$/g, '') // removes rgba / rgb string values
            .split(',') // splits them at ","
            .filter((_, index) => !forceRemoveAlpha || index !== 3)
            .map(string => parseFloat(string)) // Converts them to numbers
            .map((number, index) => (index === 3 ? Math.round(number * 255) : number)) // Converts alpha to 255 number
            .map(number => number.toString(16)) // Converts numbers to hex
            .map(string => (string.length === 1 ? '0' + string : string)) // Adds 0 when length of one number is 1
            .join('')
    );
}

export function getDefaultValue(elementTypeId: string, attributeName: string, annotations: Annotation[] = []): any {
    const definition = getAttribute(elementTypeId, attributeName);
    if (definition === undefined) {
        throw new Error(`Cannot get definition for attribute ${attributeName} of ${elementTypeId}.`);
    }
    return definition.defaultValue ?? getFallbackDefaultValue(definition.type, annotations);
}

export function getFallbackDefaultValue(type: string, annotations: Annotation[] = []): any {
    return getFallbackDefaultValueRecursive(type, [], annotations);
}

export function cleanDate(dateString?: string): string | undefined {
    if (dateString && dateString.indexOf('/') >= 0) {
        const defaultDayComponents = dateString.split('/').map(entry => (entry.length === 1 ? '0' + entry : entry));
        return defaultDayComponents.reverse().join('-');
    }
    return dateString;
}

function getFallbackDefaultValueRecursive(type: string, ancestorTypes: string[], annotations: Annotation[] = []): any {
    switch (type) {
        case 'string':
            if (isColor(type, annotations)) {
                return getFallbackDefaultValue('color', annotations);
            } else if (isFile(type, annotations)) {
                return getFallbackDefaultValue('file', annotations);
            } else if (isDate(type, annotations)) {
                return getFallbackDefaultValue('date', annotations);
            } else {
                return '';
            }
        case 'number':
            return 0;
        case 'boolean': {
            return false;
        }
        case 'date':
        case 'Date': {
            const currentDate = new Date();
            const defaultDateString = currentDate.toLocaleDateString('en-GB');
            return cleanDate(defaultDateString);
        }
        case 'color':
        case 'Color':
            switch (getColorStyle(annotations)) {
                case 'hex':
                    return '#FFFFFF';
                case 'rgb':
                    return '255, 255, 255';
                case 'rgba':
                    return '255, 255, 255, 1';
                default:
                    return '#FFFFFF';
            }
        case 'file':
        case 'File': {
            return '';
        }
        default: {
            const specifiedTypeDefinition = getCustomType(type);
            if (!specifiedTypeDefinition) {
                // could be modelElementReference: default should be 'not set', i.e. undefined
                return undefined;
            } else if (Enum.is(specifiedTypeDefinition)) {
                return specifiedTypeDefinition.literals[0];
            } else if (UserDefinedType.is(specifiedTypeDefinition)) {
                // Check potential polymorphism
                const typeOptions = getNonAbstractTypeOptions(specifiedTypeDefinition)
                if (typeOptions.length === 0) {
                    return {}; // No instantiable type (e.g. abstract type without subtypes)
                }
                const instantiableTypeDefinition: UserDefinedType = typeOptions[0] as UserDefinedType;

                const newAncestorTypes = ancestorTypes.concat([instantiableTypeDefinition.elementTypeId]);
                const defaultObject: any = { _type: instantiableTypeDefinition.elementTypeId, _value: {} };
                for (const child of instantiableTypeDefinition.attributes) {
                    if (child.defaultValue !== undefined) {
                        defaultObject._value[child.name] = child.defaultValue;
                        continue;
                    }

                    const bounds = child.bounds ?? { upperBound: 1.0, lowerBound: 1.0 };
                    const childIsList = isListAttribute(bounds.upperBound);

                    // Infinite recursion protection
                    if (bounds.lowerBound > 0 && newAncestorTypes.includes(child.type)) {
                        // Recursive user-defined type with non-zero lower
                        // bound, cannot build default value.
                        defaultObject._value[child.name] = childIsList ? [] : undefined;

                        continue;
                    }

                    if (childIsList) {
                        const defaultValues = [];
                        for (let i = 0; i++; i < bounds.lowerBound) {
                            defaultValues.push(getFallbackDefaultValue(child.type, annotations));
                        }
                        defaultObject._value[child.name] = defaultValues;
                    } else {
                        if (bounds.lowerBound > 0) {
                            defaultObject._value[child.name] = getFallbackDefaultValue(child.type, annotations);
                        }
                    }
                }
                return defaultObject;
            }
        }
    }
}

export function isListAttribute(upperBound: number): boolean {
    return upperBound === -1 || upperBound > 1;
}

export function isList(attribute: Attribute): boolean {
    return isListAttribute(attribute.bounds?.upperBound ?? 1.0);
}

export function findAttribute(attributes: Attribute[], name: string): Attribute {
    const matchingAttributes = attributes.filter(attribute => attribute.name === name);

    if (matchingAttributes.length !== 1) {
        throw new Error(`Found ${matchingAttributes.length} attributes matching the name ${name}, expected 1.`);
    }

    return matchingAttributes[0];
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

export type PropertyChange = AddValue | AssignValue | DeleteValue | ChangeType;

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

export interface ChangeType {
    kind: 'changeType';
    index?: number;
    newType: string;
    newValue: any;
}
