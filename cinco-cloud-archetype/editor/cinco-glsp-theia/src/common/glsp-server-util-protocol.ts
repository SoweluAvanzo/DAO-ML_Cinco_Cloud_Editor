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
import { RpcServer } from '@theia/core/lib/common/messaging/proxy-factory';
import { ServerArgs } from '@cinco-glsp/cinco-glsp-common';

export const GLSP_SERVER_UTIL_ENDPOINT = 'services/cc_glsp_server_util';
export const GLSPServerUtilClient = Symbol('GLSPServerUtilClient');
export interface GLSPServerUtilClient {}
export const GLSPServerUtilServer = Symbol('GLSPServerUtilServer');

export interface GLSPServerUtilServer extends RpcServer<GLSPServerUtilClient> {
    connect(): Promise<boolean>;
    // provides all files of a specified absolute folder path
    getArgs(): Promise<ServerArgs> | undefined;
    transpilationIsRunning(): Promise<boolean>;
    transpileLanguagesFolder(): Promise<void> | undefined;
    transpileWatchLanguagesFolder(): Promise<boolean | undefined>;
}
