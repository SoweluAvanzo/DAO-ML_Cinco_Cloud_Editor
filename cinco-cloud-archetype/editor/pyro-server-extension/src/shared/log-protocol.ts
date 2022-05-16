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

export const ENDPOINT = 'services/pyro_logger';
export interface PyroLogClient {
    info(msg: string): void;
}
export const PyroLogServer = Symbol('PyroLogServer');
export interface PyroLogServer extends JsonRpcServer<PyroLogClient> {
    info(msg: string): void;
    getLoggerName(): Promise<string>;
}
