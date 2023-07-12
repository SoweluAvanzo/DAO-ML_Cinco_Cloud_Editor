/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud.
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
import { ChangeRoutingPointsOperation, Logger, OperationHandler } from '@eclipse-glsp/server-node';
import { inject, injectable } from 'inversify';
import { Edge } from '../model/graph-model';
import { GraphModelState } from '../model/graph-model-state';
import { ChangeAwareRoutingPointsOperation, RoutingPoint } from '../shared/protocol/routingpoint-protocol';

@injectable()
export class RoutingPointHandler implements OperationHandler {
    operationType = ChangeRoutingPointsOperation.KIND;

    @inject(Logger)
    protected readonly logger: Logger;
    @inject(GraphModelState)
    protected readonly modelState: GraphModelState;

    execute(operation: ChangeRoutingPointsOperation): void {
        if (ChangeRoutingPointsOperation.is(operation)) {
            if (ChangeAwareRoutingPointsOperation.is(operation)) {
                for (const routingPointChanges of operation.routingPointChanges) {
                    const modelElementId = routingPointChanges.elementId;
                    const element = this.modelState.index.findElement(modelElementId) as any;
                    if (Edge.is(element)) {
                        const removedRoutingPoints = (routingPointChanges.removedRoutingPoints ?? []).map(
                            // convert routingPoint of GLSP to routingPoints of meta-specification (explicitly)
                            routingPoint =>
                                [{ x: routingPoint[0].x, y: routingPoint[0].y } as RoutingPoint, routingPoint[1]] as [RoutingPoint, number]
                        );
                        const newRoutingPoints = (routingPointChanges.newRoutingPoints ?? []).map(
                            // convert routingPoint of GLSP to routingPoints of meta-specification (explicitly)
                            routingPoint =>
                                [{ x: routingPoint[0].x, y: routingPoint[0].y } as RoutingPoint, routingPoint[1]] as [RoutingPoint, number]
                        );

                        // remove deleted routingPoints
                        for (const removed of removedRoutingPoints) {
                            // insert the point by index
                            const routingPoints1 = element.routingPoints.slice(0, removed[1]);
                            const routingPoints2 = element.routingPoints.slice(removed[1] + 1, element.routingPoints.length);
                            element.routingPoints = routingPoints1.concat(routingPoints2);
                        }
                        // add new routingPoints
                        for (const newRoutingPoint of newRoutingPoints) {
                            // inject the point by index
                            const index = newRoutingPoint[1];
                            const newPoint = newRoutingPoint[0];
                            const routingPoints1 = element.routingPoints.slice(0, index);
                            const routingPoints2 = element.routingPoints.slice(index, element.routingPoints.length);
                            element.routingPoints = routingPoints1.concat([newPoint]).concat(routingPoints2);
                        }
                    }
                }
            } else {
                for (const newRoutingPoints of operation.newRoutingPoints) {
                    const modelElementId = newRoutingPoints.elementId;
                    const element = this.modelState.index.findElement(modelElementId) as any;
                    if (Edge.is(element)) {
                        const routingPoints = (newRoutingPoints.newRoutingPoints ?? []).map(
                            // convert routingPoint of GLSP to routingPoints of meta-specification (explicitly)
                            routingPoint => ({ x: routingPoint.x, y: routingPoint.y } as RoutingPoint)
                        );

                        // update all routingPoints
                        element.routingPoints = routingPoints;
                    }
                }
            }
        }
    }
}
