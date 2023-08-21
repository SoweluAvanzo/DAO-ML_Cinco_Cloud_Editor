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

import { DEVELOPMENT_MODE, META_DEV_MODE, META_LANGUAGES_FOLDER, getAllHandlerNames } from '@cinco-glsp/cinco-glsp-common';
import * as fs from 'fs';
import { readFilesFromDirectories } from './utils/file-helper';

export class LanguageFilesRegistry {
    protected static _overwrite = false;
    protected static _registered: { name: string; cls: any }[] = [];
    protected static get DEV_MODE(): boolean {
        const metaDevModeString = process.env[META_DEV_MODE];
        const metaDevMode = metaDevModeString === 'true' || metaDevModeString === 'True' || DEVELOPMENT_MODE;
        return metaDevMode;
    }

    static register(cls: any): void {
        const containedClass = this._registered.find(r => r.name === cls.name);
        if (LanguageFilesRegistry._overwrite || containedClass === undefined) {
            if (this._overwrite) {
                // remove old containedClass
                this._registered = this._registered.filter(r => r !== containedClass);
                console.log('Reregistered: ', cls);
            } else {
                console.log('Registered: ', cls);
            }
            this._registered.push({ name: cls.name, cls: cls });
        }
    }

    static getRegistered(): any[] {
        return this._registered.map(r => r.cls);
    }

    static fetch(): void {
        if (!this.DEV_MODE) {
            // only reload files in development mode
            // in production this would be a security issue
            return;
        }
        const handlerNames = getAllHandlerNames();
        const fileMap = readFilesFromDirectories(fs, [META_LANGUAGES_FOLDER], ['.js']);
        const files = Array.from(fileMap.entries());
        const handlerToImport: { name: string; path: string; content: string }[] = [];
        for (const file of files) {
            const fileContent = file[1];
            for (const handlerName of handlerNames) {
                // tests if the file contains the declaration of a handler class
                /**
                 * TODO: This is not safe. E.g. you can put these conditions inside a `` inside the file inside a const variable.
                 * In this case that file will still be evaluated, i.e. executed.
                 */
                const classDeclaration = `class(\\s)+(${handlerName})(\\s)+extends(\\s)`;
                const classRegistration = `(LanguageFilesRegistry\\.register\\()\\s*(${handlerName})(\\s)*(\\))`;
                const containsHandlerName = new RegExp(classDeclaration, 'g').test(fileContent);
                const containsRegistration = new RegExp(classRegistration, 'g').test(fileContent);

                if (containsHandlerName && containsRegistration) {
                    handlerToImport.push({ name: handlerName, path: file[0], content: fileContent });
                    break;
                }
            }
        }
        this._overwrite = true;
        // load handler code
        for (const handler of handlerToImport) {
            // eslint-disable-next-line no-eval
            eval(`
                ${handler.content}
            `);
        }
    }
}
