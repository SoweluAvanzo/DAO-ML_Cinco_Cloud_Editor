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
import { Container, GraphModel, GraphModelState, IdentifiableElement, Node } from '@cinco-glsp/cinco-glsp-api';
import { ChangeContainerOperation, MaybePromise, OperationHandler, Point } from '@eclipse-glsp/server-node';
import { inject, injectable } from 'inversify';

@injectable()
export class ChangeContainerHandler implements OperationHandler {
    readonly operationType = ChangeContainerOperation.KIND;

    @inject(GraphModelState)
    protected readonly modelState: GraphModelState;

    execute(operation: ChangeContainerOperation): MaybePromise<void> {
        this.changeContainer(operation.elementId, operation.targetContainerId, operation.location ?? Point.ORIGIN);
    }

    protected changeContainer(elementId: string, targetContainerId: string, location: Point): void {
        const index = this.modelState.index;
        const element: IdentifiableElement | undefined = index.findElement(elementId);
        if (!element) {
            return;
        }
        if (Node.is(element)) {
            const oldParent = element.parent as Container | GraphModel;
            const newParent = index.findElement(targetContainerId) as Container | GraphModel;
            element.position = location;
            if (newParent !== undefined) {
                oldParent._containments = oldParent._containments.filter(e => e.id !== elementId);
                newParent._containments.push(element);
            }
        }
    }
}
