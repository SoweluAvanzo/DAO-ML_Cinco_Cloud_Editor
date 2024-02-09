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

import { Action, ManagedBaseAction, Operation } from './shared-protocol';
import { hasStringProp } from './type-utils';

export interface GeneratorAction extends ManagedBaseAction {
    kind: typeof GeneratorAction.KIND;
    modelElementId: string;
    targetFolder?: string;
}

export namespace GeneratorAction {
    export const KIND = 'cincoGenerate';

    export function is(object: any): object is GeneratorAction {
        return Action.hasKind(object, KIND) && hasStringProp(object, 'modelElementId');
    }

    export function create(modelElementId: string, targetFolder?: string): GeneratorAction {
        return {
            kind: KIND,
            modelElementId,
            targetFolder: targetFolder
        };
    }
}

export const GenerateGraphDiagramCommand = {
    id: 'GenerateGraphDiagram.command',
    label: 'Generate Graph',
    category: 'Cinco Cloud',
    darkIconClass: 'generate_graph_diagram_command_dark',
    lightIconClass: 'generate_graph_diagram_command_light',
    keybinding: 'alt+g'
};

export const CreateGenerateGraphDiagramCommand = {
    id: 'CreateGenerateGraphDiagramCommand.command'
};

export const CreateGeneratorTemplateCommand = {
    id: 'cincocloud.createGeneratorTemplate',
    label: 'Create Generator Template',
    category: 'Cinco Cloud'
};

export const CreateJavascriptGeneratorTemplateCommand = {
    id: 'cincocloud.createJSGeneratorTemplate',
    label: 'Create JS Generator Template',
    category: 'Cinco Cloud'
};

/**
 * Action
 *
 * This action will be dispatched to the backend by the listeners of the ActionTool
 */

export interface GeneratorViewAction extends Action {
    kind: typeof GeneratorViewAction.KIND;
    modelElementId: string;
    fileContent: string;
}
export namespace GeneratorViewAction {
    export const KIND = 'generatorRequest';

    export function is(object: any): object is GeneratorViewAction {
        return Action.hasKind(object, KIND) && hasStringProp(object, 'modelElementId') && hasStringProp(object, 'fileContent');
    }

    export function create(modelElementId: string, fileContent: string): GeneratorViewAction {
        return {
            kind: KIND,
            modelElementId,
            fileContent
        };
    }
}

/**
 * Client Action
 *
 * This action will be dispatched to the client as a response to the GeneratorAction
 */

export interface GeneratorResponseAction extends Action {
    kind: typeof GeneratorResponseAction.KIND;
    modelElementId: string;
    fileContent: string;
    targetFolder: string;
}

export namespace GeneratorResponseAction {
    export const KIND = 'generatorResponse';

    export function is(object: any): object is GeneratorResponseAction {
        return (
            Action.hasKind(object, KIND) &&
            hasStringProp(object, 'modelElementId') &&
            hasStringProp(object, 'fileContent') &&
            hasStringProp(object, 'targetFolder')
        );
    }

    export function create(modelElementId: string, fileContent: string, targetFolder: string): GeneratorResponseAction {
        return {
            kind: KIND,
            modelElementId: modelElementId,
            fileContent: fileContent,
            targetFolder: targetFolder
        };
    }
}

export interface GeneratorCreateFileOperation extends Operation {
    kind: typeof GeneratorCreateFileOperation.KIND;
    modelElementId: string;
    filesContentsMap: Map<any, string>;
}
export namespace GeneratorCreateFileOperation {
    export const KIND = 'cincoCreateGeneratedFile';

    export function is(object: any): object is GeneratorCreateFileOperation {
        return Operation.hasKind(object, KIND) && hasStringProp(object, 'modelElementId');
    }

    export function create(modelElementId: string, filesContentsMap: Map<any, string>): GeneratorCreateFileOperation {
        return {
            kind: KIND,
            isOperation: true,
            modelElementId,
            filesContentsMap
        };
    }
}

export interface GeneratorEditAction extends Action {
    kind: typeof GeneratorEditAction.KIND;
    modelElementId: string;
    targetFolder: string;
}
export namespace GeneratorEditAction {
    export const KIND = 'cincoGenerate';

    export function is(object: any): object is GeneratorCreateFileOperation {
        return Action.hasKind(object, KIND) && hasStringProp(object, 'modelElementId') && hasStringProp(object, 'targetFolder');
    }

    export function create(modelElementId: string, targetFolder: string): GeneratorEditAction {
        return {
            kind: KIND,
            modelElementId: modelElementId,
            targetFolder: targetFolder
        };
    }
}
