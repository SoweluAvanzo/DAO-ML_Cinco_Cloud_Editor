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
import { Action, ManagedBaseAction } from './shared-protocol';
import { hasStringProp } from './type-utils';

export interface ValueUpdateRequestAction extends ManagedBaseAction {
    kind: typeof ValueUpdateRequestAction.KIND;
    modelId: string; // associated id of the model
    modelElementId: string; // associated id of the model-element
    reason?: string;
    args?: any; // not yet specified
}

export namespace ValueUpdateRequestAction {
    export const KIND = 'valueUpdateRequest';

    export function is(object: any): object is ValueUpdateRequestAction {
        return Action.hasKind(object, KIND) && hasStringProp(object, 'modelId') && hasStringProp(object, 'modelElementId');
    }

    export function create(
        modelId: string,
        modelElementId: string,
        reason?: string | undefined,
        options?: { args: any }
    ): ValueUpdateRequestAction {
        return {
            kind: KIND,
            modelId,
            modelElementId,
            reason,
            ...options
        };
    }
}
