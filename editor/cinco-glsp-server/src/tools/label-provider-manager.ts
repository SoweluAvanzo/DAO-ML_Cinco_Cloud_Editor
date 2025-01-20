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

import { LabelProvider, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import {
    getLabelProvider,
    getLabelProviderOfPrime,
    hasLabelProvider,
    hasLabelProviderOfPrime,
    LabelAnnotationType,
    LabelRequestAction,
    LabelResponseAction
} from '@cinco-glsp/cinco-glsp-common';
import { Action } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { BaseHandlerManager } from './base-handler-manager';

/**
 * Handler for action
 */

@injectable()
export class LabelProviderManager extends BaseHandlerManager<LabelRequestAction, LabelProvider> {
    baseHandlerName = 'LabelProvider';
    actionKinds: string[] = [LabelRequestAction.KIND];

    hasHandlerProperty(element: ModelElement, request: LabelRequestAction): boolean {
        if (request.labelAnnotationType === LabelAnnotationType.POINTER) {
            return hasLabelProviderOfPrime(request.annotatedElementType);
        }
        return hasLabelProvider(request.annotatedElementType);
    }

    isApplicableHandler(element: ModelElement, handlerClassName: string, request: LabelRequestAction): boolean {
        if (request.labelAnnotationType === LabelAnnotationType.POINTER) {
            return getLabelProviderOfPrime(request.annotatedElementType).includes(handlerClassName);
        }
        return getLabelProvider(request.annotatedElementType).includes(handlerClassName);
    }

    handlerCanBeExecuted(handler: LabelProvider, element: ModelElement, action: LabelRequestAction, args: any): boolean | Promise<boolean> {
        return true;
    }

    async executeHandler(handler: LabelProvider, element: ModelElement, request: LabelRequestAction, args: any): Promise<Action[]> {
        const label = await handler.provide(request, args);
        return [LabelResponseAction.fromRequest(request, label)];
    }
}
