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

import { DEVELOPMENT_MODE, META_DEV_MODE, getAllHandlerNames, HookTypes } from '@cinco-glsp/cinco-glsp-common';
import { getLanguageFolder, isDevModeArg, readFilesFromDirectories } from './utils/file-helper';

export abstract class LanguageFilesRegistry {
    protected static _overwrite = true;
    protected static _registered: { name: string; cls: any }[] = [];
    protected static _registeredHooks: Map<string, Map<HookTypes, any[]>> = new Map();

    /**
     * There are three ways to activate DEV_MODE:
     * - start with arg '--metaDevMode'
     * - set environment variable 'META_DEV_MODE' to true
     * - set default DEVELOPMENT_MODE runtime variable to true
     */
    static get isMetaDevMode(): boolean {
        if (isDevModeArg()) {
            return true;
        }
        console.log('--metaDevMode - not set');
        const metaDevModeString = process.env[META_DEV_MODE];
        if (!metaDevModeString) {
            console.log('ENV VAR META_DEV_MODE - not set');
        }
        const metaDevMode = metaDevModeString === 'true' || metaDevModeString === 'True' || DEVELOPMENT_MODE;
        return metaDevMode;
    }

    static register(cls: any): void {
        const isHook = this.isHook(cls);

        if (isHook) {
            const hookNotRegistered = this._registeredHooks.get(cls.typeId) === undefined;
            if (this._overwrite || hookNotRegistered) {
                const modelTypeId: string = cls.typeId;
                const hookTypes: HookTypes[] = cls.hookTypes;
                hookTypes.forEach(hookType => {
                    let hookRegistry: Map<HookTypes, any[]> | undefined = this._registeredHooks.get(modelTypeId);
                    if (!hookRegistry) {
                        hookRegistry = new Map();
                        hookRegistry.set(hookType, [cls]);
                        this._registeredHooks.set(modelTypeId, hookRegistry);
                    } else {
                        let hookList: any[] | undefined = hookRegistry.get(hookType);
                        if (!hookList) {
                            hookList = [cls];
                        } else {
                            (hookList as any[]).push(cls);
                        }
                        hookRegistry.set(hookType, hookList);
                        this._registeredHooks.set(modelTypeId, hookRegistry);
                    }
                });
                console.log((this._overwrite && !hookNotRegistered ? 'Reregistered Hook: ' : 'Registered Hook: ') + cls.typeId);
            }
        } else {
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
    }

    static unregister(name: string): void {
        this._registered = this._registered.filter(r => r.name !== name);
        this._registeredHooks.forEach(hookRegistry => {
            hookRegistry.forEach((hookList, hookType) => {
                const filteredHookList = hookList.filter(hook => hook.name !== name);
                hookRegistry.set(hookType, filteredHookList);
            });
        });
        console.log('Unregistered: ' + name);
    }

    static getRegistered(): any[] {
        this._registered = this.fetch();
        return this._registered.map(r => r.cls);
    }

    static getRegisteredHooks(modelTypeId: string, hookType: HookTypes): any[] {
        const hookMap = this._registeredHooks.get(modelTypeId);
        if (hookMap) {
            const hooks = hookMap.get(hookType);
            if (hooks) {
                return hooks;
            }
        }
        return [];
    }

    static fetch(): { name: string; cls: any }[] {
        if (!this.isMetaDevMode) {
            // only reload files in development mode
            // in production this would be a security issue
            return this._registered;
        }
        console.log('*************** META_DEV_MODE - active ***********');

        const handlerToImport = this.collectHandlersToImport();

        // register all found
        this.registerFound(handlerToImport);

        // unregister all outdated files
        this.unregisterOutdated(handlerToImport.map(h => h.name));

        return this._registered;
    }

    static isHook(cls: any): boolean {
        return Object.prototype.hasOwnProperty.call(cls, 'hookTypes');
    }

    static collectHandlersToImport(): { name: string; path: string; content: string }[] {
        const handlerNames = getAllHandlerNames();
        const fileMap = readFilesFromDirectories([getLanguageFolder()], ['.js']);
        const files = Array.from(fileMap.entries());
        const handlerToImport: { name: string; path: string; content: string }[] = [];
        if (files.length <= 0) {
            console.log(`no files found in: ${getLanguageFolder()}`);
        } else {
            console.log(`${files.length} file(s) will be prepared for evaluation.`);
        }
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

    static registerFound(handlerToImport: { name: string; path: string; content: string }[]): void {
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
        const registered = cinco_glsp_api.LanguageFilesRegistry._registered as { name: string; cls: any }[];
        const registeredHooks = cinco_glsp_api.LanguageFilesRegistry._registeredHooks as Map<string, Map<HookTypes, any[]>>;

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
        for (const newInstance of registered) {
            // the registered files from the other package are synchronized to this instance
            this.register(newInstance.cls);
        }
        for (const [, hookRegistry] of registeredHooks) {
            const alreadyRegistered: string[] = [];
            for (const [, hookList] of hookRegistry) {
                for (const hook of hookList) {
                    // the registered hooks from the other package are synchronized to here
                    if (alreadyRegistered.includes(hook.name)) {
                        continue;
                    }
                    this.register(hook);
                    alreadyRegistered.push(hook.name);
                }
            }
        }
    }

    static unregisterOutdated(currentHandler: string[]): void {
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
        const registered = cinco_glsp_api.LanguageFilesRegistry._registered as { name: string; cls: any }[];

        // all registered that are not found in the files, that were ready to be registered
        const outdated1 = this._registered.filter(r => !currentHandler.includes(r.name));
        const outdated2 = registered.filter(r => !currentHandler.includes(r.name));

        const outdated = new Set(outdated1.concat(outdated2).map(o => o.name));
        outdated.forEach(o => {
            this.unregister(o);
            cinco_glsp_api.LanguageFilesRegistry.unregister(o);
        });
    }
}
