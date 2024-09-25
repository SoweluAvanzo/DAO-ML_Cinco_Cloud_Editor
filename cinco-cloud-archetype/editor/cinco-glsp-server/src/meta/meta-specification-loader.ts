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
import { getFiles, getLanguageFolder, getLibLanguageFolder, isBundle, readJson, DirtyFileWatcher } from '@cinco-glsp/cinco-glsp-api';
import { loadLanguage } from '@cinco-glsp/cinco-languages/lib/index';

export class MetaSpecificationLoader {
    static initialized = false;
    static reloadCallbacks: (() => Promise<void>)[] = [];

    static addReloadCallback(reloadCallback: () => Promise<void>): void {
        this.reloadCallbacks.push(reloadCallback);
    }

    static async watch(dirtyCallback: () => Promise<void>, callbackId?: string): Promise<{ dirtyCallbackId: string; watchIds: string[] }> {
        const languagesFolder = getLanguageFolder();
        if (!this.initialized) {
            // the first dirtyCallback that is always triggered is a reset of the metaspec
            this.initialized = true;
            await DirtyFileWatcher.watch(
                languagesFolder,
                META_FILE_TYPES,
                async () => {
                    MetaSpecification.clear();
                    await this.load();
                },
                'MetaSpecificationReset'
            );
        }
        return DirtyFileWatcher.watch(languagesFolder, META_FILE_TYPES, dirtyCallback, callbackId);
    }

    static unwatch(watchInfo: { dirtyCallbackId: string; watchIds: string[] }): void {
        DirtyFileWatcher.unwatch(watchInfo);
    }

    static async load(metaLanguagesFolder?: string): Promise<void> {
        const languagesFolder = metaLanguagesFolder ?? `${getLanguageFolder()}`;
        const result = await this.reloadFiles(languagesFolder, META_FILE_TYPES);
        if (this.reloadCallbacks.length > 0) {
            this.reloadCallbacks.forEach(async cb => cb());
        }
        return result;
    }

    private static async reloadFiles(metaLanguagesPath: string, fileTypes: string[]): Promise<void> {
        const files = getFiles(metaLanguagesPath, fileTypes);
        return new Promise<void>(resolve => {
            if (files.length <= 0) {
                resolve();
            }
            let countdown = files.length;
            files.forEach(async (file: string) => {
                const fileExtension = file.slice(file.indexOf('.'));
                try {
                    let metaSpec;
                    if (fileExtension === '.mgl') {
                        const parsedMgl = await loadLanguage(`${metaLanguagesPath}/${file}`, {});
                        metaSpec = JSON.parse(parsedMgl);
                    } else if (fileExtension === '.json') {
                        metaSpec = readJson(`${metaLanguagesPath}/${file}`);
                    }
                    if (metaSpec && CompositionSpecification.is(metaSpec)) {
                        MetaSpecification.merge(metaSpec);
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
