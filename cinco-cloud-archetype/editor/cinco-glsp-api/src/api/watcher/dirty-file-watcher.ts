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
import { WatchEventType } from 'fs-extra';
import * as uuid from 'uuid';
import { CincoFolderWatcher } from './cinco-folder-watcher';
import { getFileExtension } from '@cinco-glsp/cinco-glsp-common';

export class DirtyFileWatcher {
    static dirtyCallbacks: Map<
        string,
        {
            folder: string;
            fileTypes: string[];
            priority: number;
            callback: (dirtyFiles: { path: string; eventType: WatchEventType}[]
            ) => Promise<void>
        }
    > = new Map();
    static dirtyFiles: { path: string; eventType: WatchEventType }[] = [];
    static reloadDelay = 100; // heuristic value

    static async watch(
        folderToWatch: string,
        fileTypes: string[],
        dirtyCallback: (dirtyFiles: { path: string; eventType: WatchEventType }[]) => Promise<void>,
        priority: number,
        id?: string
    ): Promise<{ dirtyCallbackId: string; watchIds: string[] }> {
        const entries = CincoFolderWatcher.watchRecursive(folderToWatch, fileTypes, async (filename: string, eventType: WatchEventType) => {
            const entry = { path: filename, eventType };
            if (this.dirtyFiles.filter(e => e.path === entry.path && e.eventType === entry.eventType).length <= 0) {
                this.dirtyFiles.push(entry);
            }
        });
        const watchIds = entries.map(e => e.watchId);
        // start if not running
        if (this.dirtyCallbacks.size <= 0) {
            this.startDirtyCheck();
        }
        const dirtyCallbackId = id ? id : uuid.v4();
        this.dirtyCallbacks.set(dirtyCallbackId, { folder: folderToWatch, fileTypes: fileTypes, priority, callback: dirtyCallback });
        return { dirtyCallbackId: dirtyCallbackId, watchIds: watchIds };
    }

    static unwatch(watchInfo: { dirtyCallbackId: string; watchIds: string[] } | undefined): void {
        if (watchInfo) {
            CincoFolderWatcher.removeCallbacks(watchInfo.watchIds);
            if (this.dirtyCallbacks.has(watchInfo.dirtyCallbackId)) {
                this.dirtyCallbacks.delete(watchInfo.dirtyCallbackId);
            }
        }
    }

    static startDirtyCheck(): void {
        setTimeout(async () => {
            // all <reloadDelay>ms check if changes were made to the files,
            // by the use of the dirty-variable. If so, call the dirtyCallback.
            if (this.dirtyFiles.length > 0) {
                const dirtyFiles = this.dirtyFiles;
                this.dirtyFiles = [];
                // handle dirtyFiles
                const dirtyCallbacks = Array.from(this.dirtyCallbacks.values()).sort((e1, e2) => e1.priority - e2.priority);
                for (const dirtyCallback of dirtyCallbacks) {
                    const folder = dirtyCallback.folder;
                    const callback = dirtyCallback.callback;
                    const fileTypes = dirtyCallback.fileTypes;
                    if(fileTypes.length <= 0) {
                        await callback(dirtyFiles);
                    } else {
                        const relatedFiles = dirtyFiles.filter(
                            f => fileTypes.includes('.' + getFileExtension(f.path)) && f.path.indexOf(folder) >= 0
                        );
                        if (relatedFiles.length > 0) {
                            await callback(relatedFiles);
                        }
                    }
                }
            }
            // if not stopping, then shedule next dirtyCheck
            this.startDirtyCheck();
        }, this.reloadDelay);
    }
}
