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
import * as https from 'https';
import * as os from 'os';
import * as fs from 'fs/promises';
import * as path from 'path';
import { createWriteStream } from 'fs';
import * as extract from 'extract-zip';
import { injectable } from '@theia/core/shared/inversify';
import { ProjectInitializerClient, ProjectInitializerServer } from '../common/fetch-project-template-protocol';

@injectable()
export class ProjectInitializerServerNode implements ProjectInitializerServer {
    client: ProjectInitializerClient | undefined;

    async fetchProjectTemplate(workspaceRoot: string, url: string, zipRootDirectory?: string): Promise<void> {
        console.log('fetch project template', workspaceRoot, url);
        const tempDir = path.join(os.tmpdir(), await fs.mkdtemp('template-download'));
        await fs.mkdir(tempDir);
        const zipPath = path.join(tempDir, 'template.zip');
        await this.downloadFile(url, zipPath);
        const templateDirectory = path.join(tempDir, 'template');
        await fs.mkdir(templateDirectory);
        await extract(zipPath, { dir: templateDirectory });
        await fs.cp(path.join(templateDirectory, zipRootDirectory ?? ''), workspaceRoot, { recursive: true });
        await fs.rm(tempDir, { recursive: true });
    }

    protected async downloadFile(url: string, destination: string): Promise<void> {
        return new Promise((resolve, reject) => {
            const file = createWriteStream(destination);
            https
                .get(url, response => {
                    if (response.statusCode !== 200) {
                        reject(new Error(`Download failed with status code ${response.statusCode}: ${response.statusMessage}`));
                        return;
                    }
                    response.pipe(file);
                    file.on('finish', () => {
                        file.close();
                        resolve();
                    });
                })
                .on('error', function (err) {
                    fs.unlink(destination);
                    reject(err);
                });
        });
    }

    dispose(): void {
        console.log('ProjectInitializerServerNode - disposed!');
    }

    setClient(client: ProjectInitializerClient | undefined): void {
        this.client = client;
    }

    getClient(): ProjectInitializerClient | undefined {
        return this.client;
    }
}
