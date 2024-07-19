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

import '../../css/cinco.css';
import {
    ContainerContext,
    DiagramConfiguration,
    GLSPClientContribution,
    GLSPDiagramContextKeyService,
    GLSPDiagramManager,
    GLSPDiagramWidget,
    GLSPTheiaFrontendModule,
    registerDiagramManager
} from '@eclipse-glsp/theia-integration';
import { GLSPDiagramLanguage } from '@eclipse-glsp/theia-integration/lib/common';
import { CommandContribution, MenuContribution } from '@theia/core';
import {
    bindViewContribution,
    FrontendApplicationContribution,
    KeybindingContribution,
    WebSocketConnectionProvider,
    WidgetFactory
} from '@theia/core/lib/browser';

import { getDiagramConfiguration } from '../common/cinco-language';
import { FILESYSTEM_UTIL_ENDPOINT, FilesystemUtilClient, FilesystemUtilServer } from '../common/file-system-util-protocol';
import { CincoDiagramConfiguration } from './diagram/cinco-diagram-configuration';
import { CincoGLSPDiagramContextKeyService, CincoGLSPDiagramMananger } from './diagram/cinco-glsp-diagram-manager';
import { FileSystemUtilService } from './file-system-util-contribution';
import { GenerateGraphDiagramKeybindingContribution, GenerateGraphDiagramMenuContribution } from './generator/generator-menu-contributions';
import { GitConfigurationContribution } from './git/git-configuration-contribution';
import { ChannelAPIContribution } from './output-messages/channel-api-contribution';
import { PropertyDataHandler } from './property-widget/property-data-handler';
import { PropertyUpdateCommandContribution } from './property-widget/property-update-command';
import { CincoCloudPropertyWidget } from './property-widget/property-widget';
import { CincoCloudPropertyWidgetContribution } from './property-widget/property-widget-contribution';
import { GLSP2TheiaCommandRegistrationContribution } from './theia-registration/command-registration-interface';
import { FileProviderContribution } from './theia-registration/file-provider';
import { ValidationModelDataHandler } from './validation-widget/validation-model-data-handler';
import { ValidationModelUpdateCommandContribution } from './validation-widget/validation-model-update-command';
import { CincoCloudModelValidationWidget } from './validation-widget/validation-model-widget';
import { CincoCloudProjectValidationWidget } from './validation-widget/validation-project-widget';
import {
    CincoCloudModelValidationWidgetContribution,
    CincoCloudProjectValidationWidgetContribution,
    ValidationModelMenuContribution
} from './validation-widget/validation-widget-contribution';
import { ValidationModelWrapperCommandContribution } from './validation-widget/validation-wrapper-commands';
import { CincoGLSPClientContribution } from './cinco-glsp-client-contribution';
import { GLSPServerUtilsProvider } from './glsp-server-utils-provider';
import { GLSP_SERVER_UTIL_ENDPOINT, GLSPServerUtilClient, GLSPServerUtilServer } from '../common/glsp-server-util-protocol';
import { CincoEditorButtonConfigurator } from './menu/cinco-editor-button-configurator';
import { CincoContextMenuButtonConfigurator } from './menu/cinco-context-menu-button-configurator';
import { CincoGLSPDiagramWidget } from './diagram/cinco-glsp-diagram-widget';
import {
    createDiagramWidgetFactory,
    DiagramWidgetFactory
} from '@eclipse-glsp/theia-integration/lib/browser/diagram/diagram-widget-factory';
import { LanguageUpdater } from './meta/language-updater';
import { CINCO_LOGGING_ENDPOINT, CincoLoggingClient, CincoLoggingServer } from '../common/cinco-logging-protocol';
import { CincoLoggingClientNode, CincoLoggingContribution } from './cinco-logging-contribution';

export class CincoTheiaFrontendModule extends GLSPTheiaFrontendModule {
    protected override get diagramLanguage(): GLSPDiagramLanguage {
        return getDiagramConfiguration();
    }

