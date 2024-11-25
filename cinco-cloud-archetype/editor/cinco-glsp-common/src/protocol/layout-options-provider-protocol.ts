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
import { RequestAction, ResponseAction } from '@eclipse-glsp/protocol';
import { Action } from './shared-protocol';
import { hasStringProp } from './type-utils';
import * as crypto from 'crypto';

export interface LayoutOptionsRequestAction extends RequestAction<LayoutOptionsResponse> {
    kind: typeof LayoutOptionsRequestAction.KIND;
    modelId: string; // associated id of the model
    modelElementId: string; // associated id of the model-element
    args?: any; // not yet specified
}

export namespace LayoutOptionsRequestAction {
    export const KIND = 'layoutOptionsRequest';

    export function is(object: any): object is LayoutOptionsRequestAction {
        return Action.hasKind(object, KIND) && hasStringProp(object, 'modelId') && hasStringProp(object, 'modelElementId');
    }

    export function create(modelId: string, modelElementId: string, options?: { args: any }): LayoutOptionsRequestAction {
        return {
            kind: KIND,
            requestId: crypto.randomUUID(),
            modelId,
            modelElementId,
            ...options
        };
    }
}

export interface LayoutOptionsResponse extends ResponseAction {
    kind: typeof LayoutOptionsResponse.KIND;
    layoutOptions: string; // the resulting layout
    args?: any; // not yet specified
}

export namespace LayoutOptionsResponse {
    export const KIND = 'layoutResponse';

    export function is(object: any): object is LayoutOptionsResponse {
        return Action.hasKind(object, KIND) && hasStringProp(object, 'layoutOptions');
    }

    export function fromRequest(request: LayoutOptionsRequestAction, layoutOptions: string): LayoutOptionsResponse {
        return LayoutOptionsResponse.create(request.requestId, layoutOptions);
    }

    export function create(
        responseId: string, // should be the requestId of the request
        layoutOptions: string,
        options?: { args: any }
    ): LayoutOptionsResponse {
        return {
            kind: KIND,
            responseId: responseId,
            layoutOptions,
            ...options
        };
    }
}
