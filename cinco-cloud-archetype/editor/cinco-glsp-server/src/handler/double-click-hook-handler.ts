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
import { DoubleClickAction, DoubleClickArgument, HookType } from '@cinco-glsp/cinco-glsp-common';
import { inject, injectable } from 'inversify';
import { Action, ActionDispatcher, ActionHandler, Logger, MaybePromise } from '@eclipse-glsp/server';
import { GraphModelState, HookManager } from '@cinco-glsp/cinco-glsp-api';

@injectable()
export class DoubleClickHookHandler implements ActionHandler {
    @inject(Logger)
    readonly logger: Logger;
    @inject(GraphModelState)
    readonly modelState: GraphModelState;
    @inject(ActionDispatcher)
    readonly actionDispatcher: ActionDispatcher;

    actionKinds: string[] = [DoubleClickAction.KIND];

    execute(action: DoubleClickAction, ...args: unknown[]): MaybePromise<Action[]> {
        const parameters = {
            modelElementId: action.modelElementId
        } as DoubleClickArgument;
        const canDoubleClick = HookManager.executeHook(
            parameters,
            HookType.CAN_DOUBLE_CLICK,
            this.modelState,
            this.logger,
            this.actionDispatcher
        );
        if (canDoubleClick) {
            HookManager.executeHook(parameters, HookType.POST_DOUBLE_CLICK, this.modelState, this.logger, this.actionDispatcher);
        }
        return [];
    }
}
