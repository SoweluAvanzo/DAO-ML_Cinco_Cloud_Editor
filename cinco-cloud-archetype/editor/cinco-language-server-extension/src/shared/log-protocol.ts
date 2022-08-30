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

import { JsonRpcServer } from '@theia/core/lib/common/messaging/proxy-factory';
import { injectable } from 'inversify';

export const ENDPOINT = 'services/cc_logger';
export interface LogClient {
    info(msg: string): void;
    error(msg: string): void;
}

/* eslint-disable no-redeclare */
export const LogServer = Symbol('LogServer');
export interface LogServer extends JsonRpcServer<LogClient> {
    info(msg: string): void;
    error(msg: string): void;
    getLoggerName(): Promise<string>;
}

@injectable()
export class LogServerNode implements LogServer {
    client: LogClient | undefined;

    async getLoggerName(): Promise<string> {
        return 'LogServer';
    }

    info(msg: string): void {
        if (this.client) {
            this.client!.info(msg);
        }
    }

    error(msg: string): void {
        if (this.client) {
            this.client!.error(msg);
        }
    }

    dispose(): void {
        this.info('---disposing---');
    }

    setClient(client: LogClient | undefined): void {
        this.client = client;
    }
}
