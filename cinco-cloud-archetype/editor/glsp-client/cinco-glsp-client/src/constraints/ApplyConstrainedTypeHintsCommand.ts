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
    SModelElementSchema
} from '@eclipse-glsp/client';
import { injectable } from 'inversify';
import { Connectable, connectableFeature, deletableFeature, FeatureSet, moveFeature, SModelElement } from 'sprotty';
import { CincoGraphModel } from '../model/model';
import { isContainableByConstraints } from '../utils/constraint-utils';

@injectable()
export class ApplyConstrainedTypeHintsCommand extends ApplyTypeHintsCommand {
    override applyShapeTypeHint(element: SModelElement): void {
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
                const validSourceEdges = this.typeHintProvider.getValidEdgeElementTypes(element, 'source');
                const validTargetEdges = this.typeHintProvider.getValidEdgeElementTypes(element, 'target');
                const connectable = createConnectable(validSourceEdges, validTargetEdges);
                Object.assign(element, connectable);
            }
        }
    }
}

function createConnectable(validSourceEdges: string[], validTargetEdges: string[]): Connectable {
    return {
        canConnect: (routable, role) =>
            role === 'source' ? validSourceEdges.includes(routable.type) : validTargetEdges.includes(routable.type)
    };
}

function createContainable(container: SModelElement): Containable {
    return {
        isContainableElement: element => {
            const targetType: string =
                element instanceof SModelElement ? element.type : SModelElementSchema.is(element) ? element.toString() : element;
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
