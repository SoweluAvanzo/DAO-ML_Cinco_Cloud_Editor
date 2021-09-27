import path = require('path');
import * as vscode from 'vscode';
import { extensionContext, workbenchOutput } from '../extension';
import { copy } from '../helper/toolHelper';

const exampleFolder = "exampleFiles/";

export async function createExample(workspaceFsPath: string) {
    const pathToExampleFiles = extensionContext.asAbsolutePath(path.join(exampleFolder));
    workbenchOutput.appendLine("Creating example project to: "+workspaceFsPath);
    try {
        workspaceFsPath = vscode.Uri.parse(workspaceFsPath).fsPath;
    } catch(e) {
        workbenchOutput.appendLine("can't use vscode.Uri. It is probably not implemented correctly...");    
    }
    workbenchOutput.appendLine("CopyAll Files from: "+pathToExampleFiles+"\nto: "+workspaceFsPath);
    copy(pathToExampleFiles, workspaceFsPath, true).then(() => {
        workbenchOutput.appendLine("Example project successfully created.");
    }).catch(()=> {
        workbenchOutput.appendLine("Creation of example project failed.");
    });
}
