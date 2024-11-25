/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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
import { HookType, SelectAction, Action, SelectArgument } from '@cinco-glsp/cinco-glsp-common';
import { inject, injectable } from 'inversify';
import { ActionDispatcher, ActionHandler, Logger, GModelFactory, MaybePromise, SourceModelStorage } from '@eclipse-glsp/server';
import { GraphGModelFactory, GraphModelState, HookManager, ContextBundle } from '@cinco-glsp/cinco-glsp-api';

@injectable()
export class SelectHookHandler implements ActionHandler {
    @inject(Logger)
    readonly logger: Logger;
    @inject(GraphModelState)
    readonly modelState: GraphModelState;
    @inject(ActionDispatcher)
    readonly actionDispatcher: ActionDispatcher;
    @inject(SourceModelStorage)
    protected sourceModelStorage: SourceModelStorage;
    @inject(GModelFactory)
    protected frontendModelFactory: GraphGModelFactory;

    actionKinds: string[] = [SelectAction.KIND];

    execute(action: SelectAction, ...args: unknown[]): MaybePromise<Action[]> {
        if (!this.modelState.graphModel) {
            this.logger.info('SelectHookHandler triggered - modelState not ready.');
            return [];
        }
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
            allSelectionElements.push(this.modelState.graphModel.id); // when no element is selected, select the root
        }
        for (const element of allSelectionElements) {
            const param = parameters;
            param.modelElementId = element;
            action.modelElementId = element;
            param.isSelected = param.selectedElements.includes(param.modelElementId);

            // Can Hook
            const contextBundle = new ContextBundle(
                this.modelState,
                this.logger,
                this.actionDispatcher,
                this.sourceModelStorage,
                this.frontendModelFactory
            );
            const canSelect = HookManager.executeHook(param, HookType.CAN_SELECT, contextBundle);
            if (canSelect) {
                // Post Hook
                HookManager.executeHook(parameters, HookType.POST_SELECT, contextBundle);
            }
        }
        return [];
    }
}
