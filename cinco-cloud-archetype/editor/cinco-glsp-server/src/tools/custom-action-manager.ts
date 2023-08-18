/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud.
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
import { CustomActionHandler, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import { CustomAction, getCustomActions, hasCustomAction } from '@cinco-glsp/cinco-glsp-common';
import { Action } from '@eclipse-glsp/server-node';
import { injectable } from 'inversify';
import { BaseHandlerManager } from './base-handler-manager';

/**
 * Handler for action
 */

@injectable()
export class CustomActionManager extends BaseHandlerManager<CustomAction, CustomActionHandler> {
    baseHandlerName = 'CustomActionHandler';
    actionKinds: string[] = [CustomAction.KIND];
    hasHandlerProperty(element: ModelElement): boolean {
        const hasCustom: boolean = hasCustomAction(element.type);
        return hasCustom;
    }

    isApplicableHandler(element: ModelElement, handlerClassName: string): boolean {
        const ca = getCustomActions(element.type);
        const applicable: boolean = ca.map(v => v[0]).includes(handlerClassName);
        return applicable;
    }

    handlerCanBeExecuted(handler: CustomActionHandler, element: ModelElement, action: CustomAction, args: any): boolean | Promise<boolean> {
        const result = handler.constructor.name === action.handlerClass;
        this.logger.info('is correct handler? ' + handler + ':' + result);
        return result && handler.canExecute(action, ...args);
    }
    executeHandler(handler: CustomActionHandler, element: ModelElement, action: CustomAction, args: any): Action[] | Promise<Action[]> {
        const result = handler.execute(action, ...args);
        try {
            return result as Action[] | Promise<Action[]>;
        } catch {
            return [];
        }
    }
}
