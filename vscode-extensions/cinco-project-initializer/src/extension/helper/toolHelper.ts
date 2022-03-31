import * as path from 'path'
import { workbenchOutput } from '../main'
import * as fs from 'fs'

export function execShellCommand(cmd: string, args: string[], workingDirectory?: string, onOut?: (process: any, response: string) => void, onEnd?: (process: any, response: string) => void) : Promise<any> {
    const childProcess = require('child_process');
    return new Promise<void>((resolve, reject) => {
        console.log("executing: " + cmd + " | with: " + args + " | at: "+workingDirectory);
        //console.log("PATH: "+process.env.PATH);
        var child = childProcess.spawn(cmd, args, { cwd: workingDirectory, env: process.env });
        var response: string = "";

        // use 'child.stdin.write()' and 'child.stdin.end() for input
        child.stdout.on('data', (buffer: any) => { 
            if(buffer.toString() !== "")
                console.log("["+cmd+"] received: "+buffer.toString());
            response += buffer.toString();
            if(onOut) {
                onOut(child, buffer.toString());
            } 
        });
        
        // use 'child.stdin.write()' and 'child.stdin.end() for input
        child.stdout.on('end', (_: any) => { 
            if(onEnd) {
                onEnd(child, response);
            }
        });
        
        child.stderr.on('data', (e: string) => { 
            console.log(e.toString());
        });

        child.on('error', (e: string) => {
            console.log("Process ["+cmd+"] closed with error ["+e+"]");
            reject(e.toString());
            throw e;
        })

        child.on('close', (code: string | number) => {
            if(code !== 0) {
                console.log("Process ["+cmd+"] closed with ["+code+"]");
                reject(code);
            } else
                resolve();
        });
    });
}

export function whichExecShellCommand(cmd: string, args: string[], workingDirectory?: string, onOut?: (process: any, response: string) => void, onEnd?: (process: any, response: string) => void) : Promise<any> {
    const childProcess = require('child_process');
    var which = require('which');
    return new Promise<void>((resolve, reject) => {
        console.log("executing: " + cmd + " | with: " + args + " | at: "+workingDirectory);
        //console.log("PATH: "+process.env.PATH);
        which(cmd, (_error: any, path: any)  => {
            if(_error) {
                console.log("[ERROR]:\n"+_error);
                reject(_error);
                return;
            }
            var child = childProcess.spawn(path, args, { cwd: workingDirectory, env: process.env });
            var response: string = "";

            // use 'child.stdin.write()' and 'child.stdin.end() for input
            child.stdout.on('data', (buffer: any) => { 
                if(buffer.toString() !== "")
                    console.log("["+cmd+"] received: "+buffer.toString());
                response += buffer.toString();
                if(onOut) {
                    onOut(child, buffer.toString());
                } 
            });
            
            // use 'child.stdin.write()' and 'child.stdin.end() for input
            child.stdout.on('end', (_: any) => { 
                if(onEnd) {
                    onEnd(child, response);
                }
            });
            
            child.stderr.on('data', (e: string) => { 
                console.log(e);
            });

            child.on('error', (e: string) => {
                console.log("Process ["+cmd+"] closed with error ["+e+"]");
                reject(e);
                throw e;
            })

            child.on('close', (code: string | number) => {
                if(code !== 0) {
                    console.log("Process ["+cmd+"] closed with ["+code+"]");
                    reject(code);
                } else
                    resolve();
            });
        });
    });
}

export function download(targetPath: string, url: string, fileName: string ) {
    const request = require('request');
    const progress = require('request-progress');
    const pre = '[downloading] ';
    workbenchOutput.appendLine(pre+url);
    
    return new Promise<void>((resolve, reject) => {
        createFolder(targetPath).then( () => {
            const file = fs.createWriteStream(targetPath+path.sep+fileName);
            var stream = progress( request( url ), {
                throttle: 500
            }).on( 'progress', function ( state: { percent: number; } ) {
                //workbenchOutput.appendLine( pre + '' + ( Math.round( state.percent * 100 ) ) + "%" );
            }).on( 'error', function ( err: string ) {
                workbenchOutput.appendLine( 'error : ' + err );
                workbenchOutput.show();
                reject(err);
            }).on( 'end', function () {
                workbenchOutput.appendLine( pre + '100% Download Completed' );
            }).pipe( file );
            stream.on('finish', () => resolve());
        })
    });
}

export function createFolder(folderPath: string) {
    return new Promise<void>((resolve, reject) => {
            var e = fs.mkdirSync(folderPath, { recursive: true });
            resolve();  
        }
    );
}

export function remove(fileSystemPath: string) {
    return new Promise<void>((resolve, reject) => {
        const rimraf = require("rimraf");
        rimraf(fileSystemPath, fs, (err: any) => {
            if(err) {
                workbenchOutput.appendLine("error: "+err);
                reject(err);
            } else {
                console.log("removed from fileSystem: "+fileSystemPath);
                resolve();
            }
        })
    });
}

export function getFileName(filePath: string) {
    return filePath.substring(filePath.lastIndexOf(path.sep) + 1)
}

export function getFileLocation(filePath: string) {
    return filePath.substring(0, filePath.lastIndexOf(path.sep))
}

export function getFileExtension(filePath: string) {
    if(filePath.indexOf('.') > -1)
        return filePath.substr(filePath.lastIndexOf('.') + 1);
    return null;
}

