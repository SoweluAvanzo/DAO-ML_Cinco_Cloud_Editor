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
export enum AnnotationValues {
    PATH,
    STRING,
    CLASS,
    VALUE
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
        'PostContentChange'
    ];
    static readonly paletteAnnotations = [
        {
            name: 'icon',
            parameterLimits: [1, 1],
            valueRules: [
                {
                    positon: [0],
                    type: AnnotationValues.PATH
                }
            ]
        },
        {
            name: 'palette',
            parameterLimits: [0, -1],
            valueRules: [
                {
                    positon: [0, -1], // from 0 to infiny this rules applies
                    type: AnnotationValues.STRING
                }
            ]
        },
        {
            name: 'disable',
            parameterLimits: [0, 4],
            valueRules: [
                {
                    positon: [0, -1],
                    type: ['resize', 'move', 'select', 'create', 'delete']
                }
            ]
        }
    ];
    static readonly actionHandlerAnnotations = [
        {
            name: 'Hook',
            parameterLimits: [1, -1],
            valueRules: [
                {
                    positon: [0, 0],
                    type: AnnotationValues.STRING
                },
                {
                    positon: [1, -1],
                    type: this.HOOK_VALUES
                }
            ]
        },
        {
            name: 'DoubleClickAction',
            parameterLimits: [1, 1],
            valueRules: [
                {
                    positon: [0, 0],
                    type: AnnotationValues.STRING
                }
            ]
        },
        {
            name: 'SelectAction',
            parameterLimits: [1, 1],
            valueRules: [
                {
                    positon: [0, 0],
                    type: AnnotationValues.STRING
                }
            ]
        },
        {
            name: 'GeneratorAction',
            parameterLimits: [1, 1],
            valueRules: [
                {
                    positon: [0, 0],
                    type: AnnotationValues.STRING
                }
            ]
        },
        {
            name: 'Validation',
            parameterLimits: [1, 1],
            valueRules: [
                {
                    positon: [0, 0],
                    type: AnnotationValues.STRING
                }
            ]
        },
        {
            name: 'AppearanceProvider',
            parameterLimits: [1, 1],
            valueRules: [
                {
                    positon: [0, 0],
                    type: AnnotationValues.STRING
                }
            ]
        },
        {
            name: 'ValueProvider',
            parameterLimits: [1, 1],
            valueRules: [
                {
                    positon: [0, 0],
                    type: AnnotationValues.STRING
                }
            ]
        }
    ];
    static readonly attributeAnnotationDefinitions = [
        {
            name: 'multiline',
            parameterLimits: [0, 0]
        },
        {
            name: 'readOnly',
            parameterLimits: [0, 0]
        },
        {
            name: 'hidden',
            parameterLimits: [0, 0]
        },
        {
            name: 'color',
            parameterLimits: [0, 0]
        },
        {
            name: 'file',
            parameterLimits: [0, 0]
        },
        {
            name: 'date',
            parameterLimits: [0, 0]
        }
    ];
    static get attributeAnnotations(): string[] {
        return this.attributeAnnotationDefinitions.map(a => a.name);
    }
    static get modelElementAnnotations(): string[] {
        return this.actionHandlerAnnotations.concat(this.paletteAnnotations).map(a => a.name);
    }
    static get allAnnotations(): string[] {
        return this.attributeAnnotationDefinitions
            .concat(this.actionHandlerAnnotations)
            .concat(this.paletteAnnotations)
            .map(a => a.name);
    }
}
