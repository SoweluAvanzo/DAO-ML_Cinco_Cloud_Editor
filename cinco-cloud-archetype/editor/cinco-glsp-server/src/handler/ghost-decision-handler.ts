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
import { injectable } from 'inversify';
import { CincoJsonOperationHandler } from './cinco-json-operation-handler';
import { deletableValue, RestoreModelElementOperation } from '@cinco-glsp/cinco-glsp-common';
import { Edge, Node } from '@cinco-glsp/cinco-glsp-api';

@injectable()
export class GhostDecisionHandler extends CincoJsonOperationHandler {
    readonly operationType = RestoreModelElementOperation.KIND;

    override executeOperation({ modelElementId }: RestoreModelElementOperation): void {
        const element = this.modelState.index.findModelElement(modelElementId);
        if (Node.is(element)) {
            const container = this.modelState.index.findContainerOf(modelElementId);
            if (container === undefined) {
                throw new Error(`Could not find container for ID ${modelElementId} while restoring model element.`);
            }
            container.containments = container.containments.map(containment => {
                const node = deletableValue(containment);
                return node.id === modelElementId ? node : containment;
            });
        } else if (Edge.is(element)) {
            this.modelState.graphModel.edges = this.modelState.graphModel.edges.map(containment => {
                const edge = deletableValue(containment);
                return edge.id === modelElementId ? edge : containment;
            });
        }
        this.saveAndUpdate();
    }
}
