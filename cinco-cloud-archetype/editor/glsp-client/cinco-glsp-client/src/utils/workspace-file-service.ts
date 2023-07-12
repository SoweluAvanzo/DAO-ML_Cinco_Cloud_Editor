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

import { CommandService } from '@theia/core';
import { Endpoint } from '@theia/core/lib/browser/endpoint';
import URI from '@theia/core/lib/common/uri';
import { inject, injectable } from '@theia/core/shared/inversify';
import { FileDownloadData } from '@theia/filesystem/src/common/download/file-download-data';
import * as path from 'path';

@injectable()
export class WorkspaceFileService {
    @inject(CommandService) commandService: CommandService;
    protected static BLACKLIST = ['http://', 'https://'];

    async serveFile(filePath: string, relativeFolder?: string): Promise<string | undefined> {
        if (WorkspaceFileService.BLACKLIST.filter(e => filePath.startsWith(e)).length > 0) {
            return undefined;
        }
        const relativePath = (relativeFolder ?? '') + filePath;
        const roots: URI[] = (await this.commandService.executeCommand('rootProviderHandler')) ?? [];
        for (const root of roots) {
            // eslint-disable-next-line no-async-promise-executor
            const result = await new Promise<{ response: Response; jsonResponse: any }>(async resolve => {
                const absolutePath = path.join(root.path.fsPath(), relativePath);
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
            } // console.log('root `' + root + '` did not contain `' + relativeURI + '`. Trying next root...');
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

    protected body(uris: URI[]): FileDownloadData {
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
        return new Endpoint({ path: 'files' }).getRestUrl().toString();
    }
}
