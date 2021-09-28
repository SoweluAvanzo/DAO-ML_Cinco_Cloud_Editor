import * as vscode from 'vscode';
import { disposeAll } from './dispose';
import { PyroDocument } from './pyroDocument';
import { PYRO_HOST, PYRO_PORT, PYRO_SUBPATH } from './env_var';
import { PyroApi } from './pyroApi';
import { getExtension, getExtensionFrom, getFileNameFrom, isEmpty } from './fileNameUtils';
import { GraphModelFile } from './graphmodelFile';
import { stringify } from 'querystring';


const LOG_NAME = "PYRO-CLIENT";
export const outputChannel = vscode.window.createOutputChannel(LOG_NAME);

export class PyroEditorProvider extends PyroApi implements vscode.CustomEditorProvider<PyroDocument> {

	private static readonly viewType = 'scce.pyro';
	private static webviewOptions: vscode.WebviewOptions =  {
		enableScripts: true,
	};
	private readonly _onDidChangeCustomDocument = new vscode.EventEmitter<vscode.CustomDocumentEditEvent<PyroDocument>>();
	public readonly onDidChangeCustomDocument = this._onDidChangeCustomDocument.event;

	constructor(
		private readonly _context: vscode.ExtensionContext
	) {
		super();
		// TODO: fetch token
		this.TOKEN = 'asd';
		this.PROJECT_ID = 1;
		vscode.window.onDidChangeActiveTextEditor( (editor: vscode.TextEditor | undefined) => PyroEditorProvider.switchToPyroEditor(editor, this.TOKEN!));
		vscode.workspace.onWillDeleteFiles( (e) => e.waitUntil(this.removeModelReference(e)));
		_context.subscriptions.push(
			vscode.commands.registerCommand(
				"scce.pyro.createModel",
				(e) => this.contextCreateModelTypes(e)
			)
		);
	}

	public static register(context: vscode.ExtensionContext): vscode.Disposable {
		return vscode.window.registerCustomEditorProvider(
			PyroEditorProvider.viewType,
			new PyroEditorProvider(context),
			{
				// For this demo extension, we enable `retainContextWhenHidden` which keeps the 
				// webview alive even when it is not visible. You should avoid using this setting
				// unless is absolutely required as it does have memory overhead.
				webviewOptions: {
					//retainContextWhenHidden: true,
				},
				supportsMultipleEditorsPerDocument: false,
			});
	}

	static switchToPyroEditor(editor: vscode.TextEditor | undefined, token: string) {
		if(editor && editor.document && !editor.document.isClosed) {
			const document = editor.document;
			this.openDocumentInPyro(document, token);
		}
	}

	static async openDocumentInPyro(document: vscode.TextDocument, token: string) {
		const fileName = document.fileName;
		const fileComponents = fileName.split(".");
		if(fileComponents.length > 0) {
			const fileType = fileComponents[fileComponents.length - 1];
			const modelTypes = await this.getModelTypes(token);
			const fileTypes = Object.entries(modelTypes);

			for(const t of fileTypes) {
				// if fileType is a registered graphModel-fileType
				if(t[1] == fileType) {
					this.logging("Switching to Pyro-Editor: "+document.uri.fsPath);
					await vscode.commands.executeCommand('workbench.action.closeActiveEditor');
					await vscode.commands.executeCommand('vscode.openWith', document.uri, this.viewType);
					break;
				}
			}
		}
	}

	async isPyroCompatible(filePath: string, token: string) {
		const fileExtension = getExtensionFrom(filePath);
		if(fileExtension.length > 0) {
			const modelTypes = await PyroEditorProvider.getModelTypes(token);
			const fileTypes = Object.entries(modelTypes);
			for(const t of fileTypes) {
				if(t[1] == fileExtension) {
					return true;
				}
			}
		}
		return false;
	}

	async openCustomDocument(
		uri: vscode.Uri,
		openContext: { backupId?: string },
		_token: vscode.CancellationToken
	): Promise<PyroDocument> {
		this.logging("Trying to open as PYRO-DOCUMENT: "+uri.fsPath);

		const document: PyroDocument = await PyroDocument.create(uri, openContext.backupId);
		const listeners: vscode.Disposable[] = [];

		listeners.push(document.onDidChange(e => {
			// Tell VS Code that the document has been edited by the use.
			this._onDidChangeCustomDocument.fire({
				document,
				...e
			});
		}));
		document.onDidDispose(() => disposeAll(listeners));
		return document;
	}

