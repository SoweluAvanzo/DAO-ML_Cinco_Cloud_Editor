/* eslint-disable header/header */
import { CommandContribution, CommandRegistry } from '@theia/core/lib/common';
import { inject } from '@theia/core/shared/inversify';
import { WorkspaceService } from '@theia/workspace/lib/browser';
import { injectable } from 'inversify';

import { newProjectRegistry } from './projectCreationRegistry';

@injectable()
export class CommandRegistrationContribution implements CommandContribution {

    @inject(WorkspaceService)
    protected readonly workspaceService: WorkspaceService;

    registerCommands(commands: CommandRegistry): void {
        // registering all commands from newProjectRegistry
        for (const cmd of newProjectRegistry) {
            commands.registerCommand(cmd, {
                execute: () => {
                    // deduce rootURI
                    const rootURI = this.workspaceService.getWorkspaceRootUri(undefined);
                    if (!rootURI) {
                        alert('No workspace present.');
                        return;
                    }
                    commands.executeCommand(cmd.triggers, rootURI.path.toString()).then(() => {
                        alert('Project created!');
                    }).catch(() => {
                        alert('Creating project failed!');
                    });
                }
            });
        }
    }
}
