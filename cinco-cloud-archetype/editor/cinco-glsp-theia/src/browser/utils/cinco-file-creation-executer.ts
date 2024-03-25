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
import { Emitter, SelectionService, URI, nls } from '@theia/core';
import { DidCreateNewResourceEvent, WorkspaceRootUriAwareCommandHandler, WorkspaceService } from '@theia/workspace/lib/browser';
import { UriCommandHandler } from '@theia/core/lib/common/uri-command-handler';
import { FileService } from '@theia/filesystem/lib/browser/file-service';
import { FileStat } from '@theia/filesystem/lib/common/files';
import { FileSystemUtils } from '@theia/filesystem/lib/common';
import { WorkspaceInputDialog } from '@theia/workspace/lib/browser/workspace-input-dialog';
import isValidFilename from 'valid-filename';
import { LabelProvider, OpenerOptions, OpenerService } from '@theia/core/lib/browser';

export class CincoFileCreationExecuter extends WorkspaceRootUriAwareCommandHandler {
    protected readonly labelProvider: LabelProvider;
    protected readonly fileService: FileService;
    protected readonly openerService: OpenerService;
    private readonly onDidCreateNewFileEmitter = new Emitter<DidCreateNewResourceEvent>();

    constructor(
        label: string,
        fileExtension: string,
        labelProvider: LabelProvider,
        workspaceService: WorkspaceService,
        selectionService: SelectionService,
        fileService: FileService,
        openerService: OpenerService
    ) {
        super(workspaceService, selectionService, {
            execute: uri =>
                this.getDirectory(uri).then(parent => {
                    if (parent) {
                        const parentUri = parent.resource;
                        const fileConfig = this.getDefaultFileConfig(fileExtension);
                        const targetUri = parentUri.resolve(fileConfig.fileName + fileConfig.fileExtension);
                        const vacantChildUri = FileSystemUtils.generateUniqueResourceURI(parent, targetUri, false);

                        const dialog = new WorkspaceInputDialog(
                            {
                                title: nls.localizeByDefault('New ' + label + '...'),
                                parentUri: parentUri,
                                initialValue: vacantChildUri.path.name,
                                placeholder: nls.localize('theia/workspace/newFilePlaceholder', label + ' Name'),
                                validate: name => this.validateFileName(name, parent, true)
                            },
                            this.labelProvider
                        );

                        dialog.open().then(async name => {
                            if (name) {
                                const fileUri = parentUri.resolve(name + `.${fileExtension}`);
                                try {
                                    await this.fileService.create(fileUri);
                                    this.fireCreateNewFile({ parent: parentUri, uri: fileUri });
                                } catch (e) {
                                    console.log('Error: ' + e);
                                }
                                this.open(this.openerService, fileUri);
                            }
                        });
                    }
                })
        });
        this.labelProvider = labelProvider;
        this.fileService = fileService;
        this.openerService = openerService;
    }

    async open(openerService: OpenerService, uri: URI, options?: OpenerOptions): Promise<object | undefined> {
        const opener = await openerService.getOpener(uri, options);
        return opener.open(uri, options);
    }

    protected newWorkspaceRootUriAwareCommandHandler(handler: UriCommandHandler<URI>): WorkspaceRootUriAwareCommandHandler {
        return new WorkspaceRootUriAwareCommandHandler(this.workspaceService, this.selectionService, handler);
    }

    protected async getDirectory(candidate: URI): Promise<FileStat | undefined> {
        let stat: FileStat | undefined;
        try {
            stat = await this.fileService.resolve(candidate);
        } catch {
            //
        }
        if (stat && stat.isDirectory) {
            return stat;
        }
        return this.getParent(candidate);
    }

    protected async getParent(candidate: URI): Promise<FileStat | undefined> {
        try {
            return await this.fileService.resolve(candidate.parent);
        } catch {
            return undefined;
        }
    }

    protected getDefaultFileConfig(fileExtension: string): { fileName: string; fileExtension: string } {
        return {
            fileName: 'Untitled',
            fileExtension: '.' + fileExtension
        };
    }

    /**
     * Returns an error message if the file name is invalid. Otherwise, an empty string.
     *
     * @param name the simple file name of the file to validate.
     * @param parent the parent directory's file stat.
     * @param allowNested allow file or folder creation using recursive path
     */
    protected async validateFileName(name: string, parent: FileStat, allowNested = false): Promise<string> {
        if (!name) {
            return '';
        }
        // do not allow recursive rename
        if (!allowNested && !isValidFilename(name)) {
            return nls.localizeByDefault('The name **{0}** is not valid as a file or folder name. Please choose a different name.');
        }
        if (name.startsWith('/')) {
            return nls.localizeByDefault('A file or folder name cannot start with a slash.');
        } else if (name.startsWith(' ') || name.endsWith(' ')) {
            return nls.localizeByDefault('Leading or trailing whitespace detected in file or folder name.');
        }
        // check and validate each sub-paths
        if (name.split(/[\\/]/).some(file => !file || !isValidFilename(file) || /^\s+$/.test(file))) {
            return nls.localizeByDefault("'{0}' is not a valid file name", this.trimFileName(name));
        }
        const childUri = parent.resource.resolve(name);
        const exists = await this.fileService.exists(childUri);
        if (exists) {
            return nls.localizeByDefault(
                'A file or folder **{0}** already exists at this location. Please choose a different name.',
                this.trimFileName(name)
            );
        }
        return '';
    }

    protected trimFileName(name: string): string {
        if (name && name.length > 30) {
            return `${name.substring(0, 30)}...`;
        }
        return name;
    }

    protected fireCreateNewFile(uri: DidCreateNewResourceEvent): void {
        this.onDidCreateNewFileEmitter.fire(uri);
    }
}
