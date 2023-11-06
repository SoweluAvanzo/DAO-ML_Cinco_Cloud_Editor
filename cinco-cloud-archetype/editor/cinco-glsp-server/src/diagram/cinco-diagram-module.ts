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
import { GraphGModelFactory, GraphModelIndex, GraphModelState, GraphModelStorage, ServerResponseHandler } from '@cinco-glsp/cinco-glsp-api';
import {
    ApplyAppearanceUpdateAction,
    CustomAction,
    DIAGRAM_TYPE,
    FileProviderResponse,
    GeneratorResponseAction,
    MetaSpecificationResponseAction,
    PropertyViewResponseAction,
    ServerArgsResponse,
    ValidationModelAnswerAction
} from '@cinco-glsp/cinco-glsp-common';
import {
    ActionHandlerConstructor,
    BindingTarget,
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
import { injectable } from 'inversify';
import { CustomContextMenuItemProvider } from '../context-menu/custom-context-menu-item-provider';
import { GeneratorCreateFileHandler, GeneratorEditHandler } from '../generator/generator-tool';
import { ApplyLabelEditHandler } from '../handler/apply-label-edit-handler';
import { ChangeBoundsHandler } from '../handler/change-bounds-handler';
import { ChangeContainerHandler } from '../handler/change-container-handler';
import { DeleteHandler } from '../handler/delete-handler';
import { FileProviderHandler } from '../handler/file-provider-handler';
import { MetaSpecificationReloadHandler } from '../handler/meta-specification-reload-handler';
import { MetaSpecificationRequestHandler } from '../handler/meta-specification-request-handler';
import { RoutingPointHandler } from '../handler/routingpoint-handler';
import { SpecifiedEdgeHandler } from '../handler/specified_edge_handler';
import { SpecifiedNodeHandler } from '../handler/specified_node_handler';
import { CustomToolPaletteItemProvider } from '../palette/custom-tool-palette-item-provider';
import { PropertyEditHandler, PropertyViewHandler } from '../property-view/property-view-tool';
import { AppearanceProviderManager } from '../tools/appearance-provider-manager';
import { CustomActionManager } from '../tools/custom-action-manager';
import { DoubleClickManager } from '../tools/double-click-manager';
import { GeneratorManager } from '../tools/generator-manager';
import { ValidationManager } from '../tools/validation-manager';
import { CincoDiagramConfiguration } from './cinco-diagram-configuration';
import { ReconnectEdgeHandler } from '../handler/reconnect-edge-handler';
import { ServerArgsRequestHandler } from '../handler/server-args-handler';

@injectable()
export class CincoDiagramModule extends DiagramModule {
    readonly diagramType = DIAGRAM_TYPE;

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
        binding.add(CustomActionManager); // CustomAction
        binding.add(DoubleClickManager); // DoubleClick ActionHandler
        binding.add(AppearanceProviderManager); // DoubleClick ActionHandler
        binding.add(PropertyViewHandler); // Property View ActionHandler
        binding.add(ValidationManager); // Validation ActionHandler
        binding.add(GeneratorManager);
        binding.add(MetaSpecificationReloadHandler);
        binding.add(MetaSpecificationRequestHandler);
        binding.add(ServerArgsRequestHandler);
        binding.add(FileProviderHandler);
    }

    protected override configureClientActions(binding: InstanceMultiBinding<string>): void {
        super.configureClientActions(binding);
        binding.add(PropertyViewResponseAction.KIND);
        binding.add(GeneratorResponseAction.KIND);
        binding.add(ApplyAppearanceUpdateAction.KIND);
        binding.add(ValidationModelAnswerAction.KIND);
        binding.add(CustomAction.KIND);
        binding.add('enableToolPalette');
        binding.add(MetaSpecificationResponseAction.KIND);
        binding.add(FileProviderResponse.KIND);
        binding.add(ServerArgsResponse.KIND);
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
        binding.add(ReconnectEdgeHandler);
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
