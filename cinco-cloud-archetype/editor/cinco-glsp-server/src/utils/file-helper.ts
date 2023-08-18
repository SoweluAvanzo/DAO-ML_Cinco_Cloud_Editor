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
import { getFileExtension } from '@cinco-glsp/cinco-glsp-common';
import * as path from 'path';

export function getFilesFromDirectories(
    fs: typeof import('fs'),
    directories: string[],
    filterTypes: string[],
    encoding?: string
): string[] {
    let result: string[] = [];
    for (const dir of directories) {
        const fileUris = getFiles(fs, dir, filterTypes);
        result = result.concat(fileUris);
    }
    return result;
}

export function getFiles(fs: typeof import('fs'), absFolderPath: string, filterTypes: string[]): string[] {
    try {
        console.log(`loading files from:  ${absFolderPath}`);
        // TODO: resolve files inside folders
        return getFilesFromFolder(fs, absFolderPath, './', filterTypes);
    } catch (e) {
        console.log('failed to access filesystem.');
    }
    return [];
}

export function getFilesFromFolder(fs: typeof import('fs'), absRoot: string, folderPath: string, filterTypes?: string[]): string[] {
    const absoluteFolderPath = path.join(absRoot, folderPath);
    if (!fs.existsSync(absoluteFolderPath)) {
        return [];
    }
    // file or folder exists
    const found = fs.readdirSync(absoluteFolderPath) as string[]; // all found files and directories
    let foundFiles = found.filter(file => {
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
        const filesFromFolder = getFilesFromFolder(fs, absRoot, relativeFolderPath, filterTypes);
        filesFromFolder.forEach(file => {
            const relativeFilePath = path.join(folder, file);
            containedFiles.push(relativeFilePath);
        });
    });

    foundFiles = foundFiles.concat(containedFiles);
    if (filterTypes && filterTypes.length > 0) {
        // filter by filterTypes
        foundFiles = getFilesByExtensions(foundFiles, filterTypes);
    }
    return foundFiles;
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

export function readFiles(fs: typeof import('fs'), filePaths: string[], encoding?: string): string[] {
    const contents: string[] = [];
    for (const filePath of filePaths) {
        try {
            console.log(`reading file:  ${filePaths}`);
            encoding = encoding ?? 'utf-8';
            const buffer = fs.readFileSync(filePath);
            const content = buffer.toString();
            contents.push(content);
        } catch (e) {
            console.log(`failed to read file: ${filePaths}`);
        }
    }
    return contents;
}

export function readFilesFromDirectories(
    fs: typeof import('fs'),
    directories: string[],
    filterTypes: string[] = [],
    encoding?: string
): Map<string, string> {
    const result = new Map<string, string>();
    for (const dir of directories) {
        const absDir = `${getRootUri()}/${dir}`;
        const fileUris = getFiles(fs, absDir, filterTypes).map(f => `${absDir}/${f}`);
        const contents = readFiles(fs, fileUris);
        for (let i = 0; i < fileUris.length && contents.length; i++) {
            if (fileUris[i] && contents[i]) {
                result.set(fileUris[i], contents[i]);
            }
        }
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

export function getFilesByExtensions(files: string[], fileExtensions: string[]): string[] {
    return files.filter(f => fileExtensions.indexOf('.' + getFileExtension(f)) >= 0);
}

export function getRootUri(): string {
    const ROOT_BASE = '../../../..'; // pivot the rootBasePath to the folder above glsp-server
    const root = `${__dirname}/${ROOT_BASE}`;
    return root;
}

export function getWorkspaceRootUri(): string {
    const root = getRootUri();
    const workspacePath = `${root}/workspace`;
    return workspacePath;
}
