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
import { ActionHandlerRegistry, Args, ContextActionsProvider, EditorContext, LabeledAction, MenuItem, Point } from '@eclipse-glsp/server';
import { inject, injectable } from 'inversify';
import { CustomActionManager } from '../tools/custom-action-manager';

/**
 * The original ContextMenuItemProvider does not support asynchronouse creation of MenuItems.
 */
@injectable()
export abstract class CincoContextMenuItemProvider implements ContextActionsProvider {
    /**
     * Returns the context id of the {@link ContextMenuItemProvider}.
     */
    get contextId(): string {
        return 'context-menu';
    }

    /**
     * Returns a list of {@link MenuItem}s for a given list of selected elements at a certain mouse position.
     *
     * @param selectedElementIds The list of currently selected elementIds.
     * @param position           The current mouse position.
     * @param args               Additional arguments.
     * @returns A list of {@link MenuItem}s for a given list of selected elements at a certain mouse position.
     */
    abstract getItems(selectedElementIds: string[], position: Point, args?: Args): Promise<MenuItem[]>;

    /**
     * Returns a list of {@link LabeledAction}s for a given {@link EditorContext}.
     *
     * @param editorContext The editorContext for which the actions are returned.
     * @returns A list of {@link LabeledAction}s for a given {@link EditorContext}.
     */
    getActions(editorContext: EditorContext): Promise<LabeledAction[]> {
        const position = editorContext.lastMousePosition ? editorContext.lastMousePosition : { x: 0, y: 0 };
        return this.getItems(editorContext.selectedElementIds, position, editorContext.args);
    }
}

@injectable()
export class CustomContextMenuItemProvider extends CincoContextMenuItemProvider {
    @inject(GraphModelState)
    protected readonly modelState: GraphModelState;
    @inject(ActionHandlerRegistry)
    protected readonly actionHandlerRegistry: ActionHandlerRegistry;

    getItems(selectedElementIds: string[], position: Point, args?: Args | undefined): Promise<MenuItem[]> {
        // custom action
        return selectedElementIds.length <= 0
            ? // graphmodell was clicked
              this.getCustomMenuItems([this.modelState.graphModel.id])
            : // element was clicked
              this.getCustomMenuItems(selectedElementIds);
    }

    async getCustomMenuItems(selectedElementIds: string[]): Promise<MenuItem[]> {
        if (selectedElementIds.length <= 0) {
            return Promise.resolve([]);
        }
        const modelElement = this.modelState.index.findElement(selectedElementIds[0]) as ModelElement;
        const type = modelElement.type;
        const menuItems: MenuItem[] = [];
        const hasAc = hasCustomAction(type);
        if (hasAc) {
            const customActionManager = this.actionHandlerRegistry.get(CustomAction.KIND)?.find(v => v instanceof CustomActionManager);
            const handlers = customActionManager
                ? await (customActionManager as CustomActionManager).getExecutableHandlerFor(modelElement.id)
                : undefined;
            for (const action of getCustomActions(modelElement.type)) {
                const label = action[1];
                const enabled = handlers ? handlers.includes(action[0]) : true;
                const menuItem = {
                    id: 'action_graph_custom_' + action[0],
                    label: label,
                    sortString: label.charAt(0),
                    group: 'cinco-action',
                    icon: '',
                    actionKind: action[0],
                    args: [selectedElementIds],
                    actions: [] as CustomAction[],
                    isDisabled: !enabled
                };
                const customAction: CustomAction = CustomAction.create(selectedElementIds[0], selectedElementIds, menuItem.actionKind);
                menuItem.actions.push(customAction);
                menuItems.push(menuItem);
            }
        }
        return menuItems;
    }
}
