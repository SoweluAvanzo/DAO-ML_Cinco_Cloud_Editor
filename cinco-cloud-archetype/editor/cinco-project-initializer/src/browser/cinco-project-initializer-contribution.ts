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
import { Command, CommandRegistry, CommandService } from '@theia/core';
import { AbstractViewContribution } from '@theia/core/lib/browser/shell/view-contribution';
import { inject, injectable } from '@theia/core/shared/inversify';
import { CincoProjectInitializerWidget } from './cinco-project-initializer-widget';
import { FrontendApplication, FrontendApplicationContribution } from '@theia/core/lib/browser';
import { WorkspaceService } from '@theia/workspace/lib/browser/workspace-service';
import { ProjectInitializerClient } from '../common/fetch-project-template-protocol';

export const CincoProjectInitializerWidgetCommand: Command = {
    id: 'cincoCloudProjectInitializer:open',
    label: 'Cinco Cloud: Project Initializer'
};

@injectable()
export class CincoProjectInitializerWidgetContribution extends AbstractViewContribution<CincoProjectInitializerWidget> {
    constructor() {
        super({
            widgetId: CincoProjectInitializerWidget.ID,
            widgetName: CincoProjectInitializerWidget.LABEL,
            defaultWidgetOptions: { area: 'main' },
            toggleCommandId: CincoProjectInitializerWidgetCommand.id
        });
    }

    override registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(CincoProjectInitializerWidgetCommand, {
            execute: () => super.openView({ activate: true, reveal: true })
        });
    }
}

const repositoryDirectories = ['.git'];

@injectable()
export class CincoProjectInitializerFrontendApplicationContribution implements FrontendApplicationContribution {

    constructor(
        @inject(WorkspaceService) protected readonly workspaceService: WorkspaceService,
        @inject(CommandService) protected readonly commandService: CommandService
    ) { }

    async onDidInitializeLayout(app: FrontendApplication): Promise<void> {
        await this.checkAndOpenWidget();
    }

    protected async checkAndOpenWidget(): Promise<void> {
        const workspaceChildren = this.workspaceService.workspace?.children || [];
        let isEmpty = true;
        for (const child of workspaceChildren) {
            if (child.isDirectory && repositoryDirectories.includes(child.name)) {
                continue;
            } else {
                isEmpty = false;
                break;
            }
        }

        if (isEmpty) {
            this.commandService.executeCommand(CincoProjectInitializerWidgetCommand.id);
        }
    }
}

export class ProjectInitializerClientNode implements ProjectInitializerClient {
}
