/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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
    ActionDispatcher,
    GCompartment,
    GGraph,
    GModelElement,
    GModelElementConstructor,
    GModelFactory,
    GModelRoot,
    LayoutEngine,
    Logger,
    MaybePromise,
    ModelState,
    SourceModelStorage
} from '@eclipse-glsp/server';
import { ElkNode } from 'elkjs/lib/elk-api';
import ElkConstructor from 'elkjs/lib/elk.bundled';
import { ContainerModule, injectable } from 'inversify';
import {
    DefaultElementFilter,
    ElementFilter,
    ElkFactory,
    ElkModuleOptions,
    FallbackLayoutConfigurator,
    GlspElkLayoutEngine,
    LayoutConfigurator
} from '@eclipse-glsp/layout-elk';
import { ContextBundle, Edge, GraphModel, Node, GraphModelState, HookManager, GraphGModelFactory } from '@cinco-glsp/cinco-glsp-api';
import { HookType, LayoutArgument } from '@cinco-glsp/cinco-glsp-common';

export function configureELKLayoutModule(options: ElkModuleOptions): ContainerModule {
    return new ContainerModule(bind => {
        if (options.elementFilter) {
            bind(ElementFilter).to(options.elementFilter).inSingletonScope();
        } else {
            bind(ElementFilter).to(DefaultElementFilter).inSingletonScope();
        }

        if (options.layoutConfigurator) {
            bind(LayoutConfigurator).to(options.layoutConfigurator);
        } else {
            bind(LayoutConfigurator)
                .toDynamicValue(context => new FallbackLayoutConfigurator(options.algorithms, options.defaultLayoutOptions))
                .inSingletonScope();
        }

        const elkFactory: ElkFactory = () =>
            new ElkConstructor({
                algorithms: options.algorithms,
                defaultLayoutOptions: options.defaultLayoutOptions
            });

        bind(ElkFactory).toConstantValue(elkFactory);

        bind(GlspElkLayoutEngine)
            .toDynamicValue(context => {
                const container = context.container;
                const factory = container.get<ElkFactory>(ElkFactory);
                const filter = container.get<ElementFilter>(ElementFilter);
                const configurator = container.get<LayoutConfigurator>(LayoutConfigurator);
                const modelState = container.get<ModelState>(ModelState);
                const logger = container.get<Logger>(Logger);
                const actionDispatcher = container.get<ActionDispatcher>(ActionDispatcher);
                const storage = container.get<SourceModelStorage>(SourceModelStorage);
                const frontendModelFactory = container.get<GModelFactory>(GModelFactory);
                return new CincoGlspElkLayoutEngine(
                    factory,
                    filter,
                    configurator,
                    modelState,
                    logger,
                    actionDispatcher,
                    storage,
                    frontendModelFactory as GraphGModelFactory
                );
            })
            .inSingletonScope();
        bind(LayoutEngine).toService(GlspElkLayoutEngine);
    });
}

/**
 * An implementation of GLSP's {@link LayoutEngine} interface that retrieves the graphical model from the {@link ModelState},
 * transforms this model into an ELK graph and then invokes the underlying ELK instance for layout computation.
 */
@injectable()
export class CincoGlspElkLayoutEngine extends GlspElkLayoutEngine {
    constructor(
        elkFactory: ElkFactory,
        protected override readonly filter: ElementFilter,
        protected override readonly configurator: LayoutConfigurator,
        protected override modelState: ModelState,
        protected logger: Logger,
        protected actionDispatcher: ActionDispatcher,
        protected sourceModelStorage: SourceModelStorage,
        protected frontendModelFactory: GraphGModelFactory
    ) {
        super(elkFactory, filter, configurator, modelState);
    }

    getBundle(): ContextBundle {
        if (!(this.modelState instanceof GraphModelState)) {
            throw new Error('ModelState is not a GraphModelState. Only that kind is supported!');
        }
        return new ContextBundle(this.modelState, this.logger, this.actionDispatcher, this.sourceModelStorage, this.frontendModelFactory);
    }

    override layout(): MaybePromise<GModelRoot> {
        const root = this.modelState.root;
        if (!(root instanceof GGraph)) {
            return root;
        }
        const context = this.getBundle();
        const graphModel = context.modelState.index.findModelElement(root.id);
        if (!graphModel || !GraphModel.is(graphModel)) {
            this.logger.error('Can not layout. GraphModel was not found in modelState!');
            return root;
        }
        const graphicalElements = graphModel
            .getAllContainedElements()
            .concat(graphModel)
            .filter(m => GraphModel.is(m) || Node.is(m) || Edge.is(m));

        // check if layouting is allowed
        const blockingElement = graphicalElements.find(
            e =>
                !HookManager.executeHook(
                    { kind: 'Layout', modelElementId: e.id, elementTypeId: e.type } as LayoutArgument,
                    HookType.CAN_LAYOUT,
                    context
                )
        );
        if (blockingElement) {
            this.logger.error(
                'Can not layout. An element blocked the layouting: ' + blockingElement.type + ' (' + blockingElement.id + ')'
            );
            return root;
        }

        // pre layouting
        graphicalElements.find(e =>
            HookManager.executeHook(
                { kind: 'Layout', modelElementId: e.id, elementTypeId: e.type } as LayoutArgument,
                HookType.PRE_LAYOUT,
                context
            )
        );

        // Elk Layouting
        this.elkEdges = [];
        this.idToElkElement = new Map();
        const elkGraph = this.transformToElk(root) as ElkNode;
        const layoutedRoot = this.elk.layout(elkGraph).then(result => {
            this.applyLayout(result);
            return root;
        });

        // post layouting
        graphicalElements.find(e =>
            HookManager.executeHook(
                { kind: 'Layout', modelElementId: e.id, elementTypeId: e.type } as LayoutArgument,
                HookType.POST_LAYOUT,
                context
            )
        );

        return layoutedRoot;
    }

    /**
     * Searches for all children of the given element that are an instance of the given {@link GModelElementConstructor}
     * and are included by the {@link ElementFilter}. Also considers children that are nested inside of {@link GCompartment}s.
     * @param element The element whose children should be queried.
     * @param constructor The class instance that should be matched
     * @returns A list of all matching children.
     */
    protected override findChildren<G extends GModelElement>(element: GModelElement, constructor: GModelElementConstructor<G>): G[] {
        const result: G[] = [];
        element.children.forEach(child => {
            // TODO: Sami - fix type of the GLSP-guys: this.filter.apply(element) -> this.filter.apply(child)
            if (child instanceof constructor && this.filter.apply(child)) {
                result.push(child);
            } else if (child instanceof GCompartment) {
                result.push(...this.findChildren(child, constructor));
            }
        });

        return result;
    }
}
