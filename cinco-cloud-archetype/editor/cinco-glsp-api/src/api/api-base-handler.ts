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

import { ElementType, getSpecOf, ServerDialogAction, ServerDialogResponse, ServerOutputAction } from '@cinco-glsp/cinco-glsp-common';
import { ActionDispatcher, Logger, LogLevel, ServerMessageAction, ServerSeverity } from '@eclipse-glsp/server-node';
import { ModelElement } from '../model/graph-model';
import { GraphModelState } from '../model/graph-model-state';
import { ServerResponseHandler } from '../tools/server-dialog-response-handler';
import { getWorkspaceRootUri } from '../utils/file-helper';
import * as path from 'path';
import * as fileHelper from '../utils/file-helper';

export abstract class APIBaseHandler {
    protected readonly logger: Logger;
    readonly modelState: GraphModelState;
    protected readonly actionDispatcher: ActionDispatcher;
    CHANNEL_NAME: string | undefined;

    constructor(logger: Logger, modelState: GraphModelState, actionDispatcher: ActionDispatcher) {
        this.logger = logger;
        this.modelState = modelState;
        this.actionDispatcher = actionDispatcher;
    }

    getElement(modelElementId: string): ModelElement {
        const element = this.modelState.index.findElement(modelElementId) as ModelElement;
        return element;
    }

    getSpecification(type: string): ElementType | undefined {
        return getSpecOf(type);
    }

    /**
     * The ChannelName where the logging will be provided, will be named in the following precedence:
     *
     *      options.channelName ?? this.CHANNEL_NAME ?? this.logger.caller?.toString() ?? 'unnamed'
     * @param message
     * @param options
     */
    log(message: string, options?: { channelName?: string; show?: boolean; logLevel?: LogLevel }): void {
        switch (options?.logLevel) {
            case LogLevel.debug:
                this.logger.debug(message);
                break;
            case LogLevel.error:
                this.logger.error(message);
                break;
            case LogLevel.info:
                this.logger.info(message);
                break;
            case LogLevel.warn:
                this.logger.warn(message);
                break;
            default:
                this.logger.info(message);
                break;
        }
        const channelName = options?.channelName ?? this.CHANNEL_NAME ?? this.logger.caller?.toString() ?? 'unnamed';
        const o = {
            show: options?.show ?? false,
            logLevel: options?.logLevel?.toString() ?? LogLevel.info.toString()
        };
        const serverOutputAction = ServerOutputAction.create(channelName, message, o);
        this.actionDispatcher.dispatch(serverOutputAction);
    }

    /**
     *
     * @param message
     * @param severity "NONE" | "INFO" | "WARNING" | "ERROR" | "FATAL" | "OK"
     * @returns
     */
    notify(message: string, severity?: ServerSeverity, details?: string, timeout?: number): void {
        const serverMessageAction = ServerMessageAction.create(message, {
            severity: severity ?? 'INFO',
            details: details ?? '',
            timeout: timeout ?? 5000
        });
        this.actionDispatcher.dispatch(serverMessageAction);
    }

    dialog(title: string, message: string): Promise<string> {
        return new Promise((resolve, reject) => {
            const callback: (response: any) => void = (response: ServerDialogResponse) => resolve(response.result);
            const messageId = ServerResponseHandler.registerResponseHandling(callback);
            const serverDialog = ServerDialogAction.create(messageId, title, message);
            this.actionDispatcher.dispatch(serverDialog);
        });
    }

    readFile(relativePath: string, encoding?: string): string | undefined {
        const targetPath = path.join(getWorkspaceRootUri(), relativePath);
        return fileHelper.readFile(targetPath, encoding);
    }

    createFile(relativePath: string, content: string, encoding?: string): void {
        const targetPath = path.join(getWorkspaceRootUri(), relativePath);
        fileHelper.writeFile(targetPath, content, encoding);
    }
}
