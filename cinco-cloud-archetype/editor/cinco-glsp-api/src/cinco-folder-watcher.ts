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
import { existsDirectory } from './utils/file-helper';

interface WatchEntry {
    watcher: FSWatcher;
    callbacks: WatchCallback[];
}

interface WatchCallback {
    watchedFileTypes: string[];
    callback: (filename: string, eventType: fs.WatchEventType) => Promise<void>;
    eventAggregationMap: Map<string, number[]>;
}

export abstract class CincoFolderWatcher {
    private static watchedFolders: Map<string, WatchEntry> = new Map();
    private static eventDelta = 600;

    /*
     * watches folder
     */
    static watch(
        folderToWatch: string,
        watchedFileTypes: string[],
        callback: (filename: string, eventType: fs.WatchEventType) => Promise<void>
    ): void {
        console.log('Folder to watch: ' + folderToWatch);
        console.log('WatchedFilesTypes: ' + watchedFileTypes.toString());
        if (
            !folderToWatch || // no folder to watch specified
            !callback
        ) {
            console.log('No folder to watch or callback specified');
            return;
        }
        if (
            this.watchedFolders.has(folderToWatch) // already watches folder
        ) {
            console.log('already watches folder: ' + folderToWatch);
            console.log('adding addtional callback to watched folder: ' + folderToWatch);
        }
        // set parameter
        const watchEntry = this.watchedFolders.has(folderToWatch) ? this.watchedFolders.get(folderToWatch)! : ({} as WatchEntry);
        if (callback) {
            if (!watchEntry.callbacks) {
                watchEntry.callbacks = [];
            }
            watchEntry.callbacks.push({
                callback: callback,
                watchedFileTypes: watchedFileTypes,
                eventAggregationMap: new Map()
            });
        }
        if (!watchEntry.watcher) {
            console.log('creating watcher...');
            // start watching
            try {
                const watcher = fs.watch(
                    folderToWatch,
                    {
                        // TODO: recursive not possible on Linux until Node 20. But theia currently not Node 20 compatible.
                        // Probable workaround would be a programatical approach, but we should wait for Theia Node 20.
                        // recursive: true
                    } as WatchOptions,
                    async (eventType, filename) => {
                        console.log('Changed detected: ' + filename + ' | ' + 'Eventtype: ' + eventType);
                        if (filename) {
                            if (!this.watchedFolders.has(folderToWatch)) {
                                return;
                            }
                            const path = `${folderToWatch}/${filename}`;
                            if (existsDirectory(path)) {
                                if (eventType === 'rename') {
                                    console.log('Identified new Subfolder. Initializing watcher...');
                                    const watcherCallbacks = this.watchedFolders.get(folderToWatch)!.callbacks;
                                    for (const cb of watcherCallbacks) {
                                        this.watch(path, cb.watchedFileTypes, cb.callback);
                                    }
                                } else {
                                    console.log('This watch handling is not defined: ' + eventType);
                                }
                            } else {
                                const watcherCallbacks = this.watchedFolders.get(folderToWatch)!.callbacks;
                                for (const entry of watcherCallbacks) {
                                    const cb = entry.callback;
                                    const fileTypes = entry.watchedFileTypes;
                                    const eventAggregationMap = entry.eventAggregationMap;
                                    const fileExtension = filename.slice(filename.indexOf('.'));
                                    if (fileTypes && fileTypes.length > 0 ? !fileTypes.includes(fileExtension) : false) {
                                        continue;
                                    }
                                    const changeTimes = eventAggregationMap.get(filename) ?? [];
                                    const currentTime = Date.now();
                                    if (eventAggregationMap.has(filename)) {
                                        const deltaTimes = changeTimes.filter(cT => Math.abs(cT - currentTime) < this.eventDelta);
                                        if (deltaTimes.length > 0) {
                                            // atleast one event occured in the last eventDelta
                                            // skip this event
                                            return;
                                        }
                                    }
                                    eventAggregationMap.set(filename, changeTimes.concat(currentTime));
                                    console.log('changed:\nEventtype: ' + eventType + '\nfilename: ' + filename);
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
                    }
                );
                watchEntry.watcher = watcher;
                console.log('watcher created!');
            } catch (e) {
                console.log('something went wrong creating a watcher for folder: ' + folderToWatch + '\n' + e);
            }
        } else {
            console.log('watcher already present!');
        }
        this.watchedFolders.set(folderToWatch, watchEntry);
    }

    static unwatch(folderToWatch: string): WatchEntry | undefined {
        if (!this.watchedFolders.has(folderToWatch)) {
            return undefined;
        }
        const watchedEntry = this.watchedFolders.get(folderToWatch)!;
        for (const cbs of watchedEntry.callbacks) {
            this.removeCallback(folderToWatch, cbs.callback.toString());
        }
        watchedEntry.watcher.close();
        this.watchedFolders.delete(folderToWatch);
        return watchedEntry;
    }

    static removeCallback(folderToWatch: string, callbackName: string): WatchCallback | undefined {
        if (!this.watchedFolders.has(folderToWatch)) {
            return undefined;
        }
        const watchedEntry = this.watchedFolders.get(folderToWatch)!;
        let toRemove: WatchCallback | undefined;
        for (const entry of watchedEntry.callbacks) {
            const cb = entry.callback;
            if (cb.toString() === callbackName) {
                toRemove = entry;
            }
        }
        watchedEntry.callbacks = watchedEntry.callbacks.filter(cb => cb !== toRemove);
        return toRemove;
    }
}
