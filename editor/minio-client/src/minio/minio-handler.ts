/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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

// Special folder
const WORKSPACE_INITIALIZATION_DIRECTORY = 'workspace';
const PLUGIN_INITIALIZATION_DIRECTORY = 'plugins';

export function fetchMetaSpecification(targetFolder: string): void {
    if (MINIO_RESOURCE_ID !== '') {
        const minioClient = createClient();
        console.log('targetFolder: ' + targetFolder);
        const targetPath = path.join(targetFolder, 'meta.zip');
        console.log('targetPath: ' + targetPath);
        minioClient
            .fGetObject('projects', `${MINIO_RESOURCE_ID}.zip`, targetPath)
            .then(_ => {
                console.log('Fetched meta specification successfully');
                unzipArtifact(targetFolder, 'meta.zip');
            })
            .catch((e: any) => {
                console.log('error fetching fetching meta-specification:\n' + e);
            });
    } else {
        console.log('MINIO_RESOURCE_ID not set!');
        try {
            unzipArtifact(targetFolder, 'meta.zip');
        } catch(e) {
            console.log("Ignore this error if you test the DockerFile: "+e);
        }
    }
}

function unzipArtifact(folder: string, file: string) {
    const zip = new AdmZip(path.join(folder, file));
    zip.extractAllTo(folder, true);

    // Handle SpecialFolders
    const editorWorkspacePath = process.env.WORKSPACE_PATH ?? path.join(__dirname, '../../workspace');
    const editorPluginsPath = process.env.PLUGINS_PATH ?? path.join(__dirname, '../../browser-app/plugins');
    const workspacePath = path.join(folder, WORKSPACE_INITIALIZATION_DIRECTORY);
    const pluginsPath = path.join(folder, PLUGIN_INITIALIZATION_DIRECTORY);
    console.log('workspacePath: ' + workspacePath);
    console.log('editorWorkspacePath: ' + editorWorkspacePath);
    console.log('pluginsPath: ' + pluginsPath);
    console.log('editorPluginsPath: ' + editorPluginsPath);
    copySpecialFolder(workspacePath, editorWorkspacePath);
    copySpecialFolder(pluginsPath, editorPluginsPath, false);
}

function copySpecialFolder(sourceFolder: string, targetFolder: string, emptyCheck = true) {
    if (fs.existsSync(sourceFolder)) {
        if (!emptyCheck || isDirectoryEmpty(targetFolder)) {
            copyFolderRecursiveSync(sourceFolder, targetFolder);
            console.log(`Files from ${sourceFolder} copied to ${targetFolder}`);
            // Delete the workspacePath folder
            fs.rmdirSync(sourceFolder, { recursive: true });
        } else {
            console.log(`${targetFolder} is not empty. Skipping initialization.`);
        }
    } else {
        console.log(`${sourceFolder} does not exist. Skipping initialization.`);
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
