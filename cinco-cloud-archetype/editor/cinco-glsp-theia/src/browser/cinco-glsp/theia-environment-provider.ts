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
    CompositionSpecification,
    GenerateGraphDiagramCommand,
    GeneratorAction,
    LANGUAGE_UPDATE_COMMAND,
    MetaSpecificationReloadAction,
    MetaSpecificationReloadCommand,
    PropertyViewResponseAction,
    PropertyViewUpdateCommand,
    ServerDialogAction,
    ServerDialogResponse,
    ServerOutputAction,
    ValidationRequestAction,
    hasGeneratorAction,
    hasValidator
} from '@cinco-glsp/cinco-glsp-common';
import { CommandRegistry } from '@theia/core';
import URI from '@theia/core/lib/common/uri';
import { OutputChannel } from '@theia/output/src/browser/output-channel';
import { DefaultEnvironmentProvider, CincoPaletteTools } from '@cinco-glsp/cinco-glsp-client';
import { GLSP2TheiaCommandRegistration } from '../theia-registration/command-registration-interface';
import { ValidationRequestCommandID } from '../validation-widget/validation-widget-contribution';
import { WorkspaceRootProviderHandler } from '../theia-registration/file-provider';
import { ApplicationShell, ConfirmDialog } from '@theia/core/lib/browser';
import { ThemeService } from '@theia/core/lib/browser/theming';
import { WorkspaceService } from '@theia/workspace/lib/browser';
import { CincoGLSPDiagramMananger } from '../diagram/cinco-glsp-diagram-manager';
import { CommandAction } from '../../../../cinco-glsp-common/lib/protocol/server-message-protocol';
import { CincoGLSPDiagramWidget } from '../diagram/cinco-glsp-diagram-widget';
import { CincoGraphModel } from '@cinco-glsp/cinco-glsp-client/lib/model/model';

@injectable()
export class TheiaEnvironmentProvider extends DefaultEnvironmentProvider {
    CREATE_CHANNEL = { id: 'cinco:create_channel' };
    SHOW_CHANNEL = { id: 'output:show' };
    APPEND_LINE = { id: 'output:appendLine' };

    @inject(CommandRegistry) protected readonly commandRegistry: CommandRegistry;
    @inject(ThemeService) protected readonly themeService: ThemeService;
    @inject(WorkspaceService) protected readonly workspaceService: WorkspaceService;
    @inject(CincoGLSPDiagramMananger) diagramManager: CincoGLSPDiagramMananger;
    @inject(ApplicationShell) shell: ApplicationShell;

    get currentSourceURI(): string | undefined {
        const resourceUri = this.diagramManager.currentURI?.path.fsPath();
        return new URI(resourceUri).path.fsPath();
    }

    override async getWorkspaceRoot(): Promise<string> {
        const theiaRoots: URI[] = (await this.commandRegistry.executeCommand(WorkspaceRootProviderHandler.ID)) ?? [];
        for (const theiaRoot of theiaRoots) {
            return theiaRoot.path.fsPath();
        }
        return super.getWorkspaceRoot();
    }

    override async preInitialize(): Promise<void> {}

    override async preRequestModel?(): Promise<void> {}

    override async postModelInitialization?(): Promise<void> {}
    /**
     * Used to register commands for a model
     */
    override async postRequestModel(): Promise<void> {
        this.logger.log(this, 'Environment Provider loading - Theia');
        const sourceUri = this.currentSourceURI;
        this.activeModel = this.graphModelProvider.getGraphModelFrom(
            sourceUri ?? ''
        );

        // register meta-specification-reload for model
        const model = this.getActiveModel();
        if(model) {
            this.commandRegistry.executeCommand(GLSP2TheiaCommandRegistration.ID, {
                commandId: MetaSpecificationReloadCommand.ID + '.' + model.id,
                instanceId: model.id,
                visible: true,
                label: 'Reload Meta-Specification current model (' + sourceUri + ')',
                callbacks: [
                    () => {
                        this.actionDispatcher.dispatch(MetaSpecificationReloadAction.create([], true));
                    }
                ]
            });

            // register model-validation-request command
            this.commandRegistry.executeCommand(GLSP2TheiaCommandRegistration.ID, {
                commandId: ValidationRequestCommandID,
                instanceId: model.id,
                callbacks: [
                    async () => {
                        // send validation request
                        const action = ValidationRequestAction.create(model.id);
                        const response = await this.actionDispatcher.request(action);
                        this.commandRegistry.executeCommand('CincoCloud.updateValidationModel', response.messages);
                    }
                ]
            });
        }

        // add update for changed active graphmodel
        this.shell.onDidChangeActiveWidget(change => {
            const newWidget = change.newValue;
            if (newWidget instanceof CincoGLSPDiagramWidget) {
                const activeModelSourceUri = newWidget.modelSource.sourceUri;
                if(activeModelSourceUri) {
                    this.activeModel = this.graphModelProvider.getGraphModelFrom(activeModelSourceUri);
                } else {
                    this.activeModel = undefined;
                }
            }
        });

        // TODO: SAMI - This generatorButton causes artifacts in the theia GUI and is currently turned off
        // Also this is deprecated, as the palette should handle the generator button as of GLSP 2.0
        // register generate command
        /*
        this.registerGeneratorCommand();
        this.themeService.onDidColorThemeChange(e => {
            this.registerGeneratorCommand();
        });
        */
    }

