/* eslint-disable header/header */
import { MenuContribution } from '@theia/core';
import { FrontendApplicationContribution } from '@theia/core/lib/browser';
import { ContainerModule, interfaces } from 'inversify';

import { MenuCommandRemovalContribution } from './menu-command-removal-contribution';

export default new ContainerModule((bind: interfaces.Bind) => {
    bind(MenuContribution).to(MenuCommandRemovalContribution).inSingletonScope();
    bind(FrontendApplicationContribution).to(MenuCommandRemovalContribution).inSingletonScope();
});
