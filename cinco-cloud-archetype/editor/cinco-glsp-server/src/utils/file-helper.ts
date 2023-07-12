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

export function getFilesFromFolder(fs: typeof import('fs'), absRoot: string, folderPath: string): string[] {
    const absoluteFolderPath = path.join(absRoot, folderPath);
    if (!fs.existsSync(absoluteFolderPath)) {
        return [];
    }
    // file or folder exists
    const found = fs.readdirSync(absoluteFolderPath) as string[]; // all found files and directories
    const foundFiles = found.filter(file => {
        const absPath = path.join(absoluteFolderPath, file);
        try {
            return fs.readFileSync(absPath); // if file can be read, it is a file
        } catch (e) {
            return false;
        }
    });
    const foundFolders = found.filter(folder => {
        const absPath = path.join(absoluteFolderPath, folder);
        try {
            return fs.readdirSync(absPath); // if directory can be read, it is a folder} catch (e)
        } catch (e) {
            return false;
        }
    });
    const containedFiles: string[] = [];
    foundFolders.forEach((folder: string) => {
        const relativeFolderPath = path.join(folderPath, folder);
        const filesFromFolder = getFilesFromFolder(fs, absRoot, relativeFolderPath);
        filesFromFolder.forEach(file => {
            const relativeFilePath = path.join(folder, file);
            containedFiles.push(relativeFilePath);
        });
    });
    return foundFiles.concat(containedFiles);
}

export function readFile(fs: typeof import('fs'), filePath: string, encoding?: string): string | undefined {
    let result: string | undefined;
    encoding = encoding ?? 'utf-8';
    try {
        const buffer = fs.readFileSync(filePath);
        const content = buffer.toString();
        result = content;
    } catch (e) {
        console.log(`failed to read file: ${filePath}`);
    }
    return result;
}

export function readJson(fs: typeof import('fs'), filePath: string, encoding?: string): object | undefined {
    const content = readFile(fs, filePath);
    if (content) {
        try {
            return JSON.parse(content);
            // Do something with the parsed data
        } catch (err) {
            console.error(err);
        }
    }
    return undefined;
}

export function getFilesByExtension(files: string[], fileExtension: string): string[] {
    return files.filter(filename => filename.endsWith(fileExtension));
}

export function getWorkspaceRootUri(): string {
    const ROOT_BASE = '../../../..'; // pivot the rootBasePath to the folder above glsp-server
    const workspacePath = `${__dirname}/${ROOT_BASE}/glsp-client/workspace`;
    return workspacePath;
}
