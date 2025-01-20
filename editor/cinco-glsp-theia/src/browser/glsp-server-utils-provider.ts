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

import { ARGS_PROVIDER_ID } from '@cinco-glsp/cinco-glsp-common';
import { CommandContribution, CommandRegistry, MessageService } from '@theia/core';
import { injectable, inject } from 'inversify';
import { GLSPServerUtilServer } from '../common/glsp-server-util-protocol';

@injectable()
export class GLSPServerUtilsProvider implements CommandContribution {
    @inject(MessageService) messageService: MessageService;
    @inject(GLSPServerUtilServer) glspServerUtilServer: GLSPServerUtilServer;

    registerCommands(commands: CommandRegistry): void {
        // intialize connection
        this.glspServerUtilServer
            .connect()
            .then(_ => {
                console.log('*** glsp server util connected ***');
                commands.registerCommand(
                    { id: ARGS_PROVIDER_ID },
                    {
                        execute: () => this.glspServerUtilServer.getArgs()
                    }
                );
                commands.registerCommand(
                    { id: 'cinco-cloud.glsp.transpile', label: 'transpile languages-folder in workspace', category: 'Cinco Cloud' },
                    {
                        execute: () => {
                            console.log('triggered transpilation on languages-folder in workspace...');
                            return this.glspServerUtilServer.transpileLanguagesFolder();
                        },
                        isVisible: () => true,
                        isEnabled: () => true
                    }
                );
                this.registerWatchMode(commands);
                /*
                 * Use command like this (result: ServerArgs):
                 *
                 * commands.executeCommand(ARGS_PROVIDER_ID).then(result => {
                 *    console.log(result.metaDevMode);
                 *    console.log(result.rootFolder);
                 *    console.log(result.languagePath);
                 *    console.log(result.workspacePath);
                 *    console.log(result.port);
                 * });
                 */
            })
            .catch(error => console.log('*** Failed to start server utils: "' + error + '" ***'));
    }

    async registerWatchMode(commands: CommandRegistry): Promise<void> {
        const id = 'cinco-cloud.glsp.transpile-watch';
        const watchModeIsRunning = await this.glspServerUtilServer.transpilationIsRunning();
        const getLabel = (): string =>
            'toggle watchmode transpilation on languages-folder' + (watchModeIsRunning ? ' (Running)' : ' (Stopped)');
        commands.registerCommand(
            {
                id: id,
                label: getLabel(),
                category: 'Cinco Cloud'
            },
            {
                execute: () => {
                    console.log('toggled transpilation in watchmode on languages-folder in workspace...');
                    const result = this.glspServerUtilServer.transpileWatchLanguagesFolder();
                    result.then(starting => {
                        if (starting) {
                            this.messageService.info('started transpilation');
                        } else {
                            if (starting !== undefined) {
                                this.messageService.info('stopped transpilation');
                            } else {
                                this.messageService.info('something went wrong');
                            }
                        }
                        // commands.unregisterCommand(commands.getCommand(id)!);
                        // this.registerCommands(commands);
                        commands.getCommand(id)!.label = getLabel();
                    });
                    return result;
                },
                isVisible: () => true,
                isEnabled: () => true
            }
        );
    }
}