	async resolveCustomEditor(
		document: PyroDocument,
		webviewPanel: vscode.WebviewPanel,
		_token: vscode.CancellationToken
	): Promise<void> {
		this.logging("Resolving PYRO-EDITOR: "+document.delegate.uri.fsPath);
		// only pyro compatible iff fileType of document is a registered one

		if(!await this.isPyroCompatible(document.delegate.fileName, this.TOKEN!)) {
			this.logging("Not compatible to Pyro: "+document.delegate.uri.fsPath);
			await vscode.commands.executeCommand('workbench.action.closeActiveEditor');
			await vscode.commands.executeCommand('vscode.openWith', document.delegate.uri, 'default');
			this.logging("openedWith passed: "+document.delegate.uri.fsPath);
			return;
		}
		this.logging("Compatible to Pyro: "+document.delegate.uri.fsPath);

		// read query-information
		const jsonDocument = await this.getDocumentAsJson(document);

		// Setup initial content for the webview
		const editorWebview = webviewPanel.webview;
		editorWebview.options = PyroEditorProvider.webviewOptions;
		editorWebview.html = this.getHtmlForWebview(jsonDocument);
		editorWebview.onDidReceiveMessage(e => this.onWebviewEditorMessage(document, e));
	}

	private async getDocumentAsJson(document: PyroDocument) : Promise<any> {
		const text = document.documentData;
		if (isEmpty(text)) {
			return this.createModelReference(document);
		} else {
			try {
				return JSON.parse(text);
			} catch {
				throw new Error("Could not parse modelReference!s");
			}
		}
	}

	private async createModelReference(document: PyroDocument) {
		const filename = getFileNameFrom(document.delegate.fileName);
		const extension = getExtension(filename);

		// new file, store model id
		const modelTypes = await PyroEditorProvider.getModelTypesOf(extension, this.TOKEN!);
		const modelType = modelTypes[0];
		const model = await PyroEditorProvider.createModel(filename, modelType, this.TOKEN!);
		const modelReference: GraphModelFile = {
			id: model.id,
			modelType: modelType,
			fileExtension: extension
		};
		this.updateTextDocument(document, modelReference);
		return modelReference;
	}

	private removeModelReference(e: vscode.FileWillDeleteEvent): Thenable<vscode.WorkspaceEdit> {
		const files = e.files;
		return new Promise((resolve, reject) => {
			for(const file of files) {
				this.isPyroCompatible(file.fsPath, this.TOKEN!).then( (compatible) => {
					if(compatible) {
						vscode.workspace.openTextDocument(file).then((document) => {
							const content = document.getText();
							const model: GraphModelFile = JSON.parse(content);
							PyroEditorProvider.removeModel(model.modelType, model.id, this.TOKEN!).then( () => {
								this.logging("Successfully removed graphModel:\n"+model);
							}).catch((e) => {
								this.logging("Failed to removed graphModel:\n"+model);
								this.logging("ERROR: "+e);
							});
							resolve(true);
						});
					} else {
						resolve(false);
					}
				});
			}
		}).then();
	}

	public async contextCreateModelTypes(e: any) {
		const path = "/"+e.fsPath;
		this.logging("path into: "+path);
		if(!path) {
			vscode.window.showErrorMessage("No path for file specified. Please right click on a folder or workspace to create a Graphmodel!");
			return;
		}
		PyroApi.getModelTypes(this.TOKEN!).then((types: Map<string, string>) => {
			const items: string[] = [];
			for (const entry of Object.entries(types)) {
				items.push(entry[1] + " ("+entry[0]+")");
			}
			vscode.window.showQuickPick(items, {
				title: "Model Type",
				placeHolder: "please pick a type...",
				canPickMany: false,
			}).then((value) => {
				const fileExtension = value?.split(" (")[0];
				vscode.window.showInputBox({
					title: "Model Name",
					placeHolder: "myModel (for myModel."+fileExtension+")",
					prompt: "Please type a name for your model."
				}).then( (name) => {
					if(name) {
						this.logging("creating: "+name+"."+fileExtension);
						const fileName = name+"."+fileExtension;
						const newFile = vscode.Uri.parse(path + "/" + fileName);
						vscode.workspace.fs.writeFile(newFile, new Uint8Array()).then( (v) => {
							vscode.workspace.openTextDocument(newFile).then(document => {
								const edit = new vscode.WorkspaceEdit();
								edit.insert(newFile, new vscode.Position(0, 0), "");
								return vscode.workspace.applyEdit(edit).then(success => {
									if (success) {
										vscode.window.showTextDocument(document);
									} else {
										vscode.window.showErrorMessage('Error, Could not create file!');
									}
								});
							});
						});
					}
				});
			});
		});
	}

