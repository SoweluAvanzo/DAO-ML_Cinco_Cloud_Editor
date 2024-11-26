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

import { LayoutOptionsProvider, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import {
    getLayoutOptionsProvider,
    hasLayoutOptionsProvider,
    LayoutOptionsRequestAction,
    LayoutOptionsResponse
} from '@cinco-glsp/cinco-glsp-common';
import { Action } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { BaseHandlerManager } from './base-handler-manager';

/**
 * Handler for action
 */

@injectable()
export class LayoutOptionsProviderManager extends BaseHandlerManager<LayoutOptionsRequestAction, LayoutOptionsProvider> {
    baseHandlerName = 'LayoutOptionsProvider';
    actionKinds: string[] = [LayoutOptionsRequestAction.KIND];

    hasHandlerProperty(element: ModelElement): boolean {
        return hasLayoutOptionsProvider(element.type);
    }

    isApplicableHandler(element: ModelElement, handlerClassName: string): boolean {
        return getLayoutOptionsProvider(element.type).includes(handlerClassName);
    }

    handlerCanBeExecuted(
        handler: LayoutOptionsProvider,
        element: ModelElement,
        action: LayoutOptionsRequestAction,
        args: any
    ): boolean | Promise<boolean> {
        return true;
    }

    async executeHandler(
        handler: LayoutOptionsProvider,
        element: ModelElement,
        request: LayoutOptionsRequestAction,
        args: any
    ): Promise<Action[]> {
        const layoutOptions = await handler.provide(request, args);
        return [LayoutOptionsResponse.fromRequest(request, layoutOptions)];
    }
}
