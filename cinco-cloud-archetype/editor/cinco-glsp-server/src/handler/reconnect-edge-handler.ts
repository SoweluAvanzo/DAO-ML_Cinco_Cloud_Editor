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
import { GEdge, GLSPServerError, GNode, ReconnectEdgeOperation } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { CincoJsonOperationHandler } from './cinco-json-operation-handler';

@injectable()
export class ReconnectEdgeHandler extends CincoJsonOperationHandler {
    operationType = ReconnectEdgeOperation.KIND;

    executeOperation(operation: ReconnectEdgeOperation): void {
        if (!operation.edgeElementId || !operation.sourceElementId || !operation.targetElementId) {
            throw new GLSPServerError('Incomplete reconnect connection action');
        }

        const index = this.modelState.index;

        const gEdge = index.findByClass(operation.edgeElementId, GEdge);
        const gSource = index.findByClass(operation.sourceElementId, GNode);
        const gTarget = index.findByClass(operation.targetElementId, GNode);

        if (!gEdge) {
            throw new Error(`Invalid edge in graph model: edge ID ${operation.edgeElementId}`);
        }

        const edge = index.findEdge(gEdge.id);

        if (!edge) {
            throw new Error(`Invalid edge in source model: edge ID ${gEdge.id}`);
        }
        if (!gSource) {
            throw new Error(`Invalid source in graph model: source ID ${operation.sourceElementId}`);
        }
        if (!gTarget) {
            throw new Error(`Invalid target in graph model: target ID ${operation.targetElementId}`);
        }

        const source = index.findNode(gSource.id);
        const target = index.findNode(gTarget.id);
        if (source && target && edge.canConnectToSource(source, _ => false) && edge.canConnectToTarget(target, _ => false)) {
            edge.sourceID = gSource.id;
            edge.targetID = gTarget.id;
            edge.routingPoints = [];
        } else {
            throw new Error(`Could not change source and target of edge: ${edge.id}`);
        }
    }
}
