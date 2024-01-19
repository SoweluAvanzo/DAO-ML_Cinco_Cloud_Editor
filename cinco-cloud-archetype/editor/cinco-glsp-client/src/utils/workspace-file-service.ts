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
import { CommandService } from '@theia/core';
import URI from '@theia/core/lib/common/uri';
import { inject, injectable, optional } from 'inversify';
import * as path from 'path';
import { ServerArgsProvider } from '../meta/server-args-response-handler';
import { IActionDispatcher, TYPES } from '@eclipse-glsp/client';
import { FileProviderHandler } from '../features/action-handler/file-provider-handler';

/**
 * THEIA-related
 */
@injectable()
export class WorkspaceFileService {
    @inject(CommandService) @optional() commandService: CommandService;
    @inject(TYPES.IActionDispatcher) @optional() actionDispatcher: IActionDispatcher;
    @inject(ServerArgsProvider) @optional() ServerArgsProvider: ServerArgsProvider;
    protected static BLACKLIST = ['http://', 'https://'];

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
            if (this.commandService) {
                // workspace when in theia
                return this.serveFileIn(filePath);
            } else {
                // workspace in standalone
                const relativeWorkspacePath = (serverArgs?.workspacePath ?? '') + '/' + filePath;
                return this.serveFileInRoot(serverArgs.rootFolder, relativeWorkspacePath);
            }
        }
    }

    protected async serveFileIn(relativePath: string): Promise<string | undefined> {
        const roots: URI[] = (await this.commandService.executeCommand('workspaceRootProviderHandler')) ?? [];
        for (const root of roots) {
            const result = this.serveFileInRoot(root.path.fsPath(), relativePath);
            if (result) {
                return result;
            }
        }
        return undefined;
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

    async servedExists(filePath: string, actionDispatcher: IActionDispatcher): Promise<boolean> {
        if (WorkspaceFileService.BLACKLIST.filter(e => filePath.startsWith(e)).length > 0 || !this.commandService) {
            return true;
        }
        const serverArgs = await ServerArgsProvider.getServerArgs();
        let exists;
        if (filePath.startsWith('/')) {
            exists = await this.fileExists('', filePath, actionDispatcher);
        } else {
            exists = await this.fileExists(serverArgs?.languagePath, filePath, actionDispatcher);
            if (!exists) {
                if (this.commandService) {
                    const theiaRoots: URI[] = (await this.commandService.executeCommand('workspaceRootProviderHandler')) ?? [];
                    for (const theiaRoot of theiaRoots) {
                        return this.fileExists(theiaRoot.path.fsPath(), filePath, actionDispatcher);
                    }
                } else {
                    return this.fileExists(serverArgs?.workspacePath, filePath, actionDispatcher);
                }
            }
            return true;
        }
        return exists;
    }

    async servedExistsIn(filePath: string, actionDispatcher: IActionDispatcher): Promise<string | undefined> {
        if (WorkspaceFileService.BLACKLIST.filter(e => filePath.startsWith(e)).length > 0 || !this.commandService) {
            return filePath;
        }
        const serverArgs = await ServerArgsProvider.getServerArgs();
        let exists;
        if (filePath.startsWith('/')) {
            return (await this.fileExists('', filePath, actionDispatcher)) ? filePath : undefined;
        } else {
            exists = await this.fileExists(serverArgs?.languagePath, filePath, actionDispatcher);
            if (exists) {
                return exists ? path.join(serverArgs?.languagePath, filePath) : undefined;
            } else {
                if (this.commandService) {
                    const theiaRoots: URI[] = (await this.commandService.executeCommand('workspaceRootProviderHandler')) ?? [];
                    for (const theiaRoot of theiaRoots) {
                        if (await this.fileExists(theiaRoot.path.fsPath(), filePath, actionDispatcher)) {
                            return path.join(theiaRoot.path.fsPath(), filePath);
                        }
                    }
                    return undefined;
                } else {
                    return (await this.fileExists(serverArgs?.workspacePath, filePath, actionDispatcher))
                        ? path.join(serverArgs.workspacePath, filePath)
                        : undefined;
                }
            }
        }
    }

    protected async fileExists(dir: string, filePath: string, actionDispatcher: IActionDispatcher): Promise<boolean> {
        const response: FileProviderResponseItem[] = await FileProviderHandler.getFiles(
            dir,
            false,
            ALLOWED_IMAGE_FILE_TYPES,
            this.actionDispatcher
        );
        return response.filter(f => f.path === filePath).length > 0;
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
        return new URI(
            `${options.protocol ?? 'http'}://${options.location ?? 'localhost:' + serverArgs.webServerPort}${pathname}${url_path}`
        );
    }
}
