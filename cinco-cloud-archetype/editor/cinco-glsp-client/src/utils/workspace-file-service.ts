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

import { DEFAULT_THEIA_PORT } from '@cinco-glsp/cinco-glsp-common';
import { CommandService } from '@theia/core';
import URI from '@theia/core/lib/common/uri';
import { inject, injectable, optional } from 'inversify';
import * as path from 'path';
import { ServerArgsProvider } from '../meta/server-args-response-handler';

/**
 * THEIA-related
 */
@injectable()
export class WorkspaceFileService {
    @inject(CommandService) @optional() commandService: CommandService;
    @inject(ServerArgsProvider) @optional() ServerArgsProvider: ServerArgsProvider;
    protected static BLACKLIST = ['http://', 'https://'];

    async serveFile(filePath: string): Promise<string | undefined> {
        if (WorkspaceFileService.BLACKLIST.filter(e => filePath.startsWith(e)).length > 0 || !this.commandService) {
            // if http => return undefined
            return undefined;
        }
        const serverArgs = await ServerArgsProvider.getServerArgs();
        const relativeLanguagePath = (serverArgs?.languagePath ?? '') + '/' + filePath;
        const relativeWorkspacePath = (serverArgs?.workspacePath ?? '') + '/' + filePath;
        const result = await this.serveFileInRoot(serverArgs.rootFolder, relativeLanguagePath);
        if (result) {
            return result;
        } else {
            if (this.commandService) {
                // workspace when in theia
                return this.serveFileIn(relativeWorkspacePath);
            } else {
                // workspace in standalone
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
                const request = this.request([new URI(absolutePath)]);
                const resp = await fetch(request);
                const jsonResp = await resp.json();
                resolve({ response: resp, jsonResponse: jsonResp });
            } catch (e) {
                resolve({ response: { status: 404 } as Response, jsonResponse: {} as any });
            }
        });
        const { response, jsonResponse } = result;
        if (response.status === 200) {
            return `${this.endpoint()}/download/?id=${jsonResponse.id}`;
        }
        return undefined;
    }

    protected request(uris: URI[]): Request {
        const url = this.url(uris);
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

    protected url(uris: URI[]): string {
        const endpoint = this.endpoint();
        if (uris.length === 1) {
            // tslint:disable-next-line:whitespace
            const [uri] = uris;
            return `${endpoint}/?uri=${uri.toString()}`;
        }
        return endpoint;
    }

    protected endpoint(): string {
        const url = this.filesUrl();
        return url.endsWith('/') ? url.slice(0, -1) : url;
    }

    protected filesUrl(): string {
        return this.getRestUrl({ path: 'files' }).toString();
    }

    protected getRestUrl(options: { protocol?: string; location?: string; pathname?: string; path?: string }): URI {
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
        return new URI(`${options.protocol ?? 'http'}://${options.location ?? 'localhost:' + DEFAULT_THEIA_PORT}${pathname}${url_path}`);
    }
}
