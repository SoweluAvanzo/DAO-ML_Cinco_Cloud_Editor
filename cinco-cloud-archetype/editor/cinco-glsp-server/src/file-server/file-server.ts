/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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
import * as express from 'express';
import { DownloadLinkHandler, SingleFileDownloadHandler } from './file-download-handler';
import { DEFAULT_THEIA_PORT } from '@cinco-glsp/cinco-glsp-common';
import { FileDownloadCache } from './file-download-cache';

const fileDownloadCache = new FileDownloadCache();
const singleFileDownloadHandler = new SingleFileDownloadHandler(fileDownloadCache);
const downloadLinkHandler = new DownloadLinkHandler(fileDownloadCache);

export function startFileServer(): void {
    const app = express();
    app.get('/hello_world', (request, response) => response.send('Cinco GLSP-Server running.'));
    app.get('/files/download', (request, response) => downloadLinkHandler.handle(request, response));
    app.get('/files/', (request, response) => singleFileDownloadHandler.handle(request, response));
    app.listen(DEFAULT_THEIA_PORT, () => console.log('Running Standalone FileServer.'));
}
