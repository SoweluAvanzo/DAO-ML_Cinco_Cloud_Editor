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

import { LogLevel, Logger } from '@theia/core';
import { injectable } from '@theia/core/shared/inversify';

@injectable()
export class CincoLogger extends Logger {
    protected static listeners: ((log: string, type: string) => void)[] = [];
    protected static loggedTerminal: Map<string, { logLevel: number; log: string }[]> = new Map();

    override log(logLevel: number, arg2: any, ...params: any[]): Promise<void> {
        const type = 'BACKEND';
        return this.logProcess(type, logLevel, arg2, ...params);
    }

    logGLSPServer(logLevel: number, arg2: any, ...params: any[]): Promise<void> {
        return this.logProcess('SERVER', logLevel, arg2, ...params);
    }

    logProcess(processName: string, logLevel: number, arg2: any, ...params: any[]): Promise<void> {
        const msg = arg2;
        if (!CincoLogger.loggedTerminal.has(processName)) {
            CincoLogger.loggedTerminal.set(processName, []);
        }
        CincoLogger.loggedTerminal.get(processName)!.push({ logLevel: logLevel, log: msg });
        for (const listener of CincoLogger.listeners) {
            listener(this.toLoggedString(logLevel, msg), processName);
        }
        return super.log(logLevel, msg, params);
    }

    async getFullLog(): Promise<Map<string, string[]>> {
        const result = new Map<string, string[]>();
        for (const entry of CincoLogger.loggedTerminal.entries()) {
            const name = entry[0];
            const logs = entry[1].map(v => this.toLoggedString(v.logLevel, v.log));
            result.set(name, logs);
        }
        return result;
    }

    toLoggedString(logLevel: number, log: string): string {
        return `${LogLevel.toString(logLevel)?.toUpperCase()} ` + this.cleanANSI(log);
    }

    addListener(listener: (log: string, type: string) => void): void {
        CincoLogger.listeners.push(listener);
    }

    private cleanANSI(msg: string): string {
        // eslint-disable-next-line no-control-regex
        return msg.replace(/[\u001b\u009b][[()#;?]*(?:[0-9]{1,4}(?:;[0-9]{0,4})*)?[0-9A-ORZcf-nqry=><]/g, '');
    }
}
