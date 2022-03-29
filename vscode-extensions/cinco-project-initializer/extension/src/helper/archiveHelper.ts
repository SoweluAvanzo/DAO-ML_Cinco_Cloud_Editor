import path = require('path');
import { workbenchOutput } from '../extension';
import { getFileExtension, getFileName, createFolder, remove } from './toolHelper';

const pre = "[extracting] "

export function unzip2(archivePath: string, targetPath: string) {
    var fs = require('original-fs');
    var unzip = require('unzipper');
    const process = require('process');
    process.noAsar = true;
    return new Promise<string> ( (resolve, reject) => {
        fs.createReadStream(archivePath)
            .pipe( unzip.Extract({ path: targetPath })).on('close', () => {
                process.noAsar = false;
                resolve(targetPath);
            }).on('error', (e: any) => {
                console.log(e);
                process.noAsar = false;
            });
    });
}

export function extract(archivePath: string, targetPath: string): Promise<string> {
    return new Promise<string> ( (resolve, reject) => {
        createFolder(targetPath).then((e: any) => {
            if(e && e.code !== 'EEXIST')
                reject(e);
            var fileName = getFileName(archivePath);
            var fileExtension = getFileExtension(fileName);
            var nextFileName = fileName.substr(0, fileName.lastIndexOf('.'));
            var nextFilePath = targetPath+path.sep+nextFileName;
            console.log(pre+"fileName: "+fileName);
            console.log(pre+"fileExtension: "+fileExtension);
            console.log(pre+"next file (?): "+nextFileName);
            console.log(pre+"next path (?): "+nextFilePath);
            switch(fileExtension) {
                case 'gz':
                    ungz(archivePath,nextFilePath)
                        .then( () => remove(archivePath)
                            .then( () => resolve(extract(nextFilePath, targetPath))));
                    break;
                case 'tar':
                    untar(archivePath, targetPath)
                        .then( () => remove(archivePath)
                            .then( () => resolve(extract(nextFilePath, targetPath))));
                    break;
                case 'zip':
                    unzip(archivePath, targetPath)
                        .then( () => remove(archivePath)
                            .then( () => resolve(extract(nextFilePath, targetPath))));
                    break;
                default:
                    if(targetPath+path.sep+fileName !== archivePath) {                 
                        workbenchOutput.appendLine("error! could not unpack: "+targetPath+path.sep+fileName);
                        reject("error");
                    }
                    resolve(targetPath);
                    break;
            }
        })
    });
}

function ungz(from: string, to: string) {
    workbenchOutput.appendLine(pre+"applying gunzip to *.gz...");

    const gunzip = require('gunzip-file')
    
    return new Promise<void>((resolve, _) => {
        gunzip(from, to, () => {
            workbenchOutput.appendLine(pre+'gunzip done: '+to);
            resolve();
        })
    });
}

function untar(from: string, to: string) {
    workbenchOutput.appendLine(pre+from);

    const fs = require('fs');
    var tar = require('tar-fs');
    const original_fs = require("original-fs"); // workaround for nested *.asar
    
    return new Promise<void>( (resolve, reject) => {
        // extracting a directory
        fs.createReadStream(from).pipe(tar.extract(to, {
            fs: original_fs,
            finish: function(e: any) {
                resolve();
            },
            map: function(header: any) {
                return header;
            }
        }));
    });
}

function unzip(from: string, to: string) {
    workbenchOutput.appendLine(pre+from);

    var AdmZip = require('adm-zip');

    return new Promise<void>((resolve, reject) => {
        var zip = new AdmZip(from);
        var zipEntries = zip.getEntries(); // an array of ZipEntry records
        zip.getEntries().forEach(function(entry: { entryName: any; }) {
            var entryName = entry.entryName;
            // callback: console.log(pre+entryName);
            zip.extractEntryTo(entryName, to, true, true);  
        });
        workbenchOutput.appendLine(pre+"finished!");
        resolve();
    });
}