/********************************************************************************
 * Copyright (c) 2022 Cinco Cloud and others.
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
import { GraphModelState, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import {
    LabeledModelElementReference,
    ModelElementIndex,
    PropertyViewAction,
    PropertyViewResponseAction,
    getCustomTypes,
    getModelElementSpecifications
} from '@cinco-glsp/cinco-glsp-common';
import { Action, ActionHandler, Logger, MaybePromise } from '@eclipse-glsp/server';
import { inject, injectable } from 'inversify';

/**
 * Handler for action
 */

@injectable()
export class PropertyViewHandler implements ActionHandler {
    @inject(Logger)
    protected readonly logger: Logger;
    @inject(GraphModelState)
    protected readonly modelState: GraphModelState;

    actionKinds: string[] = [PropertyViewAction.KIND];

    execute(action: PropertyViewAction, ...args: unknown[]): MaybePromise<Action[]> {
        const element = this.modelState.index.findModelElement(action.modelElementId) as ModelElement;
        if (!element) {
            // element is not part of this graphmodel (maybe another)
            return [];
        }
        // build index
        const index = this.modelState.graphModel.index;
        const modelType = this.modelState.graphModel.type;
        const modelElementIndex: ModelElementIndex = {};
        for (const spec of getModelElementSpecifications()) {
            const allElementsOfType = index.getElements(spec.elementTypeId);
            modelElementIndex[spec.elementTypeId] = allElementsOfType.map((e: any) => {
                const id = e.id;
                const elementTypeId = spec.elementTypeId;
                const properties = e.args?.properties ?? '{ }';
                const name = JSON.parse(properties.toString())['name'] ?? '';
                const label = spec.label;
                return this.buildLabeledModelElementReference({ id: id, elementTypeId: elementTypeId, name: name, label: label });
            });
        }
        const customTypes = getCustomTypes();
        // build message
        const consecutiveActions: Action[] = [];
        if (element !== undefined) {
            consecutiveActions.push(
                PropertyViewResponseAction.create(
                    modelElementIndex,
                    modelType,
                    element.id,
                    element.propertyDefinitions,
                    customTypes,
                    element.properties,
                    action.requestId
                )
            );
        }

        // send responses
        return consecutiveActions;
    }

    protected buildLabeledModelElementReference({
        id,
        elementTypeId,
        name,
        label
    }: {
        id: string;
        elementTypeId?: string;
        name?: string;
        label?: string;
    }): LabeledModelElementReference {
        return { id, elementTypeId: elementTypeId ?? '', name: name ?? '', label: label ?? '' };
    }
}
