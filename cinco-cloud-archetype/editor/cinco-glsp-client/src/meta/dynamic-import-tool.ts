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
    ALLOWED_IMAGE_FILE_TYPES,
    ElementType,
    FileProviderRequest,
    FileProviderResponse,
    FileProviderResponseItem,
    MetaSpecification,
    RESOURCE_TYPES
} from '@cinco-glsp/cinco-glsp-common';
import { Action, IActionDispatcher, IActionHandler, ICommand } from '@eclipse-glsp/client';
import { injectable, inject } from 'inversify';
import { WorkspaceFileService } from '../utils/workspace-file-service';
import { ServerArgsProvider } from './server-args-response-handler';

@injectable()
export class DynamicImportLoader implements IActionHandler {
    @inject(WorkspaceFileService) workspaceFileService: WorkspaceFileService;

    static REQUESTED_IDs: string[] = [];
    static instance: DynamicImportLoader;

    static _locks: (() => void)[] = [];
    static _locked = false;

    handle(action: FileProviderResponse): ICommand | Action | void {
        if (!DynamicImportLoader.instance) {
            DynamicImportLoader.instance = this;
        }
        if (DynamicImportLoader.REQUESTED_IDs.indexOf(action.requestId) >= 0) {
            DynamicImportLoader.instance.importResources(action.items);
        }
    }

    static load(actionDispatcher: IActionDispatcher, supportedDynamicImportFileTypes: string[] = RESOURCE_TYPES): Promise<void> {
        const request = FileProviderRequest.create(
            [FileProviderRequest.META_LANGUAGES_FOLDER_KEYWORD],
            false,
            supportedDynamicImportFileTypes
        );
        DynamicImportLoader.REQUESTED_IDs.push(request.requestId);
        return actionDispatcher.dispatch(request);
    }

    private async importResources(resources: FileProviderResponseItem[]): Promise<void> {
        // lock
        if (DynamicImportLoader._locked) {
            await new Promise<void>(resolve => {
                DynamicImportLoader._locks.push(resolve);
            });
        }
        DynamicImportLoader._locked = true;

        const serverArgs = await ServerArgsProvider.getServerArgs();
        for (const file of resources) {
            await this.loadCSSFile(`${serverArgs.rootFolder}/${serverArgs.languagePath}/`, `${file.path}`, serverArgs.metaDevMode);
        }
        await this.addIconStyle(`${serverArgs.rootFolder}/${serverArgs.languagePath}/icons/`);

        // unlock
        const toUnlock = DynamicImportLoader._locks.pop();
        if (toUnlock) {
            toUnlock();
        }
        DynamicImportLoader._locked = false;
    }

    private async loadCSSFile(root: string, filePath: string, overwrite = false): Promise<void> {
        if (this.hasChild(filePath)) {
            if (!overwrite) {
                // only provide if not already loaded
                return;
            } else {
                this.bustChild(filePath);
            }
        }
        const url = await this.workspaceFileService.serveFileInRoot('', root + filePath);
        if (url) {
            const fileref = document.createElement('link');
            fileref.setAttribute('id', filePath);
            fileref.setAttribute('rel', 'stylesheet');
            fileref.setAttribute('type', 'text/css');
            fileref.setAttribute('href', url);
            document.getElementsByTagName('head')[0].appendChild(fileref);
        }
    }

    private async addIconStyle(iconFolder: string): Promise<void> {
        const icon_id = 'cinco.icon.style';
        this.bustChild(icon_id, 'style');

        const icon_style = document.createElement('style');
        icon_style.setAttribute('id', icon_id);

        // collect distinct types
        let types: ElementType[] = Array.from(MetaSpecification.get().nodeTypes ?? []);
        types = types.concat(Array.from(MetaSpecification.get().edgeTypes ?? []));
        const iconTypes = new Set(types.map(t => t.icon ?? t.elementTypeId.replace(':', '_').toLowerCase()));

        const css = [];
        for (const iconType of iconTypes) {
            let url = undefined;
            for (const ext of ALLOWED_IMAGE_FILE_TYPES) {
                url = await this.workspaceFileService.serveFileInRoot(iconFolder, `${iconType}${ext}`);
                if (url) {
                    break;
                }
            }
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

    private bustChild(id: string, tagType = 'LINK'): void {
        const children = Array.from(document.head.getElementsByTagName(tagType));
        for (const current of children) {
            if (current && current.id === id) {
                document.head.removeChild(current);
            }
        }
    }

    private hasChild(id: string): boolean {
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
