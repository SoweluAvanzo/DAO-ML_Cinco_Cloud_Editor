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
import { META_DEV_MODE, META_LANGUAGES_FOLDER, SERVER_LANGUAGES_FOLDER, hasArg } from '@cinco-glsp/cinco-glsp-common';
import * as path from 'path';
import * as fs from 'fs';

export const FOLDER_TYPE = ':FOLDER';

/**
 * @param targetPath
 * @param content
 * @param encoding | 'ascii', 'utf8', 'utf-8', 'utf16le', 'ucs2', 'ucs-2', 'base64', 'base64url', 'latin1', 'binary', 'hex'
 */
export function writeFile(targetPath: string, content: string, overwriteExistingFile = true, encoding = 'utf-8'): void {
    if (overwriteExistingFile || !exists(targetPath)) {
        fs.writeFileSync(targetPath, content, { encoding: encoding as BufferEncoding });
    }
}

export function getFilesFromDirectories(directories: string[], filterTypes: string[]): string[] {
    let result: string[] = [];
    for (const dir of directories) {
        const fileUris = getFiles(dir, filterTypes);
        result = result.concat(fileUris);
    }
    return result;
}

export function getFiles(absFolderPath: string, filterTypes: string[] = []): string[] {
    try {
        return getFileEntities(absFolderPath, './', filterTypes);
    } catch (e) {
        console.log('failed to access filesystem.');
    }
    return [];
}

export function getSubfolder(absFolderPath: string, depth?: number): string[] {
    if (depth === 0) {
        return [];
    }
    try {
        let subfolders = getFileEntities(absFolderPath, './', [FOLDER_TYPE]);
        for (const sf of subfolders) {
            subfolders = subfolders.concat(getSubfolder(sf, (depth ?? 0) - 1));
        }
        return subfolders;
    } catch (e) {
        console.log('failed to access filesystem.');
    }
    return [];
}

function getFileEntities(absRoot: string, folderPath: string, filterTypes?: string[]): string[] {
    const absoluteFolderPath = path.join(absRoot, folderPath);
    if (!fs.existsSync(absoluteFolderPath)) {
        return [];
    }
    // file or folder exists
    const found = fs.readdirSync(absoluteFolderPath) as string[]; // all found files and directories
    let foundEntities = found.filter(file => {
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
            return existsDirectory(absPath); // if directory can be read, it is a folder catch (e)
        } catch (e) {
            return false;
        }
    });
    const containedFiles: string[] = [];
    foundFolders.forEach((folder: string) => {
        const relativeFolderPath = path.join(folderPath, folder);
        const filesFromFolder = getFileEntities(absRoot, relativeFolderPath, filterTypes);
        filesFromFolder.forEach(file => {
            const relativeFilePath = path.join(folder, file);
            containedFiles.push(relativeFilePath);
        });
    });

    foundEntities = foundEntities.concat(containedFiles);
    if (filterTypes && filterTypes.length > 0) {
        // filter by filterTypes
        foundEntities = getFilesByExtensions(
            foundEntities,
            filterTypes.filter(f => f !== FOLDER_TYPE)
        );
        if (filterTypes.includes(FOLDER_TYPE)) {
            if (filterTypes.length === 1) {
                foundEntities = []; // remove all files, that were previously added...
            }
            foundEntities = foundEntities.concat(foundFolders.map(f => `${absRoot}/${f}`)); // ...and add only the folders
        }
    }
    return foundEntities;
}

export function readFile(filePath: string, encoding = 'utf-8'): string | undefined {
    let result: string | undefined;
    try {
        const buffer = fs.readFileSync(filePath);
        const content = buffer.toString();
        result = content;
    } catch (e) {
        console.log(`failed to read file: ${filePath}`);
    }
    return result;
}

