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

import {
    META_FILE_TYPES, MetaSpecification,
    SUPPORTED_DYNAMIC_FILE_TYPES, getAllHandlerNames
} from '@cinco-glsp/cinco-glsp-common';
import {
    existsFile, getLanguageFolder, getSubfolder, isMetaDevMode, readFile, readFilesFromDirectories,
    readFileSync
} from '../utils/file-helper';
import * as fs from 'fs';
import { DirtyFileWatcher } from '../api/watcher/dirty-file-watcher';

interface LanguageFilesRegistryEntry {
    name: string;
    cls: any;
}

interface HandlerEntry {
    name: string;
    path: string;
    content: string;
}

/**
 * This registry uses a different more lazy Watcher procedure than the rest.
 * It only needs to fetch the watched files ondemand, as semantics are event driven
 * and not that responsive.
 */
export abstract class LanguageFilesRegistry {
    private static initialized = false;
    private static _overwrite = true;
    private static _dirty: string[] = [];
    private static _registered: LanguageFilesRegistryEntry[] = [];

    static async init(): Promise<void> {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        const languagesFolder = getLanguageFolder();
        DirtyFileWatcher.watch(
            languagesFolder,
            SUPPORTED_DYNAMIC_FILE_TYPES.concat(META_FILE_TYPES),
            async (dirtyFiles: { path: string, eventType: fs.WatchEventType}[]) => {
                for(const dirtyFile of dirtyFiles) {
                    const filename = dirtyFile.path;
                    if (
                        !await existsFile(filename) // a file was deleted -> reload all folders
                        ||
                        MetaSpecification.annotationsChanged()
                    ) {
                        // (no way to identify which file it was by the default filewatcher)
                        this.reloadAllFolders();
                    } else if (!this._dirty.includes(filename)) {
                        this._dirty.push(filename);
                    }
                }
            },
            3
        );
        await this.reloadAllFolders();
    }

    static async getRegistered(): Promise<any[]> {
        if (!this.initialized) {
            throw new Error('LanguageFilesRegistry not yet initialized!');
        }
        if (isMetaDevMode()) {
            this._registered = await this.fetchDirtySemanticFiles(); // fetch dirty files (no parameter)
        }
        return this._registered.map(r => r.cls);
    }

    static getRegisteredSync(): any[] {
        if (!this.initialized) {
            throw new Error('LanguageFilesRegistry not yet initialized!');
        }
        if (isMetaDevMode()) {
            this._registered = this.fetchDirtySemanticFilesSync(); // fetch dirty files (no parameter)
        }
        return this._registered.map(r => r.cls);
    }

    private static async reloadAllFolders(): Promise<void> {
        const languagesFolder = getLanguageFolder();
        const folders = await getSubfolder(languagesFolder);
        folders.push(languagesFolder);
        this._registered = await this.fetchSemanticFiles(folders);
    }

    private static async fetchDirtySemanticFiles(): Promise<LanguageFilesRegistryEntry[]> {
        const handlerToImport = await this.collectDirtyHandlers();
        // register all found
        this.registerFound(handlerToImport);
        // unregister all outdated files
        // this.unregisterOutdated(handlerToImport.map(h => h.name));
        return this._registered;
    }

    private static fetchDirtySemanticFilesSync(): LanguageFilesRegistryEntry[] {
        const handlerToImport = this.collectDirtyHandlersSync();
        // register all found
        this.registerFound(handlerToImport);
        // unregister all outdated files
        // this.unregisterOutdated(handlerToImport.map(h => h.name));
        return this._registered;
    }

    private static async collectDirtyHandlers(): Promise<HandlerEntry[]> {
        const dirtyFiles = this._dirty;
        this._dirty = []; // clear dirty files
        const files = (await Promise.all(dirtyFiles.map(
            async df => [
                df,
                await readFile(df)
            ]
        ))).filter(
            df => df[1] !== undefined // content of read file is "not undefined"/present
        ) as [string, string][];
        return this.collectHandlersToImport(files);
    }

    private static collectDirtyHandlersSync(): HandlerEntry[] {
        const dirtyFiles = this._dirty;
        this._dirty = []; // clear dirty files
        const files = (dirtyFiles.map(
            df => [
                df,
                readFileSync(df)
            ]
        )).filter(
            df => df[1] !== undefined // content of read file is "not undefined"/present
        ) as [string, string][];
        return this.collectHandlersToImport(files);
    }

    private static async fetchSemanticFiles(languagesFolder: string[]): Promise<LanguageFilesRegistryEntry[]> {
        let handlerToImport = [] as HandlerEntry[];
        for (const lf of languagesFolder) {
            handlerToImport = handlerToImport.concat(
                await this.collectHandlersFromFolder(lf)
            );
        }
        // register all found
        this.registerFound(handlerToImport);
        // unregister all outdated files
        this.unregisterOutdated(handlerToImport.map(h => h.name));
        return this._registered;
    }

    private static async collectHandlersFromFolder(folder: string): Promise<HandlerEntry[]> {
        const fileMap = await readFilesFromDirectories([folder], SUPPORTED_DYNAMIC_FILE_TYPES);
        const files = Array.from(fileMap.entries());
        if (files.length <= 0) {
            console.log(`no files found in: ${folder}`);
        } else {
            console.log(`${files.length} file(s) will be prepared for evaluation.`);
        }
        return this.collectHandlersToImport(files);
    }

