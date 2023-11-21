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

import { GraphModelState, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import { CustomAction, getCustomActions, hasCustomAction } from '@cinco-glsp/cinco-glsp-common';
import { Args, ContextMenuItemProvider, MenuItem, Point } from '@eclipse-glsp/server';
import { inject, injectable } from 'inversify';

@injectable()
export class CustomContextMenuItemProvider extends ContextMenuItemProvider {
    @inject(GraphModelState)
    protected readonly modelState: GraphModelState;

    getItems(selectedElementIds: string[], position: Point, args?: Args | undefined): MenuItem[] {
        const menuItems: MenuItem[] = [];
        // create menuItem for action
        const customMenuItems = this.getCustomMenuItems(selectedElementIds);
        // custom action
        if (selectedElementIds.length <= 0) {
            // graphmodell was clicked
            menuItems.push(...customMenuItems);
        } else {
            // element was clicked
            menuItems.push(...customMenuItems);
        }
        return menuItems;
    }

    getCustomMenuItems(selectedElementIds: string[]): MenuItem[] {
        const modelElement = this.modelState.index.findElement(selectedElementIds[0]) as ModelElement;
        const type = modelElement.type;
        const menuItems: MenuItem[] = [];
        const hasAc = hasCustomAction(type);
        if (hasAc) {
            for (const action of getCustomActions(modelElement.type)) {
                const label = action[1];
                const menuItem = {
                    id: 'action_graph_custom_' + action[0],
                    label: label,
                    sortString: label.charAt(0),
                    group: 'cinco-action',
                    icon: '',
                    actionKind: action[0],
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
                const customAction: CustomAction = CustomAction.create(selectedElementIds[0], selectedElementIds, menuItem.actionKind);
                menuItem.actions.push(customAction);
                menuItems.push(menuItem);
            }
        }
        return menuItems;
    }
}
