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
    FileProviderRequest, FileProviderResponse, FileProviderResponseItem, META_LANGUAGES_FRONTEND_FOLDER
} from '@cinco-glsp/cinco-glsp-common';
import { Action, IActionDispatcher, IActionHandler, ICommand } from '@eclipse-glsp/client';
import { CommandService } from '@theia/core';
import { injectable } from 'inversify';

@injectable()
export class DynamicImportLoader implements IActionHandler {
    handle(action: FileProviderResponse): ICommand | Action | void {
        DynamicImportLoader.importResources(action.items);
    }

    static load(
        supportedDynamicImportFileTypes: string[],
        commandService: CommandService,
        actionDispatcher: IActionDispatcher
    ): Promise<void> {
        return new Promise<void>((resolve, _) => {
            const request = FileProviderRequest.create([META_LANGUAGES_FRONTEND_FOLDER], false, supportedDynamicImportFileTypes);
            if (commandService) {
                // used for theia applications
                (commandService.executeCommand('fileProviderHandler', request) as Promise<FileProviderResponse>).then(response => {
                    this.importResources(response.items, supportedDynamicImportFileTypes);
                    resolve();
                });
            } else {
                // used for standalone applications
                actionDispatcher.dispatch(request);
            }
        });
    }

    static importResources(resources: FileProviderResponseItem[], supportedFileTypes: string[] = []): void {
        // import all files
        const toImport = resources.filter((item: FileProviderResponseItem) => {
            const file = item.path;
            const fileExtension = file.slice(file.lastIndexOf('.'));
            const isSupported = supportedFileTypes.length <= 0 || supportedFileTypes.indexOf(fileExtension) >= 0;
            return file !== undefined && isSupported;
        });
        toImport.forEach(file => {
            import(`../../../languages/${'' + file.path}`);
        });
    }
}