    private static collectHandlersToImport(files: [string, string][]): HandlerEntry[] {
        const handlerNames = getAllHandlerNames();
        const handlerToImport: HandlerEntry[] = [];
        for (const file of files) {
            const fileContent = file[1];
            for (const handlerName of handlerNames) {
                // tests if the file contains the declaration of a handler class
                /**
                 * TODO: This is not safe. E.g. you can put these conditions inside a `` inside the file inside a const variable.
                 * In this case that file will still be evaluated, i.e. executed.
                 */
                const classDeclaration = `class(\\s)+(${handlerName})(\\s)+extends(\\s)`;
                const classDeclarationTranspiled = `var ${handlerName} = \\/\\*\\* @class \\*\\/`;
                const classRegistration = `(LanguageFilesRegistry\\.register\\()\\s*(${handlerName})(\\s)*(\\))`;
                const containsHandlerName = new RegExp(classDeclaration, 'g').test(fileContent);
                const containsHandlerNameTranspiled = new RegExp(classDeclarationTranspiled, 'g').test(fileContent);
                const containsRegistration = new RegExp(classRegistration, 'g').test(fileContent);

                if ((containsHandlerName || containsHandlerNameTranspiled) && containsRegistration) {
                    handlerToImport.push({ name: handlerName, path: file[0], content: fileContent });
                    break;
                }
            }
        }
        return handlerToImport;
    }

    private static registerFound(handlerToImport: HandlerEntry[]): void {
        /**
         * Javascript can have two of the same static classes at the same time,
         * from different packages (Package A and B both use Package C, containing the class).
         *
         * In this case the handler registers the LanguageFile in a
         * dublicate found at require('@cinco-glsp/cinco-glsp-api') instead of
         * this very static instance. Thats why it needs to self reflect to
         * access the other static instance used by the evaluated language-file
         * to update it's own.
         */
        // eslint-disable-next-line no-eval
        const cinco_glsp_api = eval("( require('@cinco-glsp/cinco-glsp-api') )");

        // Overload MetaSpecification
        // eslint-disable-next-line no-eval
        const cinco_glsp_common = eval("( require('@cinco-glsp/cinco-glsp-common') )");
        cinco_glsp_common.MetaSpecification.merge(MetaSpecification.get());
        cinco_glsp_common.MetaSpecification.importCachedComputations(MetaSpecification.exportCachedComputations());

        // load handler code
        for (const handler of handlerToImport) {
            try {
                // eslint-disable-next-line no-eval
                eval(`
                    ${handler.content}
                `);
            } catch (e) {
                console.log(e);
            }
        }
        if (handlerToImport) {
            const registered = cinco_glsp_api.LanguageFilesRegistry._registered as LanguageFilesRegistryEntry[];

            for (const newInstance of registered) {
                // the registered from the other package are synchronized to here
                this.register(newInstance.cls);
            }
        }
    }

    private static unregisterOutdated(currentHandler: string[]): void {
        /**
         * Javascript can have two of the same static classes at the same time,
         * from different packages (Package A and B both use Package C, containing the class).
         *
         * In this case the handler registers the LanguageFile in a
         * dublicate found at require('@cinco-glsp/cinco-glsp-api') instead of
         * this very static instance. Thats why it needs to self reflect to
         * access the other static instance used by the evaluated language-file
         * to update it's own.
         */
        // eslint-disable-next-line no-eval
        const cinco_glsp_api = eval("( require('@cinco-glsp/cinco-glsp-api') )");
        const registered = cinco_glsp_api.LanguageFilesRegistry._registered as LanguageFilesRegistryEntry[];

        // all registered that are not found in the files, that were ready to be registered
        const outdated1 = this._registered.filter(r => !currentHandler.includes(r.name));
        const outdated2 = registered.filter(r => !currentHandler.includes(r.name));

        const outdated = new Set(outdated1.concat(outdated2).map(o => o.name));
        outdated.forEach(o => {
            this.unregister(o);
            cinco_glsp_api.LanguageFilesRegistry.unregister(o);
        });
    }

    /**
     * This method is used by the Semantics to register themselves
     * @param cls class of the semantic (e.g. ExampleDoubleClickHandler)
     */
    static register(cls: any): void {
        const containedClass = this._registered.find(r => r.name === cls.name);
        if (this._overwrite || containedClass === undefined) {
            if (this._overwrite) {
                // remove old containedClass
                this._registered = this._registered.filter(r => r !== containedClass);
                this._registered.push({ name: cls.name, cls: cls });
                console.log('Reregistered: ' + cls.name);
            } else {
                this._registered.push({ name: cls.name, cls: cls });
                console.log('Registered: ' + cls.name);
            }
        }
    }

    /**
     * This method is used by the Semantics to unregister themselves
     * @param name name of the semantic (e.g. ExampleDoubleClickHandler)
     */
    static unregister(name: string): void {
        console.log('Uregistered: ' + name);
        this._registered = this._registered.filter(r => r.name !== name);
    }
}
