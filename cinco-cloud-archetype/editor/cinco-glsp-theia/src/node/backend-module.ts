/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
import { GLSPServerContribution } from '@eclipse-glsp/theia-integration/lib/node';
import { ConnectionHandler, JsonRpcConnectionHandler } from '@theia/core';
import { ContainerModule } from '@theia/core/shared/inversify';

import { CincoGLSPSocketServerContribution } from './cinco-glsp-server-socket-contribution';
import { FilesystemUtilServerNode } from './file-system-util-server-node';
import { GLSP_SERVER_UTIL_ENDPOINT, GLSPServerUtilClient, GLSPServerUtilServer } from '../common/glsp-server-util-protocol';
import { FILESYSTEM_UTIL_ENDPOINT, FilesystemUtilClient, FilesystemUtilServer } from '../common/file-system-util-protocol';
import { GLSPServerUtilServerNode } from './glsp_server_args-provider';

export default new ContainerModule(bind => {

    bind(CincoGLSPSocketServerContribution).toSelf().inSingletonScope();
    bind(GLSPServerContribution).toService(CincoGLSPSocketServerContribution);

    // provision of fileSystemUtils from backend to frontend
    bind(FilesystemUtilServer).to(FilesystemUtilServerNode).inSingletonScope();
    bind(ConnectionHandler)
        .toDynamicValue(
            ctx =>
                new JsonRpcConnectionHandler<FilesystemUtilClient>(FILESYSTEM_UTIL_ENDPOINT, client => {
                    const fileSystemUtils = ctx.container.get<FilesystemUtilServer>(FilesystemUtilServer);
                    fileSystemUtils.setClient(client);
                    return fileSystemUtils;
                })
        )
        .inSingletonScope();

    // provision of ServerArgs
    bind(GLSPServerUtilServerNode).to(GLSPServerUtilServerNode).inSingletonScope();
    bind(GLSPServerUtilServer).to(GLSPServerUtilServerNode).inSingletonScope();
    bind(ConnectionHandler)
        .toDynamicValue(
            ctx =>
                new JsonRpcConnectionHandler<GLSPServerUtilClient>(GLSP_SERVER_UTIL_ENDPOINT, client => {
                    const glspServerUtils = ctx.container.get<GLSPServerUtilServer>(GLSPServerUtilServer);
                    glspServerUtils.setClient(client);
                    return glspServerUtils;
                })
        )
        .inSingletonScope();
});
