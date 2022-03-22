/* eslint-disable header/header */
import { MenuContribution, MenuModelRegistry } from '@theia/core/lib/common';
import { CommonMenus } from '@theia/core/lib/browser';
import { injectable } from 'inversify';

import { initializeProjectCommand } from './initializeProjectCommand';

/**
 * MENU
 */

@injectable()
export class MenuProjectInitializationContribution implements MenuContribution {

    public registerMenus(menus: MenuModelRegistry): void {
        const menuLocation = [...CommonMenus.FILE, '0_initialize_project'];
        menus.registerMenuAction(menuLocation, {
            commandId: initializeProjectCommand.id,
            order: '0'
        });
    }
}
