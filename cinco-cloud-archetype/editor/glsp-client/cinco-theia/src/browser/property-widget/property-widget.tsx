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
import {
    Attribute,
    CustomType,
    Enum,
    UserDefinedType,
    canAdd,
    canAssign,
    canDelete
} from '@cinco-glsp/cinco-glsp-server/lib/src/shared/meta-specification';
import {
    ModelElementIndex,
    ObjectPointer,
    PrimitivePropertyType,
    PropertyChange,
    PropertyViewMessage,
    isListAttribute,
    isPrimitivePropertyType
} from '@cinco-glsp/cinco-glsp-server/lib/src/shared/protocol/property-model';
import { DiagramConfiguration } from '@eclipse-glsp/theia-integration';
import { CommandService } from '@theia/core';
import { Message, ReactWidget, codicon } from '@theia/core/lib/browser';
import { inject, injectable, postConstruct } from 'inversify';
import * as React from 'react';
import '../../../css/property-widget.css';
import { PropertyDataHandler } from './property-data-handler';

interface PropertyWidgetState {
    modelElementIndex: ModelElementIndex;
    modelElementId: string;
    attributeDefinitions: Attribute[];
    customTypeDefinitions: CustomType[];
    values: any;
}

@injectable()
export class CincoCloudPropertyWidget extends ReactWidget {
    static readonly ID = 'cincoCloudPropertyView';
    static readonly LABEL = 'Cinco Cloud Properties';

    override readonly id = CincoCloudPropertyWidget.ID;
    readonly label = CincoCloudPropertyWidget.LABEL;

    @inject(PropertyDataHandler) propertyDataHandler: PropertyDataHandler;
    @inject(CommandService) commandService: CommandService;
    @inject(DiagramConfiguration) diagramConfiguration: DiagramConfiguration;

    state: PropertyWidgetState = {
        modelElementIndex: {},
        modelElementId: '',
        attributeDefinitions: [],
        customTypeDefinitions: [],
        values: {}
    };

    constructor() {
        super();
        this.title.label = CincoCloudPropertyWidget.LABEL;
        this.title.caption = CincoCloudPropertyWidget.LABEL;
        this.title.iconClass = codicon('table');
        this.title.closable = true;
    }

    @postConstruct()
    registerDataSubscription(): void {
        this.propertyDataHandler.registerDataSubscription(() => {
            this.state.modelElementIndex = this.propertyDataHandler.currentModelElementIndex;
            this.state.modelElementId = this.propertyDataHandler.currentModelElementId;
            this.state.attributeDefinitions = this.propertyDataHandler.currentAttributeDefinitions;
            this.state.customTypeDefinitions = this.propertyDataHandler.currentCustomTypeDefinitions;
            this.state.values = this.propertyDataHandler.currentValues;
            this.update();
        });
        this.update();
    }

    protected getInputType(type: PrimitivePropertyType): React.HTMLInputTypeAttribute {
        switch (type) {
            case 'string':
                return 'text';
            case 'number':
                return 'number';
            case 'boolean':
                return 'checkbox';
            // TODO: Date, Color, etc.
        }
    }

    protected getValueAttribute(
        type: PrimitivePropertyType,
        value: any
    ): Partial<React.DetailedHTMLProps<React.InputHTMLAttributes<HTMLInputElement>, HTMLInputElement>> {
        switch (type) {
            case 'string':
            case 'number': {
                return { value: value as string | number };
            }
            case 'boolean': {
                return { checked: value as boolean };
            }
        }
    }

    protected locateAttributeDefinition(pointer: ObjectPointer, name: string): Attribute {
        let definitions: Attribute[] = this.state.attributeDefinitions;
        for (const step of pointer) {
            const nextAttribute = definitions.find(attribute => attribute.name === step.attribute);
            if (nextAttribute === undefined) {
                throw new Error(`Object pointer on undefined attribute '${step.attribute}'`);
            }
            const typeDefinition = this.getCustomTypeDefinition(nextAttribute.type);
            if (Enum.is(typeDefinition)) {
                throw new Error(`Object pointer '${step.attribute}' on enum`);
            } else if (UserDefinedType.is(typeDefinition)) {
                definitions = (typeDefinition as UserDefinedType).attributes;
            }
        }
        return definitions.find(definition => definition.name === name)!;
    }

