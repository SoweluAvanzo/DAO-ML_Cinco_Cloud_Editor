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
    ElementType,
    FileProviderRequest,
    FileProviderResponseItem,
    MetaSpecification,
    RESOURCE_TYPES,
    getAllPaletteCategories,
    getIcon,
    getPaletteIconClass,
    getPaletteIconPath
} from '@cinco-glsp/cinco-glsp-common';
import { IActionDispatcher } from '@eclipse-glsp/client';
import { WorkspaceFileService } from '../utils/workspace-file-service';
import { ServerArgsProvider } from './server-args-response-handler';
import { FileProviderHandler } from '../features/action-handler/file-provider-handler';

export class FrontendResourceLoader {
    static _locks: (() => void)[] = [];
    static _locked = false;
    static currentIconMap = new Map();
    static currentPaletteIconMap = new Map();
    static currentCSS: { id: string; css: string }[] = [];

    static async load(
        actionDispatcher: IActionDispatcher,
        workspaceFileService: WorkspaceFileService,
        supportedDynamicImportFileTypes: string[] = RESOURCE_TYPES
    ): Promise<void> {
        const items = await FileProviderHandler.getFiles(
            FileProviderRequest.META_LANGUAGES_FOLDER_KEYWORD,
            false,
            supportedDynamicImportFileTypes,
            actionDispatcher
        );
        return FrontendResourceLoader.importResources(items, workspaceFileService);
    }

    static async importResources(resources: FileProviderResponseItem[], workspaceFileService: WorkspaceFileService): Promise<void> {
        // lock
        if (FrontendResourceLoader._locked) {
            await new Promise<void>(resolve => {
                FrontendResourceLoader._locks.push(resolve);
            });
        }
        FrontendResourceLoader._locked = true;

        const serverArgs = await ServerArgsProvider.getServerArgs();

        for (const file of resources.filter(r => r.path.endsWith('diagram.css'))) {
            // TODO: Documentation
            await this.loadCSSFile(`${file.path}`, serverArgs.metaDevMode, workspaceFileService);
        }
        await this.addIconStyle(`${serverArgs.rootFolder}/${serverArgs.languagePath}/`, workspaceFileService);

        // unlock
        const toUnlock = FrontendResourceLoader._locks.pop();
        if (toUnlock) {
            toUnlock();
        }
        FrontendResourceLoader._locked = false;
    }

    static async loadCSSFile(absoluteFilePath: string, overwrite = false, workspaceFileService: WorkspaceFileService): Promise<void> {
        if (this.hasChild(absoluteFilePath)) {
            if (!overwrite) {
                // only provide if not already loaded
                return;
            } else {
                this.replaceOrBust(absoluteFilePath);
            }
        }
        const url = await workspaceFileService.serveFileInRoot('', absoluteFilePath);
        if (url) {
            const fileref = document.createElement('link');
            fileref.setAttribute('id', absoluteFilePath);
            fileref.setAttribute('rel', 'stylesheet');
            fileref.setAttribute('type', 'text/css');
            fileref.setAttribute('href', url);
            document.getElementsByTagName('head')[0].appendChild(fileref);
        }
    }