	/**
	 * Write out the json to a given document.
	 */
	private async updateTextDocument(document: PyroDocument, json: any) {
		const uri = document.uri;
		// eslint-disable-next-line @typescript-eslint/no-var-requires
		const fs = require("fs");
		const data = JSON.stringify(json, null, 2);
		fs.writeFile(uri.fsPath, data, (err: any) => {
			if (err) this.logging(err);
			this.logging("Successfully Written to File - "+uri.fsPath);
		});
	}

	/**
	 * Get the static HTML used for in our editor's webviews.
	 */
	private getHtmlForWebview(jsonDocument: any): string {
		const modelId = jsonDocument.id.toString();
		const fileExtension = jsonDocument.fileExtension.toString();
		this.logging(`accessing: http://${PYRO_HOST}${PYRO_PORT ? ':' + PYRO_PORT : ""}${PYRO_SUBPATH}/#/editor/${modelId}?ext=${fileExtension}&token=${this.TOKEN}`);
		return `
		<!DOCTYPE html>
		<html>
			<head>
				<meta charset="utf-8"/>
				<title>Pyro Model Editor</title>
				<meta http-equiv="Content-Security-Policy" content="default-src *; font-src data:; img-src *; script-src *; script-src-elem * 'nonce-2726c7f26c'; style-src * 'unsafe-inline'; ">
			</head>
			<body style="padding: 0px;">
				<iframe id="pyro_editor" src='http://${PYRO_HOST}${PYRO_PORT ? ':' + PYRO_PORT : ""}${PYRO_SUBPATH}/#/editor/${modelId}?ext=${fileExtension}&token=${this.TOKEN}' title="Pyro editor iframe" style="position:absolute; width:100%; height:100%; border:none; margin:0; padding:0;">Not Supported</iframe>
				<script nonce="2726c7f26c">

					const vscode = acquireVsCodeApi();

					window.addEventListener('message', event => {
						const message = event.data; // The json data that the extension sent
						document.getElementById('pyro_editor').contentWindow.postMessage(message);
					});
					document.getElementById('pyro_editor').contentDocument.onmessage = function(m) {
						vscode.postMessage(m);
					}
				</script>
			</body>
		</html>
		`;	
	}

	/**
	 * CURRENTLY NOT NEEDED
	 */

	private onWebviewEditorMessage(document: PyroDocument, e: any) {
		this.logging(e);
		if(e.type=="changed") {
			document.makeEdit();
		}
	}

	backupCustomDocument(document: PyroDocument, context: vscode.CustomDocumentBackupContext, cancellation: vscode.CancellationToken): Thenable<vscode.CustomDocumentBackup> {
		return document.backup(context.destination, cancellation);
	}
	
	public saveCustomDocument(document: PyroDocument, cancellation: vscode.CancellationToken): Thenable<void> {
		return document.save(cancellation);
	}
	
	public saveCustomDocumentAs(document: PyroDocument, destination: vscode.Uri, cancellation: vscode.CancellationToken): Thenable<void> {
		return document.saveAs(destination, cancellation);
	}

	public revertCustomDocument(document: PyroDocument, cancellation: vscode.CancellationToken): Thenable<void> {
		return document.revert(cancellation);
	}

	public static logging(input: any) {
		const LOG = "["+LOG_NAME+"]";
		console.log(LOG+":"+input);
		outputChannel.appendLine(input);
	}

	public logging(input: any) {
		PyroEditorProvider.logging(input);
	}
}