    protected locateObjectValue(pointer: ObjectPointer): any {
        let objectValue: any = this.state.values; // these are the attributes of the modelElement
        for (const step of pointer) {
            let nextValue;
            if (step.index !== undefined) {
                nextValue = objectValue[step.attribute][step.index];
            } else {
                nextValue = objectValue[step.attribute];
            }
            // if steppedValue does not exists => repair with defaultValue
            if (!nextValue) {
                const attributeDefinition = this.state.attributeDefinitions.find(a => a.name === step.attribute);
                if (attributeDefinition) {
                    const defaultValue = this.buildDefaultValue(attributeDefinition);
                    nextValue = defaultValue;
                    if (step.index !== undefined) {
                        objectValue[step.attribute][step.index] = nextValue;
                    } else {
                        objectValue[step.attribute] = nextValue;
                    }
                }
            }
            objectValue = nextValue;
        }
        return objectValue;
    }

    checkEnum(type: string, value: any): boolean {
        const typeDefinitions = this.state.customTypeDefinitions.filter(t => t.elementTypeId === type);
        if (typeDefinitions.length > 0) {
            const typeDefinition = typeDefinitions[0] as any;
            if (typeDefinition['literals'] !== undefined) {
                // is enum
                const literals: string[] = typeDefinition['literals'];
                const canBeAssigned = literals.indexOf(value) >= 0;
                if (!canBeAssigned) {
                    // is not inside enums domain
                    return false;
                }
            }
        }
        return true;
    }

    protected getCustomTypeDefinition(attributeType: string): CustomType | undefined {
        const customTypeDefinitions = this.state.customTypeDefinitions.filter(type => type.elementTypeId === attributeType);
        if (customTypeDefinitions.length <= 0) {
            return undefined;
        }
        return customTypeDefinitions[0];
    }

    protected buildDefaultValue(attribute: Attribute): any {
        if (isPrimitivePropertyType(attribute.type)) {
            return attribute.defaultValue ?? '';
        } else {
            const typeDefinition = this.getCustomTypeDefinition(attribute.type);
            if (!typeDefinition) {
                // could be modelElementReference: default should be 'not set', i.e. undefined
                return undefined;
            } else if (Enum.is(typeDefinition)) {
                return typeDefinition.literals[0];
            } else if (UserDefinedType.is(typeDefinition)) {
                const defaultObject: any = {};
                for (const child of typeDefinition.attributes) {
                    const bounds = child.bounds ?? { upperBound: 1.0, lowerBound: 1.0 };
                    const isList = isListAttribute(bounds.upperBound);
                    if (isPrimitivePropertyType(child.type)) {
                        // Only do this for primitive attributes to avoid infinite recursion
                        if (isList) {
                            const defaultValues = [];
                            for (let i = 0; i++; i < bounds.lowerBound) {
                                defaultValues.push(this.buildDefaultValue(child));
                            }
                            defaultObject[child.name] = defaultValues;
                        } else {
                            if (bounds.lowerBound > 0) {
                                defaultObject[child.name] = this.buildDefaultValue(child);
                            }
                        }
                    } else {
                        defaultObject[child.name] = isList ? [] : {};
                    }
                }
                return defaultObject;
            }
        }
    }

    addPropertyValue(pointer: ObjectPointer, name: string): void {
        const attributeDefinition = this.locateAttributeDefinition(pointer, name);
        const bounds = attributeDefinition.bounds ?? { upperBound: 1.0, lowerBound: 1.0 };
        const objectValue = this.locateObjectValue(pointer);
        const defaultValue = this.buildDefaultValue(attributeDefinition);
        const isList = isListAttribute(bounds.upperBound);
        if (isList) {
            if (!objectValue[name]) {
                objectValue[name] = [];
            }
            if (!canAdd(objectValue[name].length + 1, bounds)) {
                return; // TODO: check
            }
            objectValue[name].push(defaultValue);
        } else {
            objectValue[name] = defaultValue;
        }
        this.postPropertyChange(pointer, name, { kind: 'addValue', isList, defaultValue });
        this.update();
    }