export function readFiles(filePaths: string[], encoding?: string): string[] {
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

export function readFilesFromDirectories(directories: string[], filterTypes: string[] = [], encoding?: string): Map<string, string> {
    const result = new Map<string, string>();
    for (const dir of directories) {
        const absDir = isAbsolute(dir) ? dir : `${getRootUri()}/${dir}`;
        const fileUris = getFiles(absDir, filterTypes).map(f => `${absDir}/${f}`);
        const contents = readFiles(fileUris);
        for (let i = 0; i < fileUris.length && contents.length; i++) {
            if (fileUris[i] && contents[i]) {
                result.set(fileUris[i], contents[i]);
            }
        }
    }
    return result;
}

export function isAbsolute(dir: string): boolean {
    return dir.startsWith('/');
}

export function readJson(filePath: string, encoding?: string): object | undefined {
    const content = readFile(filePath);
    if (content) {
        try {
            return JSON.parse(content);
            // Do something with the parsed data
        } catch (err) {
            console.error('Error parsing json-file: ' + filePath);
            console.error(err);
        }
    }
    return undefined;
}

export function getFilesByExtension(files: string[], fileExtension: string): string[] {
    return files.filter(filename => filename.endsWith(fileExtension));
}

export function getFilesByExtensions(files: string[], fileExtensions: string[]): string[] {
    return files.filter(f => fileExtensions.indexOf(getFileExtension(f)) >= 0);
}

export function getParentDirectory(fileOrDirPath: string): string {
    return path.dirname(fileOrDirPath);
}

export function getDirectoryName(dirPath: string): string {
    return path.basename(dirPath);
}

export function getFileName(filePath: string): string {
    return path.basename(filePath);
}

export function getFileExtension(filePath: string): string {
    return path.extname(filePath);
}

export function exists(fileOrDirPath: string): boolean {
    return fs.existsSync(fileOrDirPath);
}

export function existsFile(filePath: string): boolean {
    return exists(filePath) && fs.lstatSync(filePath).isFile();
}

export function existsDirectory(dirPath: string): boolean {
    return exists(dirPath) && fs.lstatSync(dirPath).isDirectory();
}

export function readDirectory(dirPath: string): string[] {
    return fs.readdirSync(dirPath);
}

export function deleteFile(dirPath: string, force = false): void {
    fs.rmSync(dirPath, { recursive: false, force: force });
}

export function deleteDirectory(dirPath: string, recursive = false, force = false): void {
    fs.rmSync(dirPath, { recursive: recursive, force: force });
}

export function createDirectory(dirPath: string, deleteExistingDirectory = false): void {
    if (deleteExistingDirectory && exists(dirPath)) {
        deleteDirectory(dirPath, true);
    }
    fs.mkdirSync(dirPath, { recursive: true });
}

export function copyFile(sourceFilePath: string, targetFilePath: string, overwriteExistingFile = true): void {
    const mode = overwriteExistingFile ? 0 : fs.constants.COPYFILE_EXCL;
    fs.copyFileSync(sourceFilePath, targetFilePath, mode);
}

export function copyDirectory(
    sourceDirPath: string,
    targetDirPath: string,
    deleteExistingDirectories = false,
    overwriteExistingFiles = true
): void {
    createDirectory(targetDirPath, deleteExistingDirectories);
    for (const entry of readDirectory(sourceDirPath)) {
        const targetPath = path.join(targetDirPath, getFileName(entry));
        if (existsDirectory(entry)) {
            copyDirectory(entry, targetPath, deleteExistingDirectories, overwriteExistingFiles);
        } else {
            copyFile(entry, targetPath, overwriteExistingFiles);
        }
    }
}

/**
 * Bundled file is per convention in the folder 'bundle' inside servers root-folder
 */
export function isBundle(): boolean {
    return __dirname.endsWith('bundle');
}

export function getRoot(): string {
    return getRootUri();
}

export function getRootUri(): string {
    let root = getRootFolderArg();
    if (root) {
        return root;
    } else {
        const ROOT_BASE = isBundle() ? '../..' : '../../..'; // pivot the rootBasePath to the folder above glsp-server
        root = `${__dirname}/${ROOT_BASE}`;
        return root;
    }
}

export function getWorkspaceRootUri(): string {
    const workspaceFolder = getWorkspaceFolderArg();
    const root = getRootUri();
    const workspacePath = `${root}/${workspaceFolder ?? 'workspace'}`;
    return workspacePath;
}

export function getLanguageFolder(): string {
    const languagesFolder = getLanguageFolderArg();
    const root = getRootUri();
    return `${root}/${languagesFolder ?? META_LANGUAGES_FOLDER}`;
}

export function getLibLanguageFolder(): string {
    const root = getRootUri();
    return `${root}/${SERVER_LANGUAGES_FOLDER}`;
}

export function getRootFolderArg(): string | undefined {
    const argsKey = '--rootFolder';
    return getArgs(argsKey);
}

export function getLanguageFolderArg(): string | undefined {
    const argsKey = '--metaLanguagesFolder';
    return getArgs(argsKey);
}

export function getWorkspaceFolderArg(): string | undefined {
    const argsKey = '--workspaceFolder';
    return getArgs(argsKey);
}

export function getWebsocketPathArg(): string | undefined {
    const argsKey = '--websocketPath';
    return getArgs(argsKey);
}

export function getWebServerPortArg(): number | undefined {
    const argsKey = '--webServerPort';
    if (hasArg(argsKey)) {
        return Number.parseInt(getArgs(argsKey)!, 10);
    } else {
        return undefined;
    }
}

export function isMetaDevModeArg(): boolean {
    const argsKey = '--metaDevMode';
    const args = process.argv.filter(a => a.startsWith(argsKey));
    if (args.length > 0) {
        return true;
    }
    return false;
}

export function getPortArg(): string | undefined {
    const argsKey = '--port';
    return getArgs(argsKey);
}

export function getArgs(argsKey: string): string | undefined {
    const args = process.argv.filter(a => a.startsWith(argsKey));
    if (args.length > 0) {
        const result = args[0].substring(argsKey.length + 1, undefined);
        if (result) {
            return result.replace(/"|'/g, ''); // replace quotes
        }
    }
    return undefined;
}

export function isLanguageDesignMode(): boolean {
    if (isMetaDevModeArg()) {
        return true;
    }
    const metaDevModeString = process.env[META_DEV_MODE];
    if (!metaDevModeString) {
        console.log('ENV VAR META_DEV_MODE - not set');
    }
    const metaDevMode = metaDevModeString === 'true' || metaDevModeString === 'True';
    return metaDevMode;
}
