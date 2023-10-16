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

import { CommandHandler, CommandRegistry } from '@theia/core';
import { FrontendApplicationContribution } from '@theia/core/lib/browser';
import { ReactTabBarToolbarItem, TabBarToolbarItem, TabBarToolbarRegistry } from '@theia/core/lib/browser/shell/tab-bar-toolbar';
import { injectable, inject } from 'inversify';
import { EDITOR_BUTTON_REGISTRATION_COMMAND, EDITOR_BUTTON_UNREGISTRATION_COMMAND } from '@cinco-glsp/cinco-glsp-common';

@injectable()
export class CincoEditorButtonConfigurator implements FrontendApplicationContribution {
    @inject(TabBarToolbarRegistry) private readonly tabBarToolbar: TabBarToolbarRegistry;
    @inject(CommandRegistry) private readonly commands: CommandRegistry;

    initialize(): void {
        this.commands.registerCommand(EDITOR_BUTTON_REGISTRATION_COMMAND, new CincoEditorButtonRegistrator(this.tabBarToolbar));
        this.commands.registerCommand(EDITOR_BUTTON_UNREGISTRATION_COMMAND, new CincoEditorButtonUnRegistrator(this.tabBarToolbar));
    }
}

class CincoEditorButtonRegistrator implements CommandHandler {
    private readonly tabBarToolbar: TabBarToolbarRegistry;
    constructor(tabBarToolbar: TabBarToolbarRegistry) {
        this.tabBarToolbar = tabBarToolbar;
    }

    execute(buttons: (TabBarToolbarItem | ReactTabBarToolbarItem)[]): void {
        for (const button of buttons) {
            registerEditorButton(this.tabBarToolbar, button);
        }
    }
}

class CincoEditorButtonUnRegistrator implements CommandHandler {
    private readonly tabBarToolbar: TabBarToolbarRegistry;
    constructor(tabBarToolbar: TabBarToolbarRegistry) {
        this.tabBarToolbar = tabBarToolbar;
    }

    execute(buttonIds: string[]): void {
        for (const buttonId of buttonIds) {
            unregisterEditorButtons(this.tabBarToolbar, buttonId);
        }
    }
}

function registerEditorButton(tabBarToolbar: TabBarToolbarRegistry, button: TabBarToolbarItem | ReactTabBarToolbarItem): void {
    button.group = button.group ?? 'navigation';
    button.when = button.when ?? 'true';
    tabBarToolbar.registerItem(button);
}

function unregisterEditorButtons(tabBarToolbar: TabBarToolbarRegistry, buttonId: string): void {
    tabBarToolbar.unregisterItem(buttonId);
}
