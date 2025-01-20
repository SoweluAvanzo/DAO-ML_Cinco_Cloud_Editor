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
import { Container, GraphModel, IdentifiableElement, Node } from '@cinco-glsp/cinco-glsp-api';
import { ChangeContainerOperation, Point } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { CincoJsonOperationHandler } from './cinco-json-operation-handler';
import { deletableValue } from '@cinco-glsp/cinco-glsp-common';

@injectable()
export class ChangeContainerHandler extends CincoJsonOperationHandler {
    readonly operationType = ChangeContainerOperation.KIND;

    override async executeOperation(operation: ChangeContainerOperation): Promise<void> {
        await this.lockModelActions();
        await this.changeContainer(operation.elementId, operation.targetContainerId, operation.location ?? Point.ORIGIN);
        this.unlockModelActions();
    }

    protected async changeContainer(elementId: string, targetContainerId: string, location: Point): Promise<void> {
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
                oldParent.containments = oldParent.containments.filter(containment => deletableValue(containment).id !== elementId);
                newParent.containments.push(element);
            }
            await this.handleStateChange(element, false);
            await this.handleStateChange(oldParent, false);
            await this.handleStateChange(newParent);
        }
    }
}
