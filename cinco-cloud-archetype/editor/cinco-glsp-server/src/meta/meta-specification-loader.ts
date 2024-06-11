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
import { CompositionSpecification, META_FILE_TYPES, MetaSpecification } from '@cinco-glsp/cinco-glsp-common';
import { getFilesFromFolder, getLanguageFolder, getLibLanguageFolder, isBundle, readJson } from '@cinco-glsp/cinco-glsp-api';
import { loadLanguage } from '@cinco-glsp/cinco-languages/lib/index';
import * as fs from 'fs';
import { FSWatcher, WatchOptions } from 'fs-extra';
export class MetaSpecificationLoader {
    private static watchingFolder: string | undefined = undefined;
    private static watcher: FSWatcher | undefined = undefined;
    private static callback: (() => Promise<void>) | undefined;
    private static eventAggregationMap: Map<string, number[]> = new Map();
    private static eventDelta = 600;
    /*
     * watches only one folder
     */
    static watch(folderToWatch: string, callback?: () => Promise<void>): void {
        if (!folderToWatch || (this.watchingFolder === folderToWatch && this.callback?.toString() === callback?.toString())) {
            // already watching or nothing to watch
            return;
        }

        // folder changed, while already watching
        if (this.watcher && this.watchingFolder !== folderToWatch) {
            this.watcher.close();
            this.callback = undefined;
        }

        // set parameter
        this.callback = callback;
        this.watchingFolder = folderToWatch;

        // start watching
        try {
            this.watcher = fs.watch(
                folderToWatch,
                {
                    // TODO: recursive not possible on Linux until Node 20. But theia currently not Node 20 compatible.
                    // Probable workaround would be a programatical approach, but we should wait for Theia Node 20.
                    //
                    // recursive: true
                } as WatchOptions,
                async (eventType, filename) => {
                    if (filename) {
                        const fileExtension = filename.slice(filename.indexOf('.'));
                        if (META_FILE_TYPES.includes(fileExtension)) {
                            const changeTimes = this.eventAggregationMap.get(filename) ?? [];
                            const currentTime = Date.now();
                            if (this.eventAggregationMap.has(filename)) {
                                const deltaTimes = changeTimes.filter(cT => Math.abs(cT - currentTime) < this.eventDelta);
                                if (deltaTimes.length > 0) {
                                    // atleast one event occured in the last eventDelta
                                    // skip this event
                                    return;
                                }
                            }
                            this.eventAggregationMap.set(filename, changeTimes.concat(currentTime));
                            console.log('changed:\nEventtype: ' + eventType + '\nfilename: ' + filename);
                            if (this.callback) {
                                let executed = false;
                                while (!executed) {
                                    try {
                                        await this.callback()
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
        } catch (e) {
            console.log('something went wrong registering or executing file watcher: ' + e);
        }
    }

    static async load(metaLanguagesFolder?: string, callback?: () => Promise<void>): Promise<void> {
        const metaLanguagesPath = metaLanguagesFolder ?? `${getLanguageFolder()}`;

        // register watching (optional)
        this.watch(metaLanguagesPath, callback);

        const foundFiles = getFilesFromFolder(metaLanguagesPath, './');
        const files = await foundFiles.filter((file: string) => {
            const fileExtension = file.slice(file.indexOf('.'));
            const isSupported = META_FILE_TYPES.includes(fileExtension);
            return file !== undefined && isSupported;
        });
        let countdown = files.length;
        return new Promise<void>(resolve => {
            if (files.length <= 0) {
                resolve();
            }
            files.forEach(async (file: string) => {
                const fileExtension = file.slice(file.indexOf('.'));
                try {
                    if (fileExtension === '.mgl') {
                        const parsedMgl = await loadLanguage(`${metaLanguagesPath}/${file}`, {});
                        const metaSpec = JSON.parse(parsedMgl);
                        if (metaSpec && CompositionSpecification.is(metaSpec)) {
                            MetaSpecification.merge(metaSpec);
                        }
                    } else {
                        const metaSpec = readJson(`${metaLanguagesPath}/${file}`);
                        if (metaSpec && CompositionSpecification.is(metaSpec)) {
                            MetaSpecification.merge(metaSpec);
                        }
                    }
                } catch (e) {
                    console.log('Error parsing: ' + file + '\n' + e);
                }
                countdown -= 1;
                if (countdown <= 0) {
                    resolve();
                }
            });
        });
    }

    static clear(): void {
        MetaSpecification.clear();
    }

    static loadClassFiles(supportedDynamicImportFileTypes: string[]): void {
        // Import all injected language-files under './languages/*.ts'
        const languagesPath = `${getLibLanguageFolder()}`;
        const foundFiles = getFilesFromFolder(languagesPath, './');
        foundFiles
            .filter((file: string) => {
                const fileExtension = file.slice(file.indexOf('.'));
                const isSupported = supportedDynamicImportFileTypes.indexOf(fileExtension) >= 0;
                return file !== undefined && isSupported;
            })
            .forEach((file: string) => {
                // fixed internal (resources that are compiled into this server)
                if (isBundle()) {
                    require(`../../languages/${file}`);
                } else {
                    import(`../../../lib/languages/${file}`).catch(e => {
                        console.log('having error loading "' + file + '"');
                        console.log(e);
                    });
                }
            });
    }
}