export function getFiles(folderPath: string) : string[] {
    return fs.readdirSync(folderPath);
}

export function copyAll(fromAbs: string, toAbs: string, intoFolder?: boolean, excludeREs?: string[]) {
    // is copying not needed?
    if(fromAbs === toAbs)
        return new Promise<void>( (resolve, _) => resolve());
    // dependency check
    var copyfiles = require('copyfiles');
    workbenchOutput.appendLine("using require(copyfiles)");
    
    // resolve paths
    var name = path.parse(fromAbs).name;
    var to = path.relative(fromAbs, toAbs) + (intoFolder? "" : path.sep + name);
    var toCopyString = "." + path.sep + "**" + path.sep + "*"; // "copy all"-string
    var priorCwd = process.cwd();   // cache currentWorkingDirectory

    workbenchOutput.appendLine("information parsed...");
    return new Promise<void>((resolve, reject) => {
        process.chdir(fromAbs); // set directory to copy from to currentWorkingDirectory
        var toCopy= [ toCopyString, to];
        if(!excludeREs || excludeREs === null)
            excludeREs = [];
        var options = { 
            all: true, 
            error: true, 
            verbose: false, 
            follow: true,
            exclude: excludeREs
        };
        workbenchOutput.appendLine("copying files...");
        copyfiles(toCopy, options, (message: any) => {
            console.log(message);
            process.chdir(priorCwd);    // return to prior workingDirectory
            workbenchOutput.appendLine("copied files!");
            resolve();
        })
    });
}

export function copy(from: string, to: string, intoFolder?: boolean) {
    return new Promise<void>((resolve, reject) => {
        workbenchOutput.appendLine("using fs");
        try {
            copyFolderRecursiveSync(fs, from, to, intoFolder);
        } catch(e) {
            reject(e);
        }
        resolve();
    });
}

function copyFolderRecursiveSync(fs: any, source: string, target: string, intoFolder?: boolean) {
    if(target === source) {
        return;
    }
    workbenchOutput.appendLine("copyFolderRecursiveSync");
    var files = [];
    //check if folder needs to be created or integrated
    
    if(intoFolder) {
        workbenchOutput.appendLine("intoFolder");
        var targetFolder = target;
        //copy
        if ( fs.lstatSync( source ).isDirectory() ) {
            workbenchOutput.appendLine("readdirSync");
            files = fs.readdirSync( source );
            files.forEach( function ( file: string ) {
                workbenchOutput.appendLine("file: "+file);
                var curSource = path.join( source, file );
                workbenchOutput.appendLine("path: "+curSource);
                if ( fs.lstatSync( curSource ).isDirectory() ) {
                    copyFolderRecursiveSync(fs, curSource, targetFolder);
                } else {
                    copyFileSync(fs, curSource, targetFolder );
                }
            } );
        }
    } else {
        var targetFolder = path.join( target, path.basename( source ) );
        workbenchOutput.appendLine("creatingFolder: "+targetFolder);
        createFolder(targetFolder).then( () => {
            workbenchOutput.appendLine("created: "+targetFolder);
            //copy
            if ( fs.lstatSync( source ).isDirectory() ) {
                workbenchOutput.appendLine("readdirSync");
                files = fs.readdirSync( source );
                files.forEach( function ( file: string ) {
                    workbenchOutput.appendLine("file: "+file);
                    var curSource = path.join( source, file );
                    workbenchOutput.appendLine("path: "+curSource);
                    if ( fs.lstatSync( curSource ).isDirectory() ) {
                        copyFolderRecursiveSync(fs, curSource, targetFolder);
                    } else {
                        copyFileSync(fs, curSource, targetFolder );
                    }
                } );
            }
        });
    }

}

export function copyFileSyncFS(source: string, target: string) {
    return copyFileSync(fs, source, target);
}

function copyFileSync(fs: any, source: string, target: string) {
    var targetFile = target;
    workbenchOutput.appendLine("copyFileSync");

    //if target is a directory a new file with the same name will be created (overwrite)
    if ( fs.existsSync( target ) ) {
        if ( fs.lstatSync( target ).isDirectory() ) {
            targetFile = path.join( target, path.basename( source ) );
        }
    }

    fs.writeFileSync(targetFile, fs.readFileSync(source));
}

export function checkFilePath(target:string) {
    return fs.existsSync(target);
}

export async function createWriteToFile(targetPath: string, fileName: string, content: string) {
    await createFolder(targetPath);
    return new Promise<string>((resolve, reject) => {
        var targetFile = path.join(targetPath, fileName);
        fs.writeFileSync(targetFile, content);
        resolve(targetFile);
    });
}

export function renameFolder(sourcePathAbs: string, targetPathAbs: string) {
    return new Promise<string|void>( (resolve, reject) => {
        fs.rename(sourcePathAbs, targetPathAbs, (err: any) => {
            if(err)
                reject(err);
            resolve();
        });
    });
}

/**
 * Cleans Windows-created executable-files/scripts for
 * executing on linux. (Windows has different file endings,
 * rendering it unexecutable for linux)
 * @param filePath file shall be cleaned
 */
export function cleanDosFileForLinux(filePath: string) {
    console.log("*dos2unix*"); 
    var dos2unix = require('ssp-dos2unix-js').dos2unix;
    var result = dos2unix(filePath, {feedback: true, writable: true});
}
