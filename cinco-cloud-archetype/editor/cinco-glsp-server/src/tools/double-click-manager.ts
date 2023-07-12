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
import { Action } from '@eclipse-glsp/server-node';
import { injectable } from 'inversify';
import { ModelElement } from '../model/graph-model';
import { getDoubleClickActions, hasDoubleClickAction } from '../shared/meta-specification';
import { DoubleClickAction } from '../shared/protocol/double-click-protocol';
import { DoubleClickHandler } from './api/double-click-handler';
import { BaseHandlerManager } from './base-handler-manager';

/**
 * Handler for action
 */

@injectable()
export class DoubleClickManager extends BaseHandlerManager<DoubleClickAction, DoubleClickHandler> {
    baseHandlerName = 'DoubleClickHandler';
    actionKinds: string[] = [DoubleClickAction.KIND];

    hasHandlerProperty(element: ModelElement): boolean {
        return hasDoubleClickAction(element.type);
    }

    isApplicableHandler(element: ModelElement, handlerClassName: string): boolean {
        return (
            getDoubleClickActions(element).filter(
                // In the value set of annotations, there exists a value with the handlerClassName
                a => a.indexOf(handlerClassName) >= 0
            ).length > 0
        );
    }

    handlerCanBeExecuted(
        handler: DoubleClickHandler,
        element: ModelElement,
        action: DoubleClickAction,
        args: any
    ): boolean | Promise<boolean> {
        return handler.canExecute(action, args);
    }

    executeHandler(handler: DoubleClickHandler, element: ModelElement, action: DoubleClickAction, args: any): Action[] | Promise<Action[]> {
        return handler.execute(action, args);
    }
}
