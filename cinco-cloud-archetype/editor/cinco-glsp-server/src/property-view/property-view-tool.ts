/********************************************************************************
 * Copyright (c) 2022 Cinco Cloud and others.
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
import { GraphModelState, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import {
    Attribute,
    CustomType,
    EdgeType,
    ElementType,
    GraphType,
    LabeledModelElementReference,
    ModelElementIndex,
    NodeType,
    ObjectPointer,
    PropertyChange,
    PropertyEditOperation,
    PropertyViewAction,
    PropertyViewResponseAction,
    canAdd,
    canAssign,
    canDelete,
    getAttribute,
    getCustomType,
    getCustomTypes,
    getDefaultValue,
    getModelElementSpecifications,
    getSpecOf,
    isList
} from '@cinco-glsp/cinco-glsp-common';
import { Action, ActionHandler, Logger, MaybePromise, OperationHandler } from '@eclipse-glsp/server-node';
import { inject, injectable } from 'inversify';

/**
 * Handler for action
 */

@injectable()
export class PropertyViewHandler implements ActionHandler {
    @inject(Logger)
    protected readonly logger: Logger;
    @inject(GraphModelState)
    protected readonly modelState: GraphModelState;

    actionKinds: string[] = [PropertyViewAction.KIND];

    execute(action: PropertyViewAction, ...args: unknown[]): MaybePromise<Action[]> {
        const modelElementId: string = action.modelElementId;
        const element = this.modelState.index.findModelElement(action.modelElementId) as ModelElement;
        if (!element) {
            // element is not part of this graphmodel (maybe another)
            return [];
        }
        // build index
        const index = this.modelState.graphModel.index;
        const modelType = this.modelState.graphModel.type;
        const modelElementIndex: ModelElementIndex = {};
        for (const spec of getModelElementSpecifications()) {
            const allElementsOfType = index.getElements(spec.elementTypeId);
            modelElementIndex[spec.elementTypeId] = allElementsOfType.map((e: any) => {
                const id = e.id;
                const elementTypeId = spec.elementTypeId;
                const properties = e.args?.properties ?? '{ }';
                const name = JSON.parse(properties.toString())['name'] ?? '';
                const label = spec.label;
                return this.buildLabeledModelElementReference({ id: id, elementTypeId: elementTypeId, name: name, label: label });
            });
        }
        const customTypes = getCustomTypes();
        // build message
        const consecutiveActions: Action[] = [];
        if (element !== undefined) {
            consecutiveActions.push(
                PropertyViewResponseAction.create(
                    modelElementIndex,
                    modelType,
                    modelElementId,
                    element.propertyDefinitions,
                    customTypes,
                    element.properties
                )
            );
        }

        // send responses
        return consecutiveActions;
    }

    protected buildLabeledModelElementReference({
        id,
        elementTypeId,
        name,
        label
    }: {
        id: string;
        elementTypeId?: string;
        name?: string;
        label?: string;
    }): LabeledModelElementReference {
        return { id, elementTypeId: elementTypeId ?? '', name: name ?? '', label: label ?? '' };
    }
}

@injectable()
export class PropertyEditHandler implements OperationHandler {
    operationType = PropertyEditOperation.KIND;

    @inject(Logger)
    protected readonly logger: Logger;
    @inject(GraphModelState)
    protected readonly modelState: GraphModelState;

    execute(operation: PropertyEditOperation): void {
        const { modelElementId, pointer, name, change } = operation;
        const element = this.modelState.index.findElement(modelElementId) as any;
        if (!element) {
            // element is maybe contained inside another graphmodel
            return;
        }
        const { object, objectTypeSpecification } = this.locateObject(element, pointer);
        let attributeDefinition: Attribute | undefined;
        if (ElementType.is(objectTypeSpecification) || CustomType.is(objectTypeSpecification)) {
            attributeDefinition = getAttribute(objectTypeSpecification.elementTypeId, name);
        }
        if (!attributeDefinition) {
            throw new Error('Property can not be changed. Attribute-Definition not found!');
        } else {
            if (!this.checkConstraint(object, attributeDefinition, change)) {
                throw new Error('Property can not be changed. It violates a constraint!');
            }
        }
        if (element !== undefined) {
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
            }
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
            case 'assignValue': {
                // limitation: only let elements change, if the index are in the bounds of constraints
                let index = change.index;
                if (index === undefined) {
                    throw Error('Cannot assign value: Index undefined!');
                }
                index += 1; // translate index to list-size-position
                return canAssign(index, bounds);
            }
        }
        return false;
    }

    protected locateObject(element: any, pointer: ObjectPointer): { object: any; objectTypeSpecification: CustomType | undefined } {
        let object: any = element;
        let objectDefinition: Attribute | undefined = undefined;
        let objectTypeSpecification: CustomType | NodeType | EdgeType | GraphType | undefined = getSpecOf(element.type);
        for (const step of pointer) {
            if (step.index !== undefined) {
                if (ModelElement.is(object)) {
                    objectDefinition = object.getPropertyDefinition(step.attribute);
                    if (!objectDefinition) {
                        throw new Error('Undefined property: ' + step.attribute);
                    }
                    const indexedProperty = object.getProperty(step.attribute) ?? [];
                    let nextValue = indexedProperty[step.index];
                    if (!nextValue) {
                        nextValue = getDefaultValue(object.type, step.attribute);
                        indexedProperty[step.index] = nextValue;
                    }
                    object = nextValue;
                    objectTypeSpecification = getCustomType(objectDefinition.type);
                } else {
                    object = object[step.attribute][step.index];
                }
            } else {
                if (ModelElement.is(object)) {
                    objectDefinition = object.getPropertyDefinition(step.attribute);
                    if (!objectDefinition) {
                        throw new Error('Undefined property: ' + step.attribute);
                    }
                    let nextValue = object.getProperty(step.attribute);
                    if (!nextValue) {
                        nextValue = getDefaultValue(object.type, step.attribute);
                        object.setProperty(step.attribute, nextValue);
                    }
                    object = nextValue;
                    objectTypeSpecification = getCustomType(objectDefinition.type);
                } else {
                    object = object[step.attribute];
                }
            }
        }
        return { object, objectTypeSpecification };
    }
}
