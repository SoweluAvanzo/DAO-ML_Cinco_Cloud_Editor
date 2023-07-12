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
import { Action, SaveModelAction } from '@eclipse-glsp/server-node';
import { injectable } from 'inversify';
import { ModelElement } from '../model/graph-model';
import { getAppearanceProvider, hasAppearanceProvider } from '../shared/meta-specification';
import { RequestAppearanceUpdateAction } from '../shared/protocol/appearance-provider-protocol';
import { AppearanceProvider } from './api/appearance-provider';
import { BaseHandlerManager } from './base-handler-manager';

/**
 * Handler for action
 */

@injectable()
export class AppearanceProviderManager extends BaseHandlerManager<RequestAppearanceUpdateAction, AppearanceProvider> {
    baseHandlerName = 'AppearanceProvider';
    actionKinds: string[] = [RequestAppearanceUpdateAction.KIND];

    hasHandlerProperty(element: ModelElement): boolean {
        return hasAppearanceProvider(element.type);
    }

    isApplicableHandler(element: ModelElement, handlerClassName: string): boolean {
        return getAppearanceProvider(element.type) === handlerClassName;
    }

    handlerCanBeExecuted(
        handler: AppearanceProvider,
        element: ModelElement,
        action: RequestAppearanceUpdateAction,
        args: any
    ): boolean | Promise<boolean> {
        return true;
    }

    executeHandler(
        handler: AppearanceProvider,
        element: ModelElement,
        action: RequestAppearanceUpdateAction,
        args: any
    ): Action[] | Promise<Action[]> {
        const result = handler.getAppearance(action, args) as Action[] | Promise<Action[]>;
        // save appearance
        if (result instanceof Promise) {
            return new Promise<Action[]>((resolve, _) => {
                const re: Promise<Action[]> = result.then((p: Action[]) => p.concat([SaveModelAction.create()]));
                resolve(re);
            });
        } else {
            result.push(SaveModelAction.create());
        }
        return result;
    }
}
