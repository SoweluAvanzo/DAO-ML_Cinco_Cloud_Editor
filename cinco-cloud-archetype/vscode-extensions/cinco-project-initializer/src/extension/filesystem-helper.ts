import * as fs from 'fs'
import * as fsextras from 'fs-extra'

export function isDirectoryEmpty(path: string): boolean {
    const directory = fs.opendirSync(path);
    const workspaceIsEmpty = directory.readSync() === null;
    directory.close();
    return workspaceIsEmpty;
}

export function clearDirectorySync(path: string): boolean {
    try{ 
        fsextras.emptyDirSync(path);
        return true;
    }catch(error){
        return false;
    }

}
