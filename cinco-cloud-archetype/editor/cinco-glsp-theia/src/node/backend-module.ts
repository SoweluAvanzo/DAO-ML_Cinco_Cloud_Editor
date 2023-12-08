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
import { ConnectionHandler, ILogger, RpcConnectionHandler } from '@theia/core';
import { ContainerModule } from '@theia/core/shared/inversify';
import { bindAsService } from '@eclipse-glsp/protocol';

import { CincoGLSPSocketServerContribution } from './cinco-glsp-server-socket-contribution';
import { FilesystemUtilServerNode } from './file-system-util-server-node';
import { GLSP_SERVER_UTIL_ENDPOINT, GLSPServerUtilClient, GLSPServerUtilServer } from '../common/glsp-server-util-protocol';
import { FILESYSTEM_UTIL_ENDPOINT, FilesystemUtilClient, FilesystemUtilServer } from '../common/file-system-util-protocol';
import { GLSPServerUtilServerNode } from './glsp-server-util-server-node';
import { CincoGLSPServerArgsSetup } from './cinco-glsp-server-args-setup';
import { CincoLogger } from './cinco-theia-logger';

export default new ContainerModule((bind, unbind) => {
    if (isDirectWebSocketConnection()) {
        return;
    }
    unbind(ILogger);
    bind(CincoLogger).toSelf().inSingletonScope();
    bind(ILogger).to(CincoLogger);
    bind(CincoGLSPServerArgsSetup).toSelf().inSingletonScope();
    if (isIntegratedNodeServer()) {
        // executed inside this node package. Not yet implemented
        // bindAsService(bind, GLSPServerContribution, CincoGLSPNodeServerContribution);
        throw new Error('There is no implementation for an IntegratedNodeServer, yet! A use-case could be in-browser modelling.');
    } else {
        bindAsService(bind, GLSPServerContribution, CincoGLSPSocketServerContribution);
    }

    // provision of fileSystemUtils from backend to frontend
    bind(FilesystemUtilServer).to(FilesystemUtilServerNode).inSingletonScope();
    bind(ConnectionHandler)
        .toDynamicValue(
            ctx =>
                new RpcConnectionHandler<FilesystemUtilClient>(FILESYSTEM_UTIL_ENDPOINT, client => {
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
                new RpcConnectionHandler<GLSPServerUtilClient>(GLSP_SERVER_UTIL_ENDPOINT, client => {
                    const glspServerUtils = ctx.container.get<GLSPServerUtilServer>(GLSPServerUtilServer);
                    glspServerUtils.setClient(client);
                    return glspServerUtils;
                })
        )
        .inSingletonScope();
});

const directWebSocketArg = '--directWebSocket';
/**
 * Utility function to parse if the frontend should connect directly to a running GLSP WebSocket Server instance
 * and skip the binding of the backend contribution.
 * i.e. if the {@link directWebSocketArg `--directWebSocket`} argument has been passed.
 * @returns `true` if the {@link directWebSocketArg `--directWebSocket`} argument has been set.
 */
function isDirectWebSocketConnection(): boolean {
    const args = process.argv.filter(a => a.toLowerCase().startsWith(directWebSocketArg.toLowerCase()));
    return args.length > 0;
}

export const integratedArg = '--integratedNode';

/**
 * Utility function to parse if the frontend should connect to a GLSP server running directly in the backend
 * i.e. if the {@link integratedArg `--integratedNode`} argument has been passed.
 * @returns `true` if the {@link integratedArg `--integratedNode`} argument has been set.
 */
export function isIntegratedNodeServer(): boolean {
    const args = process.argv.filter(a => a.toLowerCase().startsWith(integratedArg.toLowerCase()));
    return args.length > 0;
}
