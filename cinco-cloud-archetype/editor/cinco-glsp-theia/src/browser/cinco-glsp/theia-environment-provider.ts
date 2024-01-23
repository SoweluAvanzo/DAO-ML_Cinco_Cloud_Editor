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
    LANGUAGE_UPDATE_COMMAND,
    MetaSpecificationReloadAction,
    MetaSpecificationReloadCommand,
    PropertyViewResponseAction,
    PropertyViewUpdateCommand,
    ServerDialogAction,
    ServerDialogResponse,
    ServerOutputAction,
    ValidationRequestAction
} from '@cinco-glsp/cinco-glsp-common';
import { CommandService } from '@theia/core';
import URI from '@theia/core/lib/common/uri';
import { OutputChannel } from '@theia/output/src/browser/output-channel';
import { DefaultEnvironmentProvider } from '@cinco-glsp/cinco-glsp-client';
import { GraphModelProvider } from '@cinco-glsp/cinco-glsp-client/lib/model/graph-model-provider';
import { GLSP2TheiaCommandRegistration } from '../theia-registration/command-registration-interface';

@injectable()
export class TheiaEnvironmentProvider extends DefaultEnvironmentProvider {
    CREATE_CHANNEL = { id: 'cinco:create_channel' };
    SHOW_CHANNEL = { id: 'output:show' };
    APPEND_LINE = { id: 'output:appendLine' };

    @inject(CommandService) protected readonly commandService: CommandService;
    @inject(GraphModelProvider) protected readonly graphModelProvider: GraphModelProvider;

    override async getWorkspaceRoot(): Promise<string> {
        const theiaRoots: URI[] = (await this.commandService.executeCommand('workspaceRootProviderHandler')) ?? [];
        for (const theiaRoot of theiaRoots) {
            return theiaRoot.path.fsPath();
        }
        return super.getWorkspaceRoot();
    }

    /**
     * Used to register commands for a model
     */
    override async postRequestModel(): Promise<void> {
        this.logger.log(this, 'Environment Provider loading - Theia');

        // register meta-specification-reload for model
        const model = await this.graphModelProvider.graphModel;
        const filePath = model.id; // TODO: put workspace-file-path here
        this.commandService.executeCommand(GLSP2TheiaCommandRegistration.ID, {
            commandId: MetaSpecificationReloadCommand.ID + '.' + model.id,
            instanceId: model.id,
            visible: true,
            label: 'Reload Meta-Specification current model (' + filePath + ')',
            callbacks: [
                () => {
                    this.actionDispatcher.dispatch(MetaSpecificationReloadAction.create([], true));
                }
            ]
        });

        // register model-validation-request command
        this.commandService.executeCommand(GLSP2TheiaCommandRegistration.ID, {
            commandId: 'validationRequestModel',
            instanceId: model.id,
            callbacks: [
                async () => {
                    // send validation request
                    const action = ValidationRequestAction.create(model.id);
                    const response = await this.actionDispatcher.request(action);
                    this.commandService.executeCommand('CincoCloud.updateValidationModel', response.messages);
                }
            ]
        });
    }

    override handleLogging(action: ServerOutputAction): void {
        this.commandService.executeCommand(this.CREATE_CHANNEL.id, { name: action.name }).then((v: any) => {
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
        const { ConfirmDialog } = await import('@theia/core/lib/browser');
        return new ConfirmDialog({ title, msg: wrappedMsg }).open();
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
        this.commandService.executeCommand(
            PropertyViewUpdateCommand.id,
            action.modelElementIndex,
            action.modelType,
            action.modelElementId,
            action.attributeDefinitions,
            action.customTypeDefinitions,
            action.values
        );
    }

    override propagateMetaspecification(metaSpec: CompositionSpecification): void | Promise<void> {
        this.commandService.executeCommand(LANGUAGE_UPDATE_COMMAND.id, {
            metaSpecification: metaSpec
        });
    }
}
