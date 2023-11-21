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
import { ElementType, getSpecOf } from '@cinco-glsp/cinco-glsp-common';
import {
    CreateEdgeOperation,
    CreateNodeOperation,
    GModelElement,
    Operation,
    Point,
    TriggerEdgeCreationAction,
    TriggerNodeCreationAction,
    SaveModelAction,
    CreateNodeOperationHandler,
    CreateEdgeOperationHandler,
    getRelativeLocation
} from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { CincoJsonOperationHandler } from './cinco-json-operation-handler';

export type CreateOperationHandler = SpecifiedElementHandler | CreateNodeOperationHandler | CreateEdgeOperationHandler;

@injectable()
export class SpecifiedElementHandler extends CincoJsonOperationHandler {
    _specification: ElementType | undefined;
    override readonly operationType: any;
    override label: string = this.specification?.label ?? 'undefined';

    BLACK_LIST: string[] = [];

    executeOperation(operation: Operation): void {
        throw new Error('Method not implemented.');
    }

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

    getLabelFor(elementTypeId: string): string {
        return getSpecOf(elementTypeId)?.label ?? elementTypeId;
    }

    getContainer(operation: CreateNodeOperation): GModelElement | undefined {
        const index = this.modelState.index;
        return operation.containerId ? index.get(operation.containerId) : undefined;
    }

    getLocation(operation: CreateNodeOperation): Point | undefined {
        return operation.location;
    }

    /**
     * Retrieves the diagram absolute location and the target container from the given {@link CreateNodeOperation}
     * and converts the absolute location to coordinates relative to the given container.
     *  Relative coordinates can only be retrieved if the given container element is part of
     * a hierarchy of {@link GBoundsAware} elements. This means each (recursive) parent element need to
     * implement {@link GBoundsAware}. If that is not the case this method returns `undefined`.
     * @param absoluteLocation The diagram absolute position.
     * @param container The container element.
     * @returns The relative position or `undefined`.
     */
    getRelativeLocation(operation: CreateNodeOperation): Point | undefined {
        const container = this.getContainer(operation) ?? this.modelState.root;
        const absoluteLocation = this.getLocation(operation) ?? Point.ORIGIN;
        return getRelativeLocation(absoluteLocation, container);
    }

    saveAndUpdate(): void {
        const paletteUpdateAction = {
            kind: 'enableToolPalette'
        };
        this.actionDispatcher.dispatch(paletteUpdateAction);
        // save model
        const graphmodel = this.modelState.index.getRoot();
        const fileUri = graphmodel._sourceUri;
        this.actionDispatcher.dispatch(SaveModelAction.create({ fileUri }));
    }
}

@injectable()
export class AbstractSpecifiedNodeElementHandler extends SpecifiedElementHandler implements CreateNodeOperationHandler {
    override readonly operationType = CreateNodeOperation.KIND;

    getTriggerActions(): TriggerNodeCreationAction[] {
        return this.elementTypeIds.map(e => TriggerNodeCreationAction.create(e));
    }
}
@injectable()
export class AbstractSpecifiedEdgeElementHandler extends SpecifiedElementHandler implements CreateEdgeOperationHandler {
    override readonly operationType = CreateEdgeOperation.KIND;

    getTriggerActions(): TriggerEdgeCreationAction[] {
        return this.elementTypeIds.map(e => TriggerEdgeCreationAction.create(e));
    }
}
