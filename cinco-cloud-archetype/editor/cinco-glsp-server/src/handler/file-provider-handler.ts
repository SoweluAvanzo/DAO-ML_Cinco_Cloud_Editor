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
import { Action, ActionHandler, Logger, MaybePromise } from '@eclipse-glsp/server-node';

import { FileProviderRequest, FileProviderResponse, FileProviderResponseItem } from '@cinco-glsp/cinco-glsp-common';
import * as fs from 'fs';
import { inject, injectable } from 'inversify';
import { getFilesFromDirectories, getRootUri, readFilesFromDirectories } from '../utils/file-helper';

@injectable()
export class FileProviderHandler implements ActionHandler {
    @inject(Logger)
    readonly logger: Logger;

    actionKinds: string[] = [FileProviderRequest.KIND];

    execute(action: FileProviderRequest, ...args: unknown[]): MaybePromise<Action[]> {
        const directories: string[] = action.directories;
        const readFiles: boolean = action.readFiles ?? false;

        const dirs = directories.map(dir => `${getRootUri()}/${dir}`);
        let response;
        if (readFiles) {
            const fileContents = readFilesFromDirectories(fs, dirs, action.supportedTypes);
            const items: FileProviderResponseItem[] = Array.from(fileContents.entries()).map(entry =>
                FileProviderResponseItem.create(entry[0], entry[1])
            );
            response = FileProviderResponse.create(items);
        } else {
            const files = getFilesFromDirectories(fs, dirs, action.supportedTypes);
            const items: FileProviderResponseItem[] = files.map(entry => FileProviderResponseItem.create(entry, undefined));
            response = FileProviderResponse.create(items);
        }
        return [response];
    }
}
