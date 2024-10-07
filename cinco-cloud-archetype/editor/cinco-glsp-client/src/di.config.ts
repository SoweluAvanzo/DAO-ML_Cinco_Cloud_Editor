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
    MetaSpecificationResponseAction,
    PropertyViewResponseAction,
    ServerArgsResponse,
    TypedServerMessageAction
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
    bindOrRebind,
    SetContextActions,
    configureModelElement,
    EnableDefaultToolsOnFocusLossHandler,
    FocusStateChangedAction
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
import { PropertyViewResponseActionHandler, PropertyViewTool } from './features/properties/property-view-tool';
import { RoutingPointAwareEdgeEditTool } from './features/tool/routingpoint-aware-edge-edit-tool';
import { ServerMessageHandler } from './features/action-handler/server-message-handler';
import { MetaSpecificationResponseHandler } from './meta/meta-specification-response-handler';
import { WorkspaceFileService } from './utils/workspace-file-service';
import { GraphModelProvider } from './model/graph-model-provider';
import { ServerArgsProvider } from './meta/server-args-response-handler';
import { FileProviderHandler } from './features/action-handler/file-provider-handler';
import { CinoPreparationsStartUp } from './glsp/cinco-preparations-startup';
import { RestoreViewportHandler } from '@eclipse-glsp/client/lib/features/viewport/viewport-handler';
import { CincoRestoreViewportHandler } from './glsp/cinco-viewport-handler';
import { KeyboardToolPalette } from '@eclipse-glsp/client/lib/features/accessibility/keyboard-tool-palette/keyboard-tool-palette';
import { EnvironmentProvider } from './api/environment-provider';
import { CincoToolPaletteUpdateHandler } from './glsp/cinco-tool-palette-update-handler';
import { MarkerEdgeSourceTargetConflictView } from './views/marker-edge-source-target-conflict-view';
import {
    CincoButtonDelete,
    CincoButtonRestore,
    CincoEdgeButtonSourceChoice,
    CincoEdgeButtonTargetChoice,
    CincoMarker
} from './model/model';
import { ButtonSelectChoiceView } from './views/button-select-choice';
import { ChoiceSelectionTool } from './features/tool/choice-selection-tool';
import { CincoEnableDefaultToolsOnFocusLossHandler } from './glsp/cinco-focus-fix-handler';
import { MarkerGhostView } from './views/marker-ghost';
import { ButtonDeleteView } from './views/button-delete';
import { ButtonRestoreView } from './views/button-restore';
import { GhostDecisionTool } from './features/tool/ghost-decision-tool';

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

    // graphModelProvider
    bind(TYPES.ISModelRootListener).to(GraphModelProvider);
    bind(GraphModelProvider).toSelf().inSingletonScope();

    // needs to be bound first because of DiagramLoader (Startup)
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
    bind(TYPES.IDefaultTool).to(RoutingPointAwareEdgeEditTool).inSingletonScope();
    bind(EdgeEditTool).to(RoutingPointAwareEdgeEditTool).inSingletonScope();
    bind(RoutingPointAwareEdgeEditTool).toSelf().inSingletonScope();

    // bind FrontendValidatingTypeHintProvider
    bind(FrontendValidatingTypeHintProvider).toSelf().inSingletonScope();
    rebind(TypeHintProvider).toService(FrontendValidatingTypeHintProvider);
    rebind(TYPES.ITypeHintProvider).toService(FrontendValidatingTypeHintProvider);
    configureActionHandler(context, SetTypeHintsAction.KIND, FrontendValidatingTypeHintProvider);
    rebind(ApplyTypeHintsCommand).to(ApplyConstrainedTypeHintsCommand);
    configureCommand(context, ApplyConstrainedTypeHintsCommand);

    // fix focus
    unbind(EnableDefaultToolsOnFocusLossHandler);
    bind(EnableDefaultToolsOnFocusLossHandler).to(CincoEnableDefaultToolsOnFocusLossHandler);
    configureActionHandler({ bind, isBound }, FocusStateChangedAction.KIND, EnableDefaultToolsOnFocusLossHandler);

    // bind custom palette
    if (context.isBound(ToolPalette)) {
        unbind(ToolPalette);
    }
    if (context.isBound(KeyboardToolPalette)) {
        unbind(KeyboardToolPalette);
    }
    bindOrRebind(context, ToolPalette).to(CincoToolPalette).inSingletonScope();
    configureActionHandler(context, SetContextActions.KIND, CincoToolPaletteUpdateHandler);

    // bind FrontendAppearanceProviderHandling
    configureCommand(context, ApplyAppearanceUpdateCommand);

    // bind the propertyViewTool, that will fire the PropertyViewActions to the backend and the handler processing the responses
    bind(TYPES.IDefaultTool).to(PropertyViewTool).inSingletonScope();
    configureActionHandler(context, PropertyViewResponseAction.KIND, PropertyViewResponseActionHandler);

    // bind the lazy merging tools
    bind(TYPES.IDefaultTool).to(ChoiceSelectionTool).inSingletonScope();
    bind(TYPES.IDefaultTool).to(GhostDecisionTool).inSingletonScope();

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
    configureModelElement(context, 'marker:edge-source-target-conflict', CincoMarker, MarkerEdgeSourceTargetConflictView);
    configureModelElement(context, 'marker:ghost', CincoMarker, MarkerGhostView);
    configureModelElement(context, 'button:edge-source-choice', CincoEdgeButtonSourceChoice, ButtonSelectChoiceView);
    configureModelElement(context, 'button:edge-target-choice', CincoEdgeButtonTargetChoice, ButtonSelectChoiceView);
    configureModelElement(context, 'button:delete', CincoButtonDelete, ButtonDeleteView);
    configureModelElement(context, 'button:restore', CincoButtonRestore, ButtonRestoreView);

    bind(TYPES.IDiagramStartup).toService(EnvironmentProvider);
});
