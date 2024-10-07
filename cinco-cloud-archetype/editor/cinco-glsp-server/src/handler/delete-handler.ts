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
import { Container, Edge, Node, ModelElement, HookManager } from '@cinco-glsp/cinco-glsp-api';
import { DeleteElementOperation, remove } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { CincoJsonOperationHandler } from './cinco-json-operation-handler';
import { isChoice, DeleteArgument, HookType, ValidationResponseAction, hasValidation } from '@cinco-glsp/cinco-glsp-common';

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
        if (!element) {
            return;
        }
        this.deleteElement(element, operation);
    }

    protected deleteElement(element: ModelElement, operation: DeleteElementOperation): void {
        const parameters: DeleteArgument = {
            kind: 'Delete',
            modelElementId: element.id,
            deleted: undefined
        };
        // CAN
        if (
            !HookManager.executeHook(
                parameters,
                HookType.CAN_DELETE,
                this.modelState,
                this.logger,
                this.actionDispatcher,
                this.sourceModelStorage,
                this.submissionHandler
            )
        ) {
            return;
        }
        // PRE
        HookManager.executeHook(
            parameters,
            HookType.PRE_DELETE,
            this.modelState,
            this.logger,
            this.actionDispatcher,
            this.sourceModelStorage,
            this.submissionHandler
        );
        parameters.deleted = element;
        if (Container.is(element)) {
            const containments = element.containments ?? [];
            containments.forEach((c: Node) => this.deleteElement(c, operation));
        }
        if (Node.is(element)) {
            // disjunct from container
            const containingNode = this.modelState.index.findContainment(element as Node);
            if (containingNode !== undefined) {
                remove(containingNode._containments, element);
            } else {
                remove(this.modelState.graphModel._containments, element);
            }
            // remove associated edges
            this.modelState.graphModel.edges.forEach((edge: Edge) => {
                if (edge.sourceID === element.id || edge.targetID === element.id) {
                    remove(this.modelState.graphModel._edges, edge);
                } else if (isChoice(edge.sourceID) && edge.sourceID.options.includes(element.id)) {
                    edge.sourceID = {
                        tag: 'choice',
                        options: edge.sourceID.options.filter(sourceID => sourceID !== element.id)
                    };
                }
            });
        } else if (Edge.is(element)) {
            remove(this.modelState.graphModel.edges, element);
        }
        // POST
        HookManager.executeHook(
            parameters,
            HookType.POST_DELETE,
            this.modelState,
            this.logger,
            this.actionDispatcher,
            this.sourceModelStorage,
            this.submissionHandler
        );
        // remove validation
        if (hasValidation(parameters.deleted?.type ?? '')) {
            this.actionDispatcher.dispatch(ValidationResponseAction.create(this.modelState.graphModel.id, parameters.modelElementId, []));
        }
    }
}
