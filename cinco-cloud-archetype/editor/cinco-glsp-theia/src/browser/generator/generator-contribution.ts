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
    GenerateGraphDiagramCommand,
    CreateGeneratorTemplateCommand,
    CreateJavascriptGeneratorTemplateCommand
} from '@cinco-glsp/cinco-glsp-common/lib/protocol/generator-protocol';
import { KeybindingContribution, KeybindingRegistry, LabelProvider } from '@theia/core/lib/browser';
import {
    CommandContribution,
    CommandRegistry,
    Emitter,
    MenuContribution,
    MenuModelRegistry,
    QuickInputService
} from '@theia/core/lib/common';
import { SelectionService as SelectionServiceT } from '@theia/core/lib/common/selection-service';
import { URI } from '@theia/core/lib/common/uri';
import { UriAwareCommandHandler, UriCommandHandler } from '@theia/core/lib/common/uri-command-handler';
import { inject, injectable } from '@theia/core/shared/inversify';
import { FileService } from '@theia/filesystem/lib/browser/file-service';
import { FileStat } from '@theia/filesystem/lib/common/files';
import { WorkspaceService } from '@theia/workspace/lib/browser/workspace-service';
import { GeneratorTemplate } from './generator-template';
import { FilesystemUtilServer } from '../../common/file-system-util-protocol';
import { ThemeService } from '@theia/core/lib/browser/theming';
import { GLSPDiagramMenus } from '@eclipse-glsp/theia-integration';

interface DidCreateNewResourceEvent {
    uri: URI;
    parent: URI;
}

@injectable()
export class CreateGeneratorTemplateCommandContribution implements CommandContribution {
    @inject(FileService) protected readonly fileService: FileService;
    @inject(WorkspaceService) protected readonly workspaceService: WorkspaceService;
    @inject(QuickInputService) protected readonly quickInputService: QuickInputService;

    private readonly onDidCreateNewFileEmitter = new Emitter<DidCreateNewResourceEvent>();

    registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(CreateJavascriptGeneratorTemplateCommand, {
            execute: async () => {
                let name = await this.quickInputService.input({ prompt: 'Enter your generator name', placeHolder: 'ExampleGenerator' });
                if (name === '') {
                    name = 'ExampleGenerator';
                }
                if (name) {
                    const generatorName = name.replace(/[^a-zA-Z0-9]/g, '');
                    const targetFolder = this.workspaceService.tryGetRoots()[0].resource.resolve('src');
                    await this.fileService.createFolder(targetFolder);
                    const fileUri = targetFolder.resolve('generator.js');
                    this.fileService
                        .create(fileUri, GeneratorTemplate.getJavascriptGeneratorTemplate(generatorName), { overwrite: true })
                        .then(() => {
                            this.onDidCreateNewFileEmitter.fire({ parent: fileUri.parent, uri: fileUri });
                        })
                        .catch(err => {
                            console.error('Error creating file:', err);
                        });
                }
            }
        });

        commands.registerCommand(CreateGeneratorTemplateCommand, {
            execute: async () => {
                let name = await this.quickInputService.input({ prompt: 'Enter your generator name', placeHolder: 'ExampleGenerator' });
                if (name === '') {
                    name = 'ExampleGenerator';
                }
                if (name) {
                    const generatorName = name.replace(/[^a-zA-Z0-9]/g, '');
                    const targetFolder = this.workspaceService.tryGetRoots()[0].resource.resolve('src');
                    await this.fileService.createFolder(targetFolder);
                    const fileUri = targetFolder.resolve('generator.ts');
                    this.fileService
                        .create(fileUri, GeneratorTemplate.getTypescriptGeneratorTemplate(generatorName), { overwrite: true })
                        .then(() => {
                            this.onDidCreateNewFileEmitter.fire({ parent: fileUri.parent, uri: fileUri });
                        })
                        .catch(err => {
                            console.error('Error creating file:', err);
                        });
                }
            }
        });
    }
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
    @inject(ThemeService) protected readonly themeService: ThemeService;
    @inject(WorkspaceService) protected readonly workspaceService: WorkspaceService;
    @inject(SelectionServiceT) protected readonly selectionService: SelectionServiceT;
    @inject(FilesystemUtilServer) fsUtils: FilesystemUtilServer;

    private readonly onDidCreateNewFileEmitter = new Emitter<DidCreateNewResourceEvent>();
    registerCommands(registry: CommandRegistry): void {
        this.reregisterCommand(registry);
        this.themeService.onDidColorThemeChange(e => {
            this.reregisterCommand(registry);
        });
    }

    reregisterCommand(registry: CommandRegistry): void {
        const theme = this.themeService.getCurrentTheme();
        const iconClass = theme.type === 'light' ? GenerateGraphDiagramCommand.lightIconClass : GenerateGraphDiagramCommand.darkIconClass;
        const commandIds = registry.commands.map(c => c.id);
        if (commandIds.indexOf(GenerateGraphDiagramCommand.id) >= 0) {
            registry.unregisterCommand(GenerateGraphDiagramCommand);
        }
        registry.registerCommand(
            {
                id: GenerateGraphDiagramCommand.id,
                category: GenerateGraphDiagramCommand.category,
                label: GenerateGraphDiagramCommand.label,
                iconClass: iconClass ?? GenerateGraphDiagramCommand.id
            },
            this.newWorkspaceRootUriAwareCommandHandler({
                execute: uri =>
                    this.getDirectory(uri).then(parent => {
                        if (parent) {
                            const parentUri = parent.resource;
                            const workspacePath: string = parentUri['codeUri']['path'];
                            const fileUri = this.selectionService.selection as any;
                            if (!fileUri || !fileUri['sourceUri'] || !workspacePath) {
                                throw new Error('Diagram gives insufficient information!');
                            }
                            const sourceUri = fileUri.sourceUri;
                            console.log('Triggered generation on: ' + sourceUri);
                            this.fsUtils.readFiles([sourceUri]).then(value => {
                                if (value.length <= 0) {
                                    throw new Error('Could not identify diagram id!');
                                }
                                console.log('parsing file: ' + sourceUri);
                                console.log('object file: ' + JSON.stringify(value));
                                const obj = JSON.parse(value[0]);
                                const modelId = obj['id'];
                                console.log('received modelId: ' + modelId);
                                this.submitGeneration(workspacePath, sourceUri, modelId);
                            });
                        }
                    })
            })
        );
    }

    submitGeneration(targetFolder: string, fileUri: string, modelId: string): void {
        window.postMessage({
            kind: 'cincoGenerate',
            targetFolder: targetFolder,
            fileUri: fileUri,
            modelElementId: modelId
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
        menus.registerMenuAction(GLSPDiagramMenus.DIAGRAM, {
            commandId: GenerateGraphDiagramCommand.id,
            label: GenerateGraphDiagramCommand.label
        });
    }
}

@injectable()
export class GenerateGraphDiagramKeybindingContribution implements KeybindingContribution {
    registerKeybindings(keybindings: KeybindingRegistry): void {
        keybindings.registerKeybinding({
            keybinding: GenerateGraphDiagramCommand.keybinding,
            command: GenerateGraphDiagramCommand.id,
            when: 'cincoGraphModelType !== undefined'
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
