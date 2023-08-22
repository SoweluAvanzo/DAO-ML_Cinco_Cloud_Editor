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
import { CompositionSpecification, MetaSpecification } from '@cinco-glsp/cinco-glsp-common';
import { ActionDispatcher } from '@eclipse-glsp/server-node';
import * as fs from 'fs';
import {
    getFilesFromFolder, getLanguageFolder, getLibLanguageFolder, isBundle, readJson
} from '@cinco-glsp/cinco-glsp-api';

export class MetaSpecificationLoader {

    static load(metaSpecificationFileTypes: string[], actionDispatcher?: ActionDispatcher, metaLanguagesFolder?: string): void {
        const metaLanguagesPath = metaLanguagesFolder ?? `${getLanguageFolder()}`;
        console.log(`loading files from:  ${metaLanguagesPath}`);
        const foundFiles2 = getFilesFromFolder(fs, metaLanguagesPath, './');
        foundFiles2
            .filter((file: string) => {
                const fileExtension = file.slice(file.indexOf('.'));
                const isSupported = metaSpecificationFileTypes.indexOf(fileExtension) >= 0;
                return file !== undefined && isSupported;
            })
            .forEach((file: string) => {
                console.log(`loading meta-specification:  ${file}`);
                const metaSpec = readJson(fs, `${metaLanguagesPath}/${file}`);
                if (CompositionSpecification.is(metaSpec)) {
                    MetaSpecification.merge(metaSpec);
                }
            });

        // update custom palette
        if (actionDispatcher) {
            const paletteUpdateAction = {
                kind: 'enableToolPalette'
            };
            actionDispatcher.dispatch(paletteUpdateAction);
        }
    }

    static clear(): void {
        MetaSpecification.clear();
    }

    static loadClassFiles(supportedDynamicImportFileTypes: string[]): void {
        // Import all injected language-files under './languages/*.ts'
        const languagesPath = `${getLibLanguageFolder()}`;
        console.log(`loading files from:  ${languagesPath}`);
        const foundFiles = getFilesFromFolder(fs, languagesPath, './');
        foundFiles
            .filter((file: string) => {
                const fileExtension = file.slice(file.indexOf('.'));
                const isSupported = supportedDynamicImportFileTypes.indexOf(fileExtension) >= 0;
                return file !== undefined && isSupported;
            })
            .forEach((file: string) => {
                console.log(`loading compiled resource:  ${file}`);
                // fixed internal (resources that are compiled into this server)
                if(isBundle()) {
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
