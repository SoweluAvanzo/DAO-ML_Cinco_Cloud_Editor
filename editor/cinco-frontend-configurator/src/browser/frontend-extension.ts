/* eslint-disable header/header */
import { CommandContribution, MenuContribution } from '@theia/core';
import { FrontendApplicationContribution } from '@theia/core/lib/browser';
import { ContainerModule, interfaces } from 'inversify';

import { CommandRegistrationContribution } from './commandRegistration';
import { MenuCommandRemovalContribution } from './menu-command-removal-contribution';
import { MenuProjectInitializationContribution } from './menu-project-initialization-contribution';

export default new ContainerModule((bind: interfaces.Bind) => {
    bind(MenuContribution).to(MenuCommandRemovalContribution).inSingletonScope();
    bind(MenuContribution).to(MenuProjectInitializationContribution).inSingletonScope();
    bind(CommandContribution).to(CommandRegistrationContribution).inSingletonScope();
    bind(FrontendApplicationContribution).to(MenuCommandRemovalContribution).inSingletonScope();
});
