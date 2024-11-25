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
    findParentByClass,
    GCompartment,
    GEdge,
    GGraph,
    GLabel,
    GModelElement,
    GModelElementConstructor,
    GModelFactory,
    GModelRoot,
    GNode,
    GPort,
    LayoutEngine,
    Logger,
    ModelState,
    SourceModelStorage
} from '@eclipse-glsp/server';
import { ElkEdge, ElkGraphElement, ElkLabel, ElkNode, ElkPort, ElkPrimitiveEdge } from 'elkjs/lib/elk-api';
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
import { CincoLayoutConfigurator } from './cinco-layout-configurator';

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
    protected override readonly configurator: CincoLayoutConfigurator;

    constructor(
        elkFactory: ElkFactory,
        protected override readonly filter: ElementFilter,
        configurator: LayoutConfigurator,
        protected override modelState: ModelState,
        protected logger: Logger,
        protected actionDispatcher: ActionDispatcher,
        protected sourceModelStorage: SourceModelStorage,
        protected frontendModelFactory: GraphGModelFactory
    ) {
        super(elkFactory, filter, configurator, modelState);
        this.configurator = configurator as CincoLayoutConfigurator;
    }

    getBundle(): ContextBundle {
        if (!(this.modelState instanceof GraphModelState)) {
            throw new Error('ModelState is not a GraphModelState. Only that kind is supported!');
        }
        return new ContextBundle(this.modelState, this.logger, this.actionDispatcher, this.sourceModelStorage, this.frontendModelFactory);
    }

    override async layout(): Promise<GModelRoot> {
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
        const elkGraph = (await this.transformToElkAsync(root)) as ElkNode;
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

    protected async transformToElkAsync(model: GModelElement): Promise<ElkGraphElement> {
        if (model instanceof GGraph) {
            const graph = await this.transformGraphAsync(model);
            this.elkEdges.forEach(elkEdge => {
                const parent = this.findCommonAncestor(elkEdge as ElkPrimitiveEdge);
                if (parent) {
                    parent.edges!.push(elkEdge);
                }
            });
            return graph;
        } else if (model instanceof GNode) {
            return this.transformNodeAsync(model);
        } else if (model instanceof GEdge) {
            return this.transformEdgeAsync(model);
        } else if (model instanceof GLabel) {
            return this.transformPortAsync(model);
        } else if (model instanceof GPort) {
            return this.transformPortAsync(model);
        }

        throw new Error('Type not supported: ' + model.type);
    }

    protected async transformGraphAsync(graph: GGraph): Promise<ElkGraphElement> {
        const elkGraph: ElkNode = {
            id: graph.id,
            layoutOptions: await this.configurator.applyAsync(graph)
        };
        if (graph.children) {
            elkGraph.children = (await Promise.all(
                this.findChildren(graph, GNode).map(child => this.transformToElkAsync(child))
            )) as ElkNode[];
            elkGraph.edges = [];
            this.elkEdges.push(
                ...((await Promise.all(this.findChildren(graph, GEdge).map(child => this.transformToElkAsync(child)))) as ElkEdge[])
            );
        }

        this.idToElkElement.set(graph.id, elkGraph);
        return elkGraph;
    }

    protected async transformNodeAsync(node: GNode): Promise<ElkNode> {
        const elkNode: ElkNode = {
            id: node.id,
            layoutOptions: await this.configurator.applyAsync(node)
        };

        if (node.children) {
            elkNode.children = (await Promise.all(
                this.findChildren(node, GNode).map(child => this.transformToElkAsync(child))
            )) as ElkNode[];
            elkNode.edges = [];
            this.elkEdges.push(
                ...((await Promise.all(this.findChildren(node, GEdge).map(child => this.transformToElkAsync(child)))) as ElkEdge[])
            );

            elkNode.labels = (await Promise.all(
                this.findChildren(node, GLabel).map(child => this.transformToElkAsync(child))
            )) as ElkLabel[];
            elkNode.ports = (await Promise.all(this.findChildren(node, GPort).map(child => this.transformToElkAsync(child)))) as ElkPort[];
        }

        this.transformShape(elkNode, node);
        this.idToElkElement.set(node.id, elkNode);

        return elkNode;
    }

    protected async transformEdgeAsync(edge: GEdge): Promise<ElkEdge> {
        const elkEdge: ElkPrimitiveEdge = {
            id: edge.id,
            source: edge.sourceId,
            target: edge.targetId,
            layoutOptions: await this.configurator.applyAsync(edge)
        };
        const sourceElement = this.modelState.index.get(edge.sourceId);
        if (sourceElement instanceof GPort) {
            const parentNode = findParentByClass(sourceElement, GNode);
            if (parentNode) {
                elkEdge.source = parentNode.id;
                elkEdge.sourcePort = sourceElement.id;
            }
        }

        const targetElement = this.modelState.index.get(edge.targetId);
        if (sourceElement instanceof GPort) {
            const parentNode = findParentByClass(targetElement, GNode);
            if (parentNode) {
                elkEdge.target = parentNode.id;
                elkEdge.targetPort = targetElement.id;
            }
        }

        if (edge.children) {
            elkEdge.labels = (await Promise.all(
                this.findChildren(edge, GLabel).map(child => this.transformToElkAsync(child))
            )) as ElkLabel[];
        }
        const points = edge.routingPoints;
        if (points && points.length >= 2) {
            elkEdge.sourcePoint = points[0];
            elkEdge.bendPoints = points.slice(1, points.length - 1);
            elkEdge.targetPoint = points[points.length - 1];
        }
        this.idToElkElement.set(edge.id, elkEdge);
        return elkEdge;
    }

    protected async transformLabelAsync(label: GLabel): Promise<ElkLabel> {
        const elkLabel: ElkLabel = {
            id: label.id,
            text: label.text,
            layoutOptions: await this.configurator.applyAsync(label)
        };
        this.transformShape(elkLabel, label);
        this.idToElkElement.set(label.id, elkLabel);
        return elkLabel;
    }

    protected async transformPortAsync(port: GPort): Promise<ElkPort> {
        const elkPort: ElkPort = {
            id: port.id,
            layoutOptions: await await this.configurator.applyAsync(port)
        };
        if (port.children) {
            elkPort.labels = this.findChildren(port, GLabel).map(child => this.transformToElk(child)) as ElkLabel[];
            this.elkEdges.push(...(this.findChildren(port, GEdge).map(child => this.transformToElk(child)) as ElkEdge[]));
        }
        this.transformShape(elkPort, port);
        this.idToElkElement.set(port.id, elkPort);
        return elkPort;
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
            // Note: Sami - this fixes type of the GLSP-guys: this.filter.apply(element) -> this.filter.apply(child)
            if (child instanceof constructor && this.filter.apply(child)) {
                result.push(child);
            } else if (child instanceof GCompartment) {
                result.push(...this.findChildren(child, constructor));
            }
        });

        return result;
    }
}
