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
import { DeleteElementOperation, MaybePromise, OperationHandler, remove } from '@eclipse-glsp/server-node';
import { inject, injectable } from 'inversify';
import { Edge, IdentifiableElement, Node } from '../model/graph-model';
import { GraphModelState } from '../model/graph-model-state';

@injectable()
export class DeleteHandler implements OperationHandler {
    readonly operationType = DeleteElementOperation.KIND;

    @inject(GraphModelState)
    protected modelState: GraphModelState;

    execute(operation: DeleteElementOperation): MaybePromise<void> {
        operation.elementIds.forEach(elementId => this.deleteElement(elementId));
    }

    protected deleteElement(elementId: string): void {
        const index = this.modelState.index;
        const element: IdentifiableElement | undefined = index.findElement(elementId);
        if (!element) {
            return;
        }
        if (Node.is(element)) {
            const containingNode = index.findContainment(element as Node);
            if (containingNode !== undefined) {
                remove(containingNode._containments, element);
            } else {
                remove(this.modelState.graphModel._containments, element);
            }
        } else if (Edge.is(element)) {
            remove(this.modelState.graphModel.edges, element);
        }
    }
}
