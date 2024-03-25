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
import '../../../css/property-widget.css';

import {
    Annotation,
    Attribute,
    canAdd,
    canAssign,
    canDelete,
    Enum,
    getCustomType,
    getCustomTypes,
    UserDefinedType
} from '@cinco-glsp/cinco-glsp-common/lib/meta-specification';
import {
    getFallbackDefaultValue,
    isListAttribute,
    LabeledModelElementReference,
    ModelElementIndex,
    ObjectPointer,
    PrimitivePropertyType,
    PropertyChange,
    PropertyViewMessage
} from '@cinco-glsp/cinco-glsp-common/lib/protocol/property-model';
import { DiagramConfiguration } from '@eclipse-glsp/theia-integration';
import { CommandService } from '@theia/core';
import { codicon, Message, ReactWidget, Widget } from '@theia/core/lib/browser';
import { inject, injectable, postConstruct } from 'inversify';
import * as React from 'react';
import { FileDialogService } from '@theia/filesystem/lib/browser';
import { PropertyDataHandler } from './property-data-handler';

interface PropertyWidgetState {
    modelElementIndex: ModelElementIndex;
    modelElementId: string;
    attributeDefinitions: Attribute[];
    values: Record<string, any>;
}

const FILE_PICKER_DIRECTORY_ALLOWED_KEYWORD = '%directory';

@injectable()
export class CincoCloudPropertyWidget extends ReactWidget {
    @inject(PropertyDataHandler) propertyDataHandler: PropertyDataHandler;
    @inject(CommandService) commandService: CommandService;
    @inject(DiagramConfiguration) diagramConfiguration: DiagramConfiguration;
    @inject(FileDialogService) fileDialogService: FileDialogService;

    static readonly ID = 'cincoCloudPropertyView';
    static readonly LABEL = 'Cinco Cloud Properties';

    override readonly id = CincoCloudPropertyWidget.ID;
    readonly label = CincoCloudPropertyWidget.LABEL;

    state: PropertyWidgetState = {
        modelElementIndex: {},
        modelElementId: '',
        attributeDefinitions: [],
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
            this.state.values = this.propertyDataHandler.currentValues;
            this.update();
            this.node.scrollTo(0, 0);
        });
        this.update();
    }

    protected override onActivateRequest(msg: Message): void {
        super.onActivateRequest(msg);
        const htmlElement = document.getElementById('property-widget');
        if (htmlElement) {
            htmlElement.focus();
        }
    }

    protected render(): React.JSX.Element {
        return (
            <CincoPropertiesView
                parent={this}
                parentState={() => this.getParentState()}
                commandService={this.commandService}
                propertyDataHandler={this.propertyDataHandler}
                diagramConfiguration={this.diagramConfiguration}
            ></CincoPropertiesView>
        );
    }

    getParentState(): PropertyWidgetState {
        return this.state;
    }
}

export class CincoPropertiesView extends React.Component<
    {
        parent: CincoCloudPropertyWidget;
        parentState: () => PropertyWidgetState;
        commandService: CommandService;
        propertyDataHandler: PropertyDataHandler;
        diagramConfiguration: DiagramConfiguration;
    },
    PropertyWidgetState
