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
import { EdgeType, NodeType, Point, Size, isChoice, getSpecOf, deletableValue, Deletable, isGhost } from '@cinco-glsp/cinco-glsp-common';
import {
    GEdge,
    GGraph,
    GLabel,
    GModelElement,
    GModelFactory,
    GModelRootSchema,
    GModelSerializer,
    GNode,
    GNodeBuilder
} from '@eclipse-glsp/server';
import { inject, injectable } from 'inversify';
import { Container, Edge, GraphModel, Node } from './graph-model';
import { GraphModelState } from './graph-model-state';

@injectable()
export class GraphGModelFactory implements GModelFactory {
    @inject(GraphModelState)
    readonly modelState: GraphModelState;
    @inject(GModelSerializer)
    protected serializer: GModelSerializer;

    createModel(): void {
        const graphmodel = this.modelState.graphModel;
        this.modelState.index.indexGraphModel(graphmodel);
        const newRoot = this.buildGModel();
        this.modelState.updateRoot(newRoot);
    }

    updateModel(graphModel: GraphModel): void {
        this.modelState.graphModel = graphModel;
        const newRoot = this.buildGModel();
        this.modelState.updateRoot(newRoot);
    }

    serializeGModel(): GModelRootSchema {
        return this.serializer.createSchema(this.modelState.root);
    }

    protected buildGModel(): GGraph {
        const graphModel = this.modelState.graphModel;
        const children = this.collectChildren(graphModel);
        const newRoot = GGraph.builder().type(graphModel.type).id(graphModel.id).addChildren(children).build();
        return newRoot;
    }

    protected collectChildren(container: GraphModel | Container): GModelElement[] {
        const children: GModelElement[] = [];
        container.containments.forEach(containment => {
            const element = deletableValue(containment);
            if (Container.is(element)) {
                const containerChildren = this.collectChildren(element as Container);
                const containerNode = this.createNode(containment);
                containerNode.children = containerNode.children.concat(containerChildren);
                children.push(containerNode);
            } else if (Node.is(element)) {
                children.push(this.createNode(containment));
            }
        });
        if (container instanceof GraphModel) {
            container.edges.forEach(e => children.push(...this.createEdge(e)));
        }
        return children;
    }

    protected createNode<T extends Node>(containment: Deletable<T>, add?: (preBuild: GNodeBuilder, t: T) => GNodeBuilder): GNode {
        const node = deletableValue(containment);
        const spec = getSpecOf(node.type) as NodeType | undefined;

        // css
        const cssClasses: string[] = node.cssClasses ?? [];

        // layout is specified
        const layout = 'hbox';
        const layoutOptions = spec?.view?.layoutOptions !== undefined ? spec.view.layoutOptions : [];
        const builder = GNode.builder().type(node.type).id(node.id).position(node.position);
        if (cssClasses !== undefined) {
            cssClasses?.forEach(css => builder.addCssClass(css));
        }
        if (add !== undefined) {
            add(builder, node);
        }
        if (layout !== undefined) {
            builder.layout(layout);
        }
        if (node.size !== undefined) {
            builder.addLayoutOptions({
                prefWidth: node.size.width,
                prefHeight: node.size.height
            });
        }
        if (layoutOptions.length > 0) {
            layoutOptions.forEach((option: Map<string, string>) => {
                builder.addLayoutOptions(option);
            });
        }
        // add view- and property-information
        builder.addArg('persistedView', JSON.stringify(node.view));
        builder.addArg('properties', JSON.stringify(node.properties));
        if (isGhost(containment)) {
            builder.addChildren(this.createGhostNodes(node.id, -40));
        }
        return builder.build();
    }

    protected createGhostNodes(modelElementID: string, verticalOffset: number): GNode[] {
        return [
            this.createGhostMarker(modelElementID, verticalOffset),
            this.createDeleteButton(modelElementID, verticalOffset),
            this.createRestoreButton(modelElementID, verticalOffset)
        ];
    }

    protected createGhostMarker(modelElementID: string, verticalOffset: number): GNode {
        return GNode.builder()
            .type('marker:ghost')
            .id(this.markerGhostID(modelElementID))
            .position({ x: 0, y: verticalOffset })
            .size(40, 20)
            .build();
    }

    protected createDeleteButton(modelElementID: string, verticalOffset: number): GNode {
        return GNode.builder()
            .type('button:delete')
            .id(this.buttonDeleteID(modelElementID))
            .position({ x: 0, y: verticalOffset + 20 })
            .size(20, 20)
            .addArg('modelElementID', modelElementID)
            .build();
    }

    protected createRestoreButton(modelElementID: string, verticalOffset: number): GNode {
        return GNode.builder()
            .type('button:restore')
            .id(this.buttonRestoreID(modelElementID))
            .position({ x: 20, y: verticalOffset + 20 })
            .size(20, 20)
            .addArg('modelElementID', modelElementID)
            .build();
    }

