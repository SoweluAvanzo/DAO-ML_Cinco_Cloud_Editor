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

import { CommandHandler, CommandRegistry, MenuAction, MenuModelRegistry, SelectionService, SubMenuOptions, nls } from '@theia/core';
import { FrontendApplicationContribution, LabelProvider, OpenerService } from '@theia/core/lib/browser';
import { injectable, inject } from 'inversify';
import {
    CONTEXT_MENU_BUTTON_REGISTRATION_COMMAND,
    CONTEXT_MENU_BUTTON_UNREGISTRATION_COMMAND,
    CREATE_NEW_MODEL_FILE_ID
} from '@cinco-glsp/cinco-glsp-common';
import { CincoFileCreationExecuter } from '../utils/cinco-file-creation-executer';
import { FileService } from '@theia/filesystem/lib/browser/file-service';
import { WorkspaceService } from '@theia/workspace/lib/browser';
import { EnvVariablesServer } from '@theia/core/lib/common/env-variables';

export const CINCO_CONTEXT_MENU_PATH = ['navigator-context-menu', 'navigation', 'cinco-cloud'];
let REGISTERED_BUTTONS: string[] = [];

@injectable()
export class CincoContextMenuButtonConfigurator implements FrontendApplicationContribution {
    @inject(CommandRegistry) private readonly commandRegistry: CommandRegistry;
    @inject(MenuModelRegistry) private readonly menuModelRegistry: MenuModelRegistry;
    @inject(LabelProvider) protected readonly labelProvider: LabelProvider;
    @inject(FileService) protected readonly fileService: FileService;
    @inject(OpenerService) protected readonly openerService: OpenerService;
    @inject(SelectionService) protected readonly selectionService: SelectionService;
    @inject(WorkspaceService) protected readonly workspaceService: WorkspaceService;
    @inject(EnvVariablesServer) protected readonly envVariableServer: EnvVariablesServer;

    initialize(): void {
        this.menuModelRegistry.registerSubmenu(CINCO_CONTEXT_MENU_PATH, nls.localizeByDefault('New Model...'), {
            order: 'zzz'
        } as SubMenuOptions);
        this.commandRegistry.registerCommand(
            CONTEXT_MENU_BUTTON_REGISTRATION_COMMAND,
            new CincoContextMenuButtonRegistrator(this.menuModelRegistry)
        );
        this.commandRegistry.registerCommand(
            CONTEXT_MENU_BUTTON_UNREGISTRATION_COMMAND,
            new CincoContextMenuButtonUnRegistrator(this.menuModelRegistry)
        );

        this.commandRegistry.executeCommand(CONTEXT_MENU_BUTTON_REGISTRATION_COMMAND.id, []);

        // register new command for cinco languages
        this.envVariableServer.getVariables().then(vars => {
            // don't register mgl and style creation if the editor is a model editor
            const isModelEditor = vars.filter(e => e.name === 'EDITOR_TYPE' && e.value === 'MODEL_EDITOR').length > 0;
            const isLanguageEditor = vars.filter(e => e.name === 'EDITOR_TYPE' && e.value === 'LANGUAGE_EDITOR').length > 0;
            if (!isModelEditor && isLanguageEditor) {
                this.registerCincoLanguageEntries();
            }
        });
    }

    registerCincoLanguageEntries(): void {
        this.commandRegistry.registerCommand(
            { id: CREATE_NEW_MODEL_FILE_ID + '.mgl' },
            new CincoFileCreationExecuter(
                'MGL',
                'mgl',
                this.labelProvider,
                this.workspaceService,
                this.selectionService,
                this.fileService,
                this.openerService
            )
        );
        this.commandRegistry.registerCommand(
            { id: CREATE_NEW_MODEL_FILE_ID + '.style' },
            new CincoFileCreationExecuter(
                'Style',
                'style',
                this.labelProvider,
                this.workspaceService,
                this.selectionService,
                this.fileService,
                this.openerService
            )
        );
        // prepare all context menu entries
        const menuActions = [
            {
                commandId: CREATE_NEW_MODEL_FILE_ID + '.mgl',
                label: 'Create MGL (*.mgl)',
                alt: 'Create MGL (*.mgl)',
                when: 'true',
                order: 'aaaa' // put first
            },
            {
                commandId: CREATE_NEW_MODEL_FILE_ID + '.style',
                label: 'Create Style/MSL (*.style)',
                alt: 'Create Style/MSL (*.style)',
                when: 'true',
                order: 'aaab' // put second
            }
        ];
        // register all context menu entries
        this.commandRegistry.executeCommand(CONTEXT_MENU_BUTTON_REGISTRATION_COMMAND.id, menuActions);
    }
}

class CincoContextMenuButtonRegistrator implements CommandHandler {
    private menuModelRegistry: MenuModelRegistry;

    constructor(menuModelRegistry: MenuModelRegistry) {
        this.menuModelRegistry = menuModelRegistry;
    }

    execute(menuActions: MenuAction[]): void {
        for (const menuAction of menuActions) {
            registerMenuEntry(this.menuModelRegistry, menuAction);
        }
    }
}

class CincoContextMenuButtonUnRegistrator implements CommandHandler {
    private menuModelRegistry: MenuModelRegistry;

    constructor(menuModelRegistry: MenuModelRegistry) {
        this.menuModelRegistry = menuModelRegistry;
    }

    execute(commandIds?: string[], all = false): void {
        if ((!commandIds || commandIds.length <= 0) && all) {
            for (const registeredCommand of REGISTERED_BUTTONS) {
                unregisterMenuEntry(this.menuModelRegistry, registeredCommand);
            }
        } else {
            for (const id of commandIds ?? []) {
                unregisterMenuEntry(this.menuModelRegistry, id);
            }
        }
    }
}

function registerMenuEntry(menuModelRegistry: MenuModelRegistry, menuAction: MenuAction): void {
    menuModelRegistry.registerMenuAction(CINCO_CONTEXT_MENU_PATH, menuAction);
    // keep track of registered ids
    REGISTERED_BUTTONS.push(menuAction.commandId);
}

function unregisterMenuEntry(menuModelRegistry: MenuModelRegistry, commandId: string): void {
    menuModelRegistry.unregisterMenuAction(commandId, CINCO_CONTEXT_MENU_PATH);
    // keep track of registered ids
    REGISTERED_BUTTONS = REGISTERED_BUTTONS.filter(b => b !== commandId);
}
