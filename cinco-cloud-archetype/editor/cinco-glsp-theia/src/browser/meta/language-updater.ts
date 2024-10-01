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
import {
    CONTEXT_MENU_BUTTON_REGISTRATION_COMMAND,
    CONTEXT_MENU_BUTTON_UNREGISTRATION_COMMAND,
    EDITOR_BUTTON_REGISTRATION_COMMAND,
    EDITOR_BUTTON_UNREGISTRATION_COMMAND,
    GenerateGraphDiagramCommand,
    getFileCreationCommandId,
    getFileCreationLabel,
    getGraphTypes,
    hasGeneratorAction,
    LANGUAGE_UPDATE_COMMAND,
    LanguageUpdateMessage,
    MetaSpecification
} from '@cinco-glsp/cinco-glsp-common';
import { CommandContribution, CommandRegistry, MenuAction, SelectionService } from '@theia/core';
import { LabelProvider, OpenerService } from '@theia/core/lib/browser';
import { inject } from '@theia/core/shared/inversify';
import { FileService } from '@theia/filesystem/lib/browser/file-service';
import { WorkspaceService } from '@theia/workspace/lib/browser';
import { injectable } from 'inversify';
import { CincoFileCreationExecuter } from '../utils/cinco-file-creation-executer';

/**
 * This command is fired e.g. from GLSP package to this theia package
 */
@injectable()
export class LanguageUpdater implements CommandContribution {
    @inject(CommandRegistry) protected readonly commandRegistry: CommandRegistry;
    @inject(LabelProvider) protected readonly labelProvider: LabelProvider;
    @inject(FileService) protected readonly fileService: FileService;
    @inject(OpenerService) protected readonly openerService: OpenerService;
    @inject(WorkspaceService) protected readonly workspaceService: WorkspaceService;
    @inject(SelectionService) protected readonly selectionService: SelectionService;

    static REGISTERED_CONTEXT_MENU_ENTRIES: string[] = [];
    static REGISTERED_CREATE_ACTIONS: string[] = [];

    registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(LANGUAGE_UPDATE_COMMAND, {
            execute: (message: LanguageUpdateMessage) => this.updateLanguage(message)
        });
    }

    updateLanguage(message: LanguageUpdateMessage): void {
        MetaSpecification.clear();
        MetaSpecification.merge(message.metaSpecification);
        MetaSpecification.prepareCache();
        this.updateGUIElements();
    }

    updateGUIElements(): void {
        /** Graph Generate button */
        const generateButtonId = GenerateGraphDiagramCommand.id; // only one button exists, that is modified on each update
        const generatableTypes = getGraphTypes(e => hasGeneratorAction(e.elementTypeId));
        const buttonCondition =
            generatableTypes.length > 0 ? generatableTypes.map(t => `cincoGraphModelType == '${t.elementTypeId}'`).join(' || ') : 'false';
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
        const deprecatedCommands = LanguageUpdater.REGISTERED_CREATE_ACTIONS.filter(
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
        LanguageUpdater.REGISTERED_CREATE_ACTIONS = creatableGraphModels.map(g => getFileCreationCommandId(g));

        /** Context Menu Button */
        // unregister all context menu entries
        this.commandRegistry.executeCommand(CONTEXT_MENU_BUTTON_UNREGISTRATION_COMMAND.id, LanguageUpdater.REGISTERED_CONTEXT_MENU_ENTRIES);
        // prepare all context menu entries
        const menuActions = creatableGraphModels.map(
            g =>
                ({
                    commandId: getFileCreationCommandId(g),
                    label: getFileCreationLabel(g),
                    alt: getFileCreationLabel(g),
                    when: 'true'
                }) as MenuAction
        );
        // register all context menu entries
        this.commandRegistry.executeCommand(CONTEXT_MENU_BUTTON_REGISTRATION_COMMAND.id, menuActions);
        // keep track of current context menu entries
        LanguageUpdater.REGISTERED_CONTEXT_MENU_ENTRIES = menuActions.map(a => a.commandId);
    }
}
