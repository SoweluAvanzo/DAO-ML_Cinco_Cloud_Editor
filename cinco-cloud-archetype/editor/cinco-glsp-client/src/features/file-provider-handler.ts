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

@injectable()
export class FileProviderHandler implements IActionHandler {
    @inject(TYPES.IActionDispatcher) protected actionDispatcher: IActionDispatcher;
    static REQUEST_ROUTING: Map<string, (items: FileProviderResponseItem[]) => void> = new Map();
    static instance: FileProviderHandler;

    handle(action: FileProviderResponse): ICommand | Action | void {
        if (!FileProviderHandler.instance) {
            FileProviderHandler.instance = this;
        }
        if (FileProviderHandler.REQUEST_ROUTING.has(action.requestId)) {
            const result = FileProviderHandler.REQUEST_ROUTING.get(action.requestId);
            if (result) {
                result(action.items);
            }
        }
    }

    static getFiles(folder: string, readFiles: boolean, supportedDynamicImportFileTypes: string[]): Promise<FileProviderResponseItem[]> {
        const result = new Promise<FileProviderResponseItem[]>(resolve => {
            const request = FileProviderRequest.create([folder], readFiles, supportedDynamicImportFileTypes);
            FileProviderHandler.REQUEST_ROUTING.set(request.requestId, resolve);
            this.instance.actionDispatcher.dispatch(request);
        });
        return result;
    }
}
