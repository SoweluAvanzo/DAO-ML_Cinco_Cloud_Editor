/* eslint-disable header/header */
import { BackendApplicationContribution } from '@theia/core/lib/node/backend-application';
import { ContainerModule } from 'inversify';
import { CincoCloudGrpc } from './cinco-cloud-grpc';

export default new ContainerModule(bind => {
    bind(BackendApplicationContribution).to(CincoCloudGrpc).inSingletonScope();
});
