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

import { Action, ManagedBaseAction } from './shared-protocol';

export interface CustomAction extends ManagedBaseAction {
    kind: typeof CustomAction.KIND;
    selectedElementIds: string[];
    modelElementId: string;
    handlerClass: string;
    args: any;
}
export namespace CustomAction {
    export const KIND = 'CustomAction';

    export function is(object: any): object is CustomAction {
        return Action.hasKind(object, KIND);
    }

    export function create(modelElementId: string, selectedElementIds: string[], handlerClass: string, args?: any): CustomAction {
        return {
            kind: KIND,
            selectedElementIds,
            modelElementId,
            handlerClass,
            args: args ?? {}
        };
    }
}
