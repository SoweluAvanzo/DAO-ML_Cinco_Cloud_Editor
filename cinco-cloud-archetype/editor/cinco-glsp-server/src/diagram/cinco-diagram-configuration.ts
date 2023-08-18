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
    NodeType
} from '@cinco-glsp/cinco-glsp-common';
import {
    DiagramConfiguration,
    EdgeTypeHint,
    getDefaultMapping,
    GModelElement,
    GModelElementConstructor,
    ServerLayoutKind,
    ShapeTypeHint
} from '@eclipse-glsp/server-node';
import { injectable } from 'inversify';

@injectable()
export class CincoDiagramConfiguration implements DiagramConfiguration {
    layoutKind = ServerLayoutKind.MANUAL;
    needsClientLayout = true;
    animatedUpdate = true;

    get typeMapping(): Map<string, GModelElementConstructor<GModelElement>> {
        return getDefaultMapping();
    }

    get shapeTypeHints(): ShapeTypeHint[] {
        const nodeTypes = getNodeTypes();
        const shapeTypeHints: ShapeTypeHint[] = [];
        nodeTypes.forEach((e: NodeType) => {
            shapeTypeHints.push({
                elementTypeId: e.elementTypeId,
                deletable: e.deletable,
                reparentable: e.reparentable,
                repositionable: e.repositionable,
                resizable: e.resizable,
                containableElementTypeIds: getContainmentsOf(e).map(n => n.elementTypeId)
            });
        });
        return shapeTypeHints;
    }

    get edgeTypeHints(): EdgeTypeHint[] {
        const edgeTypes = getEdgeTypes();
        const edgeTypeHints: EdgeTypeHint[] = [];
        edgeTypes.forEach((e: EdgeType) => {
            const outgoingEdgeFor = getEdgeSources(e).map(n => n.elementTypeId);
            const incomingEdgeFor = getEdgeTargets(e).map(n => n.elementTypeId);
            edgeTypeHints.push({
                elementTypeId: e.elementTypeId,
                deletable: e.deletable,
                repositionable: e.repositionable,
                routable: e.routable,
                sourceElementTypeIds: outgoingEdgeFor,
                targetElementTypeIds: incomingEdgeFor
            });
        });
        return edgeTypeHints;
    }
}
