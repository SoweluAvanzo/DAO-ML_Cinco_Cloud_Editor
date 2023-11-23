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

import { getEdgeTypes, getGraphTypes, getNodeTypes } from '@cinco-glsp/cinco-glsp-common';
import {
    SModelElement,
    SModelElementRegistration,
    SModelRegistry,
    ViewRegistration,
    ViewRegistry,
    configureView,
    createFeatureSet,
    registerModelElement
} from '@eclipse-glsp/client';
import { withDoubleClickFeature } from '../features/doubleclick-tool';
import { CincoEdge, CincoGraphModel, CincoNode } from '../model/model';
import { CincoEdgeView } from '../views/msl/cinco-edge-view';
import { CincoGraphView } from '../views/msl/cinco-graph-view';
import { CincoNodeView } from '../views/msl/cinco-node-view';

export function reregisterBindings(
    context: { bind: any; isBound: any },
    ctx: any,
    registry: SModelRegistry,
    viewRegistry: ViewRegistry
): void {
    // apply meta-specification
    const graphTypes = getGraphTypes().map((n: any) => n.elementTypeId) as any[];
    const nodeTypes = getNodeTypes().map((n: any) => n.elementTypeId) as any[];
    const edgeTypes = getEdgeTypes().map((n: any) => n.elementTypeId) as any[];
    registerBindingAndConstruction(context, ctx, registry, viewRegistry, graphTypes, CincoGraphModel, CincoGraphView);
    registerBindingAndConstruction(context, ctx, registry, viewRegistry, nodeTypes, CincoNode, CincoNodeView);
    registerBindingAndConstruction(context, ctx, registry, viewRegistry, edgeTypes, CincoEdge, CincoEdgeView);
}

function registerBindingAndConstruction(
    context: { bind: any; isBound: any },
    ctx: any,
    registry: SModelRegistry,
    viewRegistry: ViewRegistry,
    types: any[],
    constr: new () => SModelElement,
    viewConstr: any
): void {
    const registrations: SModelElementRegistration[] = [];
    const viewRegistrations: ViewRegistration[] = [];
    types.forEach(type => {
        // needs to be registered manually, since it is outside of the injection lifecycle:
        const features = {
            enable: [withDoubleClickFeature]
        };
        // manually register and bind constructor
        registerModelElement(context, type, constr, features);
        registrations.push({
            type: type,
            constr: constr,
            features
        } as SModelElementRegistration);

        // manual register and bind view
        configureView(context, type, viewConstr);
        const viewBinding = {
            type,
            factory: () => ctx.container.get(viewConstr)
        };
        viewRegistrations.push(viewBinding);
    });
    // register
    registerPostInjectionLifecycle(registry, viewRegistry, registrations, viewRegistrations);
}

function registerPostInjectionLifecycle(
    registry: SModelRegistry,
    viewRegistry: ViewRegistry,
    registrations: SModelElementRegistration[],
    viewRegistrations: ViewRegistration[]
): void {
    // register view and constructors of modelelements
    registrations.forEach(registration => {
        // register with features
        registry.register(registration.type, () => {
            // create default feature set
            let obj = registration.constr;
            let defaultFeatures;
            do {
                if (obj.name === 'CincoEdge' || obj.name === 'CincoNode') {
                    // TODO: check unsafe
                    if ((obj as any).getDefaultFeatures(registration.type)) {
                        defaultFeatures = (obj as any).getDefaultFeatures(registration.type);
                    }
                } else {
                    if (obj.DEFAULT_FEATURES) {
                        defaultFeatures = obj.DEFAULT_FEATURES;
                    }
                }
                obj = Object.getPrototypeOf(obj);
            } while (defaultFeatures === undefined && obj);
            if (!defaultFeatures && registration.features && registration.features.enable) {
                defaultFeatures = [];
            }
            if (defaultFeatures) {
                const featureSet = createFeatureSet(defaultFeatures, registration.features);
                const element = new registration.constr();
                element.features = featureSet;
                return element;
            } else {
                return new registration.constr();
            }
        });
    });
    viewRegistrations.forEach(vr => viewRegistry.register(vr.type, vr.factory()));
}
