/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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
import { ValidationAcceptor } from 'langium';
import {
    Annotation,
    isAttribute,
    isEdge,
    isGraphModel,
    isModelElement,
    isNode,
    isNodeContainer,
    isReferencedModelElement
} from '../../generated/ast';

export enum AnnotationValue {
    PATH,
    STRING
    // CLASS // TODO: somewhere in the future this can be evaluated by reading in the file
}

export interface AnnotationDescription {
    name: string;
    parameterLimits: [number, number]; // from, to
    description: string;
    valueRules: ValueRule[];
    extra?: (annotation: Annotation, acceptor: ValidationAcceptor) => void;
}

export interface ValueRule {
    position: [number, number]; // from, to
    type: string[] | AnnotationValue;
}

export class MglAnnotations {
    static readonly HOOK_VALUES = [
        'CanCreate',
        'PreCreate',
        'PostCreate',
        'CanDelete',
        'PreDelete',
        'PostDelete',
        'CanAttributeChange',
        'PostAttributeChange',
        'PreAttributeChange',
        'CanSelect',
        'PostSelect',
        'CanDoubleClick',
        'PostDoubleClick',
        'CanReconnect',
        'PreReconnect',
        'PostReconnect',
        'CanMove',
        'PreMove',
        'PostMove',
        'CanResize',
        'PreResize',
        'PostResize',
        'CanSave',
        'PostSave',
        'PostPathChange',
        'PostContentChange',
        'OnOpen',
        'CanLayout',
        'PreLayout',
        'PostLayout'
    ];
    static readonly labelProviderHandler: AnnotationDescription = {
        name: 'LabelProvider',
        parameterLimits: [1, 1],
        description: this.createHandlerDescription(
            'LabelProvider',
            '"ExampleLabelProvider"',
            'This annotation allows to set the label of the element for references e.g. inside the palette dynamically.' +
                ' The class `ExampleLabelProvider`' +
                'is executed whenever the palette is requested.'
        ),
        valueRules: [
            {
                position: [0, 0],
                type: AnnotationValue.STRING
            }
        ]
    };
    static readonly labelAnnotation: AnnotationDescription = {
        name: 'label',
        parameterLimits: [1, 1],
        description:
            'This annotation sets the label of the element for references e.g. inside the palette.' +
            ' E.g. `label("someNode")` or `label("${name}")` with a contained reference.' +
            ' The `label("${name}")` uses a declared attribute "name" as a label for mapping.',
        valueRules: [
            {
                position: [1, 1],
                type: AnnotationValue.STRING
            }
        ]
    };
    static readonly paletteAnnotations: AnnotationDescription[] = [
        {
            name: 'icon',
            parameterLimits: [1, 1],
            description:
                'This annotation sets the icon of the element inside the palette.' +
                ' E.g. `icon("mylanguage/images/icon.png")`.' +
                ' The path `mylanguage/images/icon.png` is the relative path inside the languages-folder.',
            valueRules: [
                {
                    position: [0, -1],
                    type: AnnotationValue.PATH
                }
            ]
        },
        {
            name: 'palette',
            parameterLimits: [0, -1],
            description:
                'This annotation sets the category of the palette in which the modelElement will be shown.' +
                ' E.g. `palette("My Special Nodes")`. This will create a category `My Special Nodes` inside the palette.',
            extra: (annotation: Annotation, acceptor: ValidationAcceptor) => {
                if (isGraphModel(annotation.$container.$type)) {
                    acceptor('error', 'GraphModels are not shown in the palette!', {
                        node: annotation,
                        property: 'name'
                    });
                }
            },
            valueRules: [
                {
                    position: [0, -1], // from 0 to infiny this rules applies
                    type: AnnotationValue.STRING
                }
            ]
        },
        {
            name: 'layout',
            parameterLimits: [1, 1],
            description:
                'This annotation sets the elk-based layouting options for the model element.' +
                " E.g. `layout(\"{'elk.algorithm': 'layered'}\")`. This will set the elk layoutOptions fot the annotated model element." +
                ' (Check here for further information: https://eclipse.dev/elk/documentation.html and' +
                ' https://rtsys.informatik.uni-kiel.de/elklive/) ',
            extra: (annotation: Annotation, acceptor: ValidationAcceptor) => {
                const predefinedLayouts = ['random', 'layered'];
                try {
                    if (!predefinedLayouts.includes(annotation.value[0])) {
                        JSON.parse(annotation.value[0]);
                    }
                } catch (e) {
                    acceptor(
                        'error',
                        'The layout needs to be either a predefined layout-algorithm or have a valid JSON-value for the layoutOptions!' +
                            '(predefined: ' +
                            predefinedLayouts.join(', ') +
                            ')',
                        {
                            node: annotation,
                            property: 'name'
                        }
                    );
                }
            },
            valueRules: [
                {
                    position: [1, 1], // from 0 to infiny this rules applies
                    type: AnnotationValue.STRING
                }
            ]
        },
        {
            name: 'disable',
            parameterLimits: [1, 5],
            description:
                'This annotation disables functionality on the canvas.' +
                ' E.g. `@disable(resize, move)` disables the resizing and moving of the annotated modelElement.' +
                ' You can Pick out of the following: resize, move, select, create and delete.',
            valueRules: [
                {
                    position: [0, -1],
                    type: ['resize', 'move', 'select', 'create', 'delete', 'layout']
                }
            ]
        }
    ];
    static readonly actionHandlerAnnotations: AnnotationDescription[] = [
        {
            name: 'CustomAction',
            parameterLimits: [1, 2],
            description: this.createHandlerDescription(
                'CustomAction',
                '"ExampleCustomAction", "Some label for the action"',
                'If you open a context menu, there will be an entry with the second value of the annotation. If you click on it,' +
                    ' it will execute the action associated with the `ExampleCustomAction`.'
            ),
            valueRules: [
                {
                    position: [0, -1],
                    type: AnnotationValue.STRING
                }
            ]
        },
        {
            name: 'Hook',
            parameterLimits: [1, -1],
            description: this.createHandlerDescription(
                'Hook',
                '"ExampleHook", CanCreate, PreCreate, PostCreate',
                'Various events can be realized using this functionality.' +
                    ' If you interact or change the annotated model element in any way some event is triggered.' +
                    ' The example triggers the functionality of the class `ExampleHook`. ' +
                    ' Each time a modelElement of the annotated type is about to be created, the three event types' +
                    'CanCreate, PreCreate and PostCreate are fired and can be handled. The full list of possible events are: (' +
                    this.HOOK_VALUES.join(', ') +
                    ').'
            ),
            valueRules: [
                {
                    position: [0, 0],
                    type: AnnotationValue.STRING
                },
                {
                    position: [1, -1],
                    type: MglAnnotations.HOOK_VALUES
                }
            ]
        },
        {
            name: 'DoubleClickAction',
            parameterLimits: [1, 1],
            description: this.createHandlerDescription(
                'DoubleClickAction',
                '"ExampleDoubleClickHandler"',
                'If you double click the annotated modelElement it triggers the functionality of the class `ExampleDoubleClickHandler`.'
            ),
            valueRules: [
                {
                    position: [0, 0],
                    type: AnnotationValue.STRING
                }
            ]
        },
        {
            name: 'SelectAction',
            parameterLimits: [1, 1],
            description: this.createHandlerDescription(
                'SelectAction',
                '"ExampleSelectHandler"',
                'If you select the annotated modelElement it triggers the functionality of the class `ExampleSelectHandler`.'
            ),
            valueRules: [
                {
                    position: [0, 0],
                    type: AnnotationValue.STRING
                }
            ]
        },
        {
            name: 'FileCodec',
            parameterLimits: [1, 1],
            description: this.createHandlerDescription(
                'FileCodec',
                '"ExampleFileCodec"',
                'If you open a graphModel the associated class `ExampleFileCodec` is triggered.' +
                    ' The class handles the parsing/decoding and seralization/encoding behavior.' +
                    ' Not using the annotation results in the use of default JSON format.'
            ),
            extra: (annotation: Annotation, acceptor: ValidationAcceptor) => {
                if (!isGraphModel(annotation.$container)) {
                    acceptor('error', 'Only graphModels are allowed to have fileCodecs!', {
                        node: annotation,
                        property: 'name'
                    });
                }
            },
            valueRules: [
                {
                    position: [1, 1],
                    type: AnnotationValue.STRING
                }
            ]
        },
        {
            name: 'GeneratorAction',
            parameterLimits: [1, 1],
            description: this.createHandlerDescription(
                'GeneratorAction',
                '"ExampleGenerator"',
                'If you click the generator button, at the top of the palette,' +
                    'it triggers the functionality of the class `ExampleGenerator`.'
            ),
            extra: (annotation: Annotation, acceptor: ValidationAcceptor) => {
                if (!isGraphModel(annotation.$container)) {
                    acceptor('error', 'Only graphModels are allowed to have generators!', {
                        node: annotation,
                        property: 'name'
                    });
                }
            },
            valueRules: [
                {
                    position: [0, 0],
                    type: AnnotationValue.STRING
                }
            ]
        },
        {
            name: 'Validation',
            parameterLimits: [1, 1],
            description: this.createHandlerDescription(
                'Validation',
                '"ExampleValidator"',
                'If you change the model, the class `ExampleValidator` is executed to validated the annotated modelElement.'
            ),
            valueRules: [
                {
                    position: [0, 0],
                    type: AnnotationValue.STRING
                }
            ]
        },
        {
            name: 'AppearanceProvider',
            parameterLimits: [1, 1],
            description: this.createHandlerDescription(
                'AppearanceProvider',
                '"ExampleAppearanceProvider"',
                'If you change the model, the class `ExampleAppearanceProvider`' +
                    'is executed to determine/change the appearance of the annotated modelElement.'
            ),
            valueRules: [
                {
                    position: [0, 0],
                    type: AnnotationValue.STRING
                }
            ]
        },
        {
            name: 'ValueProvider',
            parameterLimits: [1, 1],
            description: this.createHandlerDescription(
                'ValueProvider',
                '"ExampleValueProvider"',
                'If you change the model, the class `ExampleValueProvider`' +
                    'is executed to determine/change the values of the annotated modelElement.'
            ),
            valueRules: [
                {
                    position: [0, 0],
                    type: AnnotationValue.STRING
                }
            ]
        },
        {
            name: 'LayoutOptionsProvider',
            parameterLimits: [1, 1],
            description: this.createHandlerDescription(
                'LayoutOptionsProvider',
                '"ExampleLayoutOptionsProvider"',
                'This annotation sets the elk-based layouting options for the model element dynamically by using a provider-class.' +
                    'The `ÈxampleLayoutOptionsProvider` will provide the elk layoutOptions fot the annotated model element.' +
                    ' (Check here for further information: https://eclipse.dev/elk/documentation.html and' +
                    ' https://rtsys.informatik.uni-kiel.de/elklive/) '
            ),
            valueRules: [
                {
                    position: [0, 0],
                    type: AnnotationValue.STRING
                }
            ]
        }
    ];
    static readonly attributeAnnotationDefinitions: AnnotationDescription[] = [
        {
            name: 'multiline',
            parameterLimits: [0, 0],
            description:
                'This annotation visualizes the annotated attribute inside the properties-view as a multiline textfield.' +
                ' E.g. `@multiline`',
            valueRules: []
        },
        {
            name: 'readOnly',
            parameterLimits: [0, 0],
            description:
                'This annotation visualizes the annotated attribute inside the properties-view as un-editable.' + ' E.g. `@readOnly`',
            valueRules: []
        },
        {
            name: 'hidden',
            parameterLimits: [0, 0],
            description: 'This annotation hides the annotated attribute from the properties-view.' + ' E.g. `@hidden`',
            valueRules: []
        },
        {
            name: 'color',
            parameterLimits: [0, 0],
            description:
                'This annotation visualizes the annotated attribute inside the properties-view as color picker.' + ' E.g. `@color`',
            valueRules: []
        },
        {
            name: 'file',
            parameterLimits: [0, 0],
            description: 'This annotation visualizes the annotated attribute inside the properties-view as file picker.' + ' E.g. `@file`',
            valueRules: []
        },
        {
            name: 'date',
            parameterLimits: [0, 0],
            description: 'This annotation visualizes the annotated attribute inside the properties-view as date picker.' + ' E.g. `@date`',
            valueRules: []
        }
    ];

