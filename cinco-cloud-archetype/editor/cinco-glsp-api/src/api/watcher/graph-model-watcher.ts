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

import { getDiagramExtensions } from '@cinco-glsp/cinco-glsp-common';
import { getWorkspaceRootUri } from '../../utils/file-helper';
import { DirtyFileWatcher } from './dirty-file-watcher';
import { WatchEventType } from 'fs-extra';

export class GraphModelWatcher {
    static watchInfo: { dirtyCallbackId: string; watchIds: string[] } | undefined;
    static graphModelChangeCallbacks: Map<string, (dirtyFiles: { path: string; eventType: WatchEventType }[]) => Promise<void>> = new Map();

    static async watch(id?: string, folderToWatch: string = getWorkspaceRootUri()): Promise<void> {
        DirtyFileWatcher.unwatch(this.watchInfo);
        const fileTypes = getDiagramExtensions().map(e => '.' + e);
        this.watchInfo = await DirtyFileWatcher.watch(
            folderToWatch,
            fileTypes,
            async (dirtyFiles: { path: string; eventType: WatchEventType }[]) => {
                for (const callback of this.graphModelChangeCallbacks.values()) {
                    await callback(dirtyFiles);
                }
            },
            1,
            'GraphModelWatcher_' + id
        );
    }

    static addCallback(id: string, callback: (dirtyFiles: { path: string; eventType: WatchEventType }[]) => Promise<void>): string {
        this.graphModelChangeCallbacks.set(id, callback);
        return id;
    }

    static removeCallback(id: string): void {
        if (id && this.graphModelChangeCallbacks.has(id)) {
            this.graphModelChangeCallbacks.delete(id);
        }
    }
}