    assignPropertyValue(pointer: ObjectPointer, attributeDefinition: Attribute, index: number | undefined, value: any): void {
        const name = attributeDefinition.name;
        if (!this.checkEnum(attributeDefinition.type, value)) {
            return;
        }
        const objectValue = this.locateObjectValue(pointer);
        const type = attributeDefinition.type;
        const typeDefinitions = this.state.customTypeDefinitions.filter(t => t.elementTypeId === type);
        if (typeDefinitions.length > 0) {
            const typeDefinition = typeDefinitions[0] as any;
            if (typeDefinition['literals'] !== undefined) {
                // isEnum
                const literals: string[] = typeDefinition['literals'];
                const canBeAssigned = literals.indexOf(value) >= 0;
                if (!canBeAssigned) {
                    return;
                }
            }
        }

        if (!canAssign(index ?? 0.0, attributeDefinition.bounds ?? { lowerBound: 1.0, upperBound: 1.0 })) {
            return; // TODO: check
        }
        if (index !== undefined) {
            objectValue[name][index] = value;
        } else {
            objectValue[name] = value;
        }
        this.postPropertyChange(pointer, name, { kind: 'assignValue', index, value });
        this.update();
    }

    deletePropertyValue(pointer: ObjectPointer, attributeDefinition: Attribute, index?: number): void {
        const name = attributeDefinition.name;
        const objectValue = this.locateObjectValue(pointer);
        const bounds = attributeDefinition.bounds ?? { lowerBound: 1.0, upperBound: 1.0 };
        if (index !== undefined) {
            if (!canDelete(objectValue[name].length - 1, bounds)) {
                return; // TODO: check
            }
            objectValue[name].splice(index, 1);
        } else {
            objectValue[name] = undefined;
        }
        this.postPropertyChange(pointer, name, { kind: 'deleteValue', index });
        this.update();
    }

    protected postPropertyChange(pointer: ObjectPointer, name: string, change: PropertyChange): void {
        this.postMessage({
            kind: 'editProperty',
            modelElementId: this.state.modelElementId,
            pointer,
            name,
            change
        });
    }

    protected postMessage(message: PropertyViewMessage): void {
        window.postMessage(message);
    }

    protected override onActivateRequest(msg: Message): void {
        super.onActivateRequest(msg);
        const htmlElement = document.getElementById('property-widget');
        if (htmlElement) {
            htmlElement.focus();
        }
    }

    protected render(): React.ReactNode {
        const modelElementIndex = this.state.modelElementIndex;
        let modelElement;
        let modelElementType;
        for (const [key, modelElements] of Object.entries(modelElementIndex)) {
            for (const element of modelElements) {
                if (element.id === this.state.modelElementId) {
                    modelElementType = key;
                    modelElement = element;
                    break;
                }
            }
        }
        if (this.propertyDataHandler.currentModelElementId === undefined || modelElement === undefined) {
            return <div id='property-widget'>No model element has been selected.</div>;
        }
        const tooltip = `type: ${modelElementType}\nid: ${this.state.modelElementId}`;

        const name = modelElement.label !== '' ? modelElement.label : modelElementType;
        const info = '(' + (modelElement.label !== '' ? modelElementType + ', ' : '') + modelElement.id + ')';
        const header = (
            <div title={tooltip}>
                <b>{name}</b> <i>{info}</i> <br />
            </div>
        );
        const properties =
            this.state.attributeDefinitions.length > 0 ? (
                <table className='property-table'>
                    <tbody>
                        {this.state.attributeDefinitions.map(attributeDefinition =>
                            this.renderProperty([], attributeDefinition, this.state.values[attributeDefinition.name])
                        )}
                    </tbody>
                </table>
            ) : (
                <div>This model element has no properties.</div>
            );

        return (
            <div id='property-widget'>
                {header}
                {properties}
            </div>
        );
    }

