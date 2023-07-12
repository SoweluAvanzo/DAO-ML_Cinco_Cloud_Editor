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
    FileProviderResponse,
    FileProviderResponseItem
} from '@cinco-glsp/cinco-glsp-server/lib/src/shared/protocol/meta-specification-reload-protocol';
import { CommandService } from '@theia/core';

export class DynamicImportLoader {
    static load(supportedDynamicImportFileTypes: string[], commandService: CommandService): Promise<void> {
        return new Promise<void>((resolve, _) =>
            (
                commandService.executeCommand('fileProviderHandler', {
                    directories: ['glsp-client/languages'],
                    readFiles: false
                }) as Promise<FileProviderResponse>
            ).then(response => {
                // import all files
                response.items
                    .filter((item: FileProviderResponseItem) => {
                        const file = item.path;
                        const fileExtension = file.slice(file.lastIndexOf('.'));
                        const isSupported = supportedDynamicImportFileTypes.indexOf(fileExtension) >= 0;
                        if (isSupported) {
                            console.log('identified supported file of type: ' + fileExtension);
                        }
                        return file !== undefined && isSupported;
                    })
                    .forEach(file => {
                        import(`../../../languages/${'' + file.path}`);
                    });
                resolve();
            })
        );
    }
}
