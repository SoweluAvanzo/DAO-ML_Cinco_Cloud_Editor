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
import {
    EdgeType,
    getContainmentsOf,
    getEdgeSources,
    getEdgeTargets,
    getEdgeTypes,
    getNodeTypes,
    NodeType,
    isResizeable,
    isMovable,
    isDeletable,
    getGraphTypes
} from '@cinco-glsp/cinco-glsp-common';
import {
    DiagramConfiguration,
    EdgeTypeHint,
    GEdge,
    getDefaultMapping,
    GGraph,
    GModelElement,
    GModelElementConstructor,
    GNode,
    ServerLayoutKind,
    ShapeTypeHint
} from '@eclipse-glsp/server';
import { injectable } from 'inversify';

@injectable()
export class CincoDiagramConfiguration implements DiagramConfiguration {
    layoutKind = ServerLayoutKind.MANUAL;
    needsClientLayout = true;
    animatedUpdate = true;

    get typeMapping(): Map<string, GModelElementConstructor<GModelElement>> {
        const mapping = getDefaultMapping();
        const graphTypes = getGraphTypes().map(t => t.elementTypeId);
        const nodeTypes = getNodeTypes().map(t => t.elementTypeId);
        const edgeTypes = getEdgeTypes().map(t => t.elementTypeId);

        graphTypes.forEach(t => mapping.set(t, GGraph));
        nodeTypes.forEach(t => mapping.set(t, GNode));
        edgeTypes.forEach(t => mapping.set(t, GEdge));

        return mapping;
    }

    get shapeTypeHints(): ShapeTypeHint[] {
        const nodeTypes = getNodeTypes();
        const shapeTypeHints: ShapeTypeHint[] = [];
        nodeTypes.forEach((e: NodeType) => {
            const elementTypeId = e.elementTypeId;
            shapeTypeHints.push({
                elementTypeId: elementTypeId,
                deletable: isDeletable(elementTypeId),
                reparentable: e.reparentable ?? true,
                repositionable: isMovable(elementTypeId),
                resizable: isResizeable(elementTypeId),
                containableElementTypeIds: getContainmentsOf(e).map(n => n.elementTypeId)
            });
        });
        return shapeTypeHints;
    }

    get edgeTypeHints(): EdgeTypeHint[] {
        const edgeTypes = getEdgeTypes();
        const edgeTypeHints: EdgeTypeHint[] = edgeTypes.map((e: EdgeType) => {
            const outgoingEdgeFor = getEdgeSources(e.elementTypeId).map(n => n.elementTypeId);
            const incomingEdgeFor = getEdgeTargets(e.elementTypeId).map(n => n.elementTypeId);
            const elementTypeId = e.elementTypeId;
            return {
                elementTypeId: elementTypeId,
                deletable: isDeletable(elementTypeId),
                repositionable: isMovable(elementTypeId),
                routable: e.routable ?? true,
                sourceElementTypeIds: outgoingEdgeFor,
                targetElementTypeIds: incomingEdgeFor
            };
        });
        return edgeTypeHints;
    }
}
