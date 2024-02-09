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

import { RequestAction, ResponseAction } from '@eclipse-glsp/protocol';
import { Attribute } from '../meta-specification';
import { ModelElementIndex, ObjectPointer, PropertyChange } from './property-model';
import { Action, Operation } from './shared-protocol';
import { hasObjectProp, hasStringProp } from './type-utils';
import * as uuid from 'uuid';

/**
 * Theia
 */

export const PropertyViewUpdateCommand = { id: 'CincoCloud.updatePropertyView' };
export const CincoCloudPropertyWidgetCommand = { id: 'cincoCloudProperty:toggle' };

/**
 * Action
 *
 * This action will be dispatched to the backend by the listeners of the ActionTool
 */

export interface PropertyViewAction extends RequestAction<PropertyViewResponseAction> {
    kind: typeof PropertyViewAction.KIND;
    modelElementId: string;
}
export namespace PropertyViewAction {
    export const KIND = 'propertyViewRequest';

    export function create(modelElementId: string): PropertyViewAction {
        return {
            kind: KIND,
            modelElementId,
            requestId: uuid.v4()
        };
    }
}

/**
 * Client Action
 *
 * This action will be dispatched to the client as a response to the PropertyViewAction
 */

export interface PropertyViewResponseAction extends ResponseAction {
    kind: typeof PropertyViewResponseAction.KIND;
    modelElementIndex: ModelElementIndex;
    modelType: string;
    modelElementId: string;
    attributeDefinitions: Attribute[];
    values: any;
}
export namespace PropertyViewResponseAction {
    export const KIND = 'propertyViewResponse';

    export function create(
        modelElementIndex: ModelElementIndex,
        modelType: string,
        modelElementId: string,
        attributeDefinitions: Attribute[],
        values: any,
        responseId: string
    ): PropertyViewResponseAction {
        return {
            kind: KIND,
            modelElementIndex,
            modelType,
            modelElementId,
            attributeDefinitions,
            values,
            responseId: responseId
        };
    }
}

export interface PropertyEditOperation extends Operation {
    kind: typeof PropertyEditOperation.KIND;
    modelElementId: string;
    pointer: ObjectPointer;
    name: string;
    change: PropertyChange;
}

export namespace PropertyEditOperation {
    export const KIND = 'propertyEdit';

    export function is(object: any): object is PropertyEditOperation {
        return (
            Operation.hasKind(object, KIND) &&
            hasStringProp(object, 'modelElementId') &&
            hasStringProp(object, 'name') &&
            hasObjectProp(object, 'pointer') &&
            hasObjectProp(object, 'change')
        );
    }

    export function create(modelElementId: string, pointer: ObjectPointer, name: string, change: PropertyChange): PropertyEditOperation {
        return {
            kind: KIND,
            isOperation: true,
            modelElementId,
            pointer,
            name,
            change
        };
    }
}

export interface PropertyEditAction extends Action {
    kind: typeof PropertyEditAction.KIND;
    modelElementId: string;
    pointer: ObjectPointer;
    name: string;
    change: PropertyChange;
}
export namespace PropertyEditAction {
    export const KIND = 'propertyEdit';

    export function create(modelElementId: string, pointer: ObjectPointer, name: string, change: PropertyChange): PropertyEditAction {
        return { kind: KIND, modelElementId, pointer, name, change };
    }
}
