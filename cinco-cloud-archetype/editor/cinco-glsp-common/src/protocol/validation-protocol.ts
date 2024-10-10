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
import * as uuid from 'uuid';

/**
 * Theia
 */

export const ValidationModelUpdateCommand = { id: 'CincoCloud.updateValidationModel' };
export const CincoCloudProjectValidationWidgetCommand = { id: 'cincoCloudProjectValidation:toggle' };
export const CincoCloudModelValidationWidgetCommand = { id: 'cincoCloudModelValidation:toggle' };
export const ValidationModelWrapperCommand = {
    id: 'CincoCloud.triggerModelValidation',
    label: 'Validate Model',
    category: 'Cinco Cloud'
};

/**
 * Model
 */

export enum ValidationStatus {
    Pass,
    Info,
    Warning,
    Error
}

export interface ValidationMessage {
    status: ValidationStatus;
    name: string;
    message: string;
}

/**
 * Action
 *
 * This action will be dispatched to the backend by the listeners of the ActionTool
 */

export interface ValidationRequestAction extends RequestAction<ValidationResponseAction> {
    kind: typeof ValidationRequestAction.KIND;
    modelId: string;
    modelElementId: string;
    requestId: string;
}
export namespace ValidationRequestAction {
    export const KIND = 'validationRequest';

    export function create(modelId: string, modelElementId: string): ValidationRequestAction {
        return {
            kind: KIND,
            modelId,
            modelElementId,
            requestId: uuid.v4()
        };
    }
}

export interface ValidationResponseAction extends ResponseAction {
    kind: typeof ValidationResponseAction.KIND;
    modelId: string;
    modelElementId: string; // associated id of the model-element
    messages: ValidationMessage[];
}
export namespace ValidationResponseAction {
    export const KIND = 'validationResponse';

    export function create(
        modelId: string,
        modelElementId: string,
        messages: ValidationMessage[],
        responseId: string = uuid.v4()
    ): ValidationResponseAction {
        return {
            kind: KIND,
            modelId,
            modelElementId,
            messages,
            responseId
        };
    }

    export function containsInfos(validationResults: ValidationResponseAction[]): boolean {
        return containsStatus(validationResults, ValidationStatus.Info);
    }

    export function containsPass(validationResults: ValidationResponseAction[]): boolean {
        return containsStatus(validationResults, ValidationStatus.Pass);
    }

    export function containsWarnings(validationResults: ValidationResponseAction[]): boolean {
        return containsStatus(validationResults, ValidationStatus.Warning);
    }

    export function containsErrors(validationResults: ValidationResponseAction[]): boolean {
        return containsStatus(validationResults, ValidationStatus.Error);
    }

    export function containsStatus(validationResults: ValidationResponseAction[], status: ValidationStatus): boolean {
        for (const res of validationResults) {
            const stats = res.messages.filter(m => m.status === status);
            if (stats.length > 0) {
                return true;
            }
        }
        return false;
    }
}
