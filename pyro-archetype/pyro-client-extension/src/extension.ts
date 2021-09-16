import * as vscode from 'vscode';
import { PyroEditorProvider } from './pyroEditor';

export function activate(context: vscode.ExtensionContext) {
	// Register our custom editor providers
	context.subscriptions.push(PyroEditorProvider.register(context));
}
