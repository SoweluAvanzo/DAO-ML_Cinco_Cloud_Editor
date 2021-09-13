/*!
 * Copyright (c) 2019-2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
import { BackendApplicationContribution } from '@theia/core/lib/node/backend-application';
import { ContainerModule } from 'inversify';
import { ServerLauncher } from './server-launcher';

export default new ContainerModule(bind => {
    bind(BackendApplicationContribution).to(ServerLauncher).inSingletonScope();
});
