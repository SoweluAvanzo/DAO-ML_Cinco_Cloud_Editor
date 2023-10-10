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

import * as minio_values from './minio_values';

let clientInstance;

export function createClient() {
    const minio = require('minio');
    if (clientInstance === undefined) {
        clientInstance = new minio.Client({
            endPoint: minio_values.MINIO_HOST,
            port: Number(minio_values.MINIO_PORT),
            useSSL: false,
            accessKey: minio_values.MINIO_ACCESS_KEY,
            secretKey: minio_values.MINIO_SECRET_KEY
        });
    }
    return clientInstance;
}
