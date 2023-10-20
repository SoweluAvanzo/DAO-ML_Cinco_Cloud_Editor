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
import { updateMetaSpecification } from '../../common/cinco-language';
import {
    getFileCreationLabel,
    getFileCreationCommandId,
    CONTEXT_MENU_BUTTON_REGISTRATION_COMMAND,
    CONTEXT_MENU_BUTTON_UNREGISTRATION_COMMAND,
    EDITOR_BUTTON_REGISTRATION_COMMAND,
    EDITOR_BUTTON_UNREGISTRATION_COMMAND,
    GenerateGraphDiagramCommand,
    MetaSpecificationReloadAction,
    MetaSpecificationResponseAction,
    getGraphTypes,
    hasGeneratorAction
} from '@cinco-glsp/cinco-glsp-common';
import { CommandHandler, CommandRegistry, MenuAction, SelectionService } from '@theia/core';
import { Action, GLSPClient, ActionMessage } from '@eclipse-glsp/protocol';
import { WorkspaceService } from '@theia/workspace/lib/browser';
import { FileService } from '@theia/filesystem/lib/browser/file-service';
import { LabelProvider, OpenerService } from '@theia/core/lib/browser';
import { CincoGLSPClientContribution } from '../cinco-glsp-client-contribution';
import { CincoFileCreationExecuter } from '../utils/cinco-file-creation-executer';

let REGISTERED_CONTEXT_MENU_ENTRIES: string[] = [];
let REGISTERED_CREATE_ACTIONS: string[] = [];

export class MetaSpecificationReloadCommandHandler implements CommandHandler {
    // This handling process overwrites the glsp-clients actionMessage callback
    // This callback should be used is used to propagate the actionMessage again to the glsp package
    protected readonly callback: (m: ActionMessage) => Promise<void>;
    protected readonly client: GLSPClient;
    protected readonly commandRegistry: CommandRegistry;
    protected readonly labelProvider: LabelProvider;
    protected readonly fileService: FileService;
    protected readonly openerService: OpenerService;
    protected readonly workspaceService: WorkspaceService;
    protected readonly selectionService: SelectionService;

    constructor(
        client: GLSPClient,
        commandRegistry: CommandRegistry,
        labelProvider: LabelProvider,
        workspaceService: WorkspaceService,
        selectionService: SelectionService,
        fileService: FileService,
        openerService: OpenerService,
        callback: (m: ActionMessage) => Promise<void>
    ) {
        this.client = client;
        this.callback = callback;
        this.commandRegistry = commandRegistry;
        this.labelProvider = labelProvider;
        this.workspaceService = workspaceService;
        this.selectionService = selectionService;
        this.fileService = fileService;
        this.openerService = openerService;
    }

    execute(): void {
        // request & reload metaspecification
        this.sendGLSPSystemAction(this.client, MetaSpecificationReloadAction.create([], true), (response: ActionMessage) => {
            this.callback(response).then(_ => {
                // handle only MetaSpecificationResponseAction
                if (response.action && response.action.kind === MetaSpecificationResponseAction.KIND) {
                    const metaSpecificationResponseAction = response.action as MetaSpecificationResponseAction;
                    const metaSpecification = metaSpecificationResponseAction.metaSpecification;
                    updateMetaSpecification(metaSpecification);
                    // update editor buttons
                    this.updateGUIElements();
                }
            });
        });
    }

    sendGLSPSystemAction(client: GLSPClient, action: Action, callback?: (e: any) => void): void {
        client.sendActionMessage({
            clientId: CincoGLSPClientContribution.SYSTEM_ID,
            action: action
        });
        if (callback) {
            client.onActionMessage(response => {
                callback(response);
            });
        }
    }

    updateGUIElements(): void {
        /** Graph Generate button */
        const generateButtonId = GenerateGraphDiagramCommand.id; // only one button exists, that is modified on each update
        const generatableTypes = getGraphTypes(e => hasGeneratorAction(e.elementTypeId));
        const buttonCondition = generatableTypes.length > 0 ?
            generatableTypes.map(t => `cincoGraphModelType == '${t.elementTypeId}'`).join(' || ')
            : 'false';
        this.commandRegistry.executeCommand(EDITOR_BUTTON_UNREGISTRATION_COMMAND.id, [generateButtonId]).then(() => {
            console.log('Updating generate button condition: ' + buttonCondition);
            this.commandRegistry.executeCommand(EDITOR_BUTTON_REGISTRATION_COMMAND.id, [
                {
                    id: generateButtonId,
                    command: GenerateGraphDiagramCommand.id,
                    when: buttonCondition
                }
            ]);
        });

        /** Create File Creation Commands */
        const creatableGraphModels = getGraphTypes(); // TODO: transient graphmodels not yet implemented
        // unregister deprecated commands
        const deprecatedCommands = REGISTERED_CREATE_ACTIONS.filter(
            a => creatableGraphModels.map(g => getFileCreationCommandId(g)).indexOf(a) < 0 // true if it does not exist anymore
        );
        deprecatedCommands.forEach(a => this.commandRegistry.unregisterCommand(a));
        // register and update all current actions
        for (const g of creatableGraphModels) {
            const fileCreationCommandId = getFileCreationCommandId(g);
            // if already exists ...
            if (this.commandRegistry.commandIds.indexOf(fileCreationCommandId) >= 0) {
                // ...unregister to update command
                this.commandRegistry.unregisterCommand(fileCreationCommandId);
            }
            // register new command
            this.commandRegistry.registerCommand(
                { id: fileCreationCommandId },
                new CincoFileCreationExecuter(
                    g.label,
                    g.diagramExtension,
                    this.labelProvider,
                    this.workspaceService,
                    this.selectionService,
                    this.fileService,
                    this.openerService
                )
            );
        }
        // keep track of current actions
        REGISTERED_CREATE_ACTIONS = creatableGraphModels.map(g => getFileCreationCommandId(g));

        /** Context Menu Button */
        // unregister all context menu entries
        this.commandRegistry.executeCommand(CONTEXT_MENU_BUTTON_UNREGISTRATION_COMMAND.id, REGISTERED_CONTEXT_MENU_ENTRIES);
        // prepare all context menu entries
        const menuActions = creatableGraphModels.map(
            g =>
                ({
                    commandId: getFileCreationCommandId(g),
                    label: getFileCreationLabel(g),
                    alt: getFileCreationLabel(g),
                    when: 'true'
                } as MenuAction)
        );
        // register all context menu entries
        this.commandRegistry.executeCommand(CONTEXT_MENU_BUTTON_REGISTRATION_COMMAND.id, menuActions);
        // keep track of current context menu entries
        REGISTERED_CONTEXT_MENU_ENTRIES = menuActions.map(a => a.commandId);
    }
}
