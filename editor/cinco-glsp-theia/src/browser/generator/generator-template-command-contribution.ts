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
import { CreateGeneratorTemplateCommand, CreateJavascriptGeneratorTemplateCommand } from '@cinco-glsp/cinco-glsp-common';
import { CommandContribution, CommandRegistry, Emitter, QuickInputService } from '@theia/core/lib/common';
import { URI } from '@theia/core/lib/common/uri';
import { inject, injectable } from '@theia/core/shared/inversify';
import { FileService } from '@theia/filesystem/lib/browser/file-service';
import { WorkspaceService } from '@theia/workspace/lib/browser/workspace-service';
import { GeneratorTemplate } from './generator-template';

interface DidCreateNewResourceEvent {
    uri: URI;
    parent: URI;
}

@injectable()
export class GeneratorTemplateCreationCommandContribution implements CommandContribution {
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
