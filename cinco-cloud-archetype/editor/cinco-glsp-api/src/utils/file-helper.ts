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
import { META_DEV_MODE, META_LANGUAGES_FOLDER, SERVER_LANGUAGES_FOLDER, getGraphTypes, hasArg } from '@cinco-glsp/cinco-glsp-common';
import * as path from 'path';
import * as fs from 'fs';
import * as os from 'os';
import { fileURLToPath } from 'url';

export const FOLDER_TYPE = ':FOLDER';

export async function getFilesFromDirectories(directories: string[], filterTypes: string[]): Promise<string[]> {
    let result: string[] = [];
    for (const dir of directories) {
        let fileUris = await getFiles(dir, filterTypes);
        fileUris = fileUris.map(f => path.join(dir, f));
        result = result.concat(fileUris);
    }
    return Array.from(new Set(result));
}

export function getFilesFromDirectoriesSync(directories: string[], filterTypes: string[]): string[] {
    let result: string[] = [];
    for (const dir of directories) {
        let fileUris = getFilesSync(dir, filterTypes);
        fileUris = fileUris.map(f => path.join(dir, f));
        result = result.concat(fileUris);
    }
    return Array.from(new Set(result));
}

async function getFileEntities(absRoot: string, folderPath: string, filterTypes?: string[]): Promise<string[]> {
    const absoluteFolderPath = path.join(absRoot, folderPath);
    const pathExists = await exists(absoluteFolderPath);
    if (!pathExists) {
        return [];
    }

    const found = await readDirectory(absoluteFolderPath); // all found files and directories
    // search for existing files
    const foundFileEntities = (
        await Promise.all(
            found.map(async potentialFile => {
                const absPath = path.join(absoluteFolderPath, potentialFile);
                const isFile = await existsFile(absPath);
                return { potentialFile, isFile };
            })
        )
    )
        .filter(value => value.isFile)
        .map(value => value.potentialFile);

    // search for existing folders
    const foundFolders = (
        await Promise.all(
            found.map(async potentialFolder => {
                const absPath = path.join(absoluteFolderPath, potentialFolder);
                const isFolder = await existsDirectory(absPath);
                return { potentialFolder, isFolder };
            })
        )
    )
        .filter(value => value.isFolder)
        .map(value => value.potentialFolder);

    const containedFiles: string[] = [];
    await Promise.all(
        foundFolders.map(async (folder: string) => {
            const relativeFolderPath = path.join(folderPath, folder);
            const filesFromFolder = await getFileEntities(absRoot, relativeFolderPath, filterTypes);
            filesFromFolder.forEach(file => {
                const relativeFilePath = path.join(folder, file);
                containedFiles.push(relativeFilePath);
            });
        })
    );

    let allFoundFileEntities = foundFileEntities.concat(containedFiles);
    if (filterTypes && filterTypes.length > 0) {
        // filter by filterTypes
        allFoundFileEntities = getFilesByExtensions(
            allFoundFileEntities,
            filterTypes.filter(f => f !== FOLDER_TYPE)
        );

        // filter object for folders :FOLDER as a type
        if (filterTypes.includes(FOLDER_TYPE)) {
            if (filterTypes.length === 1) {
                // Only :FOLDER is defined => remove all files, that were previously added...
                allFoundFileEntities = [];
            }
            // ...and add only the folders
            allFoundFileEntities = allFoundFileEntities.concat(foundFolders.map(f => `${absRoot}/${f}`));
        }
    }
    return allFoundFileEntities;
}

