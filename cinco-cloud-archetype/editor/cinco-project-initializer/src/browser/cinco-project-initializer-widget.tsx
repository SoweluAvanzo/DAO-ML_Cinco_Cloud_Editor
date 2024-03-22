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
import { inject, injectable } from '@theia/core/shared/inversify';
import { ApplicationShell, ReactWidget, codicon } from '@theia/core/lib/browser';
import { FileService } from '@theia/filesystem/lib/browser/file-service';
import { WorkspaceService } from '@theia/workspace/lib/browser/workspace-service';
import * as React from 'react';
import { generateMGL, generateMSL } from './initialize-project';
import { CommandService, MessageService } from '@theia/core';
import '../../src/browser/cinco-project-initializer-style.css';
import * as logo from '../../src/browser/cinco-cloud-logo.png';
import { ProjectInitializerServer } from '../common/fetch-project-template-protocol';

@injectable()
export class CincoProjectInitializerWidget extends ReactWidget {
    static readonly ID = 'cincoCloudProjectInitializer';
    static readonly LABEL = 'Cinco Cloud Project Initializer';

    @inject(FileService)
    protected readonly fileService!: FileService;

    @inject(WorkspaceService)
    protected readonly workspaceService!: WorkspaceService;

    @inject(ApplicationShell)
    protected readonly shell!: ApplicationShell;

    @inject(CommandService)
    protected readonly commandService!: CommandService;

    @inject(ProjectInitializerServer)
    protected readonly projectInitializerServer!: ProjectInitializerServer;

    @inject(MessageService)
    protected readonly messageService!: MessageService;

    constructor() {
        super();
        this.id = CincoProjectInitializerWidget.ID;
        this.title.label = CincoProjectInitializerWidget.LABEL;
        this.title.caption = CincoProjectInitializerWidget.LABEL;
        this.title.closable = true;
        this.title.iconClass = codicon('lightbulb-sparkle');
        this.update();
    }

    closeWidget(): void {
        const widget = this.shell.widgets.find(w => w.id === this.id);
        if (widget) {
            this.shell.closeWidget(this.id);
        }
    }

    protected render(): React.JSX.Element {
        return (
            <CincoProjectInitializerView
                fileService={this.fileService}
                workspaceService={this.workspaceService}
                commandService={this.commandService}
                projectInitializerServer={this.projectInitializerServer}
                closeWidget={() => this.closeWidget()}
                onError={error => this.messageService.error(error)}
            />
        );
    }
}

interface CincoProjectInitializerViewProps {
    fileService: FileService;
    workspaceService: WorkspaceService;
    commandService: CommandService;
    projectInitializerServer: ProjectInitializerServer;
    onError(error: any): void;
    closeWidget: () => void;
}

interface CincoProjectInitializerViewState {
    view: string;
}

export class CincoProjectInitializerView extends React.Component<CincoProjectInitializerViewProps, CincoProjectInitializerViewState> {
    constructor(props: CincoProjectInitializerViewProps) {
        super(props);
        this.state = {
            view: 'initial'
        };
    }

    showInitialView(): void {
        this.setState({ view: 'initial' });
    }

    showInitializeProject(): void {
        this.setState({ view: 'initialize' });
    }

    showCreateExampleProject(): void {
        this.setState({ view: 'createExample' });
    }

    initializeProject(submitEvent: any): void {
        submitEvent.preventDefault();

        const form = submitEvent.target;
        const formData = new FormData(form);
        const formJson = Object.fromEntries((formData as any).entries());

        const projectName = formJson.projectName ?? 'Example';
        const mglFileName = formJson.projectName ? `${formJson.projectName.toLowerCase()}.mgl` : 'example.mgl';
        const mslFileName = formJson.projectName ? `${formJson.projectName.toLowerCase()}.style` : 'example.style';
        this.createNewFile(mglFileName, generateMGL(projectName, mslFileName));
        this.createNewFile(mslFileName, generateMSL());
        this.props.closeWidget();
    }

    async createExampleProject(downloadUrl: string, zipRootDirectory?: string): Promise<void> {
        const rootUri = this.props.workspaceService.tryGetRoots()[0]?.resource;
        if (rootUri) {
            try {
                await this.props.projectInitializerServer.fetchProjectTemplate(rootUri.path.fsPath(), downloadUrl, zipRootDirectory);
                this.props.closeWidget();
            } catch (error) {
                this.props.onError((error as Error).message);
            }
        } else {
            console.log('No workspace root found.');
        }
    }

    async createNewFile(fileName: string, content: string): Promise<void> {
        const rootUri = this.props.workspaceService.tryGetRoots()[0]?.resource;
        if (rootUri) {
            const fileUri = rootUri.resolve(fileName);
            try {
                await this.props.fileService.create(fileUri, content, { overwrite: false });
            } catch (e) {
                console.error(`Error creating file ${fileName}:`, e);
            }
        } else {
            console.log('No workspace root found.');
        }
    }

    override render(): React.JSX.Element {
        const exampleProjects = [
            {
                name: 'Flowgraph',
                downloadUrl: 'https://ls5gitlab.cs.tu-dortmund.de/cinco-cloud-examples/flowgraph/-/archive/main/flowgraph-main.zip',
                zipRootDirectory: 'flowgraph-main'
            },
            {
                name: 'Webstory',
                downloadUrl: 'https://ls5gitlab.cs.tu-dortmund.de/cinco-cloud-examples/webstory/-/archive/main/webstory-main.zip',
                zipRootDirectory: 'webstory-main'
            }
        ];

        switch (this.state.view) {
            case 'initialize':
                return (
                    <main id='nameInputView'>
                        <form onSubmit={e => this.initializeProject(e)}>
                            <input type='text' pattern='^[A-Za-z]+$' name='projectName' placeholder='Enter project name' />
                            <div>
                                <button type='button' onClick={() => this.showInitialView()}>
                                    Back
                                </button>
                                <button type='submit'>Confirm</button>
                            </div>
                        </form>
                    </main>
                );
            case 'createExample': {
                const exampleProjectButtons = exampleProjects.map(exampleProject => (
                    <button
                        key={exampleProject.name}
                        onClick={() => this.createExampleProject(exampleProject.downloadUrl, exampleProject.zipRootDirectory)}
                    >
                        Example: {exampleProject.name}
                    </button>
                ));
                return (
                    <main id='exampleProjectsView'>
                        {exampleProjectButtons}
                        <button onClick={() => this.showInitialView()}>Back</button>
                    </main>
                );
            }
            case 'initial':
            default:
                return (
                    <main id='initial-view'>
                        <img src={logo} alt='Cinco Cloud Logo' />
                        <h1>Welcome to Cinco Cloud!</h1>
                        <button onClick={() => this.showInitializeProject()}>Initialize Project</button>
                        <button onClick={() => this.showCreateExampleProject()}>Create Example Project</button>
                    </main>
                );
        }
    }
}
