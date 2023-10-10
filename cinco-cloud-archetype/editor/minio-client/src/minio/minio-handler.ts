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

import * as path from 'path';
import { createClient } from './minio-client';
import { MINIO_RESOURCE_ID } from './minio_values';
import AdmZip = require('adm-zip');

export function fetchMetaSpecification(targetFolder: string): void {
    if (MINIO_RESOURCE_ID !== '') {
        const minioClient = createClient();
        console.log('targetFolder: ' + targetFolder);
        const targetPath = path.join(targetFolder, 'meta.zip');
        console.log('targetPath: ' + targetPath);
        minioClient.fGetObject('projects', `${MINIO_RESOURCE_ID}.zip`, targetPath).then((_) => {
            const zip = new AdmZip(targetPath);
            zip.extractAllTo(targetFolder, true);
            console.log('Fetched meta specification successfully');
        }).catch(e => {
            console.log('error fetching fetching meta-specification!');
        });
    } else {
        console.log('MINIO_RESOURCE_ID not set!');
    }
}
