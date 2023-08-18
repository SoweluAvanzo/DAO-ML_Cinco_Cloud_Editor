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
import { injectable } from 'inversify';
import * as path from 'path';
import { FilesystemUtilClient, FilesystemUtilServer } from '../common/file-system-util-protocol';

@injectable()
export class FilesystemUtilServerNode implements FilesystemUtilServer {
    client: FilesystemUtilClient | undefined;

    async connect(): Promise<boolean> {
        return true;
    }

    getFiles(absFolderPath: string): Promise<string[]> {
        if (!this.client) {
            throw new Error('No client connected!');
        }
        return new Promise<string[]>((resolve, reject) => {
            try {
                console.log(`loading files from:  ${absFolderPath}`);
                import('fs')
                    .then(fs => {
                        // TODO: resolve files inside folders
                        const foundFiles = this.getFilesFromFolder(fs, absFolderPath, './');
                        resolve(foundFiles);
                    })
                    .catch(e => reject(e));
            } catch (e) {
                console.log('failed to access filesystem.');
                reject(e);
            }
        });
    }

    readFiles(filePaths: string[], encoding?: string): Promise<string[]> {
        if (!this.client) {
            throw new Error('No client connected!');
        }
        return new Promise<string[]>((resolve, reject) => {
            const contents: string[] = [];
            for (const filePath of filePaths) {
                try {
                    console.log(`reading file:  ${filePaths}`);
                    import('fs')
                        .then(fs => {
                            // TODO: resolve files inside folders
                            encoding = encoding ?? 'utf-8';
                            const buffer = fs.readFileSync(filePath);
                            const content = buffer.toString();
                            contents.push(content);
                        })
                        .catch(e => reject(e));
                } catch (e) {
                    console.log(`failed to read file: ${filePaths}`);
                    reject(e);
                }
            }
            resolve(contents);
        });
    }

    getFilesFromFolder(fs: typeof import('fs'), absRoot: string, folderPath: string): string[] {
        const absoluteFolderPath = path.join(absRoot, folderPath);
        if (!fs.existsSync(absoluteFolderPath)) {
            return [];
        }
        // file or folder exists
        const found = fs.readdirSync(absoluteFolderPath) as string[]; // all found files and directories
        const foundFiles = found.filter(file => {
            const absPath = path.join(absoluteFolderPath, file);
            try {
                return fs.readFileSync(absPath); // if file can be read, it is a file
            } catch (e) {
                return false;
            }
        });
        const foundFolders = found.filter(folder => {
            const absPath = path.join(absoluteFolderPath, folder);
            try {
                return fs.readdirSync(absPath); // if directory can be read, it is a folder} catch (e)
            } catch (e) {
                return false;
            }
        });
        const containedFiles: string[] = [];
        foundFolders.forEach((folder: string) => {
            const relativeFolderPath = path.join(folderPath, folder);
            const filesFromFolder = this.getFilesFromFolder(fs, absRoot, relativeFolderPath);
            filesFromFolder.forEach(file => {
                const relativeFilePath = path.join(folder, file);
                containedFiles.push(relativeFilePath);
            });
        });
        return foundFiles.concat(containedFiles);
    }

    push(some: any): void {
        if (this.client) {
            // this.client!.receive(some);
        }
    }

    dispose(): void {
        this.push('fileSystemUtilServer disposed.');
    }

    getClient(): FilesystemUtilClient | undefined {
        return this.client;
    }

    setClient(client: FilesystemUtilClient | undefined): void {
        this.client = client;
        // this.client?.setServer(this);
    }
}