    static get attributeAnnotationNames(): string[] {
        return MglAnnotations.attributeAnnotations.map(a => a.name);
    }
    static get attributeAnnotations(): AnnotationDescription[] {
        return MglAnnotations.attributeAnnotationDefinitions;
    }

    static get modelElementAnnotationNames(): string[] {
        return this.modelElementAnnotations.map(a => a.name);
    }
    static get modelElementAnnotations(): AnnotationDescription[] {
        return MglAnnotations.actionHandlerAnnotations
            .concat(MglAnnotations.paletteAnnotations)
            .concat([MglAnnotations.labelAnnotation, MglAnnotations.labelProviderHandler])
            .concat([]);
    }

    static get primeReferenceAnnotationNames(): string[] {
        return this.primeReferenceAnnotations.map(a => a.name);
    }
    static get primeReferenceAnnotations(): AnnotationDescription[] {
        return [MglAnnotations.labelAnnotation, MglAnnotations.labelProviderHandler];
    }

    static get allAnnotationNames(): string[] {
        return this.allAnnotationDefinitions.map(a => a.name);
    }
    static get allAnnotationDefinitions(): AnnotationDescription[] {
        return MglAnnotations.attributeAnnotationDefinitions
            .concat(MglAnnotations.actionHandlerAnnotations)
            .concat(MglAnnotations.paletteAnnotations)
            .concat([MglAnnotations.labelAnnotation, MglAnnotations.labelProviderHandler]);
    }