    /**
     * @deprecated
     */
    protected registerGeneratorCommand(): void {
        // register generate command
        const theme = this.themeService.getCurrentTheme();
        const iconClass = theme.type === 'light' ? GenerateGraphDiagramCommand.lightIconClass : GenerateGraphDiagramCommand.darkIconClass;
        const commandIds = this.commandRegistry.commands.map(c => c.id);
        if (commandIds.indexOf(GenerateGraphDiagramCommand.id) >= 0) {
            this.commandRegistry.unregisterCommand(GenerateGraphDiagramCommand);
        }
        const workspaceService = this.workspaceService;
        const actionDispatcher = this.actionDispatcher;
        const graphModelProvider = this.graphModelProvider;
        this.commandRegistry.registerCommand(
            {
                id: GenerateGraphDiagramCommand.id,
                category: GenerateGraphDiagramCommand.category,
                label: GenerateGraphDiagramCommand.label,
                iconClass: iconClass ?? GenerateGraphDiagramCommand.id
            },
            {
                async execute(): Promise<void> {
                    const roots = workspaceService.tryGetRoots();
                    if (roots.length <= 0) {
                        throw Error('No workspace root found. Make sure a workspace is opened.');
                    }
                    const model = await graphModelProvider.graphModel;
                    const workspacePath: string = roots[0].resource.path.fsPath();
                    const action = GeneratorAction.create(model.id, workspacePath);
                    actionDispatcher.dispatch(action);
                }
            }
        );
    }

    override handleLogging(action: ServerOutputAction): void {
        this.commandRegistry.executeCommand(this.CREATE_CHANNEL.id, { name: action.name }).then((v: any) => {
            const outputChannel: OutputChannel = v as OutputChannel;
            outputChannel.appendLine(action.message);
            if (action.show) {
                outputChannel.show();
            }
        });
        super.handleLogging(action);
    }

    override showDialog(action: ServerDialogAction): void {
        // this condition opens a popup dialog
        this.showDialogInTheia(action.title, action.message).then(v => {
            const response = ServerDialogResponse.create(action.messageId, '' + v);
            this.actionDispatcher.dispatch(response);
        });
    }

    async showDialogInTheia(title: string, msg: string): Promise<boolean | undefined> {
        const wrappedMsg = this.wrapMessage(msg);
        return new ConfirmDialog({ title, msg: wrappedMsg }).open();
    }

    override async handleCommand(command: CommandAction): Promise<void> {
        return this.commandRegistry.executeCommand(command.commandId, command.args);
    }

    wrapMessage(msg: string): HTMLDivElement {
        const scrollDiv = document.createElement('div');
        scrollDiv.className = 'scroll-div';
        const pre = document.createElement('pre');
        pre.textContent = msg;
        scrollDiv.appendChild(pre);
        return scrollDiv;
    }

    override provideProperties(action: PropertyViewResponseAction): void | Promise<void> {
        this.commandRegistry.executeCommand(
            PropertyViewUpdateCommand.id,
            action.modelElementIndex,
            action.modelType,
            action.modelElementId,
            action.attributeDefinitions,
            action.values
        );
    }

    override propagateMetaspecification(metaSpec: CompositionSpecification): void | Promise<void> {
        this.commandRegistry.executeCommand(LANGUAGE_UPDATE_COMMAND.id, {
            metaSpecification: metaSpec
        });
    }

    override provideTools(model: CincoGraphModel): CincoPaletteTools[] {
        const tools = [
            {
                id: '_default'
            },
            {
                id: '_delete'
            }
        ];
        if(model) {
            if (hasValidator(model.type)) {
                tools.push(
                    {
                        id: 'cinco.validate-tool',
                        codicon: 'pass',
                        title: 'Validate model',
                        action: async (_: any) => {
                            if(!model) {
                                return;
                            }
                            const action = ValidationRequestAction.create(model.id);
                            const validationResponse = await this.actionDispatcher.request(action);
                            this.commandRegistry.executeCommand('CincoCloud.updateValidationModel', validationResponse.messages);
                        },
                        shortcut: ['AltLeft', 'KeyV']
                    } as CincoPaletteTools
                );
            }
            if (hasGeneratorAction(model.type)) {
                tools.push(
                    {
                        id: 'cinco.generate-tool',
                        codicon: 'run-all',
                        title: 'Generate',
                        action: async (_: any) => {
                            if(!model) {
                                return;
                            }
                            const workspacePath: string = await this.getWorkspaceRoot();
                            const action = GeneratorAction.create(model.id, workspacePath);
                            this.actionDispatcher.dispatch(action);
                        },
                        shortcut: ['AltLeft', 'KeyG']
                    } as CincoPaletteTools
                );
            }
        }
        // right-most element
        tools.push(
            {
                id: '_search'
            }
        );

        return tools;
    }
}

