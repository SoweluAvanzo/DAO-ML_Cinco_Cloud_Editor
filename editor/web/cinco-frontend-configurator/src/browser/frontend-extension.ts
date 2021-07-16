/* eslint-disable header/header */

import { ContainerModule, interfaces } from 'inversify';
import { MenuContribution } from '@theia/core';
import { MenuCommandRemovalContribution } from './menu-command-removal-contribution';
import { FrontendApplicationContribution } from '@theia/core/lib/browser';

export default new ContainerModule((bind: interfaces.Bind) => {
    bind(MenuContribution).to(MenuCommandRemovalContribution).inSingletonScope();
    bind(FrontendApplicationContribution).to(MenuCommandRemovalContribution).inSingletonScope();
});