function getFileEntitiesSync(absRoot: string, folderPath: string, filterTypes?: string[]): string[] {
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
            return existsDirectorySync(absPath); // if directory can be read, it is a folder catch (e)
        } catch (e) {
            return false;
        }
    });
    const containedFiles: string[] = [];
    foundFolders.forEach((folder: string) => {
        const relativeFolderPath = path.join(folderPath, folder);
        const filesFromFolder = getFileEntitiesSync(absRoot, relativeFolderPath, filterTypes);
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

export function getFiles(absFolderPath: string, filterTypes: string[] = []): Promise<string[]> {
    try {
        return getFileEntities(absFolderPath, './', filterTypes);
    } catch (e) {
        console.log('failed to access filesystem.');
    }
    return new Promise<string[]>(resolve => resolve([]));
}

export function getFilesSync(absFolderPath: string, filterTypes: string[] = []): string[] {
    try {
        return getFileEntitiesSync(absFolderPath, './', filterTypes);
    } catch (e) {
        console.log('failed to access filesystem.');
    }
    return [];
}

export async function getSubfolder(absFolderPath: string, depth?: number): Promise<string[]> {
    if (depth === 0) {
        return [];
    }
    try {
        let subfolders = await getFileEntities(absFolderPath, './', [FOLDER_TYPE]);
        for (const sf of subfolders) {
            subfolders = subfolders.concat(await getSubfolder(sf, (depth ?? 0) - 1));
        }
        return subfolders;
    } catch (e) {
        console.log('failed to access filesystem.');
    }
    return [];
}

export function getSubfolderSync(absFolderPath: string, depth?: number): string[] {
    if (depth === 0) {
        return [];
    }
    try {
        let subfolders = getFileEntitiesSync(absFolderPath, './', [FOLDER_TYPE]);
        for (const sf of subfolders) {
            subfolders = subfolders.concat(getSubfolderSync(sf, (depth ?? 0) - 1));
        }
        return subfolders;
    } catch (e) {
        console.log('failed to access filesystem.');
    }
    return [];
}

export async function writeFile(
    targetPath: string,
    content: string,
    overwriteExistingFile = true,
    encoding: NodeJS.BufferEncoding = 'utf-8'
): Promise<void> {
    const doesExist = await exists(targetPath);
    return new Promise<void>(resolve => {
        if (overwriteExistingFile || !doesExist) {
            fs.writeFile(
                targetPath,
                content,
                {
                    encoding: encoding
                } as fs.WriteFileOptions,
                (err: NodeJS.ErrnoException | null) => {
                    if (err) {
                        throw new Error('Could not write file: ' + targetPath);
                    }
                    resolve();
                }
            );
        } else {
            resolve();
        }
    });
}

export function writeFileSync(targetPath: string, content: string, overwriteExistingFile = true, encoding = 'utf-8'): void {
    if (overwriteExistingFile || !existsSync(targetPath)) {
        fs.writeFileSync(targetPath, content, { encoding: encoding as BufferEncoding });
    }
}

export async function readFile(filePath: string, encoding: NodeJS.BufferEncoding = 'utf8'): Promise<string | undefined> {
    return new Promise<string | undefined>((resolve, reject) => {
        fs.readFile(filePath, { encoding: encoding }, (err: NodeJS.ErrnoException | null, data: string) => {
            if (err) {
                console.log(`failed to read file: ${filePath}`);
            }
            resolve(err ? undefined : data);
        });
    });
}

export function readFileSync(filePath: string, encoding: NodeJS.BufferEncoding = 'utf-8'): string | undefined {
    let result: string | undefined;
    try {
        return fs.readFileSync(filePath, { encoding: encoding });
    } catch (e) {
        console.log(`failed to read file: ${filePath}`);
    }
    return result;
}

export async function readFiles(filePaths: string[], encoding: NodeJS.BufferEncoding = 'utf-8'): Promise<(string | undefined)[]> {
    const promises: Promise<string | undefined>[] = [];
    for (const filePath of filePaths) {
        promises.push(readFile(filePath, encoding));
    }
    return Promise.all(promises);
}

export function readFilesSync(filePaths: string[], encoding: NodeJS.BufferEncoding = 'utf-8'): string[] {
    const contents: string[] = [];
    for (const filePath of filePaths) {
        try {
            console.log(`reading file:  ${filePaths}`);
            const buffer = fs.readFileSync(filePath, encoding);
            const content = buffer.toString();
            contents.push(content);
        } catch (e) {
            console.log(`failed to read file: ${filePaths}`);
        }
    }
    return contents;
}

export async function readFilesFromDirectories(
    directories: string[],
    filterTypes: string[] = [],
    encoding: NodeJS.BufferEncoding = 'utf8'
): Promise<Map<string, string>> {
    const result = new Map<string, string>();
    for (const dir of directories) {
        const absDir = isAbsolute(dir) ? dir : `${getRootUri()}/${dir}`;
        const fileUris = (await getFiles(absDir, filterTypes)).map(f => `${absDir}/${f}`);
        const contents = await readFiles(fileUris, encoding);
        for (let i = 0; i < fileUris.length && contents.length; i++) {
            const content = contents[i];
            if (fileUris[i] && content) {
                result.set(fileUris[i], content);
            }
        }
    }
    return result;
}

export function readFilesFromDirectoriesSync(directories: string[], filterTypes: string[] = [], encoding?: string): Map<string, string> {
    const result = new Map<string, string>();
    for (const dir of directories) {
        const absDir = isAbsolute(dir) ? dir : `${getRootUri()}/${dir}`;
        const fileUris = getFilesSync(absDir, filterTypes).map(f => `${absDir}/${f}`);
        const contents = readFilesSync(fileUris);
        for (let i = 0; i < fileUris.length && contents.length; i++) {
            if (fileUris[i] && contents[i]) {
                result.set(fileUris[i], contents[i]);
            }
        }
    }
    return result;
}

export function getModelFiles(): Promise<string[]> {
    const workspacePath = getWorkspaceRootUri();
    const modelFileExtensions = getGraphTypes().map(gT => '.' + gT.diagramExtension);
    return getFiles(workspacePath, modelFileExtensions);
}

export function getModelFilesSync(): string[] {
    const workspacePath = getWorkspaceRootUri();
    const modelFileExtensions = getGraphTypes().map(gT => '.' + gT.diagramExtension);
    return getFilesSync(workspacePath, modelFileExtensions);
}

export async function readJson(filePath: string, options?: { hideError?: boolean; encoding?: string }): Promise<object | undefined> {
    const content = await readFile(filePath);
    if (content) {
        try {
            return JSON.parse(content);
        } catch (err) {
            if (!options || !options.hideError) {
                console.error('Error parsing json-file: ' + filePath);
                console.error(err);
            }
        }
    }
    return undefined;
}

export function readJsonSync(filePath: string, options?: { hideError?: boolean; encoding?: string }): object | undefined {
    const content = readFileSync(filePath);
    if (content) {
        try {
            return JSON.parse(content);
        } catch (err) {
            if (!options || !options.hideError) {
                console.error('Error parsing json-file: ' + filePath);
                console.error(err);
            }
        }
    }
    return undefined;
}

/**
 * Path helper
 */

export function isAbsolute(dir: string): boolean {
    return dir.startsWith('/');
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

export function toPath(sourceUri: string): string {
    let sourcePath = sourceUri.startsWith('file://') ? fileURLToPath(sourceUri) : sourceUri;
    if (os.platform() === 'win32') {
        // Remove the leading slash if it exists (Windows paths don't have it)
        sourcePath = sourcePath.replace(/^\//, '');
    }
    return sourcePath;
}

/**
 * File/Folder state
 */

export async function existsFile(filePath: string): Promise<boolean> {
    const stat = await fsStats(filePath);
    return (stat?.isFile() ?? false) && !(stat?.isDirectory() || false);
}

export function existsFileSync(filePath: string): boolean {
    try {
        return fsStatsSync(filePath).isFile();
    } catch (e) {
        console.log(e);
        return false;
    }
}

export async function existsDirectory(dirPath: string): Promise<boolean> {
    const stat = await fsStats(dirPath);
    return stat?.isDirectory() ?? false;
}

export function existsDirectorySync(dirPath: string): boolean {
    try {
        return fsStatsSync(dirPath).isDirectory();
    } catch (e) {
        console.log(e);
        return false;
    }
}

export async function exists(fileOrDirPath: string): Promise<boolean> {
    try {
        await fs.promises.access(fileOrDirPath);
        return true;
    } catch (e) {
        console.log(e);
        return false;
    }
}

export function existsSync(fileOrDirPath: string): boolean {
    return fs.existsSync(fileOrDirPath);
}

async function fsStats(fileOrDirPath: string): Promise<fs.Stats | undefined> {
    return new Promise<fs.Stats>(resolve =>
        fs.lstat(fileOrDirPath, (err: NodeJS.ErrnoException | null, stats: fs.Stats) => {
            if (err) {
                console.log(err.message);
            }
            resolve(stats);
        })
    ).catch(reason => {
        console.log('Failed fsStats: ' + reason);
        return undefined;
    });
}

function fsStatsSync(fileOrDirPath: string): fs.Stats {
    return fs.lstatSync(fileOrDirPath);
}

/**
 * File/Folder Content
 */

export async function createDirectory(dirPath: string, deleteExistingDirectory = false): Promise<void> {
    return new Promise<void>((resolve, reject) => {
        exists(dirPath)
            .then(doesExist => {
                if (deleteExistingDirectory && doesExist) {
                    deleteDirectory(dirPath, true)
                        .then(_ => {
                            fs.mkdir(dirPath, { recursive: true }, __ => resolve());
                        })
                        .catch(e => {
                            reject(e);
                        });
                }
                fs.mkdir(dirPath, { recursive: true }, (err: NodeJS.ErrnoException | null, _) => (err ? reject(err) : resolve()));
            })
            .catch(e => reject(e));
    });
}

export function createDirectorySync(dirPath: string, deleteExistingDirectory = false): void {
    if (deleteExistingDirectory && existsSync(dirPath)) {
        deleteDirectorySync(dirPath, true);
    }
    fs.mkdirSync(dirPath, { recursive: true });
}

export async function readDirectory(dirPath: string): Promise<string[]> {
    return new Promise<string[]>((resolve, reject) => {
        fs.readdir(dirPath, (err: NodeJS.ErrnoException | null, files: string[]) => {
            if (err) {
                reject(err);
            }
            resolve(files);
        });
    });
}

export function readDirectorySync(dirPath: string): string[] {
    try {
        return fs.readdirSync(dirPath);
    } catch (e) {
        console.log(e);
        return [];
    }
}

export async function deleteFile(dirPath: string, force = false): Promise<void> {
    return new Promise<void>((resolve, reject) => {
        fs.rm(dirPath, { recursive: false, force: force }, (err: NodeJS.ErrnoException | null) => {
            if (err) {
                reject(err);
            }
            resolve();
        });
    });
}

export function deleteFileSync(dirPath: string, force = false): void {
    try {
        fs.rmSync(dirPath, { recursive: false, force: force });
    } catch (e) {
        console.log(e);
    }
}

export async function deleteDirectory(dirPath: string, recursive = false, force = false): Promise<void> {
    return new Promise<void>((resolve, reject) => {
        fs.rm(dirPath, { recursive: recursive, force: force }, (err: NodeJS.ErrnoException | null) => {
            if (err) {
                reject(err);
            }
            resolve();
        });
    });
}

export function deleteDirectorySync(dirPath: string, recursive = false, force = false): void {
    try {
        fs.rmSync(dirPath, { recursive: recursive, force: force });
    } catch (e) {
        console.log(e);
    }
}

export async function copyFile(sourceFilePath: string, targetFilePath: string, overwriteExistingFile = true): Promise<void> {
    const mode = overwriteExistingFile ? 0 : fs.constants.COPYFILE_EXCL;
    return new Promise<void>((resolve, reject) => {
        fs.copyFile(sourceFilePath, targetFilePath, mode, (err: NodeJS.ErrnoException | null) => {
            err ? reject(err) : resolve();
        });
    });
}

export function copyFileSync(sourceFilePath: string, targetFilePath: string, overwriteExistingFile = true): void {
    const mode = overwriteExistingFile ? 0 : fs.constants.COPYFILE_EXCL;
    try {
        fs.copyFileSync(sourceFilePath, targetFilePath, mode);
    } catch (e) {
        console.log(e);
    }
}

export async function copyDirectory(
    sourceDirPath: string,
    targetDirPath: string,
    deleteExistingDirectories = false,
    overwriteExistingFiles = true
): Promise<void> {
    await createDirectory(targetDirPath, deleteExistingDirectories);
    const directories = await readDirectory(sourceDirPath);
    return new Promise<void>((resolve, reject) => {
        const copyCalls = directories.map(async entry => {
            const directoryExists = await existsDirectory(entry);
            const targetPath = path.join(targetDirPath, getFileName(entry));
            if (directoryExists) {
                await copyDirectory(entry, targetPath, deleteExistingDirectories, overwriteExistingFiles);
            } else {
                await copyFile(entry, targetPath, overwriteExistingFiles);
            }
        });
        Promise.all(copyCalls)
            .then(_ => {
                resolve();
            })
            .catch(reason => reject(reason));
    });
}

export function copyDirectorySync(
    sourceDirPath: string,
    targetDirPath: string,
    deleteExistingDirectories = false,
    overwriteExistingFiles = true
): void {
    createDirectorySync(targetDirPath, deleteExistingDirectories);
    for (const entry of readDirectorySync(sourceDirPath)) {
        const targetPath = path.join(targetDirPath, getFileName(entry));
        if (existsDirectorySync(targetPath)) {
            copyDirectorySync(entry, targetPath, deleteExistingDirectories, overwriteExistingFiles);
        } else {
            copyFileSync(entry, targetPath, overwriteExistingFiles);
        }
    }
}

/**
 * Special Folder/Paths
 */

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

/**
 * Args
 */

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

export function getTranspilationModeArg(): string | undefined {
    const argsKey = '--transpilationMode';
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

export function isMetaDevMode(): boolean {
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
