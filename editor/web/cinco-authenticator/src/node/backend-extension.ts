/* eslint-disable header/header */
import { BackendApplicationContribution } from '@theia/core/lib/node/backend-application';
import { ContainerModule } from 'inversify';
import { CincoAuthenticator } from './cinco-authenticator';

export default new ContainerModule(bind => {
    bind(BackendApplicationContribution).to(CincoAuthenticator).inSingletonScope();
});
