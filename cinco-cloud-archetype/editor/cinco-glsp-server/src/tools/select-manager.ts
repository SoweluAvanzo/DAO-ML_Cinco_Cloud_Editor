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
import { SelectHandler, HookManager, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import { getSelectActions, hasSelectAction, HookType, SelectAction, SelectArgument } from '@cinco-glsp/cinco-glsp-common';
import { Action } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { BaseHandlerManager } from './base-handler-manager';

/**
 * Handler for action
 */

@injectable()
export class SelectManager extends BaseHandlerManager<SelectAction, SelectHandler> {
    baseHandlerName = 'SelectHandler';
    actionKinds: string[] = [SelectAction.KIND];

    override async execute(action: SelectAction, ...args: unknown[]): Promise<Action[]> {
        // parse
        const selectedElements = action.selectedElementsIDs;
        const deselectedElements = action.deselectedElementsIDs;
        // Not yet used: const unselect = action.deselectAll;
        const parameters = {
            modelElementId: '',
            selectedElements: selectedElements,
            deselectedElements: deselectedElements
        } as SelectArgument;
        const allSelectionElements = Array.from(new Set(selectedElements.concat(deselectedElements)));
        if (selectedElements.length <= 0) {
            allSelectionElements.push(action.root); // when no element is selected, select the root
        }
        let results: Action[] = [];
        for (const element of allSelectionElements) {
            const param = parameters;
            param.modelElementId = element;
            action.modelElementId = element;
            param.isSelected = param.selectedElements.includes(param.modelElementId);

            // Can Hook
            const canSelect = HookManager.executeHook(param, HookType.CAN_SELECT, this.modelState, this.logger, this.actionDispatcher);
            if (canSelect) {
                // Action
                const result = await super.execute(action, args);
                results = results.concat(result);

                // Post Hook
                HookManager.executeHook(parameters, HookType.POST_SELECT, this.modelState, this.logger, this.actionDispatcher);
            }
        }
        return results;
    }

    hasHandlerProperty(element: ModelElement): boolean {
        return hasSelectAction(element.type);
    }

    isApplicableHandler(element: ModelElement, handlerClassName: string): boolean {
        return (
            getSelectActions(element.type).filter(
                // In the value set of annotations, there exists a value with the handlerClassName
                a => a.indexOf(handlerClassName) >= 0
            ).length > 0
        );
    }

    handlerCanBeExecuted(handler: SelectHandler, element: ModelElement, action: SelectAction, args: any): boolean | Promise<boolean> {
        return handler.canExecute(action, args);
    }

    executeHandler(handler: SelectHandler, element: ModelElement, action: SelectAction, args: any): Action[] | Promise<Action[]> {
        return handler.execute(action, args);
    }
}
