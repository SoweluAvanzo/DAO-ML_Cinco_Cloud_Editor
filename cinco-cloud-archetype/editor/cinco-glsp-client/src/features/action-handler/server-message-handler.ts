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
import { ServerDialogAction, ServerDialogResponse, ServerOutputAction } from '@cinco-glsp/cinco-glsp-common';
import { Action, IActionDispatcher, IActionHandler, ILogger, TYPES } from '@eclipse-glsp/client';
import { CommandService } from '@theia/core';
import { OutputChannel } from '@theia/output/src/browser/output-channel';
import { inject, injectable, optional } from 'inversify';

export const CREATE_CHANNEL = { id: 'cinco:create_channel' };
export const SHOW_CHANNEL = { id: 'output:show' };
export const APPEND_LINE = { id: 'output:appendLine' };

@injectable()
export class ServerMessageHandler implements IActionHandler {
    @inject(TYPES.IActionDispatcher)
    protected dispatcher: IActionDispatcher;
    @inject(TYPES.ILogger)
    protected logger: ILogger;
    @inject(CommandService)
    @optional()
    protected commandService: CommandService | undefined;

    handle(action: Action): void {
        // check if the client acts inside a theia
        if (this.commandService) {
            if (ServerOutputAction.is(action)) {
                this.commandService.executeCommand(CREATE_CHANNEL.id, { name: action.name }).then((v: any) => {
                    const outputChannel: OutputChannel = v as OutputChannel;
                    outputChannel.appendLine(action.message);
                    if (action.show) {
                        outputChannel.show();
                    }
                });
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
            } else if (ServerDialogAction.is(action)) {
                // this condition opens a popup dialog
                this.showDialog(action.title, action.message).then(v => {
                    const response = ServerDialogResponse.create(action.messageId, '' + v);
                    this.dispatcher.dispatch(response);
                });
            }
        } else {
            throw new Error('ServerMessage Output for standalone editors is not yet implemented for the CincoGLSPClient.');
        }
    }

    async showDialog(title: string, msg: string): Promise<boolean | undefined> {
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
}
