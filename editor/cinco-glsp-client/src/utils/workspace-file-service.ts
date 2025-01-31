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

import { ALLOWED_IMAGE_FILE_TYPES, FileProviderResponseItem } from '@cinco-glsp/cinco-glsp-common';
import URI from '@theia/core/lib/common/uri';
import { inject, injectable, optional } from 'inversify';
import * as path from 'path';
import { ServerArgsProvider } from '../meta/server-args-response-handler';
import { IActionDispatcher, TYPES } from '@eclipse-glsp/client';
import { FileProviderHandler } from '../features/action-handler/file-provider-handler';
import { EnvironmentProvider, IEnvironmentProvider } from '../api/environment-provider';

@injectable()
export class WorkspaceFileService {
    @inject(EnvironmentProvider) @optional() environmentProvider: IEnvironmentProvider;
    @inject(TYPES.IActionDispatcher) @optional() actionDispatcher: IActionDispatcher;
    @inject(ServerArgsProvider) @optional() ServerArgsProvider: ServerArgsProvider;
    protected static BLACKLIST = ['http://', 'https://'];
    protected static CACHED_FILES: Map<string, FileProviderResponseItem[]> = new Map();

    // TODO: SAMI - This polling should be replaced by pushes from the server, who watches the files. But this is currently sufficient
    protected static RELOAD_DELAY = 2000;
    protected static FILE_TIMER: NodeJS.Timeout | undefined = undefined;

    static initUpdatePolling(actionDispatcher: IActionDispatcher): void {
        if (!this.FILE_TIMER) {
            this.startDirtyCheck(actionDispatcher);
        }
    }

    protected static async startDirtyCheck(actionDispatcher: IActionDispatcher): Promise<void> {
        this.FILE_TIMER = setTimeout(async () => {
            try {
                for (const folder of this.CACHED_FILES.keys()) {
                    await this.updateCachedFiles(folder, actionDispatcher);
                }
            } catch (e) {
                console.log(e);
            }
            this.startDirtyCheck(actionDispatcher);
        }, this.RELOAD_DELAY);
    }

    protected static async updateCachedFiles(dir: string, actionDispatcher: IActionDispatcher): Promise<void> {
        const newFiles = await FileProviderHandler.getFiles(dir, false, ALLOWED_IMAGE_FILE_TYPES, actionDispatcher);
        WorkspaceFileService.CACHED_FILES.set(dir, newFiles);
    }

    protected static dirIsCached(dir: string): boolean {
        return WorkspaceFileService.CACHED_FILES.get(dir) !== undefined;
    }

    async serveFile(filePath: string): Promise<string | undefined> {
        if (WorkspaceFileService.BLACKLIST.filter(e => filePath.startsWith(e)).length > 0) {
            return filePath;
        }
        const serverArgs = await ServerArgsProvider.getServerArgs();
        const relativeLanguagePath = (serverArgs?.languagePath ?? '') + '/' + filePath;
        const result = await this.serveFileInRoot(serverArgs.rootFolder, relativeLanguagePath);
        if (result) {
            return result;
        } else {
            const relativeWorkspacePath = (serverArgs?.workspacePath ?? '') + '/' + filePath;
            return this.serveFileInRoot(serverArgs.rootFolder, relativeWorkspacePath);
        }
    }

    async serveFileInRoot(root: string, relativePath: string): Promise<string | undefined> {
        // eslint-disable-next-line no-async-promise-executor
        const result = await new Promise<{ response: Response; jsonResponse: any }>(async resolve => {
            const absolutePath = path.join(root, relativePath);
            try {
                const request = await this.request([new URI(absolutePath)]);
                const resp = await fetch(request);
                const jsonResp = await resp.json();
                resolve({ response: resp, jsonResponse: jsonResp });
            } catch (e) {
                resolve({ response: { status: 404 } as Response, jsonResponse: {} as any });
            }
        });
        const { response, jsonResponse } = result;
        if (response.status === 200) {
            return `${await this.endpoint()}/download/?id=${jsonResponse.id}`;
        }
        return undefined;
    }

