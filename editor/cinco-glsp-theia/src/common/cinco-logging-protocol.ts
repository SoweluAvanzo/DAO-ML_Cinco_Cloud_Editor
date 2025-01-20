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

export const CINCO_LOGGING_ENDPOINT = 'services/cc_cinco_logging';
export const CincoLoggingClient = Symbol('CincoLoggingClient');
export const CincoLoggingServer = Symbol('CincoLoggingServer');

export interface CincoLoggingClient {
    forward(msg: string, type: 'BACKEND' | 'SERVER'): void;
}

export interface CincoLoggingServer extends RpcServer<CincoLoggingClient> {
    connect(): Promise<boolean>;
    getLog(): Promise<Map<'BACKEND' | 'SERVER', string[]>> | undefined;
    registerLogging(): void | undefined;
}
