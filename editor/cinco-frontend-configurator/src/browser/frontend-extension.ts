/* eslint-disable header/header */
import { CommandContribution, MenuContribution } from '@theia/core';
import { FrontendApplicationContribution } from '@theia/core/lib/browser';
import { ContainerModule, interfaces } from 'inversify';

import { MenuCommandRemovalContribution } from './menu-command-removal-contribution';
import { ExampleCommandContribution, MenuExampleCreationContribution } from './menu-example-contribution';

export default new ContainerModule((bind: interfaces.Bind) => {
    bind(MenuContribution).to(MenuCommandRemovalContribution).inSingletonScope();
    bind(MenuContribution).to(MenuExampleCreationContribution).inSingletonScope();
    bind(CommandContribution).to(ExampleCommandContribution).inSingletonScope();
    bind(FrontendApplicationContribution).to(MenuCommandRemovalContribution).inSingletonScope();
});
