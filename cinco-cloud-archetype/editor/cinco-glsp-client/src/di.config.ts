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
    FileProviderResponse,
    GeneratorResponseAction,
    MetaSpecificationResponseAction,
    PropertyViewResponseAction,
    ServerArgsResponse,
    TypedServerMessageAction,
    ValidationModelResponseAction
} from '@cinco-glsp/cinco-glsp-common';
import {
    ApplyTypeHintsCommand,
    ChangeBoundsTool,
    ConsoleLogger,
    DeleteElementContextMenuItemProvider,
    EdgeEditTool,
    LogLevel,
    SetDirtyStateAction,
    SetTypeHintsAction,
    TYPES,
    ToolPalette,
    TypeHintProvider,
    configureActionHandler,
    configureCommand,
    configureDefaultModelElements,
    initializeDiagramContainer,
    ContainerConfiguration,
    bindOrRebind
} from '@eclipse-glsp/client';
import 'balloon-css/balloon.min.css';
import { Container, ContainerModule } from 'inversify';
import { ApplyConstrainedTypeHintsCommand } from './features/constraints/ApplyConstrainedTypeHintsCommand';
import { FrontendValidatingTypeHintProvider } from './features/constraints/FrontendValidatingTypeHintProvider';
import { ChangeContainerTool } from './features/tool/change-container-tool';
import { CincoToolManager } from './glsp/cinco-tool-manager';
import { DirtyStateHandler } from './features/action-handler/dirty-state-handler';
import { DoubleClickTool } from './features/tool/doubleclick-tool';
import { CincoToolPalette } from './glsp/cinco-tool-palette';
import { ApplyAppearanceUpdateCommand } from './features/gui/frontend-appearance-update-handler';
import { GeneratorResponseActionHandler, GeneratorTool } from './features/generator/generator-tool';
import { PropertyViewResponseActionHandler, PropertyViewTool } from './features/properties/property-view-tool';
import { RoutingPointAwareEdgeEditTool } from './features/tool/routingpoint-aware-edge-edit-tool';
import { ServerMessageHandler } from './features/action-handler/server-message-handler';
import { ValidationModelResponseActionHandler, ValidationTool } from './features/validation/validation-tool';
import { MetaSpecificationResponseHandler } from './meta/meta-specification-response-handler';
import { WorkspaceFileService } from './utils/workspace-file-service';
import { GraphModelProvider } from './model/graph-model-provider';
import { MetaSpecificationTheiaCommand } from './meta/meta-specification-theia-command';
import { ServerArgsProvider } from './meta/server-args-response-handler';
import { FileProviderHandler } from './features/action-handler/file-provider-handler';
import { CinoPreparationsStartUp } from './glsp/cinco-preparations-startup';
import { CincoGLSPCommandStack } from './glsp/cinco-command-stack';
import { RestoreViewportHandler } from '@eclipse-glsp/client/lib/features/viewport/viewport-handler';
import { CincoRestoreViewportHandler } from './glsp/cinco-viewport-handler';

export function initializeCincoDiagramContainer(container: Container, ...containerConfiguration: ContainerConfiguration): Container {
    return initializeDiagramContainer(container, cincoDiagramModule, ...containerConfiguration);
}

