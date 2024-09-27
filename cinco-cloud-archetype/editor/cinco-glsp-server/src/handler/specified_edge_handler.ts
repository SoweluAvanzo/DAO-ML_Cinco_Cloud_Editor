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
import { Edge, GraphModelIndex, HookManager } from '@cinco-glsp/cinco-glsp-api';
import { getEdgeSpecOf, getEdgeTypes, EdgeType, CreateEdgeArgument, HookType } from '@cinco-glsp/cinco-glsp-common';
import { CreateEdgeOperation } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { AbstractSpecifiedEdgeElementHandler } from './specified_element_handler';

@injectable()
export class SpecifiedEdgeHandler extends AbstractSpecifiedEdgeElementHandler {
    override get elementTypeIds(): string[] {
        const result = getEdgeTypes()
            .map((e: EdgeType) => e.elementTypeId)
            .filter((e: string) => this.BLACK_LIST.indexOf(e) < 0);
        return result;
    }

    override executeOperation(operation: CreateEdgeOperation): void {
        // CAN
        const parameters: CreateEdgeArgument = {
            kind: 'Create',
            elementKind: 'Edge',
            elementTypeId: operation.elementTypeId,
            modelElementId: '<NONE>',
            sourceElementId: operation.sourceElementId,
            targetElementId: operation.targetElementId
        };
        const canCreate = (): boolean =>
            HookManager.executeHook(
                parameters,
                HookType.CAN_CREATE,
                this.modelState,
                this.logger,
                this.actionDispatcher,
                this.sourceModelStorage,
                this.submissionHandler
            );
        if (this.checkConstraints(operation) && canCreate()) {
            // PRE
            HookManager.executeHook(
                parameters,
                HookType.PRE_CREATE,
                this.modelState,
                this.logger,
                this.actionDispatcher,
                this.sourceModelStorage,
                this.submissionHandler
            );
            const edge = this.createEdge(operation.sourceElementId, operation.targetElementId, operation.elementTypeId);
            edge.index = this.modelState.index;
            const graphmodel = this.modelState.index.getRoot();
            graphmodel.edges.push(edge);
            this.modelState.refresh();
            // POST
            parameters.modelElementId = edge.id;
            HookManager.executeHook(
                parameters,
                HookType.POST_CREATE,
                this.modelState,
                this.logger,
                this.actionDispatcher,
                this.sourceModelStorage,
                this.submissionHandler
            );
            this.saveAndUpdate();
        }
    }

    protected createEdge(sourceID: string, targetID: string, elementTypeId: string): Edge {
        const edge = new Edge();
        edge.initialize({ type: elementTypeId, sourceID, targetID });
        return edge;
    }

    checkConstraints(operation: CreateEdgeOperation): boolean {
        const index = this.modelState.index as GraphModelIndex;
        const source = index.findNode(operation.sourceElementId);
        const target = index.findNode(operation.targetElementId);
        const specification = getEdgeSpecOf(operation.elementTypeId);
        if (source === undefined || target === undefined || specification === undefined) {
            return false;
        }
        const canBeEdgeSource: boolean = source?.canBeEdgeSource(operation.elementTypeId);
        const canBeEdgeTarget: boolean = target?.canBeEdgeTarget(operation.elementTypeId);
        return canBeEdgeSource && canBeEdgeTarget;
    }
}