    protected createEdge<T extends Edge>(containment: Deletable<T>): GModelElement[] {
        const edge = deletableValue(containment);
        if (isGhost(containment) || isChoice(edge.sourceID) || isChoice(edge.targetID)) {
            const sourceSegments = edge.sourceIDs.map(sourceID =>
                this.buildEdgeSegment(
                    edge,
                    this.edgeSourceSegmentID(edge.id, sourceID),
                    sourceID,
                    this.markerEdgeSourceTargetConflictID(edge.id),
                    isChoice(edge.sourceID)
                        ? [
                              GLabel.builder()
                                  .type('button:edge-source-choice')
                                  .id(this.buttonEdgeSourceChoiceID(edge.id, sourceID))
                                  .size(20, 20)
                                  .text('foobar')
                                  .edgePlacement({
                                      position: 0.5,
                                      rotate: false,
                                      side: 'on',
                                      offset: 0
                                  })
                                  .addArg('edgeID', edge.id)
                                  .addArg('sourceID', sourceID)
                                  .build()
                          ]
                        : []
                )
            );
            const conflictMarker = this.buildConflictMarker(edge, isGhost(containment) ? this.createGhostNodes(edge.id, 0) : []);
            const targetSegments = edge.targetIDs.map(targetID =>
                this.buildEdgeSegment(
                    edge,
                    this.edgeTargetSegmentID(edge.id, targetID),
                    this.markerEdgeSourceTargetConflictID(edge.id),
                    targetID,
                    isChoice(edge.targetID)
                        ? [
                              GLabel.builder()
                                  .type('button:edge-target-choice')
                                  .id(this.buttonEdgeTargetChoiceID(edge.id, targetID))
                                  .size(20, 20)
                                  .text('foobar')
                                  .edgePlacement({
                                      position: 0.5,
                                      rotate: false,
                                      side: 'on',
                                      offset: 0
                                  })
                                  .addArg('edgeID', edge.id)
                                  .addArg('targetID', targetID)
                                  .build()
                          ]
                        : []
                )
            );
            return [...sourceSegments, conflictMarker, ...targetSegments];
        } else {
            return [this.buildEdgeSegment(edge, edge.id, edge.sourceID, edge.targetID, [])];
        }
    }

    protected buildEdgeSegment<T extends Edge>(edge: T, id: string, sourceID: string, targetID: string, childen: GModelElement[]): GEdge {
        const spec = getSpecOf(edge.type) as EdgeType | undefined;
        const cssClasses = edge.cssClasses ?? [];
        const routerKind = spec?.view?.routerKind;

        const builder = GEdge.builder() //
            .type(edge.type)
            .id(id)
            .sourceId(sourceID)
            .targetId(targetID)
            .addChildren(childen);
        cssClasses.forEach((css: string) => builder.addCssClass(css));
        if (routerKind !== undefined) {
            builder.routerKind(routerKind);
        }
        builder.addArg('routingPoints', JSON.stringify(edge.routingPoints));
        builder.addArg('persistedView', JSON.stringify(edge.view));
        builder.addArg('properties', JSON.stringify(edge.properties));
        return builder.build();
    }

    protected buildConflictMarker(edge: Edge, children: GNode[]): GNode {
        return GNode.builder()
            .type('marker:edge-source-target-conflict')
            .id(this.markerEdgeSourceTargetConflictID(edge.id))
            .position(this.calculateConflictMarkerPosition(edge.sources, edge.targets))
            .size(conflictMarkerSize, conflictMarkerSize)
            .addChildren(children)
            .build();
    }

    protected calculateConflictMarkerPosition(sources: ReadonlyArray<Node>, targets: ReadonlyArray<Node>): Point {
        const sourcePositions = this.nodeCenterPoints(sources);
        const targetPositions = this.nodeCenterPoints(targets);
        const markerCenter = this.positionAverage([this.positionAverage(sourcePositions), this.positionAverage(targetPositions)]);

        return {
            x: markerCenter.x - conflictMarkerSize / 2,
            y: markerCenter.y - conflictMarkerSize / 2
        };
    }

    protected nodeCenterPoints(nodes: ReadonlyArray<Node>): ReadonlyArray<Point> {
        return Array.from(nodes).map(node => this.centerPoint(this.absolutePosition(node), node._size ?? { width: 0, height: 0 }));
    }

    protected centerPoint(position: Point, size: Size): Point {
        return {
            x: position.x + size.width / 2,
            y: position.y + size.height / 2
        };
    }

    protected positionAverage(points: ReadonlyArray<Point>): Point {
        return {
            x: this.average(points.map(point => point.x)),
            y: this.average(points.map(point => point.y))
        };
    }

    protected absolutePosition(node: Node): Point {
        const iterate = (absolutePosition: Point, element: Node | GraphModel | undefined): Point => {
            if (element === undefined || GraphModel.is(element)) {
                return absolutePosition;
            } else {
                return iterate(
                    { x: absolutePosition.x + element.position.x, y: absolutePosition.y + element.position.y },
                    this.modelState.index.findContainerOf(element.id)
                );
            }
        };
        return iterate({ x: 0, y: 0 }, node);
    }

    protected average(values: number[]): number {
        return values.reduce((a, b) => a + b, 0) / values.length;
    }

    protected markerGhostID(nodeID: string): string {
        return `ghost-marker-${nodeID}`;
    }

    protected buttonDeleteID(nodeID: string): string {
        return `button-delete-${nodeID}`;
    }

    protected buttonRestoreID(nodeID: string): string {
        return `button-restore-${nodeID}`;
    }

    protected edgeSourceSegmentID(edgeID: string, nodeID: string): string {
        return `edge-source-segement-${edgeID}-${nodeID}`;
    }

    protected edgeTargetSegmentID(edgeID: string, nodeID: string): string {
        return `edge-target-segement-${edgeID}-${nodeID}`;
    }

    protected markerEdgeSourceTargetConflictID(edgeID: string): string {
        return `conflict-marker-${edgeID}`;
    }

    protected buttonEdgeSourceChoiceID(edgeID: string, sourceID: string): string {
        return `button-edge-source-choice-${edgeID}-${sourceID}`;
    }

    protected buttonEdgeTargetChoiceID(edgeID: string, targetID: string): string {
        return `button-edge-target-choice-${edgeID}-${targetID}`;
    }
}

const conflictMarkerSize = 40;
