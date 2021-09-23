/* eslint-disable header/header */
import { QuickInputService } from '@theia/core/lib/browser';
import {
    Command,
    CommandContribution,
    CommandRegistry,
    MAIN_MENU_BAR,
    MenuContribution,
    MenuModelRegistry,
    MessageService
} from '@theia/core/lib/common';
import { inject } from '@theia/core/shared/inversify';
import { injectable } from 'inversify';

/**
 * MENU
 */

@injectable()
export class MenuExampleCreationContribution implements MenuContribution {

    public registerMenus(menus: MenuModelRegistry): void {
        const menuLocation = [...MAIN_MENU_BAR, '1_file', '1_new'];
        const subSubMenuPath = [...menuLocation, 'example-creation-menu'];
        menus.registerSubmenu(subSubMenuPath, 'Create Project...', { order: '3' });
        menus.registerMenuAction(subSubMenuPath, {
            commandId: SampleCommand.id,
            order: '1'
        });
    }
}

/**
 * COMMAND
 */

const SampleCommand: Command = {
    id: 'cinco-example-creation-command',
    label: 'Create Flowgraph-Example'
};

@injectable()
export class ExampleCommandContribution implements CommandContribution {

    @inject(QuickInputService)
    protected readonly quickInputService: QuickInputService;

    @inject(MessageService)
    protected readonly messageService: MessageService;

    registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(SampleCommand, {
            execute: () => {
                alert('This is a sample command!');
            }
        });
    }
}
