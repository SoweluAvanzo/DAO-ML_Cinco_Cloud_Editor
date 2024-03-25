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
import { CommandContribution, CommandHandler, CommandRegistry } from '@theia/core';
import URI from '@theia/core/lib/common/uri';
import { WorkspaceService } from '@theia/workspace/lib/browser/workspace-service';
import { inject, injectable } from 'inversify';
import * as path from 'path';
import { FilesystemUtilServer } from '../../common/file-system-util-protocol';

/**
 * FileProviderContribution registers a theia command.
 * The registered theia-command 'FileProviderHandler' can be used to fetch file information from the backend the client.
 *
 * implemented functionalities:
 * - fetch all relative file-path, that are nested inside a given folder in the backend
 */

@injectable()
export class FileProviderContribution implements CommandContribution {
    @inject(WorkspaceService)
    protected workspaceService: WorkspaceService;
    @inject(FilesystemUtilServer) fsUtils: FilesystemUtilServer;

    registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(
            { id: FileProviderHandler.ID, label: 'Providing file-information', category: 'Cinco Cloud'},
            new FileProviderHandler(this.workspaceService, this.fsUtils)
        );
        commands.registerCommand(
            { id: WorkspaceRootProviderHandler.ID, label: 'Providing root-information', category: 'Cinco Cloud'},
            new WorkspaceRootProviderHandler(this.workspaceService, this.fsUtils)
        );
    }
}

export class FileProviderParameter {
    directories: string[];
    readFiles?: boolean;
    filter?: string[];
}

export interface FileProviderResponse {
    items: FileProviderResponseItem[];
}

export interface FileProviderResponseItem {
    path: string;
    content: string | undefined;
}

export class FileProviderHandler implements CommandHandler {
    static ID = 'fileProviderHandler';
    static ROOT_FROM_WORKSPACE = '../';
    protected workspaceService: WorkspaceService;
    protected fsUtils: FilesystemUtilServer;
    protected ready: Promise<void>;
    protected WORKSPACE_ROOTS: URI[] = [];

    constructor(workspaceService: WorkspaceService, fsUtils: FilesystemUtilServer) {
        this.workspaceService = workspaceService;
        this.fsUtils = fsUtils;

        // cache information
        this.ready = new Promise<void>(resolve => {
            this.workspaceService.ready.then(() => {
                this.workspaceService.roots.then(roots => {
                    const rootUris = roots.map(r => r.resource);
                    rootUris.forEach(root => {
                        console.log(root);
                        this.WORKSPACE_ROOTS.push(root);
                        resolve();
                    });
                });
            });
        });
    }

    /**
     * Searches for files either in a folder specified by an absolutePath (starting with '/'),
     * or a relative path inside the workspace folder(s).
     * @param args
     * @returns
     */
    execute(...args: any[]): any {
        return new Promise<FileProviderResponse>((resolve, reject) => {
            const param = args[0] as FileProviderParameter;
            this.ready
                .then(() => {
                    param.directories.forEach(directory => {
                        if(this.isAbsolute(directory)) {
                            this.resolveFiles(directory, param.readFiles ?? false, param.filter ?? [], resolve);
                        } else {
                            this.WORKSPACE_ROOTS.forEach(root => {
                                const pivotPath = path.join(root.path.fsPath());
                                const uriPath = path.join(pivotPath, directory);
                                this.resolveFiles(uriPath, param.readFiles ?? false, param.filter ?? [], resolve);
                            });
                        }
                    });
                })
                .catch(e => reject(e));
        });
    }

    resolveFiles(uriPath: string, readFiles: boolean, fileTypes: string[], resolve: any): void {
        console.log('searching for files in: ' + uriPath);
        const result = this.fsUtils.getFiles(uriPath);
        if (result) {
            result.then(files => {
                if (readFiles) {
                    let filteredFiles = files;
                    if (fileTypes.length > 0) {
                        filteredFiles = files.filter(f => {
                            const fileExtension = f.slice(f.lastIndexOf('.'));
                            return fileTypes!.includes(fileExtension);
                        });
                    }
                    this.fsUtils.readFiles(filteredFiles.map(f => `${uriPath}/${f}`)).then(contents => {
                        const response = this.buildResponse(filteredFiles, contents);
                        resolve(response);
                    });
                } else {
                    const response = this.buildResponse(files);
                    resolve(response);
                }
            });
        }
    }

    isEnabled?(...args: any[]): boolean {
        return true;
    }

    isVisible?(...args: any[]): boolean {
        return false;
    }

    isToggled?(...args: any[]): boolean {
        return true;
    }

    isAbsolute(uri: string): boolean {
        return uri.charAt(0) === '/';
    }

    private buildResponse(filePaths: string[], contents?: string[]): FileProviderResponse {
        const response: FileProviderResponse = {
            items: []
        };
        for (let i = 0; i < filePaths.length; i++) {
            response.items.push({ path: filePaths[i], content: contents ? contents[i] : undefined } as FileProviderResponseItem);
        }
        return response;
    }
}

export class WorkspaceRootProviderHandler implements CommandHandler {
    static ID = 'workspaceRootProviderHandler';

    protected workspaceService: WorkspaceService;
    protected fsUtils: FilesystemUtilServer;
    protected ready: Promise<void>;
    protected WORKSPACE_ROOTS: URI[] = [];

    constructor(workspaceService: WorkspaceService, fsUtils: FilesystemUtilServer) {
        this.workspaceService = workspaceService;
        this.fsUtils = fsUtils;

        // cache information
        this.ready = new Promise<void>(resolve => {
            this.workspaceService.ready.then(() => {
                this.workspaceService.roots.then(roots => {
                    const rootUris = roots.map(r => r.resource);
                    rootUris.forEach(root => {
                        console.log(root);
                        this.WORKSPACE_ROOTS.push(root);
                        resolve();
                    });
                });
            });
        });
    }

    execute(...args: any[]): any {
        return new Promise<URI[]>((resolve, reject) => {
            this.ready
                .then(() => {
                    resolve(this.WORKSPACE_ROOTS);
                })
                .catch(e => reject(e));
        });
    }

    isEnabled?(...args: any[]): boolean {
        return true;
    }

    isVisible?(...args: any[]): boolean {
        return false;
    }

    isToggled?(...args: any[]): boolean {
        return true;
    }
}
