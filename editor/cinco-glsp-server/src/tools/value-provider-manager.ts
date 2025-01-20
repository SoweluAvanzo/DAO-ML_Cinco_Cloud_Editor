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
import { ValueProvider, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import { ValueUpdateRequestAction, getValueProvider, hasValueProvider } from '@cinco-glsp/cinco-glsp-common';
import { Action } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { BaseHandlerManager } from './base-handler-manager';

/**
 * Handler for action
 */

@injectable()
export class ValueProviderManager extends BaseHandlerManager<ValueUpdateRequestAction, ValueProvider> {
    baseHandlerName = 'ValueProvider';
    actionKinds: string[] = [ValueUpdateRequestAction.KIND];

    hasHandlerProperty(element: ModelElement): boolean {
        return hasValueProvider(element.type);
    }

    isApplicableHandler(element: ModelElement, handlerClassName: string): boolean {
        return getValueProvider(element.type).includes(handlerClassName);
    }

    handlerCanBeExecuted(
        handler: ValueProvider,
        element: ModelElement,
        action: ValueUpdateRequestAction,
        args: any
    ): boolean | Promise<boolean> {
        return true;
    }

    executeHandler(
        handler: ValueProvider,
        element: ModelElement,
        action: ValueUpdateRequestAction,
        args: any
    ): Action[] | Promise<Action[]> {
        return handler.updateValue(action, args) as Action[] | Promise<Action[]>;
    }
}
