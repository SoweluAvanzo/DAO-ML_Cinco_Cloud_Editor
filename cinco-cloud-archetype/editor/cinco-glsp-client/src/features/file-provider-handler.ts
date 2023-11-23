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
import { FileProviderRequest, FileProviderResponse, FileProviderResponseItem } from '@cinco-glsp/cinco-glsp-common';
import { Action, IActionDispatcher, IActionHandler, ICommand, TYPES } from '@eclipse-glsp/client';
import { injectable, inject } from 'inversify';
import { WorkspaceFileService } from '../utils/workspace-file-service';

@injectable()
export class FileProviderHandler implements IActionHandler {
    @inject(WorkspaceFileService) protected workspaceFileService: WorkspaceFileService;
    @inject(TYPES.IActionDispatcher) protected actionDispatcher: IActionDispatcher;
    protected static REQUEST_ROUTING: Map<string, (items: FileProviderResponseItem[]) => void> = new Map();
    protected static _INSTANCE_QUEUE: ((fileProviderHandler: FileProviderHandler) => void)[] = [];
    protected static _instance: FileProviderHandler;

    handle(action: FileProviderResponse): ICommand | Action | void {
        this.updateInstance();
        if (FileProviderHandler.REQUEST_ROUTING.has(action.requestId)) {
            const result = FileProviderHandler.REQUEST_ROUTING.get(action.requestId);
            if (result) {
                result(action.items);
            }
        }
    }

    updateInstance(): void {
        if (!FileProviderHandler._instance) {
            if (FileProviderHandler._INSTANCE_QUEUE.length > 0) {
                for (const waiting of FileProviderHandler._INSTANCE_QUEUE) {
                    waiting(this);
                }
            }
            FileProviderHandler._instance = this;
            FileProviderHandler._INSTANCE_QUEUE = [];
        }
    }

    static async getFiles(
        folder: string,
        readFiles = false,
        supportedDynamicImportFileTypes: string[] = [],
        actionDispatcher?: IActionDispatcher
    ): Promise<FileProviderResponseItem[]> {
        const result = new Promise<FileProviderResponseItem[]>(resolve => {
            const request = FileProviderRequest.create([folder], readFiles, supportedDynamicImportFileTypes);
            FileProviderHandler.REQUEST_ROUTING.set(request.requestId, resolve);
            if (actionDispatcher) {
                actionDispatcher.dispatch(request);
            } else {
                this.instance.then(i => {
                    i.actionDispatcher.dispatch(request);
                });
            }
        });
        return result;
    }

    static async getWorkspaceFileService(): Promise<WorkspaceFileService> {
        return (await this.instance).workspaceFileService;
    }

    static get instance(): Promise<FileProviderHandler> {
        if (this._instance) {
            return new Promise<FileProviderHandler>(resolve => resolve(this._instance));
        } else {
            return new Promise<FileProviderHandler>(resolve => {
                this._INSTANCE_QUEUE.push(resolve);
            });
        }
    }
}
