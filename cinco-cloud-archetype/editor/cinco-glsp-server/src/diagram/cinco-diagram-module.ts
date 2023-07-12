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
    ActionHandlerConstructor,
    ComputedBoundsActionHandler,
    ContextMenuItemProvider,
    DiagramConfiguration,
    DiagramModule,
    GModelFactory,
    GModelIndex,
    InstanceMultiBinding,
    ModelState,
    OperationHandlerConstructor,
    SourceModelStorage,
    ToolPaletteItemProvider
} from '@eclipse-glsp/server-node';
import { BindingTarget } from '@eclipse-glsp/server-node/lib/di/binding-target';
import { injectable } from 'inversify';
import { CustomContextMenuItemProvider } from '../context-menu/custom-context-menu-item-provider';
import { GeneratorCreateFileHandler, GeneratorEditHandler } from '../generator/generator-tool';
import { ApplyLabelEditHandler } from '../handler/apply-label-edit-handler';
import { ChangeBoundsHandler } from '../handler/change-bounds-handler';
import { ChangeContainerHandler } from '../handler/change-container-handler';
import { DeleteHandler } from '../handler/delete-handler';
import { MetaSpecificationReloadHandler } from '../handler/meta-specification-reload-handler';
import { RoutingPointHandler } from '../handler/routingpoint-handler';
import { ShortestPathAction } from '../handler/shortest-path-action';
import { SpecifiedEdgeHandler } from '../handler/specified_edge_handler';
import { SpecifiedNodeHandler } from '../handler/specified_node_handler';
import { GraphGModelFactory } from '../model/graph-gmodel-factory';
import { GraphModelIndex } from '../model/graph-model-index';
import { GraphModelState } from '../model/graph-model-state';
import { GraphModelStorage } from '../model/graph-storage';
import { CustomToolPaletteItemProvider } from '../palette/custom-tool-palette-item-provider';
import { PropertyEditHandler, PropertyViewHandler } from '../property-view/property-view-tool';
import { ApplyAppearanceUpdateAction } from '../shared/protocol/appearance-provider-protocol';
import { GeneratorResponseAction } from '../shared/protocol/generator-protocol';
import { PropertyViewResponseAction } from '../shared/protocol/property-protocol';
import { ValidationModelAnswerAction } from '../shared/protocol/validation-protocol';
import { AppearanceProviderManager } from '../tools/appearance-provider-manager';
import { DoubleClickManager } from '../tools/double-click-manager';
import { GeneratorManager } from '../tools/generator-manager';
import { ServerResponseHandler } from '../tools/server-dialog-response-handler';
import { ValidationManager } from '../tools/validation-manager';
import { CincoDiagramConfiguration } from './cinco-diagram-configuration';

@injectable()
export class CincoDiagramModule extends DiagramModule {
    readonly diagramType = 'cinco-diagram';

    protected bindDiagramConfiguration(): BindingTarget<DiagramConfiguration> {
        return CincoDiagramConfiguration;
    }

    protected bindSourceModelStorage(): BindingTarget<SourceModelStorage> {
        return GraphModelStorage;
    }

    protected bindModelState(): BindingTarget<ModelState> {
        return { service: GraphModelState };
    }

    protected bindGModelFactory(): BindingTarget<GModelFactory> {
        return GraphGModelFactory;
    }

    protected override configureActionHandlers(binding: InstanceMultiBinding<ActionHandlerConstructor>): void {
        super.configureActionHandlers(binding);
        binding.add(ServerResponseHandler); // Response Handle for e.g. Dialogs
        binding.add(ComputedBoundsActionHandler);
        binding.add(ShortestPathAction); // CustomAction
        binding.add(DoubleClickManager); // DoubleClick ActionHandler
        binding.add(AppearanceProviderManager); // DoubleClick ActionHandler
        binding.add(PropertyViewHandler); // Property View ActionHandler
        binding.add(ValidationManager); // Validation ActionHandler
        binding.add(GeneratorManager);
        binding.add(MetaSpecificationReloadHandler);
    }

    protected override configureClientActions(binding: InstanceMultiBinding<string>): void {
        super.configureClientActions(binding);
        binding.add(PropertyViewResponseAction.KIND);
        binding.add(GeneratorResponseAction.KIND);
        binding.add(ApplyAppearanceUpdateAction.KIND);
        binding.add(ValidationModelAnswerAction.KIND);
        binding.add('enableToolPalette');
    }

    protected override configureOperationHandlers(binding: InstanceMultiBinding<OperationHandlerConstructor>): void {
        super.configureOperationHandlers(binding);
        binding.add(SpecifiedNodeHandler);
        binding.add(SpecifiedEdgeHandler);
        binding.add(ChangeBoundsHandler);
        binding.add(ApplyLabelEditHandler);
        binding.add(DeleteHandler);
        binding.add(ChangeContainerHandler);
        binding.add(PropertyEditHandler);
        binding.add(RoutingPointHandler);
        binding.add(GeneratorEditHandler);
        binding.add(GeneratorCreateFileHandler);
    }

    protected override bindGModelIndex(): BindingTarget<GModelIndex> {
        this.context.bind(GraphModelIndex).toSelf().inSingletonScope();
        return { service: GraphModelIndex };
    }

    protected override bindToolPaletteItemProvider(): BindingTarget<ToolPaletteItemProvider> | undefined {
        return CustomToolPaletteItemProvider;
    }

    protected override bindContextMenuItemProvider(): BindingTarget<ContextMenuItemProvider> | undefined {
        return CustomContextMenuItemProvider;
    }
}
