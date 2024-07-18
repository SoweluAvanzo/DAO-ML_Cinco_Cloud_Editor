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
    static dirtyCallbacks: Map<string, { folder: string; fileTypes: string[]; callback: (dirtyFiles: string[]) => Promise<void> }> =
        new Map();
    static dirtyFiles: string[] = [];
    static reloadDelay = 100; // heuristic value

    static async watch(
        folderToWatch: string,
        fileTypes: string[],
        dirtyCallback: (dirtyFiles: string[]) => Promise<void>
    ): Promise<string[]> {
        const entries = CincoFolderWatcher.watchRecursive(
            folderToWatch,
            fileTypes,
            async (filename: string, _eventType: WatchEventType) => {
                if (!this.dirtyFiles.includes(filename)) {
                    this.dirtyFiles.push(filename);
                }
            }
        );
        // start if not running
        if (this.dirtyCallbacks.size <= 0) {
            this.startDirtyCheck();
        }
        this.dirtyCallbacks.set(uuid.v4(), { folder: folderToWatch, fileTypes: fileTypes, callback: dirtyCallback });
        return entries.map(e => e.watchId);
    }

    static unwatch(watchIds: string[]): void {
        CincoFolderWatcher.removeCallbacks(watchIds);
    }

    static startDirtyCheck(): void {
        setTimeout(async () => {
            // all <reloadDelay>ms check if changes were made to the files,
            // by the use of the dirty-variable. If so, call the dirtyCallback.
            if (this.dirtyFiles.length > 0) {
                const dirtyFiles = this.dirtyFiles;
                this.dirtyFiles = [];
                // handle dirtyFiles
                for (const dirtyCallback of this.dirtyCallbacks.values()) {
                    const folder = dirtyCallback.folder;
                    const callback = dirtyCallback.callback;
                    const fileTypes = dirtyCallback.fileTypes;
                    const relatedFiles = dirtyFiles.filter(f => fileTypes.includes('.' + getFileExtension(f)) && f.indexOf(folder) >= 0);
                    if (relatedFiles.length > 0) {
                        await callback(relatedFiles);
                    }
                }
            }
            // if not stopping, then shedule next dirtyCheck
            this.startDirtyCheck();
        }, this.reloadDelay);
    }
}