    static async addIconStyle(iconFolder: string, workspaceFileService: WorkspaceFileService): Promise<void> {
        // collect distinct icon types
        let types: ElementType[] = Array.from(MetaSpecification.get().nodeTypes ?? []);
        types = types.concat(Array.from(MetaSpecification.get().edgeTypes ?? []));
        const iconMap = new Map<string, string>();
        for (const t of types) {
            const typeClass = t.elementTypeId.replace(':', '_');
            const iconPath = getIcon(t.elementTypeId) ?? '';
            iconMap.set(typeClass, iconPath);
        }

        // collect palette icon types
        const paletteCategories = getAllPaletteCategories(false);
        const paletteIconMap = new Map<string, string>();
        for (const p of paletteCategories) {
            const paletteIconClass = getPaletteIconClass(p);
            const paletteIconPath = getPaletteIconPath(p);
            if (paletteIconClass && paletteIconPath) {
                paletteIconMap.set(paletteIconClass, paletteIconPath);
            }
        }

        const changedIcons = this.hasChanged(this.currentIconMap, iconMap);
        const changedPaletteIcons = this.hasChanged(this.currentPaletteIconMap, paletteIconMap);
        if (changedIcons || changedPaletteIcons) {
            // get changes
            const { newValues: newValuesIcon, removedKeys: removedKeysIcon } = this.getChanges(this.currentIconMap, iconMap);
            const { newValues: newValuesPalette, removedKeys: removedKeysPalette } = this.getChanges(
                this.currentPaletteIconMap,
                paletteIconMap
            );
            this.currentIconMap = iconMap;
            this.currentPaletteIconMap = paletteIconMap;

            // serve new
            // remove outdated
            this.currentCSS = this.currentCSS.filter(
                iconType =>
                    !removedKeysIcon.includes(iconType.id) &&
                    !removedKeysPalette.includes(iconType.id) &&
                    !Array.from(newValuesIcon.keys()).includes(iconType.id) &&
                    !Array.from(newValuesPalette.keys()).includes(iconType.id)
            );
            this.currentCSS = this.currentCSS.concat(await this.serveIcons(newValuesIcon, iconFolder, workspaceFileService));
            this.currentCSS = this.currentCSS.concat(await this.serveIcons(newValuesPalette, iconFolder, workspaceFileService));
            // translate
            const css = this.currentCSS.map(e => e.css).join('\n');

            const icon_id = 'cinco.icon.style';
            const icon_style = document.createElement('style');
            // create new
            icon_style.setAttribute('id', icon_id);
            icon_style.innerHTML = `
                ${css}
            `;
            this.replaceOrBust(icon_id, icon_style);
        }
    }

    static hasChanged(oldMap: Map<string, any>, newMap: Map<string, any>): boolean {
        if (oldMap.size !== newMap.size) {
            return true;
        }
        for (const entry of oldMap.entries()) {
            const key = entry[0];
            const value = entry[1];
            if (
                !newMap.has(key) || // key was removed in newMap
                newMap.get(key) !== value // value has changed in newMap
            ) {
                return true;
            }
        }
        return false;
    }

    static getChanges(oldMap: Map<string, any>, newMap: Map<string, any>): { newValues: Map<string, any>; removedKeys: string[] } {
        const newValues = new Map();
        const removedKeys: string[] = [];

        oldMap.forEach((value, key) => {
            if (!newMap.has(key)) {
                // removed value
                removedKeys.push(key);
            } else if (newMap.get(key) !== value) {
                // changed Values
                newValues.set(key, newMap.get(key));
            }
        });
        newMap.forEach((value, key) => {
            if (!oldMap.has(key)) {
                // new Values
                newValues.set(key, newMap.get(key));
            }
        });
        return {
            newValues,
            removedKeys
        };
    }

    static async serveIcons(
        iconMap: Map<string, string>,
        iconFolder: string,
        workspaceFileService: WorkspaceFileService
    ): Promise<{ id: string; css: string }[]> {
        const css: { id: string; css: string }[] = [];
        for (const icon of iconMap.entries()) {
            const iconType = icon[0];
            const iconPath = icon[1];
            let url = undefined;
            if (iconPath && iconPath.length > 0) {
                url = await workspaceFileService.serveFileInRoot(iconFolder, iconPath);
            }
            if (url) {
                css.push({
                    id: iconType,
                    css: `
                            .codicon-${iconType} {
                                width: 16px;
                                height: 16px;
                                background-image: ${url ? `url('${url}')` : 'none'};
                                background-repeat: no-repeat;
                                background-position: center;
                            }
                        `
                });
            }
        }
        return css;
    }

    static replaceOrBust(id: string, replacingElement?: HTMLElement): void {
        const children = Array.from(document.head.getElementsByTagName(replacingElement?.tagName.toLowerCase() ?? 'LINK'));
        for (const current of children) {
            if (current && current.id === id) {
                document.head.removeChild(current);
                if (replacingElement) {
                    document.head.appendChild(replacingElement);
                }
                return;
            }
        }
        if (replacingElement) {
            document.head.appendChild(replacingElement);
        }
    }

    static hasChild(id: string): boolean {
        const children = document.head.getElementsByTagName('LINK');
        const length = children.length;
        for (let i = 0; i < length; i++) {
            const current = children.item(i);
            if (current && current.id === id) {
                return true;
            }
        }
        return false;
    }
}
