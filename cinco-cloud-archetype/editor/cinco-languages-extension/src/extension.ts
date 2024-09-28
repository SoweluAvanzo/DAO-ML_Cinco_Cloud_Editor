/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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
import * as vscode from 'vscode';
import * as path from 'path';
import { LanguageClient, LanguageClientOptions, ServerOptions, TransportKind } from 'vscode-languageclient/node';
import { LanguageJobMode, languageHandlingAction } from './mgl/cli';
import { error } from 'console';

let mglClient: LanguageClient;
let mslClient: LanguageClient;

// This function is called when the extension is activated.
export function activate(context: vscode.ExtensionContext): void {
    // Start language servers to make languages available to use
    mglClient = startMGLClient(context);
    mslClient = startMSLClient(context);

    // Add command to allow triggering generation
    context.subscriptions.push(
        vscode.commands.registerCommand('cincoCloud.generateCincoProduct', () => {
            // Retrieve active editor and check if its an MGL
            const activeEditor = vscode.window.activeTextEditor;
            const filePath = activeEditor?.document.uri.fsPath;
            if (filePath && filePath.endsWith('mgl')) {
                languageHandlingAction(filePath, {}, LanguageJobMode.GENERATE);
            } else {
                error('Please open a MGL to generate a meta-specification!');
            }
        })
    );

    // Add command to allow triggering generation and included upload
    context.subscriptions.push(
        vscode.commands.registerCommand('cincoCloud.uploadCincoProduct', () => {
            // Retrieve active editor and check if its an MGL
            const activeEditor = vscode.window.activeTextEditor;
            const filePath = activeEditor?.document.uri.fsPath;
            if (filePath && filePath.endsWith('mgl')) {
                languageHandlingAction(filePath, {}, LanguageJobMode.UPLOAD);
            } else {
                error('Please open a MGL to generate a meta-specification!');
            }
        })
    );
}

// This function is called when the extension is deactivated.
export function deactivate(): Thenable<void> | undefined {
    if (mglClient) {
        return mglClient.stop();
    }
    if (mslClient) {
        return mslClient.stop();
    }
    return undefined;
}

function startMGLClient(context: vscode.ExtensionContext): LanguageClient {
    const serverModule = context.asAbsolutePath(path.join('out', 'mgl', 'language-server', 'main'));
    // The debug options for the server
    // --inspect=6009: runs the server in Node's Inspector mode so VS Code can attach to the server for debugging.
    // By setting `process.env.DEBUG_BREAK` to a truthy value, the language server will wait until a debugger is attached.
    const debugOptions = {
        execArgv: ['--nolazy', `--inspect${process.env.DEBUG_BREAK ? '-brk' : ''}=${process.env.DEBUG_SOCKET || '6009'}`]
    };

    // If the extension is launched in debug mode then the debug server options are used
    // Otherwise the run options are used
    const serverOptions: ServerOptions = {
        run: { module: serverModule, transport: TransportKind.ipc },
        debug: { module: serverModule, transport: TransportKind.ipc, options: debugOptions }
    };

    const fileSystemWatcher = vscode.workspace.createFileSystemWatcher('**/*.mgl');
    context.subscriptions.push(fileSystemWatcher);

    // Options to control the language client
    const clientOptions: LanguageClientOptions = {
        documentSelector: [{ scheme: 'file', language: 'mgl' }],
        synchronize: {
            // Notify the server about file changes to files contained in the workspace
            fileEvents: fileSystemWatcher
        }
    };

    // Create the language client and start the client.
    const client = new LanguageClient('mgl', 'MGL', serverOptions, clientOptions);

    // Start the client. This will also launch the server
    client.start();
    return client;
}

function startMSLClient(context: vscode.ExtensionContext): LanguageClient {
    const serverModule = context.asAbsolutePath(path.join('out', 'msl', 'language-server', 'main'));
    // The debug options for the server
    // --inspect=6009: runs the server in Node's Inspector mode so VS Code can attach to the server for debugging.
    // By setting `process.env.DEBUG_BREAK` to a truthy value, the language server will wait until a debugger is attached.
    const debugOptions = {
        execArgv: ['--nolazy', `--inspect${process.env.DEBUG_BREAK ? '-brk' : ''}=${process.env.DEBUG_SOCKET || '6010'}`]
    };

    // If the extension is launched in debug mode then the debug server options are used
    // Otherwise the run options are used
    const serverOptions: ServerOptions = {
        run: { module: serverModule, transport: TransportKind.ipc },
        debug: { module: serverModule, transport: TransportKind.ipc, options: debugOptions }
    };

    const fileSystemWatcher = vscode.workspace.createFileSystemWatcher('**/*.style');
    context.subscriptions.push(fileSystemWatcher);

    // Options to control the language client
    const clientOptions: LanguageClientOptions = {
        documentSelector: [{ scheme: 'file', language: 'msl' }],
        synchronize: {
            // Notify the server about file changes to files contained in the workspace
            fileEvents: fileSystemWatcher
        }
    };

    // Create the language client and start the client.
    const client = new LanguageClient('msl', 'MSL', serverOptions, clientOptions);

    // Start the client. This will also launch the server
    client.start();
    return client;
}