    static checkAnnotation(annotation: Annotation, acceptor: ValidationAcceptor): void {
        if (isAttribute(annotation.$container)) {
            const supported = MglAnnotations.annotationIsSupported(MglAnnotations.attributeAnnotationNames, annotation, acceptor);
            if (supported) {
                MglAnnotations.handleAnnotationValidation(annotation, MglAnnotations.attributeAnnotations, acceptor);
            }
        } else if (isModelElement(annotation.$container)) {
            const supported = MglAnnotations.annotationIsSupported(MglAnnotations.modelElementAnnotationNames, annotation, acceptor);
            if (supported) {
                MglAnnotations.handleAnnotationValidation(annotation, MglAnnotations.modelElementAnnotations, acceptor);
            }
        } else if (isReferencedModelElement(annotation.$container) && annotation.$container.$containerProperty === 'primeReference') {
            const supported = MglAnnotations.annotationIsSupported(MglAnnotations.primeReferenceAnnotationNames, annotation, acceptor);
            if (supported) {
                MglAnnotations.handleAnnotationValidation(annotation, MglAnnotations.primeReferenceAnnotations, acceptor);
            }
        } else {
            const message = 'Unknown Annotation "' + annotation.name + '"!';
            acceptor('warning', message, {
                node: annotation,
                property: 'name'
            });
        }
    }

