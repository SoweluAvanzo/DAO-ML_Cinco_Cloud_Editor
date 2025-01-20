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
import { Appearance } from '../meta-specification';
import { Action } from './shared-protocol';
import { hasArrayProp, hasObjectProp, hasStringProp } from './type-utils';

export interface AppearanceUpdateRequestAction extends Action {
    kind: typeof AppearanceUpdateRequestAction.KIND;
    modelId: string; // associated id of the model
    modelElementId: string; // associated id of the model-element
    args?: any; // e.g. a specific css-class
}

export namespace AppearanceUpdateRequestAction {
    export const KIND = 'appearanceUpdateRequest';

    export function is(object: any): object is AppearanceUpdateRequestAction {
        return Action.hasKind(object, KIND) && hasStringProp(object, 'modelElementId');
    }

    export function create(modelId: string, modelElementId: string, options?: { args: any }): AppearanceUpdateRequestAction {
        return {
            kind: KIND,
            modelId,
            modelElementId,
            ...options
        };
    }
}

/**
 * send from server to client to apply apply the appearance update
 */
export interface ApplyAppearanceUpdateAction extends Action {
    kind: typeof ApplyAppearanceUpdateAction.KIND;

    /**
     * ModelElement id
     */
    modelElementId: string;

    /**
     * css-based appearance update
     */
    cssClasses?: string[];

    /**
     * cinco-appearance based appearance update
     */
    appearance?: Appearance;
}

export namespace ApplyAppearanceUpdateAction {
    export const KIND = 'applyAppearanceUpdate';

    export function is(object: any): object is ApplyAppearanceUpdateAction {
        return (
            Action.hasKind(object, KIND) &&
            hasStringProp(object, 'modelElementId') &&
            (hasArrayProp(object, 'cssClasses') || hasObjectProp(object, 'appearance'))
        );
    }

    export function create(modelElementId: string, cssClasses?: string[], appearance?: Appearance): ApplyAppearanceUpdateAction {
        return {
            kind: KIND,
            modelElementId,
            cssClasses,
            appearance
        };
    }
}
