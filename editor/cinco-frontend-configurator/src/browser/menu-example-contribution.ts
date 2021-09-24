/* eslint-disable header/header */
import {
    Command,
    CommandContribution,
    CommandRegistry,
    MAIN_MENU_BAR,
    MenuContribution,
    MenuModelRegistry
} from '@theia/core/lib/common';
import { inject } from '@theia/core/shared/inversify';
import { WorkspaceService } from '@theia/workspace/lib/browser';
import { injectable } from 'inversify';

/**
 * MENU
 */

@injectable()
export class MenuExampleCreationContribution implements MenuContribution {

    public registerMenus(menus: MenuModelRegistry): void {
        const menuLocation = [...MAIN_MENU_BAR, '1_file', '1_new'];
        const subSubMenuPath = [...menuLocation, 'example-creation-menu'];
        menus.registerSubmenu(subSubMenuPath, 'New Project...', { order: '3' });
        menus.registerMenuAction(subSubMenuPath, {
            commandId: ExampleCreatorCommand.id,
            order: '1'
        });
    }
}

/**
 * COMMAND
 */

const ExampleCreatorCommand: Command = {
    id: 'cinco-example-creation-command',
    label: 'Create Flowgraph-Example'
};

// eslint-disable-next-line @typescript-eslint/camelcase
const cinco_example_creator_command_id = 'cinco.command.create_example'; // TODO:

@injectable()
export class ExampleCommandContribution implements CommandContribution {

    @inject(WorkspaceService)
    protected readonly workspaceService: WorkspaceService;

    registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(ExampleCreatorCommand, {
            execute: () => {
                // deduce rootURI
                const rootURI = this.workspaceService.getWorkspaceRootUri(undefined);
                if (!rootURI) {
                    alert('No workspace present.');
                    return;
                }
                commands.executeCommand(cinco_example_creator_command_id, rootURI).then(() => {
                    alert('Project created!');
                }).catch(() => {
                    alert('Creating project failed!');
                });
            }
        });
    }
}
