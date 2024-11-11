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
import { Container, Edge, Node, HookManager, ModelElement, ModelElementContainer } from '@cinco-glsp/cinco-glsp-api';
import { DeleteElementOperation, remove } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { CincoJsonOperationHandler } from './cinco-json-operation-handler';
import {
    isChoice,
    DeleteArgument,
    HookType,
    deletableValue,
    filterOptions,
    Deletable,
    ValidationResponseAction,
    hasValidation
} from '@cinco-glsp/cinco-glsp-common';

@injectable()
export class DeleteHandler extends CincoJsonOperationHandler {
    readonly operationType = DeleteElementOperation.KIND;

    executeOperation(operation: DeleteElementOperation): void {
        operation.elementIds.forEach(elementId => this.deleteElementById(elementId, operation));
        this.saveAndUpdate();
    }

    protected deleteElementById(elementId: string, operation: DeleteElementOperation): void {
        const index = this.modelState.index;
        const element: ModelElement | undefined = index.findModelElement(elementId);
        if (element === undefined) {
            return;
        }

        const parameters: DeleteArgument = {
            kind: 'Delete',
            modelElementId: element.id,
            deleted: undefined
        };
        // CAN
        if (!HookManager.executeHook(parameters, HookType.CAN_DELETE, this.getBundle())) {
            return;
        }
        // PRE
        HookManager.executeHook(parameters, HookType.PRE_DELETE, this.getBundle());
        parameters.deleted = element;

        if (Node.is(element)) {
            const container = index.findContainerOf(elementId);
            if (container === undefined) {
                return;
            }
            const containment = container.containments.find(c => deletableValue(c).id === elementId)!;
            this.deleteNode(container, containment);
        } else if (Edge.is(element)) {
            const container = this.modelState.graphModel;
            const containment = container.edges.find(c => deletableValue(c).id === elementId)!;
            remove(this.modelState.graphModel.edges, containment);
        }

        // POST
        HookManager.executeHook(parameters, HookType.POST_DELETE, this.getBundle());
        // remove validation
        if (hasValidation(parameters.deleted?.type ?? '')) {
            this.actionDispatcher.dispatch(ValidationResponseAction.create(this.modelState.graphModel.id, parameters.modelElementId, []));
        }
    }

    protected deleteNode(container: ModelElementContainer, containment: Deletable<Node>): void {
        const node = deletableValue(containment);
        if (Container.is(node)) {
            for (const child of node.containments) {
                this.deleteNode(node, child);
            }
        }

        // remove associated edges
        const toDelete: Deletable<Edge>[] = [];
        // collect
        this.modelState.graphModel.edges.forEach((edgeContainment: Deletable<Edge>) => {
            const edge = deletableValue(edgeContainment);
            if (edge.sourceID === node.id || edge.targetID === node.id) {
                toDelete.push(edgeContainment);
            } else if (isChoice(edge.sourceID) && edge.sourceID.options.includes(node.id)) {
                edge.sourceID = filterOptions(edge.sourceID, sourceID => sourceID !== node.id);
            } else if (isChoice(edge.targetID) && edge.targetID.options.includes(node.id)) {
                edge.targetID = filterOptions(edge.targetID, targetID => targetID !== node.id);
            }
        });
        // remove
        toDelete.forEach(edgeContainment => {
            remove(this.modelState.graphModel.edges, edgeContainment);
        });

        // remove node
        remove(container.containments, containment);
    }
}
