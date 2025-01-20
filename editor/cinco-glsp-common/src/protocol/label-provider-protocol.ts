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
import { Action, ManagedBaseAction } from './shared-protocol';
import { hasStringProp } from './type-utils';
import * as crypto from 'crypto';

export enum LabelAnnotationType {
    POINTER,
    INSTANCE
}

export interface LabelRequestAction extends RequestAction<LabelResponseAction>, ManagedBaseAction {
    kind: typeof LabelRequestAction.KIND;
    modelId: string; // associated id of the model
    modelElementId: string; // specific modelElement for which a label should be provided
    annotatedElementType: string; // specififies the modelElementType from which the labelProvider-annotation originates from
    labelAnnotationType: LabelAnnotationType; // specifies where the annotation is placed (e.g. on prime-property or node-property)
    args?: any; // not yet specified
}

export namespace LabelRequestAction {
    export const KIND = 'labelRequest';

    export function is(object: any): object is LabelRequestAction {
        return (
            Action.hasKind(object, KIND) &&
            hasStringProp(object, 'modelId') &&
            hasStringProp(object, 'modelElementId') &&
            hasStringProp(object, 'annotatedElementType') &&
            hasStringProp(object, 'labelAnnotationType')
        );
    }

    export function create(
        modelId: string,
        modelElementId: string,
        annotatedElementType: string,
        labelAnnotationType: LabelAnnotationType = LabelAnnotationType.INSTANCE,
        options?: { args: any }
    ): LabelRequestAction {
        return {
            kind: KIND,
            requestId: crypto.randomUUID(),
            modelId,
            modelElementId,
            annotatedElementType,
            labelAnnotationType,
            ...options
        };
    }
}

export interface LabelResponseAction extends ResponseAction {
    kind: typeof LabelResponseAction.KIND;
    label: string; // the resulting label
    args?: any; // not yet specified
}

export namespace LabelResponseAction {
    export const KIND = 'labelResponse';

    export function is(object: any): object is LabelResponseAction {
        return Action.hasKind(object, KIND) && hasStringProp(object, 'label');
    }

    export function fromRequest(request: LabelRequestAction, label: string): LabelResponseAction {
        return LabelResponseAction.create(request.requestId, label);
    }

    export function create(
        responseId: string, // should be the requestId of the request
        label: string,
        options?: { args: any }
    ): LabelResponseAction {
        return {
            kind: KIND,
            responseId: responseId,
            label,
            ...options
        };
    }
}
