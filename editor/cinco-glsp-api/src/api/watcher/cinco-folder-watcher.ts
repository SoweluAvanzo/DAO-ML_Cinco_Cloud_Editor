/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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

import * as fs from 'fs';
import { FSWatcher, WatchOptions } from 'fs-extra';
import { existsDirectory, getSubfolderSync, readFile } from '../../utils/file-helper';
import * as uuid from 'uuid';

interface WatchEntry {
    watcher: FSWatcher;
    callbacks: WatchCallback[];
}

interface WatchCallback {
    watchedFileTypes: string[];
    callback: (filename: string, eventType: fs.WatchEventType) => Promise<void>;
    id: string;
}

export abstract class CincoFolderWatcher {
    private static watchedFolders: Map<string, WatchEntry> = new Map();
    private static eventAggregationMap: Map<string, string | undefined> = new Map();

    static watchRecursive(
        folderToWatch: string,
        fileTypes: string[],
        callback: (filename: string, eventType: fs.WatchEventType) => Promise<void>,
        callbackCondition?: () => boolean
    ): { folderPath: string; watchId: string }[] {
        const entries: { folderPath: string; watchId: string }[] = [] as { folderPath: string; watchId: string }[];
        const foldersToWatch = getSubfolderSync(folderToWatch);
        foldersToWatch.push(folderToWatch);
        if (!callbackCondition || callbackCondition()) {
            // Initialize FileWatcher on languages Folder and subfolders
            for (const f of foldersToWatch) {
                const watchId = CincoFolderWatcher.watch(f, fileTypes, callback);
                if (watchId) {
                    entries.push({
                        folderPath: f,
                        watchId: watchId
                    });
                }
            }
        }
        return entries;
    }

    /*
     * watches folder
     */
    static watch(
        folderToWatch: string,
        watchedFileTypes: string[],
        callback: (filename: string, eventType: fs.WatchEventType) => Promise<void>
    ): string | undefined {
        console.log('Folder to watch: ' + folderToWatch);
        console.log('WatchedFilesTypes: ' + watchedFileTypes.toString());
        if (
            !folderToWatch || // no folder to watch specified
            !callback
        ) {
            console.log('No folder to watch or no callback specified');
            return;
        }
        // set parameter
        const watchId = uuid.v4();
        const watchEntry = this.watchedFolders.has(folderToWatch) ? this.watchedFolders.get(folderToWatch)! : ({} as WatchEntry);
        if (!watchEntry.callbacks) {
            watchEntry.callbacks = [];
        }
        watchEntry.callbacks.push({
            callback: callback,
            watchedFileTypes: watchedFileTypes,
            id: watchId
        });
        if (!watchEntry.watcher) {
            console.log('creating watcher...');
            // start watching
            try {
                const watcher = fs.watch(folderToWatch, {} as WatchOptions, async (eventType, filename) => {
                    if (filename) {
                        if (!this.watchedFolders.has(folderToWatch)) {
                            return;
                        }
                        const path = `${folderToWatch}/${filename}`;
                        if (await existsDirectory(path)) {
                            // recursive workaround to watch new folders inside watched folders
                            if (eventType === 'rename') {
                                console.log('Identified new Subfolder. Initializing watcher...');
                                const watcherCallbacks = this.watchedFolders.get(folderToWatch)!.callbacks;
                                for (const cb of watcherCallbacks) {
                                    this.watch(path, cb.watchedFileTypes, cb.callback);
                                }
                            }
                        } else {
                            const currentContent = await readFile(path);
                            if (this.eventAggregationMap.has(path)) {
                                const lastContent = this.eventAggregationMap.get(path)!;
                                if (currentContent === lastContent) {
                                    console.log('no change, skipping callbacks');
                                    this.eventAggregationMap.set(path, currentContent);
                                    return;
                                }
                            }
                            this.eventAggregationMap.set(path, currentContent);
                            const watcherCallbacks = this.watchedFolders.get(folderToWatch)!.callbacks;
                            for (const entry of watcherCallbacks) {
                                const cb = entry.callback;
                                const fileTypes = entry.watchedFileTypes;
                                const fileExtension = filename.slice(filename.indexOf('.'));
                                if (fileTypes && fileTypes.length > 0 ? !fileTypes.includes(fileExtension) : false) {
                                    continue;
                                }
                                let executed = false;
                                while (!executed) {
                                    try {
                                        await cb(path, eventType)
                                            .catch(e => {
                                                console.log('An error occured executing file watcher callback: ' + e);
                                            })
                                            .then(_ => {
                                                executed = true;
                                            });
                                    } catch (e) {
                                        console.log('something went wrong executing file watcher callback: ' + e);
                                    }
                                }
                            }
                        }
                    }
                });
                watchEntry.watcher = watcher;
                console.log('watcher created!');
            } catch (e) {
                console.log('something went wrong creating a watcher for folder: ' + folderToWatch + '\n' + e);
            }
        } else {
            console.log('watcher already present!');
        }
        this.watchedFolders.set(folderToWatch, watchEntry);
        return watchId;
    }

    static unwatch(folderToWatch: string): WatchEntry | undefined {
        if (!this.watchedFolders.has(folderToWatch)) {
            return undefined;
        }
        const watchedEntry = this.watchedFolders.get(folderToWatch)!;
        for (const cbs of watchedEntry.callbacks) {
            this.removeCallback(cbs.callback.toString());
        }
        watchedEntry.watcher.close();
        this.watchedFolders.delete(folderToWatch);
        return watchedEntry;
    }

    static removeCallbacks(watchIds: string[]): void {
        for (const watchId of watchIds) {
            this.removeCallback(watchId);
        }
    }

    static removeCallback(watchId: string): WatchCallback | undefined {
        let toRemove: WatchCallback | undefined;
        let found = false;
        const foldersToWatch = Array.from(this.watchedFolders.entries());
        for (const folderToWatch of foldersToWatch) {
            const watchEntry = folderToWatch[1];
            if (watchEntry) {
                for (const cb of watchEntry.callbacks) {
                    if (cb.id === watchId) {
                        toRemove = cb;
                        found = true;
                        break;
                    }
                }
                if (found) {
                    watchEntry.callbacks = watchEntry.callbacks.filter(cb => cb !== toRemove);
                    folderToWatch[1] = watchEntry;
                    break;
                }
            }
        }
        return toRemove;
    }
}
