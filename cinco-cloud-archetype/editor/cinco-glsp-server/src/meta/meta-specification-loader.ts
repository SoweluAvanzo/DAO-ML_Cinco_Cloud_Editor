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
import {
    CincoFolderWatcher,
    getFiles,
    getLanguageFolder,
    getLibLanguageFolder,
    getSubfolder,
    isBundle,
    readJson
} from '@cinco-glsp/cinco-glsp-api';
import { loadLanguage } from '@cinco-glsp/cinco-languages/lib/index';
import { WatchEventType } from 'fs-extra';
export class MetaSpecificationLoader {
    static async watch(callback: () => Promise<void>): Promise<void> {
        const languagesFolder = getLanguageFolder();
        const folders = getSubfolder(languagesFolder);
        folders.push(languagesFolder);
        for (const f of folders) {
            CincoFolderWatcher.watch(f, META_FILE_TYPES, async (filename: string, eventType: WatchEventType) => {
                console.log('changed in: ' + f + ' | ' + filename);
                console.log('eventType: ' + eventType);
                const subFolders = getSubfolder(languagesFolder);
                folders.push(languagesFolder);
                MetaSpecificationLoader.clear();
                for (const sf of subFolders) {
                    await this.reloadFiles(sf, META_FILE_TYPES);
                }
                return callback();
            });
        }
    }

    static async load(metaLanguagesFolder?: string): Promise<void> {
        const languagesFolder = metaLanguagesFolder ?? `${getLanguageFolder()}`;
        const folders = getSubfolder(languagesFolder);
        folders.push(languagesFolder);
        for (const f of folders) {
            await this.reloadFiles(f, META_FILE_TYPES);
        }
        return;
    }

    private static async reloadFiles(metaLanguagesPath: string, fileTypes: string[]): Promise<CompositionSpecification> {
        const files = getFiles(metaLanguagesPath, fileTypes);
        let countdown = files.length;
        return new Promise<CompositionSpecification>(resolve => {
            if (files.length <= 0) {
                resolve(MetaSpecification.get());
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
                    } else if (fileExtension === '.json') {
                        const metaSpec = readJson(`${metaLanguagesPath}/${file}`);
                        if (metaSpec && CompositionSpecification.is(metaSpec)) {
                            MetaSpecification.merge(metaSpec);
                        }
                    } else {
                        console.log('File not handled: ' + file);
                    }
                } catch (e) {
                    console.log('Error parsing: ' + file + '\n' + e);
                }
                countdown -= 1;
                if (countdown <= 0) {
                    resolve(MetaSpecification.get());
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
        const foundFiles = getFiles(languagesPath);
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