export const cincoDiagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, isBound, rebind };

    unbind(TYPES.ILogger);
    unbind(TYPES.LogLevel);
    bind(TYPES.ILogger).to(ConsoleLogger).inSingletonScope();
    bind(TYPES.LogLevel).toConstantValue(LogLevel.warn);

    // custom
    bind(WorkspaceFileService).toSelf().inSingletonScope();
    bind(GraphModelProvider).toSelf().inSingletonScope();

    // needs to be bound first because of DiagramLoader (Startup)
    bindOrRebind(context, TYPES.ICommandStack).to(CincoGLSPCommandStack).inSingletonScope();
    bind(CinoPreparationsStartUp)
        .toSelf()
        .inSingletonScope()
        .onActivation((ctx: any, injectable: CinoPreparationsStartUp) => {
            injectable.setContext(context, ctx);
            return injectable;
        });
    bind(TYPES.IDiagramStartup).toService(CinoPreparationsStartUp);

    // rebind CincoRestoreViewportHandler for RestoreViewportHandler
    unbind(RestoreViewportHandler);
    bind(RestoreViewportHandler).to(CincoRestoreViewportHandler);

    bind(DeleteElementContextMenuItemProvider).toSelf();
    bind(TYPES.IContextMenuItemProvider).toService(DeleteElementContextMenuItemProvider);

    // add doubleclick tool
    bind(TYPES.IDefaultTool).to(DoubleClickTool);

    // change container handling
    unbind(ChangeBoundsTool);
    bind(TYPES.IDefaultTool).to(ChangeContainerTool);
    bind(ChangeBoundsTool).to(ChangeContainerTool);
    bind(ChangeContainerTool).toSelf().inSingletonScope();

    // change edge handling
    unbind(EdgeEditTool);
    bind(TYPES.IDefaultTool).to(RoutingPointAwareEdgeEditTool);
    bind(EdgeEditTool).to(RoutingPointAwareEdgeEditTool);
    bind(RoutingPointAwareEdgeEditTool).toSelf().inSingletonScope();

    // bind FrontendValidatingTypeHintProvider
    bind(FrontendValidatingTypeHintProvider).toSelf().inSingletonScope();
    rebind(TypeHintProvider).toService(FrontendValidatingTypeHintProvider);
    rebind(TYPES.ITypeHintProvider).toService(FrontendValidatingTypeHintProvider);
    configureActionHandler(context, SetTypeHintsAction.KIND, FrontendValidatingTypeHintProvider);
    rebind(ApplyTypeHintsCommand).to(ApplyConstrainedTypeHintsCommand);
    configureCommand(context, ApplyConstrainedTypeHintsCommand);

    // bind custom palette
    bind(CincoToolPalette).toSelf().inSingletonScope();
    rebind(ToolPalette).to(CincoToolPalette).inSingletonScope();

    // bind FrontendAppearanceProviderHandling
    configureCommand(context, ApplyAppearanceUpdateCommand);

    // bind the propertyViewTool, that will fire the PropertyViewActions to the backend and the handler processing the responses
    bind(TYPES.IDefaultTool).to(PropertyViewTool);
    configureActionHandler(context, PropertyViewResponseAction.KIND, PropertyViewResponseActionHandler);

    // bind the generatorTool, that will fire the GeneratorActions to the backend and the handler processing the responses
    bind(TYPES.IDefaultTool).to(GeneratorTool).inSingletonScope();
    configureActionHandler(context, GeneratorResponseAction.KIND, GeneratorResponseActionHandler);

    // bind the validation tool, that will fire ValidationRequestActions to the backend
    bind(TYPES.IDefaultTool).to(ValidationTool);
    configureActionHandler(context, ValidationModelResponseAction.KIND, ValidationModelResponseActionHandler);

    // bind tool, that registers a theia-command to fetch the meta-specification
    bind(TYPES.IDefaultTool).to(MetaSpecificationTheiaCommand);

    // GLSPToolManager
    rebind(TYPES.IToolManager).to(CincoToolManager).inSingletonScope();
    bind(CincoToolManager).toSelf().inSingletonScope();

    // actions
    configureActionHandler(context, TypedServerMessageAction.KIND, ServerMessageHandler);
    configureActionHandler(context, SetDirtyStateAction.KIND, DirtyStateHandler);
    configureActionHandler(context, MetaSpecificationResponseAction.KIND, MetaSpecificationResponseHandler);
    configureActionHandler(context, FileProviderResponse.KIND, FileProviderHandler);
    configureActionHandler(context, ServerArgsResponse.KIND, ServerArgsProvider);

    configureDefaultModelElements(context);
});
