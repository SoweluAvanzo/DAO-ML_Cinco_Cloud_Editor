/* eslint-disable header/header */
import { ContainerModule, interfaces } from 'inversify';
import { CommandContribution, MenuContribution } from '@theia/core';
import { FrontendApplicationContribution, WidgetFactory } from '@theia/core/lib/browser';
import { FILE_NAVIGATOR_ID } from '@theia/navigator/lib/browser/navigator-widget';

import { CommandRegistrationContribution } from './commandRegistration';
import { MenuCommandRemovalContribution } from './menu-command-removal-contribution';
import { FrontendEventController } from './frontend-event-controller/frontend-event-controller';
import { MenuProjectInitializationContribution } from './menu-project-initialization-contribution';
import { createFileNavigatorWidget, CustomFileNavigatorWidget } from './webview-dnd/custom-file-navigator-widget';

export default new ContainerModule((bind: interfaces.Bind) => {
    bind(MenuContribution).to(MenuCommandRemovalContribution).inSingletonScope();
    bind(MenuContribution).to(MenuProjectInitializationContribution).inSingletonScope();
    bind(CommandContribution).to(CommandRegistrationContribution).inSingletonScope();
    bind(FrontendApplicationContribution).to(MenuCommandRemovalContribution).inSingletonScope();

    // Mask FileNavigatorWidget to enable dragAndDrop management
    bind(WidgetFactory).toDynamicValue(({ container }) => ({
        id: FILE_NAVIGATOR_ID,
        createWidget: () => container.get(CustomFileNavigatorWidget)
    })).inSingletonScope();
    bind(CustomFileNavigatorWidget).toDynamicValue(ctx =>
        createFileNavigatorWidget(ctx.container)
    ).inSingletonScope();
    // Initialize Custom Frontend-EventController to enable webview-theia-communication
    FrontendEventController.init();
});
