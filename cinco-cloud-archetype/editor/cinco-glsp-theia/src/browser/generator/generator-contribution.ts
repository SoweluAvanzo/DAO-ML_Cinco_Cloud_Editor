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
    CreateGenerateGraphDiagramCommand,
    GenerateGraphDiagramCommand
} from '@cinco-glsp/cinco-glsp-common/lib/protocol/generator-protocol';
import { TYPES } from '@eclipse-glsp/client';
import { SelectionService } from '@eclipse-glsp/client/lib/features/select/selection-service';
import { DiagramMenus } from '@eclipse-glsp/theia-integration';
import { LabelProvider, OpenerService } from '@theia/core/lib/browser';
import {
    CommandContribution,
    CommandRegistry,
    CommandService,
    Emitter,
    MenuContribution,
    MenuModelRegistry
} from '@theia/core/lib/common';
import { SelectionService as SelectionServiceT } from '@theia/core/lib/common/selection-service';
import { URI } from '@theia/core/lib/common/uri';
import { UriAwareCommandHandler, UriCommandHandler } from '@theia/core/lib/common/uri-command-handler';
import { inject, injectable } from '@theia/core/shared/inversify';
import { FileService } from '@theia/filesystem/lib/browser/file-service';
import { FilesystemSaveResourceService } from '@theia/filesystem/lib/browser/filesystem-save-resource-service';
import { FileStat } from '@theia/filesystem/lib/common/files';
import { WorkspaceService } from '@theia/workspace/lib/browser/workspace-service';

interface DidCreateNewResourceEvent {
    uri: URI;
    parent: URI;
}

@injectable()
export class CreateGenerateGraphDiagramCommandContribution implements CommandContribution {
    @inject(FileService) protected readonly fileService: FileService;
    @inject(WorkspaceService) protected readonly workspaceService: WorkspaceService;
    @inject(SelectionServiceT) protected readonly selectionService: SelectionServiceT;
    @inject(LabelProvider) protected readonly labelProvider: LabelProvider;

    private readonly onDidCreateNewFileEmitter = new Emitter<DidCreateNewResourceEvent>();

    registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(CreateGenerateGraphDiagramCommand, {
            execute: (modelElementId: string, fileContent: string, fileUri: URI) => {
                this.fileService.create(fileUri, fileContent, { overwrite: true }).then(() => {
                    this.onDidCreateNewFileEmitter.fire({ parent: fileUri.parent, uri: fileUri });
                });
            }
        });
    }

    protected async getDirectory(candidate: URI): Promise<FileStat | undefined> {
        let stat: FileStat | undefined;
        try {
            stat = await this.fileService.resolve(candidate);
            // eslint-disable-next-line no-empty
        } catch {}
        if (stat && stat.isDirectory) {
            return stat;
            // eslint-disable-next-line no-empty
        } else {
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
}

@injectable()
export class GenerateGraphDiagramCommandContribution implements CommandContribution {
    @inject(FileService) protected readonly fileService: FileService;
    @inject(LabelProvider) protected readonly labelProvider: LabelProvider;
    @inject(OpenerService) protected readonly openerService: OpenerService;
    @inject(WorkspaceService) protected readonly workspaceService: WorkspaceService;
    @inject(SelectionServiceT) protected readonly selectionService: SelectionServiceT;
    @inject(TYPES.SelectionService) protected selectionServiceType: SelectionService;
    @inject(CommandService) protected readonly commandService: CommandService;
    @inject(FilesystemSaveResourceService) protected readonly filesystemSaveResourceService: FilesystemSaveResourceService;

    private readonly onDidCreateNewFileEmitter = new Emitter<DidCreateNewResourceEvent>();
    registerCommands(registry: CommandRegistry): void {
        registry.registerCommand(
            GenerateGraphDiagramCommand,
            this.newWorkspaceRootUriAwareCommandHandler({
                execute: uri =>
                    this.getDirectory(uri).then(parent => {
                        if (parent) {
                            const parentUri = parent.resource;
                            const workspacePath: string = parentUri['codeUri']['path'];
                            this.submitGeneration(workspacePath);
                            // eslint-disable-next-line no-empty
                        } else {
                        }
                    })
            })
        );
    }

    submitGeneration(targetFolder: string): void {
        window.postMessage({
            kind: 'cincoGenerate',
            targetFolder: targetFolder
        });
    }
    protected async getDirectory(candidate: URI): Promise<FileStat | undefined> {
        let stat: FileStat | undefined;
        try {
            stat = await this.fileService.resolve(candidate);
            // eslint-disable-next-line no-empty
        } catch {}
        if (stat && stat.isDirectory) {
            return stat;
            // eslint-disable-next-line no-empty
        } else {
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
    protected fireCreateNewFile(uri: DidCreateNewResourceEvent): void {
        this.onDidCreateNewFileEmitter.fire(uri);
    }
    protected newWorkspaceRootUriAwareCommandHandler(handler: UriCommandHandler<URI>): WorkspaceRootUriAwareCommandHandler {
        return new WorkspaceRootUriAwareCommandHandler(this.workspaceService, this.selectionService, handler);
    }
}

@injectable()
export class GenerateGraphDiagramMenuContribution implements MenuContribution {
    registerMenus(menus: MenuModelRegistry): void {
        menus.registerMenuAction(DiagramMenus.DIAGRAM, {
            commandId: GenerateGraphDiagramCommand.id,
            label: GenerateGraphDiagramCommand.label
        });
    }
}

export class WorkspaceRootUriAwareCommandHandler extends UriAwareCommandHandler<URI> {
    constructor(
        protected readonly workspaceService: WorkspaceService,
        selectionService: SelectionServiceT,
        handler: UriCommandHandler<URI>
    ) {
        super(selectionService, handler);
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    public override isEnabled(...args: any[]): boolean {
        return super.isEnabled(...args) && !!this.workspaceService.tryGetRoots().length;
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    public override isVisible(...args: any[]): boolean {
        return super.isVisible(...args) && !!this.workspaceService.tryGetRoots().length;
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    protected override getUri(...args: any[]): URI | undefined {
        const uri = super.getUri(...args);
        // Return the `uri` immediately if the resource exists in any of the workspace roots and is of `file` scheme.
        if (uri && uri.scheme === 'file' && this.workspaceService.getWorkspaceRootUri(uri)) {
            return uri;
        }
        // Return the first root if available.
        if (this.workspaceService.tryGetRoots().length) {
            return this.workspaceService.tryGetRoots()[0].resource;
        }
        return undefined;
    }
}
