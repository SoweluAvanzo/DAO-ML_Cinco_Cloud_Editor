/* eslint-disable header/header */
import { MAIN_MENU_BAR, MenuContribution, MenuModelRegistry } from '@theia/core/lib/common';
import { injectable } from 'inversify';

import { newProjectRegistry } from './projectCreationRegistry';

/**
 * MENU
 */

@injectable()
export class MenuExampleCreationContribution implements MenuContribution {

    public registerMenus(menus: MenuModelRegistry): void {
        const menuLocation = [...MAIN_MENU_BAR, '1_file', '1_new'];
        const subSubMenuPath = [...menuLocation, 'example-creation-menu'];
        menus.registerSubmenu(subSubMenuPath, 'New Project...', { order: '3' });
        // registering all menu-commands from newProjectRegistry
        let index = 0;
        for (const cmd of newProjectRegistry) {
            menus.registerMenuAction(subSubMenuPath, {
                commandId: cmd.id,
                order: index.toString()
            });
            index++;
        }
    }
}