    protected renderProperty(pointer: ObjectPointer, attributeDefinition: Attribute, value: any): JSX.Element {
        // default bounds, if no are specified
        const bounds = attributeDefinition.bounds ?? { upperBound: 1.0, lowerBound: 1.0 };
        const pathId = pointer
            .map(({ attribute, index }) => {
                if (index !== undefined) {
                    return `${attribute}-${index}`;
                } else {
                    return attribute;
                }
            })
            .join('-');
        const inputIdPrefix = `property-input-${pathId}-${attributeDefinition.name}`;
        const isList = isListAttribute(bounds.upperBound);

        // list-operation activation definitions
        const objectValue = this.locateObjectValue(pointer);
        const listLength = (objectValue[attributeDefinition.name]?.length ?? 0.0) + 1.0;
        const addCellActivated = isList && canAdd(listLength, bounds);
        const deleteCellActivated = (lengthIndex: number | undefined): boolean =>
            canDelete(lengthIndex === undefined ? 0.0 : lengthIndex - 1, bounds);

        const valueList: any = (() => {
            if (isList) {
                return value ?? [];
            } else {
                if (value !== undefined) {
                    return [value];
                } else {
                    return [attributeDefinition.defaultValue ?? ''];
                }
            }
        })();
        const firstInputId = (() => {
            if (valueList.length > 0) {
                if (isList) {
                    return `${inputIdPrefix}-0`;
                } else {
                    return inputIdPrefix;
                }
            } else {
                return undefined;
            }
        })();
        const header = (
            <th className='property-header' rowSpan={Math.max(1, valueList.length + (isList ? 1 : 0))}>
                <label htmlFor={firstInputId}>
                    {attributeDefinition.name}
                    {this.renderCardinalityIndicator(bounds.lowerBound, bounds.upperBound)}
                </label>
            </th>
        );
        const addCell = addCellActivated ? (
            <td className='button-cell'>
                <button type='button' className='action-button' onClick={() => this.addPropertyValue(pointer, attributeDefinition.name)}>
                    <span className='codicon codicon-add'></span>
                </button>
            </td>
        ) : (
            <td className='button-cell'>
                <button type='button' className='action-button' disabled>
                    <span className='codicon codicon-add'></span>
                </button>
            </td>
        );
        if (isList && valueList.length === 0) {
            return (
                <tr key={`${attributeDefinition.name}-empty`}>
                    {header}
                    {addCell}
                    <td className='empty-cell'>Empty</td>
                </tr>
            );
        } else {
            return (
                <>
                    {(valueList as any[]).map((listItemValue, index) => (
                        <tr key={`${attributeDefinition.name}-${index}`}>
                            {index === 0 && header}
                            <td className='button-cell'>
                                {!(bounds.lowerBound === 1 && bounds.upperBound === 1) && deleteCellActivated(valueList.length) && (
                                    <button
                                        type='button'
                                        className='action-button'
                                        onClick={() => this.deletePropertyValue(pointer, attributeDefinition, isList ? index : undefined)}
                                    >
                                        <span className='codicon codicon-trash'></span>
                                    </button>
                                )}
                                {!(bounds.lowerBound === 1 && bounds.upperBound === 1) && !deleteCellActivated(valueList.length) && (
                                    <button type='button' className='action-button' disabled>
                                        <span className='codicon codicon-trash'></span>
                                    </button>
                                )}
                            </td>
                            <td>{this.renderPropertyValue(pointer, attributeDefinition, listItemValue, index, inputIdPrefix)}</td>
                        </tr>
                    ))}
                    {isList && (
                        <tr key={`${attributeDefinition.name}-add`}>
                            {addCell}
                            <td></td>
                        </tr>
                    )}
                </>
            );
        }
    }

