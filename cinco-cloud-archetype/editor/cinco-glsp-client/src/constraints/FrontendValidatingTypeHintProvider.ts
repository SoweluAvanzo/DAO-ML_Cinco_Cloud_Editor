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
    EdgeTypeHint,
    getElementTypeId,
    ShapeTypeHint,
    TypeHint,
    TypeHintProvider,
    GModelElement,
    GNode,
    IActionDispatcher,
    TYPES
} from '@eclipse-glsp/client';
import { inject, injectable } from 'inversify';
import { canBeEdgeSource, canBeEdgeTarget } from '../utils/constraint-utils';

/**
 * This class's purpose is to make sure, that the typeHints are dynamically fetched and not statically set.
 * The execute-method is called, whenever the context, i.e. mode, changes.
 */
@injectable()
export class FrontendValidatingTypeHintProvider extends TypeHintProvider {
    @inject(TYPES.IActionDispatcherProvider) protected actionDispatcherProvider: () => Promise<IActionDispatcher>;

    getValidEdgeElementTypes(input: GModelElement | GModelElement | string, role: 'source' | 'target'): string[] {
        const elementTypeId = getElementTypeId(input);
        if (role === 'source' || role === 'target') {
            return Array.from(
                // all compatible edgeTypeId
                Array.from(this.edgeHints.values())
                    .filter(hint =>
                        role === 'source'
                            ? (hint.sourceElementTypeIds ?? []).some(sourceElementTypeId => elementTypeId === sourceElementTypeId)
                            : (hint.targetElementTypeIds ?? []).some(targetElementTypeId => elementTypeId === targetElementTypeId)
                    )
                    .map(hint => hint.elementTypeId)
                    // all explicit elementTypes that can relate to the concrete source/target element
                    .filter(edgeType => this.canBeRelated(edgeType, input, role))
            );
        }
        return [];
    }

    override getShapeTypeHint(input: GModelElement | GModelElement | string): ShapeTypeHint | undefined {
        return this.getTypeHint(input, this.shapeHints);
    }

    override getEdgeTypeHint(input: GModelElement | GModelElement | string): EdgeTypeHint | undefined {
        return this.getTypeHint(input, this.edgeHints);
    }

    /**
     * Copied from '@eclipse-glsp/client' but disabled polymorphic features.
     * @param input
     * @param hints
     * @returns
     */
    override getTypeHint<T extends TypeHint>(input: GModelElement | GModelElement | string, hints: Map<string, T>): T | undefined {
        const type = getElementTypeId(input);
        return hints.get(type);
    }

    canBeRelated(edgeType: string, input: GModelElement | string, role: 'source' | 'target'): boolean {
        // access the specification and check if the node can be part of the relation
        if (typeof input === 'string') {
            return true; // input is type, cannot be checked
        }
        if (input === undefined) {
            return false;
        }
        if (input instanceof GNode) {
            if (role === 'source' && 'outgoingEdges' in input && input instanceof GNode) {
                return canBeEdgeSource(input, edgeType);
            } else if (role === 'target' && 'incomingEdges' in input) {
                return canBeEdgeTarget(input, edgeType);
            }
        }
        return false;
    }
}
