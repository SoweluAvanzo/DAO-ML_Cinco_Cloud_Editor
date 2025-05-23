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
import { SelectHandler, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import { getSelectActions, hasSelectAction, SelectAction } from '@cinco-glsp/cinco-glsp-common';
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
        if (!action.modelElementId) {
            if (!this.modelState.graphModel) {
                return [];
            }
            action.modelElementId = this.modelState.graphModel?.id;
        }
        return super.execute(action, args);
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
