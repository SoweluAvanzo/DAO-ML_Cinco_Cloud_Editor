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
import { CommandContribution, CommandHandler, CommandRegistry } from '@theia/core';
import { Disposable } from '@theia/core/shared/vscode-languageserver-protocol';
import { injectable } from 'inversify';

/**
 * These classes are used as basis to serve a command to register commands from the cinco-glsp-client-context
 */

@injectable()
export class GLSP2TheiaCommandRegistrationContribution implements CommandContribution {
    registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(
            { id: GLSP2TheiaCommandRegistration.ID, label: 'glsp2theia command registration' },
            new GLSP2TheiaCommandRegistration(commands)
        );
    }
}

export class GLSP2TheiaCommandRegistration implements CommandHandler {
    static ID = 'registerFromGLSP2Theia';
    commands: CommandRegistry;

    constructor(commands: CommandRegistry) {
        this.commands = commands;
    }

    execute(...args: any[]): any {
        return new Promise<Disposable>(resolve => {
            const param = args[0] as GLSP2TheiaCommandRegistrationParameter;
            const commandId = param.commandId;
            const disposable = this.commands.registerCommand({ id: commandId }, new GLSP2TheiaCommandHandler(param));
            resolve(disposable);
        });
    }

    isEnabled?(...args: any[]): boolean {
        return true;
    }

    isVisible?(...args: any[]): boolean {
        return false;
    }

    isToggled?(...args: any[]): boolean {
        return true;
    }
}

export interface GLSP2TheiaCommandRegistrationParameter {
    commandId: string;
    callback: (arg: any) => any;
    visible?: boolean;
}

export class GLSP2TheiaCommandHandler implements CommandHandler {
    commandId: string;
    callback: (arg: any) => any;
    visible: boolean;

    constructor(config: GLSP2TheiaCommandRegistrationParameter) {
        this.callback = config.callback;
        this.commandId = config.commandId;
        this.visible = config.visible ?? false;
    }

    execute(...args: any[]): any {
        return this.callback(args);
    }

    isEnabled?(...args: any[]): boolean {
        return true;
    }

    isVisible?(...args: any[]): boolean {
        return this.visible;
    }

    isToggled?(...args: any[]): boolean {
        return true;
    }
}
