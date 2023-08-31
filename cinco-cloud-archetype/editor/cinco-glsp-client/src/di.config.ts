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
    createClientContainer,
    overrideViewerOptions
} from '@eclipse-glsp/client';
import { GLSPToolManager } from '@eclipse-glsp/client/lib/base/tool-manager/glsp-tool-manager';
import 'balloon-css/balloon.min.css';
import { Container, ContainerModule } from 'inversify';
import { ApplyConstrainedTypeHintsCommand } from './constraints/ApplyConstrainedTypeHintsCommand';
import { FrontendValidatingTypeHintProvider } from './constraints/FrontendValidatingTypeHintProvider';
import { ChangeContainerTool } from './features/change-container-tool';
import { CustomToolManager } from './features/custom-tool-manager';
import { DirtyStateHandler } from './features/dirty-state-handler';
import { DoubleClickTool } from './features/doubleclick-tool';
import { DynamicToolPalette } from './features/dynamic-palette-tool';
import { ApplyAppearanceUpdateCommand } from './features/frontend-appearance-update-handler';
import { GeneratorResponseActionHandler, GeneratorTool } from './features/generator-tool';
import { MouseContextTracker } from './features/mouse-tool';
import { PropertyViewResponseActionHandler, PropertyViewTool } from './features/property-view-tool';
import { RoutingPointAwareEdgeEditTool } from './features/routingpoint-aware-edge-edit-tool';
import { ServerMessageHandler } from './features/server-message-handler';
import { ValidationModelResponseActionHandler, ValidationTool } from './features/validation-tool';
import { DynamicImportLoader } from './meta/dynamic-import-tool';
import { MetaModelSideLoader } from './meta/meta-model-sideloader';
import { MetaSpecificationResponseHandler } from './meta/meta-specification-response-handler';
import { WorkspaceFileService } from './utils/workspace-file-service';
import { GraphModelProvider } from './model/graph-model-provider';
import { MetaSpecificationTheiaCommand } from './meta/meta-specification-theia-command';

export function createCincoDiagramContainer(widgetId: string): Container {
    const container = createClientContainer(cincoDiagramModule);

    overrideViewerOptions(container, {
        baseDiv: widgetId,
        hiddenDiv: widgetId + '_hidden',
        needsClientLayout: true
    });

    return container;
}

export const cincoDiagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, isBound, rebind };

    unbind(TYPES.ILogger);
    unbind(TYPES.LogLevel);
    bind(TYPES.ILogger).to(ConsoleLogger).inSingletonScope();
    bind(TYPES.LogLevel).toConstantValue(LogLevel.warn);
    bind(DeleteElementContextMenuItemProvider).toSelf();
    bind(TYPES.IContextMenuItemProvider).toService(DeleteElementContextMenuItemProvider);

    // bind TypedServerMessageHandling
    configureActionHandler(context, TypedServerMessageAction.KIND, ServerMessageHandler);

    // add service to provide files as url from a uri
    bind(WorkspaceFileService).toSelf().inSingletonScope();

    // bind MouseContextTracker
    bind(MouseContextTracker).toSelf();

    // bind the doubleClickTool, that will fire the doubleClickActions to the backend
    bind(TYPES.IDefaultTool).to(DoubleClickTool);

    // change container handling
    bind(ChangeContainerTool).toSelf().inSingletonScope();
    bind(TYPES.IDefaultTool).to(ChangeContainerTool);
    bind(ChangeBoundsTool).to(ChangeContainerTool);
    unbind(ChangeBoundsTool);

    // change edge handling
    bind(RoutingPointAwareEdgeEditTool).toSelf().inSingletonScope();
    bind(TYPES.IDefaultTool).to(RoutingPointAwareEdgeEditTool);
    bind(EdgeEditTool).to(RoutingPointAwareEdgeEditTool);
    unbind(EdgeEditTool);

    // bind FrontendValidatingTypeHintProvider
    bind(FrontendValidatingTypeHintProvider).toSelf().inSingletonScope();
    rebind(TypeHintProvider).toService(FrontendValidatingTypeHintProvider);
    rebind(TYPES.ITypeHintProvider).toService(FrontendValidatingTypeHintProvider);
    configureActionHandler(context, SetTypeHintsAction.KIND, FrontendValidatingTypeHintProvider);
    rebind(ApplyTypeHintsCommand).to(ApplyConstrainedTypeHintsCommand);
    configureCommand(context, ApplyConstrainedTypeHintsCommand);

    // bind custom palette
    bind(DynamicToolPalette).toSelf().inSingletonScope();
    rebind(ToolPalette).to(DynamicToolPalette).inSingletonScope();

    // bind FrontendAppearanceProviderHandling
    configureCommand(context, ApplyAppearanceUpdateCommand);

    // bind the propertyViewTool, that will fire the PropertyViewActions to the backend and the handler processing the responses
    bind(TYPES.IDefaultTool).to(PropertyViewTool);
    configureActionHandler(context, PropertyViewResponseAction.KIND, PropertyViewResponseActionHandler);

    // bind the generatorTool, that will fire the GeneratorActions to the backend and the handler processing the responses
    bind(TYPES.IDefaultTool).to(GeneratorTool);
    configureActionHandler(context, GeneratorResponseAction.KIND, GeneratorResponseActionHandler);

    // bind the validation tool, that will fire ValidationRequestActions to the backend
    bind(TYPES.IDefaultTool).to(ValidationTool);
    configureActionHandler(context, ValidationModelResponseAction.KIND, ValidationModelResponseActionHandler);

    // bind dirty state handler
    configureActionHandler(context, SetDirtyStateAction.KIND, DirtyStateHandler);
    configureActionHandler(context, MetaSpecificationResponseAction.KIND, MetaSpecificationResponseHandler);
    configureActionHandler(context, FileProviderResponse.KIND, DynamicImportLoader);

    configureDefaultModelElements(context);

    bind(GraphModelProvider).toSelf().inSingletonScope();
    bind(TYPES.IDefaultTool).to(MetaSpecificationTheiaCommand);

    // swap GLSPToolManager
    rebind(GLSPToolManager)
        .to(CustomToolManager)
        .inSingletonScope()
        .onActivation((ctx: any, injectable: any) => {
            // register modelelements (after the meta-specification is loaded)
            CustomToolManager.registerCallBack(MetaModelSideLoader.createPostRegistrationCallback(context, ctx));
            return injectable;
        });
});
