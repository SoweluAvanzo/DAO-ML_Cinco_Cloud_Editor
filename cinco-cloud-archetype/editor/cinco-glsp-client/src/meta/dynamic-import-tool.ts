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
    RESOURCE_TYPES
} from '@cinco-glsp/cinco-glsp-common';
import { IActionDispatcher } from '@eclipse-glsp/client';
import { WorkspaceFileService } from '../utils/workspace-file-service';
import { ServerArgsProvider } from './server-args-response-handler';
import { FileProviderHandler } from '../features/file-provider-handler';

export class DynamicImportLoader {
    static _locks: (() => void)[] = [];
    static _locked = false;

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
        return DynamicImportLoader.importResources(items, workspaceFileService);
    }

    static async importResources(resources: FileProviderResponseItem[], workspaceFileService: WorkspaceFileService): Promise<void> {
        // lock
        if (DynamicImportLoader._locked) {
            await new Promise<void>(resolve => {
                DynamicImportLoader._locks.push(resolve);
            });
        }
        DynamicImportLoader._locked = true;

        const serverArgs = await ServerArgsProvider.getServerArgs();
        for (const file of resources) {
            await this.loadCSSFile(
                `${serverArgs.rootFolder}/${serverArgs.languagePath}/`,
                `${file.path}`,
                serverArgs.metaDevMode,
                workspaceFileService
            );
        }
        await this.addIconStyle(`${serverArgs.rootFolder}/${serverArgs.languagePath}/icons/`, workspaceFileService);

        // unlock
        const toUnlock = DynamicImportLoader._locks.pop();
        if (toUnlock) {
            toUnlock();
        }
        DynamicImportLoader._locked = false;
    }

    static async loadCSSFile(root: string, filePath: string, overwrite = false, workspaceFileService: WorkspaceFileService): Promise<void> {
        if (this.hasChild(filePath)) {
            if (!overwrite) {
                // only provide if not already loaded
                return;
            } else {
                this.bustChild(filePath);
            }
        }
        const url = await workspaceFileService.serveFileInRoot('', root + filePath);
        if (url) {
            const fileref = document.createElement('link');
            fileref.setAttribute('id', filePath);
            fileref.setAttribute('rel', 'stylesheet');
            fileref.setAttribute('type', 'text/css');
            fileref.setAttribute('href', url);
            document.getElementsByTagName('head')[0].appendChild(fileref);
        }
    }

    static async addIconStyle(iconFolder: string, workspaceFileService: WorkspaceFileService): Promise<void> {
        const icon_id = 'cinco.icon.style';
        this.bustChild(icon_id, 'style');

        const icon_style = document.createElement('style');
        icon_style.setAttribute('id', icon_id);

        // collect distinct types
        let types: ElementType[] = Array.from(MetaSpecification.get().nodeTypes ?? []);
        types = types.concat(Array.from(MetaSpecification.get().edgeTypes ?? []));
        const iconTypes = Array.from(new Set(types.map(t => t.icon ?? t.elementTypeId.replace(':', '_')))) as string[];

        const icon_files = (await FileProviderHandler.getFiles(iconFolder)).map(v => v.path) as string[];
        const existingIconTypes = icon_files.filter(t => iconTypes.includes(t.slice(0, t.lastIndexOf('.'))));

        const css = [];
        for (const iconFile of existingIconTypes) {
            const url = await workspaceFileService.serveFileInRoot(iconFolder, iconFile);
            const iconType = iconFile.slice(0, iconFile.lastIndexOf('.')).replace('.', '_');
            if (url) {
                css.push(`
                    .codicon-${iconType} {
                        width: 16px;
                        height: 16px;
                        background-image: url('${url}');
                    }
                `);
            }
        }
        icon_style.innerHTML = `
            ${css.join('\n')}
        `;
        document.getElementsByTagName('head')[0].appendChild(icon_style);
    }

    static bustChild(id: string, tagType = 'LINK'): void {
        const children = Array.from(document.head.getElementsByTagName(tagType));
        for (const current of children) {
            if (current && current.id === id) {
                document.head.removeChild(current);
            }
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
