/********************************************************************************
 * Copyright (c) 2022 Cinco Cloud.
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
import { Action, Args, ContextMenuItemProvider, MenuItem, Point } from '@eclipse-glsp/server-node';

import { injectable } from 'inversify';
@injectable()
export class CustomContextMenuItemProvider extends ContextMenuItemProvider {
    getItems(selectedElementIds: string[], position: Point, args?: Args | undefined): MenuItem[] {
        const menuItems: MenuItem[] = [];
        // create menuItem for action
        const menuItem = this.getCustomMenuItem(selectedElementIds);
        // custom action
        if (selectedElementIds.length <= 0) {
            // graphmodell was clicked
            menuItems.push(menuItem);
        } else {
            // element was clicked
            menuItems.push(menuItem);
        }
        return menuItems;
    }

    getCustomMenuItem(selectedElementIds: string[]): MenuItem {
        // creating example action-specification
        const label = 'Calculate shortest path';
        const menuItem = {
            id: 'action_flowgraph_action-name',
            label: label,
            sortString: label.charAt(0),
            group: 'cinco-action',
            icon: '',
            actionKind: CustomAction.KIND,
            args: [selectedElementIds],
            actions: [] as CustomAction[]
            /*
                // TODO:
                isEnabled: () => true,
                isVisible: () => true,
                isToggled: () => true,
                children: []
            */
        };
        const customAction: CustomAction = CustomAction.create(selectedElementIds);
        menuItem.actions.push(customAction);
        return menuItem;
    }
}
export interface CustomAction extends Action {
    kind: typeof CustomAction.KIND;
    selectedElementIds: string[];
}
export namespace CustomAction {
    export const KIND = 'ExampleAction';

    export function create(selectedElementIds: string[]): CustomAction {
        return {
            kind: KIND,
            selectedElementIds
        };
    }
}
export interface CustomAction extends Action {}
