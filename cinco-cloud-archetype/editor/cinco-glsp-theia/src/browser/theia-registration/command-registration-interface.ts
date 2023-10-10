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
    static registeredCommands = new Map<string, Map<string, GLSP2TheiaCommandRegistrationParameter>>();

    constructor(commands: CommandRegistry) {
        this.commands = commands;
    }

    execute(...args: any[]): any {
        return new Promise<Disposable>(resolve => {
            const param = args[0] as GLSP2TheiaCommandRegistrationParameter;
            const commandId = param.commandId ?? 'undefined';
            const label = param.label ?? commandId;
            const instanceId = param.instanceId;
            if(GLSP2TheiaCommandRegistration.registeredCommands.has(commandId)) {
                // expand registered command and add callback
                const registeredCommands = GLSP2TheiaCommandRegistration.registeredCommands.get(commandId)!;
                // update (overwrite) param for model
                registeredCommands.set(instanceId, param);
                // update command
                this.commands.unregisterCommand(commandId);
                const params = Array.from(registeredCommands.values());
                const disposable = this.commands.registerCommand(
                    {id: commandId, label: label, category: 'Cinco Cloud'  },
                    new GLSP2TheiaCommandHandler(param.commandId, params, param.visible));
                resolve(disposable);
            } else {
                // register new
                GLSP2TheiaCommandRegistration.registeredCommands.set(commandId, new Map());
                GLSP2TheiaCommandRegistration.registeredCommands.get(commandId)!.set(instanceId, param);
                const disposable = this.commands.registerCommand(
                    {id: commandId, label: label, category: 'Cinco Cloud' },
                    new GLSP2TheiaCommandHandler(param.commandId, [param], param.visible));
                resolve(disposable);
            }
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
    instanceId: string;
    callbacks: ((arg: any) => any)[];
    visible?: boolean;
    label?: string;
}

export class GLSP2TheiaCommandHandler implements CommandHandler {
    commandId: string;
    entries: GLSP2TheiaCommandRegistrationParameter[];
    visible: boolean;

    constructor(commandId: string, entries: GLSP2TheiaCommandRegistrationParameter[], visible?: boolean) {
        this.commandId = commandId;
        this.entries = entries;
        this.visible = visible ?? false;
    }

    execute(instanceId?: string, ...args: any[]): any { // filterable for modelIds, types or something else
        const results: any[] = [];
        let toExecute = this.entries;
        if(instanceId) {
            toExecute = toExecute.filter(entry => entry.instanceId === instanceId);
        }
        for(const entry of toExecute) {
            for(const callback of entry.callbacks) {
                try {
                    const result = callback(args);
                    results.push(result);
                } catch(e) {
                    console.log(e);
                }
            }
        }
        return results;
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
