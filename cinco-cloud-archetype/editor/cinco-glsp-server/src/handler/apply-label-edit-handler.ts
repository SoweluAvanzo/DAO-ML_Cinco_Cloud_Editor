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
import { PropertyViewRequestAction } from '@cinco-glsp/cinco-glsp-common';
import { ApplyLabelEditOperation } from '@eclipse-glsp/protocol';
import { GLSPServerError, GNode, toTypeGuard } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { CincoJsonOperationHandler } from './cinco-json-operation-handler';

@injectable()
export class ApplyLabelEditHandler extends CincoJsonOperationHandler {
    readonly operationType = ApplyLabelEditOperation.KIND;

    executeOperation(operation: ApplyLabelEditOperation): void {
        const index = this.modelState.index;
        // Retrieve the parent node of the label that should be edited
        const activityNode = index.findParentElement(operation.labelId, toTypeGuard(GNode));
        if (activityNode) {
            const activity = index.findModelElement(activityNode.id);
            if (!activity) {
                throw new GLSPServerError(`Could not retrieve the parent task for the label with id ${operation.labelId}`);
            }
            this.actionDispatcher.dispatchAfterNextUpdate(PropertyViewRequestAction.create(activityNode.id));
            activity.setProperty('name', operation.text);
        }
    }
}