    static annotationIsSupported(supportedAnnotations: string[], annotation: Annotation, acceptor: ValidationAcceptor): boolean {
        if (!supportedAnnotations.includes(annotation.name)) {
            const message = 'Unknown Annotation "' + annotation.name + '"! Supported are: ' + supportedAnnotations.join(', ');
            acceptor('error', message, {
                node: annotation,
                property: 'name'
            });
            return false;
        }
        return true;
    }

    static handleAnnotationValidation(
        annotation: Annotation,
        supportedAnnotations: AnnotationDescription[],
        acceptor: ValidationAcceptor
    ): void {
        const name = annotation.name;
        const values = annotation.value;
        const annotationDefinitions = supportedAnnotations.filter(a => a.name === name);
        for (const annotationDefinition of annotationDefinitions) {
            const description = annotationDefinition.description;
            // add doku
            acceptor('hint', description, {
                node: annotation
            });

            // add errors and warnings
            const limits = annotationDefinition.parameterLimits;
            if (limits[0] > values.length) {
                const text =
                    limits[0] === limits[1]
                        ? limits[0] === 0
                            ? 'no'
                            : 'exactly ' + limits[0]
                        : 'atleast ' + limits[0] + (limits[1] > limits[0] ? ' and atmost ' + limits[1] : '');
                const message = 'Annotation has too few parameters! It needs ' + text + ' arguments! Use it like this: ' + description;
                acceptor('error', message, {
                    node: annotation
                });
            }
            if (limits[1] > limits[0] && limits[1] < values.length) {
                const message = 'Annotation has too many parameters! Use it like this: ' + description;
                acceptor('error', message, {
                    node: annotation
                });
            }
            // check value rules
            const valueRules = annotationDefinition.valueRules;
            for (let i = 0; i < values.length; i++) {
                const annotationValue = values[i];
                const applyableRules = valueRules.filter(
                    r => r.position[0] <= i && ((r.position[0] <= r.position[1] && i <= r.position[1]) || r.position[0] > r.position[1])
                );
                for (const rule of applyableRules) {
                    switch (rule.type) {
                        case AnnotationValue.PATH:
                            {
                                const message =
                                    'Make sure that this path points to the file inside the languages folder! E.g. ' + description;
                                acceptor('hint', message, {
                                    node: annotation,
                                    property: 'value'
                                });
                            }
                            break;
                        case AnnotationValue.STRING:
                            break;
                        default:
                            {
                                let possibleValues = rule.type as string[];
                                if (annotation.name === 'Hook') {
                                    possibleValues = [
                                        'CanCreate',
                                        'PreCreate',
                                        'PostCreate',
                                        'CanDelete',
                                        'PreDelete',
                                        'PostDelete',
                                        'CanAttributeChange',
                                        'PostAttributeChange',
                                        'PreAttributeChange',
                                        'CanSelect',
                                        'PostSelect',
                                        'CanDoubleClick',
                                        'PostDoubleClick',
                                        'CanLayout',
                                        'PreLayout',
                                        'PostLayout'
                                    ];
                                    if (isGraphModel(annotation.$container)) {
                                        possibleValues = possibleValues.concat([
                                            'CanSave',
                                            'PostSave',
                                            'PostPathChange',
                                            'PostContentChange',
                                            'OnOpen'
                                        ]);
                                    } else if (isEdge(annotation.$container)) {
                                        possibleValues = possibleValues.concat([
                                            'CanMove',
                                            'PreMove',
                                            'PostMove',
                                            'CanReconnect',
                                            'PreReconnect',
                                            'PostReconnect'
                                        ]);
                                    } else if (isNode(annotation.$container) || isNodeContainer(annotation.$container)) {
                                        possibleValues = possibleValues.concat([
                                            'CanMove',
                                            'PreMove',
                                            'PostMove',
                                            'CanResize',
                                            'PreResize',
                                            'PostResize'
                                        ]);
                                    }
                                }
                                if (!possibleValues.includes(annotationValue)) {
                                    const message =
                                        'The value "' +
                                        annotationValue +
                                        '" is not allowed! Please pick one of these: ' +
                                        possibleValues.join(', ');
                                    acceptor('error', message, {
                                        node: annotation
                                    });
                                }
                            }
                            break;
                    }
                }
                if (annotationDefinition.extra) {
                    annotationDefinition.extra(annotation, acceptor);
                }
            }
        }
    }

    static createHandlerDescription(annotationName: string, signature: string, description: string): string {
        return (
            'This annotation can attach functions to the annotated modelElement.' +
            ' E.g. `@' +
            annotationName +
            ' ( ' +
            signature +
            ' )`. ' +
            description +
            ' That class must be present inside' +
            ' the languages-folder. Look for more information and examples here:' +
            ' `https://gitlab.com/scce/cinco-cloud/-/tree/main/cinco-cloud-archetype/editor/workspace`'
        );
    }
}
