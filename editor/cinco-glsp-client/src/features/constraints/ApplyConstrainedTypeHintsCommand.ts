/********************************************************************************
 * Copyright (c) 2019-2022 Cinco Cloud.
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
    ApplyTypeHintsCommand,
    Containable,
    containerFeature,
    reparentFeature,
    resizeFeature,
    Connectable,
    connectableFeature,
    deletableFeature,
    FeatureSet,
    moveFeature,
    GModelElement,
    isGModelElementSchema,
    GEdge
} from '@eclipse-glsp/client';
import { injectable, inject } from 'inversify';
import { CincoGraphModel } from '../../model/model';
import { isContainableByConstraints } from '../../utils/constraint-utils';
import { FrontendValidatingTypeHintProvider } from './FrontendValidatingTypeHintProvider';

@injectable()
export class ApplyConstrainedTypeHintsCommand extends ApplyTypeHintsCommand {
    @inject(FrontendValidatingTypeHintProvider)
    protected override typeHintProvider: FrontendValidatingTypeHintProvider;

    override applyShapeTypeHint(element: GModelElement): void {
        const hint = this.typeHintProvider.getShapeTypeHint(element);
        if (isModifiableFeatureSet(element.features)) {
            if (hint) {
                addOrRemove(element.features, deletableFeature, hint.deletable);
                addOrRemove(element.features, moveFeature, hint.repositionable);
                addOrRemove(element.features, resizeFeature, hint.resizable);
                addOrRemove(element.features, reparentFeature, hint.reparentable);
            }

            // add container feature
            addOrRemove(element.features, containerFeature, true);
            const containable = createContainable(element);
            Object.assign(element, containable);

            // add edges feature
            if (!(element instanceof CincoGraphModel)) {
                addOrRemove(element.features, connectableFeature, true);
                const connectable = createConnectable(element, this.typeHintProvider);
                Object.assign(element, connectable);
            }
        }
    }
}

function createConnectable(element: GModelElement, typeHintProvider: FrontendValidatingTypeHintProvider): Connectable {
    return {
        canConnect: (routable: GEdge, role: 'source' | 'target'): boolean => {
            const validSourceEdges = typeHintProvider.getValidEdgeElementTypes(element, 'source');
            const validTargetEdges = typeHintProvider.getValidEdgeElementTypes(element, 'target');
            return role === 'source' ? validSourceEdges.includes(routable.type) : validTargetEdges.includes(routable.type);
        }
    };
}

function createContainable(container: GModelElement): Containable {
    return {
        isContainableElement: element => {
            const targetType: string =
                element instanceof GModelElement ? element.type : isGModelElementSchema(element) ? element.toString() : element;
            return isContainableByConstraints(container, targetType);
        }
    };
}

function addOrRemove(features: Set<symbol>, feature: symbol, add: boolean): void {
    if (add && !features.has(feature)) {
        features.add(feature);
    } else if (!add && features.has(feature)) {
        features.delete(feature);
    }
}

function isModifiableFeatureSet(featureSet?: FeatureSet): featureSet is FeatureSet & Set<symbol> {
    return featureSet !== undefined && featureSet instanceof Set;
}