    async download(url: string): Promise<string | undefined> {
        // eslint-disable-next-line no-async-promise-executor
        const result = await new Promise<string | undefined>(async resolve => {
            try {
                const resp = await fetch(url);
                const responseText = await resp.text();
                resolve(responseText);
            } catch (e) {
                resolve(undefined);
            }
        });
        return result;
    }

    async servedExistsIn(filePath: string, actionDispatcher: IActionDispatcher): Promise<string | undefined> {
        if (WorkspaceFileService.BLACKLIST.filter(e => filePath.startsWith(e)).length > 0) {
            return filePath;
        }
        const serverArgs = await ServerArgsProvider.getServerArgs();
        WorkspaceFileService.initUpdatePolling(this.actionDispatcher);
        let exists;
        if (filePath.startsWith('/')) {
            return (await this.fileExists('', filePath, actionDispatcher)) ? filePath : undefined;
        } else {
            exists = await this.fileExists(serverArgs?.languagePath, filePath, actionDispatcher);
            if (exists) {
                return exists ? path.join(serverArgs?.languagePath, filePath) : undefined;
            } else {
                const workspacePath: string = await this.environmentProvider.getWorkspaceRoot();
                return (await this.fileExists(serverArgs?.workspacePath ?? workspacePath, filePath, actionDispatcher))
                    ? path.join(serverArgs.workspacePath, filePath)
                    : undefined;
            }
        }
    }

    protected async fileExists(dir: string, filePath: string, actionDispatcher: IActionDispatcher): Promise<boolean> {
        if (!WorkspaceFileService.dirIsCached(dir)) {
            await WorkspaceFileService.updateCachedFiles(dir, actionDispatcher);
        }
        const files: FileProviderResponseItem[] = WorkspaceFileService.CACHED_FILES.get(dir) ?? [];
        const folderPath = path.join(dir, filePath);
        return files.filter(f => f.path.endsWith(folderPath)).length > 0;
    }

    protected async request(uris: URI[]): Promise<Request> {
        const url = await this.toFileUrl(uris);
        const init = this.requestInit(uris);
        return new Request(url, init);
    }

    protected requestInit(uris: URI[]): RequestInit {
        if (uris.length === 1) {
            return {
                body: undefined,
                method: 'GET'
            };
        }
        return {
            method: 'PUT',
            body: JSON.stringify(this.body(uris)),
            headers: new Headers({ 'Content-Type': 'application/json' })
        };
    }

    protected body(uris: URI[]): { uris: string[] } {
        return {
            uris: uris.map(u => u.toString(true))
        };
    }

    protected async toFileUrl(uris: URI[]): Promise<string> {
        const endpoint = await this.endpoint();
        if (uris.length === 1) {
            // tslint:disable-next-line:whitespace
            const [uri] = uris;
            return `${endpoint}/?uri=${uri.toString()}`;
        }
        return endpoint;
    }

    protected async endpoint(): Promise<string> {
        const url = await this.filesUrl();
        return url.endsWith('/') ? url.slice(0, -1) : url;
    }

    protected async filesUrl(): Promise<string> {
        return (await this.getRestUrl({ path: 'files' })).toString();
    }

    protected async getRestUrl(options: { protocol?: string; location?: string; pathname?: string; path?: string }): Promise<URI> {
        const serverArgs = await ServerArgsProvider.getServerArgs();
        let url_path = '';
        if (options.path) {
            if (options.path.startsWith('/')) {
                url_path = options.path;
            } else {
                url_path = '/' + options.path;
            }
        }
        let pathname = '';
        if (options.pathname) {
            if (options.pathname === '/') {
                pathname = '';
            } else if (options.pathname.endsWith('/')) {
                pathname = options.pathname.substring(0, options.pathname.length - 1);
            } else {
                pathname = options.pathname;
            }
        }
        const hostname =
            options.location ?? (window.location.hostname && window.location.hostname.length > 0 ? window.location.hostname : '0.0.0.0');
        const host =
            hostname +
            (serverArgs.webServerHostMapping
                ? '/' + serverArgs.webServerHostMapping
                : serverArgs.webServerPort
                  ? `:${serverArgs.webServerPort}`
                  : '');
        const protocol = (options.protocol ?? 'http') + (serverArgs.useSSL ? 's' : '');
        return new URI(`${protocol}://${host}${pathname}${url_path}`);
    }
}
