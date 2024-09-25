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

import { inject, injectable } from 'inversify';
import {
    IActionDispatcher,
    IDiagramStartup,
    ILogger,
    KeyCode,
    MaybePromise,
    SelectionService,
    TYPES,
    hasStringProp
} from '@eclipse-glsp/client';
import {
    CompositionSpecification,
    GeneratorAction,
    PropertyViewResponseAction,
    ServerDialogAction,
    ServerOutputAction,
    CommandAction,
    ValidationRequestAction,
    hasGeneratorAction,
    hasValidator,
    CINCO_STARTUP_RANK
} from '@cinco-glsp/cinco-glsp-common';
import { ServerArgsProvider } from '../meta/server-args-response-handler';
import { GraphModelProvider } from '../model/graph-model-provider';
import { CincoGraphModel, CincoModelElement } from '../model/model';

export interface CincoPaletteTools {
    id: string;
}

export namespace CincoPaletteTools {
    export function is(object: any): object is CincoPaletteTools {
        return (object !== undefined && hasStringProp(object, 'id')) || CincoCustomTool.is(object);
    }
}

export interface CincoCustomTool extends CincoPaletteTools {
    title: string;
    codicon: string;
    action?: (event: any) => void;
    shortcut?: KeyCode[];
}

export namespace CincoCustomTool {
    export function is(object: any): object is CincoCustomTool {
        return object !== undefined && hasStringProp(object, 'codicon') && hasStringProp(object, 'title');
    }
}

export const EnvironmentProvider = Symbol('IEnvironmentProvider');
export interface IEnvironmentProvider extends IDiagramStartup {
    getWorkspaceRoot(): Promise<string>;
    handleLogging(action: ServerOutputAction): void | Promise<void>;
    handleCommand(command: CommandAction): void | Promise<void>;
    showDialog(action: ServerDialogAction): void | Promise<void>;
    selectedElementsChanged(modelElementId: string[]): void | Promise<void>;
    provideProperties(action: PropertyViewResponseAction): void | Promise<void>;
    propagateMetaspecification(metaSpec: CompositionSpecification): void | Promise<void>;
    provideTools(): CincoPaletteTools[];
    postRequestMetaSpecification(): Promise<void> | void;
    getCurrentModel(): CincoGraphModel;
    get actionDispatcher(): IActionDispatcher;
    selectedElements(): CincoModelElement[];
}

@injectable()
export class DefaultEnvironmentProvider implements IEnvironmentProvider {
    handleCommand(command: CommandAction): void | Promise<void> {
        throw new Error('Method not implemented.');
    }
    preInitialize?(): MaybePromise<void> {}
    preRequestModel?(): MaybePromise<void> {}
    postModelInitialization?(): MaybePromise<void> {}
    static _rank: number = CINCO_STARTUP_RANK - 2; // needs to be before CincoToolPalette (has: CINCO_STARTUP_RANK - 1)
    rank: number = DefaultEnvironmentProvider._rank;
    @inject(TYPES.IActionDispatcher) protected _actionDispatcher: IActionDispatcher;
    @inject(TYPES.ILogger) protected logger: ILogger;
    @inject(GraphModelProvider)
    protected readonly graphModelProvider: GraphModelProvider;
    @inject(SelectionService) protected selectionService: SelectionService;

    protected selectedElementIds: string[];
    protected model: CincoGraphModel;

    async getWorkspaceRoot(): Promise<string> {
        const serverArgs = await ServerArgsProvider.getServerArgs();
        return serverArgs?.workspacePath;
    }

    getCurrentModel(): CincoGraphModel {
        return this.model;
    }

    handleLogging(action: ServerOutputAction): void | Promise<void> {
        switch (
            action.logLevel // "NONE" | "INFO" | "WARNING" | "ERROR" | "FATAL" | "OK"
        ) {
            case 'INFO':
                this.logger.info(this, action.message);
                break;
            case 'ERROR':
                this.logger.error(this, action.message);
                break;
            case 'FATAL':
                this.logger.error(this, action.message);
                break;
            case 'WARNING':
                this.logger.warn(this, action.message);
                break;
            default:
                this.logger.log(this, action.message);
                break;
        }
    }

    showDialog(action: ServerDialogAction): void {
        alert('ShowDialog not implemented: ' + action.message);
    }

    selectedElementsChanged(modelElementIds: string[]): void | Promise<void> {
        this.selectedElementIds = modelElementIds;
    }

    selectedElements(): CincoModelElement[] {
        return this.selectionService.getSelectedElements().filter(s => CincoModelElement.is(s));
    }

    provideProperties(action: PropertyViewResponseAction): void | Promise<void> {
        this.logger.log(this, 'No propaties view available.');
    }

    propagateMetaspecification(metaSpec: CompositionSpecification): void | Promise<void> {
        this.logger.log(this, 'Metaspecification does not need to be propagated.');
    }

    async postRequestModel(): Promise<void> {
        this.model = await this.graphModelProvider.graphModel;
    }

    postRequestMetaSpecification(): Promise<void> | void {
        this.logger.log(this, 'Received metaspec.');
    }

    provideTools(): CincoPaletteTools[] {
        let tools = [
            {
                id: '_default'
            },
            {
                id: '_delete'
            },
            /*
            {
                id: '_marquee'
            },
            {
                id: '_validate'
            },*/
            {
                id: 'cinco.validate-tool',
                codicon: 'pass',
                title: 'Validate model',
                action: async (_: any) => {
                    const model = await this.graphModelProvider.graphModel;
                    const action = ValidationRequestAction.create(model.id);
                    const validationResponse = await this.actionDispatcher.request(action);
                    let messageText = '';
                    for (const message of validationResponse.messages) {
                        messageText += `{
                            Name: ${message.name},
                            Status: ${message.status},
                            Message: ${message.message},
                        }\n`;
                    }
                    alert('Validation View not implemented: ' + messageText);
                },
                shortcut: ['AltLeft', 'KeyV']
            } as CincoPaletteTools,
            {
                id: 'cinco.generate-tool',
                codicon: 'run-all',
                title: 'Generate',
                action: async (_: any) => {
                    const model = await this.graphModelProvider.graphModel;
                    const workspacePath: string = await this.getWorkspaceRoot();
                    const action = GeneratorAction.create(model.id, workspacePath);
                    this.actionDispatcher.dispatch(action);
                    alert('Triggered Generator. Output behaviour not yet implemented.');
                },
                shortcut: ['AltLeft', 'KeyG']
            } as CincoPaletteTools,
            {
                id: '_search'
            }
        ];
        if (!this.model || !hasGeneratorAction(this.model.type)) {
            tools = tools.filter(t => t.id !== 'cinco.generate-tool');
        }
        if (!this.model || !hasValidator(this.model.type)) {
            tools = tools.filter(t => t.id !== 'cinco.validate-tool');
        }
        return tools;
    }

    get actionDispatcher(): IActionDispatcher {
        return this._actionDispatcher;
    }
}
