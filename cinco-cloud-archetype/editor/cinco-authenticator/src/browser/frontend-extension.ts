/* eslint-disable header/header */
import { FrontendApplicationContribution } from '@theia/core/lib/browser';
import { ContainerModule, interfaces } from 'inversify';

import { CommandProvider } from './command-provider';

export default new ContainerModule((bind: interfaces.Bind) => {
    bind(FrontendApplicationContribution).to(CommandProvider).inSingletonScope();
});
