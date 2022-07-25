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

import * as path from 'path';
import * as cp from 'child_process';
import { createClient } from '../shared/minio-client';
import { Client } from 'minio';
import { EXTERNAL_PYRO_SUBPATH, PYRO_SERVER_BINARIES_FILE } from './environment-vars';

export async function fetchBinaries(): Promise<void> {
    return new Promise<void>(resolve => {
        if (PYRO_SERVER_BINARIES_FILE !== '') {
            const minioClient: Client = createClient();
            const file = path.resolve(__dirname, '..', '..', 'pyro-server-binaries.zip');
            minioClient.fGetObject('projects', PYRO_SERVER_BINARIES_FILE, file.toString(), e => {
                if (e) {
                    const msg = 'Failed to fetch pyro server binary';
                    console.log(msg);
                    throw new Error(`${msg}` + e);
                }

                console.log('Fetched pyro-server binaries');
                const unpackScriptPath = path.resolve(__dirname, '..', '..', 'unpack-pyro-server.sh');
                cp.spawnSync('chmod', ['+X', unpackScriptPath.toString()]);
                const buffer = cp.spawnSync('sh', [unpackScriptPath.toString(), EXTERNAL_PYRO_SUBPATH]);
                console.log(String(buffer.stdout));
                console.log(String(buffer.stderr));
                resolve();
            });
        } else {
            resolve();
        }
    });
}
