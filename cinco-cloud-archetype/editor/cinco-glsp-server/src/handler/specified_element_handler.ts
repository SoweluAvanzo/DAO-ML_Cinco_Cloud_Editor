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
import { GraphModelIndex } from '@cinco-glsp/cinco-glsp-api';
import { ElementType, NodeType, getSpecOf } from '@cinco-glsp/cinco-glsp-common';
import {
    CreateEdgeOperation,
    CreateNodeOperation,
    GModelElement,
    Operation,
    Point,
    TriggerEdgeCreationAction,
    TriggerNodeCreationAction,
    CreateNodeOperationHandler,
    ActionDispatcher,
    SaveModelAction
} from '@eclipse-glsp/server-node';
import { injectable, inject } from 'inversify';

@injectable()
export class SpecifiedElementHandler extends CreateNodeOperationHandler {
    @inject(GraphModelIndex)
    protected index: GraphModelIndex;
    @inject(ActionDispatcher)
    readonly actionDispatcher: ActionDispatcher;

    override label: string = this.specification?.label ?? 'undefined';
    _specification: ElementType | undefined;

    BLACK_LIST: string[] = [];

    get elementTypeId(): string | undefined {
        return this.elementTypeIds.length > 0 ? this.elementTypeIds[0] : undefined;
    }

    get elementTypeIds(): string[] {
        return [];
    }

    get specification(): ElementType | undefined {
        return this._specification;
    }

    set specification(spec: ElementType | undefined) {
        this._specification = spec;
    }

    execute(operation: Operation): void {
        throw new Error('Method not implemented.');
    }

    override get operationType(): string {
        if (NodeType.is(this._specification)) {
            return CreateNodeOperation.KIND;
        } else {
            return CreateEdgeOperation.KIND;
        }
    }

    override getTriggerActions(): (TriggerEdgeCreationAction | TriggerNodeCreationAction)[] {
        if (NodeType.is(this._specification)) {
            return this.elementTypeIds.map(e => TriggerNodeCreationAction.create(e));
        } else {
            return this.elementTypeIds.map(e => TriggerEdgeCreationAction.create(e));
        }
    }

    getLabelFor(elementTypeId: string): string {
        return getSpecOf(elementTypeId)?.label ?? elementTypeId;
    }

    override getContainer(operation: CreateNodeOperation): GModelElement | undefined {
        const index = this.modelState.index;
        return operation.containerId ? index.get(operation.containerId) : undefined;
    }

    override getLocation(operation: CreateNodeOperation): Point | undefined {
        return operation.location;
    }

    saveAndUpdate(): void {
        const paletteUpdateAction = {
            kind: 'enableToolPalette'
        };
        this.actionDispatcher.dispatch(paletteUpdateAction);
        // save model
        const graphmodel = this.index.getRoot();
        const fileUri = graphmodel._sourceUri;
        this.actionDispatcher.dispatch(SaveModelAction.create({ fileUri }));
    }
}
