import * as fs from 'fs'

export function isDirectoryEmpty(path: string): boolean {
    const directory = fs.opendirSync(path);
    const workspaceIsEmpty = directory.readSync() === null;
    directory.close();
    return workspaceIsEmpty;
}
