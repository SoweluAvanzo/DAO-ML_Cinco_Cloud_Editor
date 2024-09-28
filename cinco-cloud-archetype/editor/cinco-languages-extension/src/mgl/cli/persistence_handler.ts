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
import fs from 'fs';
import path from 'path';
import * as vscode from 'vscode';
import AdmZip from 'adm-zip';
import { Client } from 'minio';
import { MINIO_RESOURCE_ID, createClient } from '../../minio/minio-client';
import { CreateBuildJobMessage } from '../../../generated/cinco-cloud_pb';
import { client } from '../../grpc/grpc-client';

interface ServerArgs {
    metaDevMode: boolean;
    rootFolder: string;
    languagePath: string;
    workspacePath: string;
    port: number;
}

export function getLanguageFolder(rootPath?: string): Promise<string> {
    return new Promise<string>(async (resolve, reject) => {
        let serverArgs: ServerArgs;
        try {
            serverArgs = await vscode.commands.executeCommand('cinco.provide.glsp-server-args');
        } catch (e) {
            if (vscode.workspace.workspaceFolders) {
                const rootFolder = vscode.workspace.workspaceFolders[0].uri.fsPath;
                resolve(rootFolder + '/' + 'languages');
                return;
            }
            serverArgs = {
                languagePath: 'workspace/languages',
                rootFolder: __dirname + '/../../../editor'
            } as ServerArgs;
        }
        const languagesFolder = rootPath ?? serverArgs.rootFolder + '/' + serverArgs.languagePath;
        resolve(languagesFolder);
    });
}

/**
 * Saves the content into a file in the LanguagesFolder for MetaSpecification.
 * @param content
 * @param fileName
 * @param targetFolder
 */
export function saveToFolder(content: string, fileName: string, targetFolder: string): Promise<void> {
    return new Promise<void>(async (resolve, reject) => {
        // create if not existing
        if (!fs.existsSync(targetFolder)) {
            fs.mkdirSync(targetFolder, { recursive: true });
        }
        const targetPath = targetFolder + '/' + fileName;
        console.log('Integrating meta-specification to: ' + targetPath);
        fs.writeFileSync(targetPath, content);
        resolve();
    });
}

export function uploadToMinio(directoryPath: string, onError: (e: any) => void): Promise<void> {
    // collect and zip files
    const zip = new AdmZip();
    // Get items in the directory
    const items = fs.readdirSync(directoryPath);
    items.forEach(item => {
        const fullPath = path.join(directoryPath, item);
        if (fs.statSync(fullPath).isDirectory()) {
            zip.addLocalFolder(fullPath, item);
        } else {
            zip.addLocalFile(fullPath);
        }
    });

    // upload files to minio
    const minioClient: Client = createClient();
    return minioClient
        .putObject('projects', MINIO_RESOURCE_ID + '.zip', zip.toBuffer())
        .then(success => {
            console.log('Stored meta specification successfully');
            client.createBuildJob(new CreateBuildJobMessage().setProjectid(parseInt(MINIO_RESOURCE_ID)), (error, response) => {
                if (error) {
                    throw Error('Failed to submit build job:\n' + error);
                }
                onError(error);
            });
        })
        .catch(error => {
            onError(error);
            throw Error('Failed to submit create build job:\n' + error);
        });
}