    protected renderCardinalityIndicator(lowerBound: number, upperBound?: number): JSX.Element | undefined {
        return (
            <sup>
                {(() => {
                    if (lowerBound === 1 && upperBound === 1) {
                        // Create an empty <sup> for consistent spacing
                        return '';
                    }
                    if (lowerBound === 0 && upperBound === 1) {
                        return '?';
                    }
                    if (lowerBound === 0 && (upperBound === undefined || upperBound < 0)) {
                        // Render * with <sup> for consistent spacing
                        return '＊';
                    }
                    if (lowerBound === 1 && (upperBound === undefined || upperBound < 0)) {
                        return '+';
                    }
                    return `[${lowerBound},${upperBound === undefined || upperBound < lowerBound ? '＊' : upperBound}]`;
                })()}
            </sup>
        );
    }

    protected renderPropertyValue(
        pointer: ObjectPointer,
        attributeDefinition: Attribute,
        value: any,
        index: number,
        inputIdPrefix: string
    ): JSX.Element {
        const bounds = attributeDefinition.bounds ?? { upperBound: 1.0, lowerBound: 1.0 };
        const isList = isListAttribute(bounds.upperBound);
        const inputId = isList ? `${inputIdPrefix}-${index}` : inputIdPrefix;
        switch (attributeDefinition.type) {
            case 'string':
            case 'number':
            case 'boolean': {
                const primitiveType: PrimitivePropertyType = attributeDefinition.type;
                const inputType = this.getInputType(attributeDefinition.type);
                return (
                    <input
                        type={inputType}
                        {...this.getValueAttribute(attributeDefinition.type, value)}
                        onChange={event => {
                            const newValue = (() => {
                                switch (primitiveType) {
                                    case 'string':
                                    case 'number': {
                                        return event.currentTarget.value;
                                    }
                                    case 'boolean': {
                                        return event.currentTarget.checked;
                                    }
                                }
                            })();
                            this.assignPropertyValue(pointer, attributeDefinition, isList ? index : undefined, newValue);
                        }}
                        id={inputId}
                        className={`property-input property-input-${inputType}`}
                    />
                );
            }
            default: {
                const typeDefinition = this.getCustomTypeDefinition(attributeDefinition.type);
                if (Enum.is(typeDefinition)) {
                    return (
                        <select
                            value={value}
                            onChange={event =>
                                this.assignPropertyValue(
                                    pointer,
                                    attributeDefinition,
                                    isList ? index : undefined,
                                    event.currentTarget.value
                                )
                            }
                            id={inputId}
                        >
                            <option value=''></option>
                            {typeDefinition.literals.map(option => (
                                <option value={option} key={option}>
                                    {option}
                                </option>
                            ))}
                        </select>
                    );
                } else if (UserDefinedType.is(typeDefinition)) {
                    return (
                        <table className='attribute-table'>
                            <tbody>
                                {typeDefinition.attributes.map(childDefinition =>
                                    this.renderProperty(
                                        pointer.concat({ attribute: attributeDefinition.name, index: isList ? index : undefined }),
                                        childDefinition,
                                        value[childDefinition.name]
                                    )
                                )}
                            </tbody>
                        </table>
                    );
                } else {
                    return (
                        <select
                            value={value}
                            onChange={event =>
                                this.assignPropertyValue(
                                    pointer,
                                    attributeDefinition,
                                    isList ? index : undefined,
                                    event.currentTarget.value
                                )
                            }
                            id={inputId}
                        >
                            <option value=''></option>
                            {this.state.modelElementIndex[attributeDefinition.type].map(({ id, name, label }) => (
                                // ModelElementReference identifier specified in the following fallback order:
                                // name => label => attributeDefinitionType
                                <option value={id} key={id}>
                                    {name !== '' ? name : label !== '' ? label : attributeDefinition.type} (
                                    {name !== '' ? label + ', ' : ''}
                                    {id})
                                </option>
                            ))}
                        </select>
                    );
                }
            }
        }
    }
}
