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
import * as fs from 'fs';
import { createClient } from './minio-client';
import { MINIO_RESOURCE_ID } from './minio_values';
import AdmZip = require('adm-zip');

const WORKSPACE_INITIALIZATION_DIRECTORY = 'workspace';

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
            
            // Check if WORKSPACE_INITIALIZATION_DIRECTORY exists
            const workspacePath = path.join(targetFolder, WORKSPACE_INITIALIZATION_DIRECTORY);
            const editorWorkspacePath = process.env.WORKSPACE_PATH;

            if (fs.existsSync(workspacePath)) {
                if (isDirectoryEmpty(editorWorkspacePath)) {
                    copyFolderRecursiveSync(workspacePath, editorWorkspacePath);
                    console.log(`Files from ${workspacePath} copied to ${editorWorkspacePath}`);

                    // Delete the workspacePath folder
                    fs.rmdirSync(workspacePath, { recursive: true });
                } else {
                    console.log(`${editorWorkspacePath} is not empty. Skipping initialization.`);
                }
            } else {
                console.log(`${workspacePath} does not exist. Skipping initialization.`);
            }
        }).catch(e => {
            console.log('error fetching fetching meta-specification!');
        });
    } else {
        console.log('MINIO_RESOURCE_ID not set!');
    }
}

function isDirectoryEmpty(directory: string): boolean {
    return fs.readdirSync(directory).length === 0;
}

function copyFolderRecursiveSync(source: string, target: string) {
    const files = fs.readdirSync(source);
    files.forEach(file => {
        const sourcePath = path.join(source, file);
        const targetPath = path.join(target, file);

        if (fs.statSync(sourcePath).isDirectory()) {
            if (!fs.existsSync(targetPath)) {
                fs.mkdirSync(targetPath);
            }
            copyFolderRecursiveSync(sourcePath, targetPath);
        } else {
            fs.copyFileSync(sourcePath, targetPath);
        }
    });
}
