/* eslint-disable header/header */
import { FrontendApplicationContribution } from '@theia/core/lib/browser';
import { ContainerModule, interfaces } from 'inversify';

import { ProjectIdProvider } from './projectid-provider';

export default new ContainerModule((bind: interfaces.Bind) => {
    bind(FrontendApplicationContribution).to(ProjectIdProvider).inSingletonScope();
});
