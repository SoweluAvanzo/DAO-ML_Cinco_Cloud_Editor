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
import { HookManager, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import {
    Attribute,
    ObjectPointer,
    PropertyChange,
    PropertyEditOperation,
    canAdd,
    canAssign,
    canDelete,
    findAttribute,
    getUserDefinedType,
    isList,
    AttributeChangeArgument,
    HookType
} from '@cinco-glsp/cinco-glsp-common';
import { injectable } from 'inversify';
import { CincoJsonOperationHandler } from './cinco-json-operation-handler';

@injectable()
export class PropertyEditHandler extends CincoJsonOperationHandler {
    operationType = PropertyEditOperation.KIND;

    override executeOperation(operation: PropertyEditOperation): void {
        const { modelElementId, pointer, name, change } = operation;
        const element = this.modelState.index.findElement(modelElementId) as any;
        if (!element) {
            // element is maybe contained inside another graphmodel
            return;
        }
        const { attributes, object } = this.locateObject(element, pointer);
        const attributeDefinition: Attribute = findAttribute(attributes, name);

        // CAN
        const inConstraint = this.checkConstraint(object, attributeDefinition, change);
        const parameters: AttributeChangeArgument = {
            kind: 'AttributeChange',
            modelElementId: modelElementId,
            oldValue: element.getProperty(name),
            operation: operation
        };
        const canSetValue = (): boolean => HookManager.executeHook(parameters, HookType.CAN_ATTRIBUTE_CHANGE, this.getBundle());
        if (inConstraint && canSetValue() && element !== undefined) {
            // PRE
            HookManager.executeHook(parameters, HookType.PRE_ATTRIBUTE_CHANGE, this.getBundle());

            // Change
            switch (change.kind) {
                case 'addValue': {
                    const { defaultValue } = change;
                    if (change.isList) {
                        if (object instanceof ModelElement) {
                            const list = object.getProperty(name) ?? [];
                            list.push(defaultValue);
                            object.setProperty(name, list);
                        } else {
                            const list = object[name] ?? [];
                            list.push(defaultValue);
                            object[name] = list;
                        }
                    } else {
                        if (object instanceof ModelElement) {
                            object.setProperty(name, defaultValue);
                        } else {
                            object[name].push(defaultValue);
                        }
                    }
                    break;
                }
                case 'assignValue': {
                    const { index, value } = change;
                    if (index !== undefined) {
                        if (object instanceof ModelElement) {
                            const list = object.getProperty(name);
                            list[index] = value;
                            object.setProperty(name, list);
                        } else {
                            object[name][index] = value;
                        }
                    } else {
                        if (object instanceof ModelElement) {
                            object.setProperty(name, value);
                        } else {
                            object[name] = value;
                        }
                    }
                    break;
                }
                case 'deleteValue': {
                    const { index } = change;
                    if (index !== undefined) {
                        if (object instanceof ModelElement) {
                            let list = object.getProperty(name) as any[];
                            // list.splice(index = 1, 1): on an array with ['1', '2'], it returns ['2'], which is weird
                            const listA = list.slice(0, index);
                            const listB = list.slice(index + 1);
                            list = listA.concat(listB);
                            object.setProperty(name, list);
                        } else {
                            object[name].splice(index, 1);
                        }
                    } else {
                        if (object instanceof ModelElement) {
                            object.setProperty(name, undefined);
                        } else {
                            delete object[name];
                        }
                    }
                    break;
                }
                case 'changeType': {
                    const { index, newValue } = change;
                    if (index !== undefined) {
                        object[name][index] = newValue;
                    } else {
                        object[name] = newValue;
                    }
                    break;
                }
            }

            // POST
            HookManager.executeHook(parameters, HookType.POST_ATTRIBUTE_CHANGE, this.getBundle());
        }
    }

    /**
     * @returns boolean value. true, iff the next change is still in the constraints range.
     */
    checkConstraint(hostObject: any, attributeDefinition: Attribute, change: PropertyChange): boolean {
        const propertyName = attributeDefinition.name;
        const bounds = attributeDefinition.bounds ?? { lowerBound: 1.0, upperBound: 1.0 };
        if (!isList(attributeDefinition)) {
            return true;
        }

        let list: any[];
        if (hostObject instanceof ModelElement) {
            list = hostObject.getProperty(propertyName) ?? [];
        } else {
            list = hostObject[propertyName] ?? [];
        }
        const listLength = list.length;
        switch (change.kind) {
            case 'addValue': {
                return canAdd(listLength + 1, bounds);
            }
            case 'deleteValue': {
                return canDelete(listLength - 1, bounds);
            }
            case 'assignValue':
            case 'changeType': {
                // limitation: only let elements change, if the index are in the bounds of constraints
                let index = change.index;
                if (index === undefined) {
                    throw Error('Cannot assign value: Index undefined!');
                }
                index += 1; // translate index to list-size-position
                return canAssign(index, bounds);
            }
        }
    }

    protected locateObject(element: ModelElement, pointer: ObjectPointer): { attributes: Attribute[]; object: any } {
        let attributes: Attribute[] = element.propertyDefinitions;
        let object: Record<string, any> = element.properties;

        for (const segment of pointer) {
            object = segment.index !== undefined ? object[segment.attribute][segment.index] : object[segment.attribute];

            // subType of specified type might have been instantiated instead of specified type
            const type = object._type ?? findAttribute(attributes, segment.attribute).type;
            const userDefinedType = getUserDefinedType(type);

            if (userDefinedType === undefined) {
                throw new Error(`Cannot find user-defined type ${type}`);
            }

            attributes = userDefinedType.attributes;
        }

        return { attributes, object };
    }
}
