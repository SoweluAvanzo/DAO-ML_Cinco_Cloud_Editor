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

/**
 * Saves the content into a file in the LanguagesFolder for MetaSpecification.
 * @param content 
 * @param fileName 
 * @returns the path to the languagesFolder
 */
export function saveToLanguagesFolder(content: string, fileName: string): Promise<string> {
    return new Promise<string>((resolve, reject) => {
        vscode.commands.executeCommand( 'cinco.provide.glsp-server-args').then( result => {
            const serverArgs = result as ServerArgs;
            const languagesFolder = serverArgs.rootFolder + '/' + serverArgs.languagePath;
            // create if not existing
            if (!fs.existsSync(languagesFolder)) {
                fs.mkdirSync(languagesFolder, { recursive: true });
            }
            const targetPath = languagesFolder + '/' + fileName;
            console.log('Integrating meta-specification to: '+ targetPath);
            fs.writeFileSync(targetPath, content);
            resolve(languagesFolder);
            //  update meta-specification in glsp-server
            vscode.commands.executeCommand('cinco.meta-specification.reload');
        }).then(() => {}, (e) => {
            reject(e)
        })
    });
}

export function uploadToMinio(directoryPath: string): void {
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
    minioClient.putObject('projects', MINIO_RESOURCE_ID + '.zip', zip.toBuffer()).then(success => {
        console.log('Stored meta specification successfully');
        client.createBuildJob(new CreateBuildJobMessage().setProjectid(parseInt(MINIO_RESOURCE_ID)), (error, response) => {
            if(error) {
                console.error('Failed to submit create build job');
            }
        });
    }).catch(error =>
        console.log('Unable to store meta specification', error)
    );
}