    override configure(context: ContainerContext): void {
        // Property Widget
        context.bind(CincoCloudPropertyWidget).toSelf();
        context
            .bind(WidgetFactory)
            .toDynamicValue(ctx => ({
                id: CincoCloudPropertyWidget.ID,
                createWidget: () => ctx.container.get<CincoCloudPropertyWidget>(CincoCloudPropertyWidget)
            }))
            .inSingletonScope();
        bindViewContribution(context.bind, CincoCloudPropertyWidgetContribution);
        context.bind(FrontendApplicationContribution).toService(CincoCloudPropertyWidgetContribution);
        context.bind(PropertyDataHandler).toSelf().inSingletonScope();
        context.bind(CommandContribution).to(PropertyUpdateCommandContribution);
        context.bind(CommandContribution).to(GLSP2TheiaCommandRegistrationContribution);
        context.bind(CommandContribution).to(FileProviderContribution);
        context.bind(KeybindingContribution).to(GenerateGraphDiagramKeybindingContribution);
        context.bind(MenuContribution).to(GenerateGraphDiagramMenuContribution);

        // provision of fileSystemUtils from backend to frontend
        context
            .bind(FilesystemUtilServer)
            .toDynamicValue(ctx => {
                const client: FilesystemUtilClient = {};
                const connection = ctx.container.get(WebSocketConnectionProvider);
                return connection.createProxy<FilesystemUtilServer>(FILESYSTEM_UTIL_ENDPOINT, client);
            })
            .inSingletonScope();
        context.bind(FrontendApplicationContribution).to(FileSystemUtilService);

        // provision of logging from backend to frontend
        context
            .bind(CincoLoggingServer)
            .toDynamicValue(ctx => {
                const client: CincoLoggingClient = new CincoLoggingClientNode();
                const connection = ctx.container.get(WebSocketConnectionProvider);
                return connection.createProxy<CincoLoggingServer>(CINCO_LOGGING_ENDPOINT, client);
            })
            .inSingletonScope();
        context.bind(FrontendApplicationContribution).to(CincoLoggingContribution);

        // Validation Widgets
        context.bind(CincoCloudModelValidationWidget).toSelf();
        context
            .bind(WidgetFactory)
            .toDynamicValue(ctx => ({
                id: CincoCloudModelValidationWidget.ID,
                createWidget: () => ctx.container.get<CincoCloudModelValidationWidget>(CincoCloudModelValidationWidget)
            }))
            .inSingletonScope();
        bindViewContribution(context.bind, CincoCloudModelValidationWidgetContribution);
        context.bind(FrontendApplicationContribution).toService(CincoCloudModelValidationWidgetContribution);
        context.bind(CincoCloudProjectValidationWidget).toSelf();
        context
            .bind(WidgetFactory)
            .toDynamicValue(ctx => ({
                id: CincoCloudProjectValidationWidget.ID,
                createWidget: () => ctx.container.get<CincoCloudProjectValidationWidget>(CincoCloudProjectValidationWidget)
            }))
            .inSingletonScope();
        bindViewContribution(context.bind, CincoCloudProjectValidationWidgetContribution);
        context.bind(FrontendApplicationContribution).toService(CincoCloudProjectValidationWidgetContribution);
        context.bind(MenuContribution).to(ValidationModelMenuContribution);
        context.bind(CommandContribution).to(ValidationModelWrapperCommandContribution);
        context.bind(ValidationModelDataHandler).toSelf().inSingletonScope();
        context.bind(CommandContribution).to(ValidationModelUpdateCommandContribution);
        context.bind(FrontendApplicationContribution).to(CincoEditorButtonConfigurator);
        context.bind(FrontendApplicationContribution).to(CincoContextMenuButtonConfigurator);

        // bind new DiagramMananger (e.g. for file/diagramExtensions)
        context.bind(CincoGLSPDiagramMananger).toSelf().inSingletonScope();
        context.bind(GLSPDiagramManager).to(CincoGLSPDiagramMananger);
        context.unbind(GLSPDiagramContextKeyService);
        context.bind(GLSPDiagramContextKeyService).to(CincoGLSPDiagramContextKeyService);
        context.bind(CincoGLSPDiagramContextKeyService).toSelf().inSingletonScope();
        context.bind(GLSPDiagramWidget).to(CincoGLSPDiagramWidget);
        context.bind(CincoGLSPDiagramWidget).toSelf();

        // bind update mechanism for meta-specification changes
        context.bind(CommandContribution).to(LanguageUpdater);

        // server args from backend to frontend
        context.bind(GLSPServerUtilsProvider).to(GLSPServerUtilsProvider);
        context.bind(CommandContribution).to(GLSPServerUtilsProvider);
        context
            .bind(GLSPServerUtilServer)
            .toDynamicValue(ctx => {
                const client: GLSPServerUtilClient = {};
                const connection = ctx.container.get(WebSocketConnectionProvider);
                return connection.createProxy<GLSPServerUtilServer>(GLSP_SERVER_UTIL_ENDPOINT, client);
            })
            .inSingletonScope();
        context.bind(FrontendApplicationContribution).to(GLSPServerUtilsProvider);

        // bind git configuration
        context.bind(FrontendApplicationContribution).to(GitConfigurationContribution);
    }

    override configureDiagramManager(context: ContainerContext): void {
        const diagramManagerServiceId = Symbol(`DiagramManager_${this.diagramLanguage.diagramType}`);
        context
            .bind(diagramManagerServiceId)
            .toDynamicValue(dynamicContext => {
                const manager = dynamicContext.container.resolve(CincoGLSPDiagramMananger);
                manager.doConfigure(this.diagramLanguage);
                return manager;
            })
            .inSingletonScope();
        registerDiagramManager(context.bind, diagramManagerServiceId, false);
    }

    override bindDiagramWidgetFactory(context: ContainerContext): void {
        context
            .bind(DiagramWidgetFactory)
            .toDynamicValue(ctx => createDiagramWidgetFactory(ctx, this.diagramLanguage.diagramType))
            .inSingletonScope();
    }

    bindDiagramConfiguration(context: ContainerContext): void {
        context.bind(DiagramConfiguration).to(CincoDiagramConfiguration);
        context.bind(FrontendApplicationContribution).to(ChannelAPIContribution);
    }

    override bindGLSPClientContribution(context: ContainerContext): void {
        context.bind(GLSPClientContribution).to(CincoGLSPClientContribution);
    }
}

export default new CincoTheiaFrontendModule();
