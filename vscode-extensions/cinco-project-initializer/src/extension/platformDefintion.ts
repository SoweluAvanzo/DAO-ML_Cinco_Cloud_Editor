import * as path from 'path'
import { workbenchOutput } from './main'
import { execShellCommand, whichExecShellCommand } from './helper/toolHelper'

const windowsLink: string = 'win32-x64-archive';
const linuxLink: string = 'linux-x64';
const osxLink: string = 'darwin';
export const linuxExtractedFolder = "VSCode-linux-x64";
export const windows: string = "windows";
export const linux: string = "linux";
export const osx: string = "macOS";

export const supportedPlatforms = [
    windows, linux, osx
]

export const javaVersion = ">=1.11";
export const nodeVersion = ["12", "15"];
export const npmVersion = ["6", "7"];

export const vscodeBaseUrl: string = "https://update.code.visualstudio.com";
export const vscodeVersion: string = "1.55.0";
export const vscodeRelease: string = "stable";

export function isSupportedPlattform(platform: string) {
    return supportedPlatforms.indexOf(platform) > -1;
}

export function getArchiveTypeOf(platform: string) {
    switch(platform) {
        case linux:
            return ".tar.gz";
        case windows:
            return ".zip";
        case osx:
            return ".zip";
        default:
            return null;
    }
}

export function getCurrentPlatform() {
    var os = process.platform
    switch(os) {
        case "win32":
            return windows;
        case "linux":
            return linux;
        case "darwin":
            return osx;
        default:
            throw new Error("Platform not supported");
    }
}

function getLinkName(platform: string) {
    switch(platform) {
        case windows:
            return windowsLink;
        case linux:
            return linuxLink;
        case osx:
            return osxLink;
    }
}

export function getDownloadUrlOf(platform: string) {
    return vscodeBaseUrl+"/"+vscodeVersion+"/"+getLinkName(platform)+"/"+vscodeRelease;
}

export function getDownloadFileNameOf(platform: string) { 
    return "vscode-"+vscodeVersion+"-"+vscodeRelease+"-"+platform;
}

export function getExtensionFolder(platform: string) {
    return getDataFolder(platform) + path.sep + "extensions";
}

export function getDataFolder(platform: string) {
    switch(platform) {
        case linux:
            return path.sep + "data";
        case windows:
            return path.sep + "data";
        case osx:
            return path.sep + "code-portable-data";
    }
}

export function mapConfigPlatform(platform: string) {
    switch(platform) {
        case 'Linux':
            return linux;
        case "Windows":
            return windows;
        case "macOs":
            return osx;
        default:
            throw new Error("platform unknown: "+platform);
    }
}


/**
 * CONDITION
 */

export async function checkNode() {
    return await checkVersion("node", nodeVersion);
}
export async function checkNpm() {
    return true; // temporary turned off: await checkWhichVersion("npm", npmVersion);
}

export async function checkVersion(program: string, versions: string[], workingDirectory?: string) {
    try{
        var currentVersion = "";
        await execShellCommand(program, ["--version"], workingDirectory, (_, response) => {
            try {
                currentVersion = response.replace(/\s+/g, "");
                console.log(response);
            } catch(e) {
                workbenchOutput.appendLine("[Log] error finding "+program+" version.");
            }
        });
        if(!currentVersion)
            return false;
        
        currentVersion = currentVersion.split('.')[0].replace(/[a-zA-Z]+/g, "");
        var neededMin = versions[0].split('.')[0];
        var neededMax = versions[1].split('.')[0];
        if(!currentVersion || !(neededMin <= currentVersion && currentVersion < neededMax)) {
            return false;
        }
        return true;
    } catch(e) {
        return false;
    } 
}

export async function checkWhichVersion(program: string, versions: string[], workingDirectory?: string) {
    try{
        var currentVersion = "";
        await whichExecShellCommand(program, ["--version"], workingDirectory, (_, response) => {
            try {
                currentVersion = response.replace(/\s+/g, "");
                console.log(response);
            } catch(e) {
                workbenchOutput.appendLine("[Log] error finding "+program+" version.");
            }
        });
        if(!currentVersion)
            return false;
        
        currentVersion = currentVersion.split('.')[0].replace(/[a-zA-Z]+/g, "");
        var neededMin = versions[0].split('.')[0];
        var neededMax = versions[1].split('.')[0];
        if(!currentVersion || !(neededMin <= currentVersion && currentVersion < neededMax)) {
            return false;
        }
        return true;
    } catch(e) {
        return false;
    } 
}
