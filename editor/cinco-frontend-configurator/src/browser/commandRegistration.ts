/* eslint-disable header/header */
import { CommandContribution, CommandRegistry } from '@theia/core/lib/common';
import { inject } from '@theia/core/shared/inversify';
import { WorkspaceService } from '@theia/workspace/lib/browser';
import { injectable } from 'inversify';

import { initializeProjectCommand } from './initializeProjectCommand';

@injectable()
export class CommandRegistrationContribution implements CommandContribution {

    @inject(WorkspaceService)
    protected readonly workspaceService: WorkspaceService;

    registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(initializeProjectCommand, {
            execute: () => {
                // deduce rootURI
                const rootURI = this.workspaceService.getWorkspaceRootUri(undefined);
                if (!rootURI) {
                    alert('No workspace present.');
                    return;
                }
                commands.executeCommand(initializeProjectCommand.triggers, rootURI.path.toString()).catch(() => {
                    alert('Openening project initialization dialog failed!');
                });
            }
        });
    }
}