> {
    constructor(props: {
        parent: CincoCloudPropertyWidget;
        parentState: () => PropertyWidgetState;
        commandService: CommandService | Readonly<CommandService>;
        propertyDataHandler: PropertyDataHandler;
        diagramConfiguration: DiagramConfiguration;
        }) {
        super(props);
        this.state = this.props.parentState();
    }

    override render(): React.JSX.Element {
        this.state = this.props.parentState();
        const modelElementIndex = this.state.modelElementIndex;
        const modelElement = getModelElement(this.state?.modelElementId, modelElementIndex)!;
        const modelElementType = modelElement?.elementTypeId;

        if (this.props.propertyDataHandler.currentModelElementId === undefined || modelElement === undefined) {
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

        // filter out all attributes, that are hidden
        const isHidden: (attributeDefinition: any) => boolean = attributeDefinition =>
            (attributeDefinition.annotations ?? []).filter((annotation: any) => annotation.name === 'hidden').length <= 0;
        const attributeDefinitions = this.state.attributeDefinitions.filter(a => isHidden(a));

        return (
            <div id='property-widget'>
                {header}
                {
                    // properties
                    attributeDefinitions.length > 0 ? (
                        <table className='property-table'>
                            <tbody>
                                {attributeDefinitions.map(attributeDefinition => (
                                    <CincoPropertyView
                                        parent={this.props.parent}
                                        parentState={this.props.parentState}
                                        pointer={[]}
                                        attributeDefinition={attributeDefinition}
                                    ></CincoPropertyView>
                                ))}
                            </tbody>
                        </table>
                    ) : (
                        <div>This model element has no properties.</div>
                    )
                }
            </div>
        );
    }
}

export class CincoPropertyView extends React.Component<
    {
        parent: CincoCloudPropertyWidget;
        parentState: () => PropertyWidgetState;
        pointer: ObjectPointer;
        attributeDefinition: Attribute;
    },
    PropertyWidgetState
> {
    constructor(props: {
        parent: CincoCloudPropertyWidget;
        parentState: () => PropertyWidgetState;
        pointer: ObjectPointer;
        attributeDefinition: Attribute;
    }) {
        super(props);
        this.state = this.props.parentState();
    }

    override render(): React.JSX.Element {
        this.state = this.props.parentState();
        const pointer: ObjectPointer = this.props.pointer;
        const attributeDefinition: Attribute = this.props.attributeDefinition;
        const value = locateObjectValue(this.state, pointer)[this.props.attributeDefinition.name];

        // default bounds, if no are specified
        const bounds = attributeDefinition.bounds ?? { upperBound: 1.0, lowerBound: 1.0 };
        const isList = isListAttribute(bounds.upperBound);

        // prepare id
        const pathId = pointer
            .map(({ attribute, index }) => {
                if (index !== undefined) {
                    return `${attribute}-${index}`;
                } else {
                    return attribute;
                }
            })
            .join('-');
        const postfix = pathId ? `${pathId}-${attributeDefinition.name}` : `${attributeDefinition.name}`;
        const inputIdPrefix = `property-input-${postfix}`;

        // list-operation activation definitions
        const objectValue = locateObjectValue(this.state, pointer);
        const listLength = (objectValue[attributeDefinition.name]?.length ?? 0.0) + 1.0;
        const addCellActivated = isList && canAdd(listLength, bounds);
        const deleteCellActivated = (lengthIndex: number | undefined): boolean =>
            canDelete(lengthIndex === undefined ? 0.0 : lengthIndex - 1, bounds);

        const valueList: any = isList ? value ?? [] : [value ?? attributeDefinition.defaultValue ?? ''];
        const firstInputId = valueList.length > 0 ? `${inputIdPrefix + (isList ? '-0' : '')}` : undefined;

        // prepare header
        const header = (
            <th className='property-header' rowSpan={Math.max(1, valueList.length + (isList ? 1 : 0))}>
                <label htmlFor={firstInputId}>
                    {attributeDefinition.name}
                    {this.renderCardinalityIndicator(bounds.lowerBound, bounds.upperBound)}
                </label>
            </th>
        );

        // prepare add button
        const addCell = addCellActivated ? (
            <td className='button-cell'>
                <button
                    type='button'
                    className='action-button'
                    onClick={() =>
                        addPropertyValue(
                        this.props.parent, (newState: PropertyWidgetState) => { this.setState(newState); }, this.state,
                        pointer, attributeDefinition.name
                    )
                }>
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

        // prepare values
        if (isList && valueList.length === 0) {
            // empty list
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
                                {
                                    // delete button
                                    !(bounds.lowerBound === 1 && bounds.upperBound === 1) && deleteCellActivated(valueList.length) && (
                                        <button
                                            type='button'
                                            className='action-button'
                                            onClick={() =>
                                                 deletePropertyValue(
                                                        this.props.parent,
                                                        (newState: PropertyWidgetState) => { this.setState(newState);
                                                    },
                                                    this.state,
                                                    pointer,
                                                    attributeDefinition,
                                                    isList ? index : undefined
                                                )
                                            }
                                        >
                                            <span className='codicon codicon-trash'></span>
                                        </button>
                                    )
                                }
                                {
                                    // clear button
                                    !(bounds.lowerBound === 1 && bounds.upperBound === 1) && !deleteCellActivated(valueList.length) && (
                                        <button type='button' className='action-button' disabled>
                                            <span className='codicon codicon-trash'></span>
                                        </button>
                                    )
                                }
                            </td>
                            <td>
                                <CincoPropertyEntry
                                    parent={this.props.parent}
                                    parentState={this.props.parentState}
                                    pointer={pointer}
                                    attributeDefinition={attributeDefinition}
                                    index={index}
                                    inputIdPrefix={inputIdPrefix}
                                ></CincoPropertyEntry>
                            </td>
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

    /**
     * HEADER COMPONENTS
     */

    protected renderCardinalityIndicator(lowerBound: number, upperBound?: number): React.JSX.Element | undefined {
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
}

export class CincoPropertyEntry extends React.Component<
    {
        parent: CincoCloudPropertyWidget;
        parentState: () => PropertyWidgetState;
        pointer: ObjectPointer;
        attributeDefinition: Attribute;
        index: number;
        inputIdPrefix: string;
    },
    PropertyWidgetState
> {
    constructor(props: any) {
        super(props);
    }

    override render(): React.JSX.Element {
        this.state = this.props.parentState();
        const pointer = this.props.pointer;
        const attributeDefinition = this.props.attributeDefinition;
        const valueName = this.props.attributeDefinition.name;
        const index = this.props.index;
        const inputIdPrefix = this.props.inputIdPrefix;

        const bounds = attributeDefinition.bounds ?? { upperBound: 1.0, lowerBound: 1.0 };
        const isList = isListAttribute(bounds.upperBound);
        const inputId = isList ? `${inputIdPrefix}-${index}` : inputIdPrefix;

        const currentElement = getCurrentModelElement(this.state);
        if (!currentElement) {
            throw new Error('No current element while rendering properties widget.');
        }

        const objectValue = locateObjectValue(this.state, pointer);
        const value = this.getValueAttribute(
            attributeDefinition.type as PrimitivePropertyType,
            isList ? objectValue[valueName][index] : objectValue[valueName]
        );

        // check annotations
        const annotations = attributeDefinition.annotations ?? [];
        const isReadOnly = attributeDefinition.final || annotations.filter(a => a.name === 'readOnly').length > 0;
        switch (attributeDefinition.type) {
            case 'string':
            case 'number':
            case 'boolean': {
                const inputType = this.getInputType(attributeDefinition.type, annotations ?? []);
                const isDisabled = isReadOnly && ['checkbox', 'color', 'date', 'file'].includes(inputType);
                if (inputType === 'textarea') {
                    return (
                        <textarea
                            onChange={event => {
                                const newValue = event.currentTarget.value;
                                // propagate
                                assignPropertyValue(
                                    this.props.parent,
                                    (newState: PropertyWidgetState) => this.setState(newState),
                                    this.state,
                                    pointer,
                                    attributeDefinition,
                                    isList ? index : undefined,
                                    newValue
                                );
                                const rows = calculateRowsForString(newValue);
                                event.target.rows = rows;
                            }}
                            readOnly={isReadOnly}
                            value={value.value}
                            placeholder=''
                            rows={calculateRowsForString(value.value as string)}
                            id={inputId}
                            style={{ width: '100%', height: '100%', boxSizing: 'border-box', resize: 'none', wordWrap: 'normal' }}
                            className={`property-input property-input-${inputType}`}
                        />
                    );
                } else if (inputType === 'file') {
                    const fileDialogService = this.props.parent.fileDialogService;
                    const fileTypes = (attributeDefinition.annotations ?? []).map(a => a.values).flat();
                    const directorySelectable = fileTypes.filter(t => t === FILE_PICKER_DIRECTORY_ALLOWED_KEYWORD).length > 0;
                    const nonDirectoryFileTypesDefined = fileTypes.filter(t => t !== FILE_PICKER_DIRECTORY_ALLOWED_KEYWORD).length > 0;
                    const onlyDirectory = !nonDirectoryFileTypesDefined && directorySelectable;
                    return (
                        <span style={{ display: 'flex', width: '100%' }}>
                            <input
                                type={'button'}
                                value={'Select'}
                                onClick={event => {
                                    fileDialogService
                                        .showOpenDialog({
                                            title: `Select a file for ${attributeDefinition.name}`,
                                            canSelectFiles: !onlyDirectory,
                                            canSelectMany: false,
                                            canSelectFolders: directorySelectable || !nonDirectoryFileTypesDefined,
                                            openLabel: 'Select',
                                            modal: true,
                                            filters: {
                                                'FileType(s)': fileTypes.flat()
                                            }
                                        })
                                        .then(uri => {
                                            if (!uri) {
                                                return;
                                            }
                                            const newValue = uri?.path.fsPath();
                                            // propagate
                                            assignPropertyValue(
                                                this.props.parent,
                                                (newState: PropertyWidgetState) => this.setState(newState),
                                                this.state,
                                                pointer,
                                                attributeDefinition,
                                                isList ? index : undefined,
                                                newValue
                                            );
                                        });
                                }}
                                readOnly={isReadOnly}
                                disabled={isDisabled}
                                id={inputId}
                                className={`property-input property-input-${inputType}`}
                            />
                            <input
                                {...value}
                                readOnly={true}
                                disabled={isDisabled}
                                style={{ width: '100%' }}
                                className={`property-input property-input-${inputType}`}
                            />
                        </span>
                    );
                } else {
                return (
                    <input
                        type={inputType}
                        {...value}
                        onChange={event => {
                            const newValue = attributeDefinition.type === 'boolean'
                                ? event.currentTarget.checked : event.currentTarget.value;
                            // propagate
                            assignPropertyValue(
                                this.props.parent, (newState: PropertyWidgetState) => this.setState(newState), this.state,
                                pointer, attributeDefinition, isList ? index : undefined, newValue
                            );
                        }}
                        readOnly={isReadOnly}
                            disabled={isDisabled}
                            id={inputId}
                            className={`property-input property-input-${inputType}`}
                        />
                    );
                }
            }
            default: {
                const typeDefinition = getCustomType(attributeDefinition.type);
                if (Enum.is(typeDefinition)) {
                    return (
                        <select
                            value={value.value}
                            onChange={event => {
                                // prepare new State
                                assignPropertyValue(
                                    this.props.parent,
                                    (newState: PropertyWidgetState) => this.setState(newState),
                                    this.state,
                                    pointer,
                                    attributeDefinition,
                                    isList ? index : undefined,
                                    event.currentTarget.value
                                );
                            }}
                            disabled={isReadOnly}
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
                                {
                                    // userdefined type
                                    typeDefinition.attributes.map(childDefinition => (
                                        <CincoPropertyView
                                            parent={this.props.parent}
                                            parentState={this.props.parentState}
                                            pointer={pointer.concat({
                                                attribute: attributeDefinition.name,
                                                index: isList ? index : undefined
                                            })}
                                            attributeDefinition={childDefinition}
                                        ></CincoPropertyView>
                                    ))
                                }
                            </tbody>
                        </table>
                    );
                } else {
                    return (
                        <select
                            value={value.value}
                            onChange={event =>
                                assignPropertyValue(
                                    this.props.parent,
                                    (newState: PropertyWidgetState) => this.setState(newState),
                                    this.state,
                                    pointer,
                                    attributeDefinition,
                                    isList ? index : undefined,
                                    event.currentTarget.value
                                )
                            }
                            disabled={isReadOnly}
                            id={inputId}
                        >
                            <option value=''></option>
                            {this.state.modelElementIndex[attributeDefinition.type].map(({ id, name, label }) => (
                                // ModelElementReference identifier specified in the following fallback order:
                                // name => label => attributeDefinitionType
                                <option value={id} key={id}>
                                    {name ? name : label ? label : attributeDefinition.type}
                                    ({name ? label + ', ' : ''}{id})
                                </option>
                            ))}
                        </select>
                    );
                }
            }
        }
    }

    protected getInputType(type: PrimitivePropertyType, annotations: Annotation[]): React.HTMLInputTypeAttribute {
        const isColor = annotations.filter(a => a.name === 'color').length > 0;
        const isFile = annotations.filter(a => a.name === 'file').length > 0;
        const isDate = annotations.filter(a => a.name === 'date').length > 0;
        const isMultiLine = annotations.filter(a => a.name === 'multiline').length > 0;
        switch (type) {
            case 'string':
                if (isMultiLine) {
                    return 'textarea';
                } else if (isFile) {
                    return 'file';
                } else if (isDate) {
                    return 'date';
                } else if (isColor) {
                    return 'color';
                }
                return 'text';
            case 'number':
                return 'number';
            case 'boolean':
                return 'checkbox';
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
                const parsedValue = typeof value === 'string' ? (value === 'false' ? false : true) : value;
                return { checked: parsedValue as boolean };
            }
            default: {
                return { value: value };
            }
        }
    }
}

/**
 * CELL OPERATIONS
 */

function addPropertyValue(
    widget: Widget,
    setState: (newState: PropertyWidgetState) => void,
    state: PropertyWidgetState,
    pointer: ObjectPointer,
    name: string
): void {
    const attributeDefinition = locateAttributeDefinition(state, pointer, name);
    const bounds = attributeDefinition.bounds ?? { upperBound: 1.0, lowerBound: 1.0 };
    const defaultValue = getFallbackDefaultValue(attributeDefinition.type);
    const isList = isListAttribute(bounds.upperBound);

    // TODO: The follwoing code mutates the state, the shallow copy in the next
    // line does not prevent that.
    const newState = { ...state };
    const objectValue = locateObjectValue(newState, pointer);
    if (isList) {
        if (!objectValue[name]) {
            objectValue[name] = [];
        }
        if (!canAdd(objectValue[name].length + 1, bounds)) {
            return;
        }
        objectValue[name].push(defaultValue);
    } else {
        objectValue[name] = defaultValue;
    }
    setState(newState);

    // persist data
    postPropertyChange(state.modelElementId, pointer, name, { kind: 'addValue', isList, defaultValue });
    widget.update();
}

function assignPropertyValue(
    widget: Widget,
    setState: (newState: PropertyWidgetState) => void,
    state: PropertyWidgetState,
    pointer: ObjectPointer,
    attributeDefinition: Attribute,
    index: number | undefined,
    value: any
): void {
    const name = attributeDefinition.name;

    // validation
    if (!checkEnum(attributeDefinition.type, value)) {
        return;
    }
    const type = attributeDefinition.type;
    const typeDefinitions = getCustomTypes().filter(t => t.elementTypeId === type);
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
        return;
    }

    // update state
    const newState = { ...state };
    const objectValue = locateObjectValue(newState, pointer);
    if (index !== undefined) {
        objectValue[name][index] = value;
    } else {
        objectValue[name] = value;
    }
    setState(newState);

    // persist data
    postPropertyChange(state.modelElementId, pointer, name, { kind: 'assignValue', index, value });

    // update
    widget.update();
}

function deletePropertyValue(
    widget: Widget,
    setState: (newState: PropertyWidgetState) => void,
    state: PropertyWidgetState,
    pointer: ObjectPointer,
    attributeDefinition: Attribute,
    index?: number
): void {
    const name = attributeDefinition.name;
    const bounds = attributeDefinition.bounds ?? { lowerBound: 1.0, upperBound: 1.0 };

    // update state
    const newState = { ...state };
    const objectValue = locateObjectValue(newState, pointer);
    if (index !== undefined) {
        if (!canDelete(objectValue[name].length - 1, bounds)) {
            return;
        }
        objectValue[name].splice(index, 1);
    } else {
        objectValue[name] = '';
    }
    setState(newState);

    // persist data
    postPropertyChange(state.modelElementId, pointer, name, { kind: 'deleteValue', index });
    widget.update();
}

/**
 * COMMUNICATE
 */

function postPropertyChange(modelElementId: string, pointer: ObjectPointer, name: string, change: PropertyChange): void {
    postMessage({
        kind: 'editProperty',
        modelElementId: modelElementId,
        pointer,
        name,
        change
    });
}

function postMessage(message: PropertyViewMessage): void {
    window.postMessage(message);
}

/**
 * VALUE HELPER
 */

function checkEnum(type: string, value: any): boolean {
    const typeDefinitions = getCustomTypes().filter(t => t.elementTypeId === type);
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

/**
 * FUNCTIONS FOR LOCATING VALUES
 */

function locateObjectValue(state: PropertyWidgetState, pointer: ObjectPointer): any {
    let objectValue: any = state.values; // these are the attributes of the modelElement
    for (const step of pointer) {
        let nextValue;
        if (step.index !== undefined) {
            nextValue = objectValue[step.attribute][step.index];
        } else {
            nextValue = objectValue[step.attribute];
        }
        // if steppedValue does not exists => repair with defaultValue
        if (!nextValue) {
            const attributeDefinition = state.attributeDefinitions.find(a => a.name === step.attribute);
            if (attributeDefinition) {
                const defaultValue = getFallbackDefaultValue(attributeDefinition.type);
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

function locateAttributeDefinition(state: PropertyWidgetState, pointer: ObjectPointer, name: string): Attribute {
    let definitions: Attribute[] = state.attributeDefinitions;
    for (const step of pointer) {
        const nextAttribute = definitions.find(attribute => attribute.name === step.attribute);
        if (nextAttribute === undefined) {
            throw new Error(`Object pointer on undefined attribute '${step.attribute}'`);
        }
        const typeDefinition = getCustomType(nextAttribute.type);
        if (Enum.is(typeDefinition)) {
            throw new Error(`Object pointer '${step.attribute}' on enum`);
        } else if (UserDefinedType.is(typeDefinition)) {
            definitions = (typeDefinition as UserDefinedType).attributes;
        }
    }
    return definitions.find(definition => definition.name === name)!;
}

function getCurrentModelElement(state: PropertyWidgetState): LabeledModelElementReference | undefined {
    return getModelElement(state.modelElementId, state.modelElementIndex);
}

function getModelElement(modelElementId: string, modelElementIndex: ModelElementIndex): LabeledModelElementReference | undefined {
    for (const [, modelElements] of Object.entries(modelElementIndex)) {
        for (const element of modelElements) {
            if (element.id === modelElementId) {
                return element;
            }
        }
    }
    return undefined;
}

function calculateRowsForString(inputString: string): number {
    if (!inputString) {
        return 1; // Minimum of 1 row
    }

    return inputString.split('\n').length;
}
