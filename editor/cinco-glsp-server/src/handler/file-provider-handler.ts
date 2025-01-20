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
import { Action, ActionHandler, Logger } from '@eclipse-glsp/server';
import { FileProviderRequest, FileProviderResponse, FileProviderResponseItem } from '@cinco-glsp/cinco-glsp-common';
import { inject, injectable } from 'inversify';
import {
    DirtyFileWatcher,
    existsFile,
    getFilesFromDirectoriesSync,
    getLanguageFolder,
    getRootUri,
    getWorkspaceRootUri,
    readFilesFromDirectories
} from '@cinco-glsp/cinco-glsp-api';
import { WatchEventType } from 'fs-extra';
import * as path from 'path';
@injectable()
export class FileProviderHandler implements ActionHandler {
    @inject(Logger)
    readonly logger: Logger;

    actionKinds: string[] = [FileProviderRequest.KIND];

    static CACHED_FILES: string[] = [];

    static async init(): Promise<void> {
        const workspacePath = getWorkspaceRootUri();
        const languagesPath = getLanguageFolder();
        const cachedFiles = getFilesFromDirectoriesSync([workspacePath, languagesPath], []);
        FileProviderHandler.CACHED_FILES = cachedFiles;
        await DirtyFileWatcher.watch(
            workspacePath,
            [],
            async (dirtyFiles: { path: string; eventType: WatchEventType }[]): Promise<void> => {
                console.log('New DirtyFiles!');
                await this.updateCachedFiles(dirtyFiles);
                return Promise.resolve();
            },
            3
        );
        if (!languagesPath.startsWith(workspacePath)) {
            await DirtyFileWatcher.watch(
                languagesPath,
                [],
                async (dirtyFiles: { path: string; eventType: WatchEventType }[]): Promise<void> => {
                    try {
                        await this.updateCachedFiles(dirtyFiles);
                    } catch (e) {
                        console.log('A cinco-glsp-server-error occured caching files:\n' + e);
                    }
                    return Promise.resolve();
                },
                3
            );
        }
    }

    static async updateCachedFiles(dirtyFiles: { path: string; eventType: WatchEventType }[]): Promise<void> {
        const toRemove: string[] = [];
        const toAdd: string[] = [];
        console.log('Found DirtyFiles: [\n' + dirtyFiles.map(d => d.path).join(',\n') + '\n]');
        await Promise.all(
            dirtyFiles.map(async (dirtyFile: { path: string; eventType: WatchEventType }): Promise<void> => {
                const resolvedPath = path.resolve(dirtyFile.path);
                const deleted = !(await existsFile(resolvedPath));
                if (deleted) {
                    toRemove.push(resolvedPath);
                } else if (this.CACHED_FILES.indexOf(resolvedPath) < 0) {
                    toAdd.push(resolvedPath);
                }
            })
        );
        console.log('Removed: [\n' + toRemove.join(',\n') + '\n]');
        console.log('New File: [\n' + toAdd.join(',\n') + '\n]');
        if (toRemove.length > 0) {
            this.CACHED_FILES = this.CACHED_FILES.filter(c => toRemove.indexOf(c) < 0);
        }
        this.CACHED_FILES = Array.from(new Set(this.CACHED_FILES.concat(toAdd)));
    }

    async execute(action: FileProviderRequest, ...args: unknown[]): Promise<Action[]> {
        const directories: string[] = action.directories;
        const readFiles: boolean = action.readFiles ?? false;

        const dirs = directories
            .map(
                dir =>
                    dir === FileProviderRequest.META_LANGUAGES_FOLDER_KEYWORD
                        ? getLanguageFolder()
                        : this.isAbsolutePath(dir)
                          ? dir // absolute
                          : path.join(getRootUri(), dir) // relative to root
            )
            .map(p => path.normalize(p));
        let items: FileProviderResponseItem[];
        if (readFiles) {
            const fileContents = await readFilesFromDirectories(dirs, action.supportedTypes);
            items = Array.from(fileContents.entries()).map(entry => FileProviderResponseItem.create(entry[0], entry[1]));
        } else {
            const cachedFiles = this.getCachedFiles();
            const files = cachedFiles.filter(
                f =>
                    // check for directory
                    dirs.filter(d => f.startsWith(d)).length > 0 && action.supportedTypes.filter(s => f.endsWith(s)).length > 0
            );
            // files = await getFilesFromDirectories(dirs, action.supportedTypes);
            items = files.map(entry => FileProviderResponseItem.create(entry, undefined));
        }
        const response = FileProviderResponse.create(items, action.requestId);
        return [response];
    }

    getCachedFiles(): string[] {
        return FileProviderHandler.CACHED_FILES;
    }

    isAbsolutePath(p: string): boolean {
        return p.startsWith('/') || p.startsWith('file://');
    }
}
