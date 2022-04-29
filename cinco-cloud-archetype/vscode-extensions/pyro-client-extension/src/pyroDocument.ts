import * as vscode from 'vscode';
import { Disposable } from './dispose';

/**
 * Define the type of edits used in pyro files.
 */
export interface PyroDocumentDelegate {
	getFileData(): Promise<string>;
}

/**
 * Define the document (the data model) used for pyro files.
 */
export class PyroDocument extends Disposable implements vscode.CustomDocument {

	static async create(
		uri: vscode.Uri,
		backupId: string | undefined,
	): Promise<PyroDocument | PromiseLike<PyroDocument>> {
		// If we have a backup, read that. Otherwise read the resource from the workspace
		const dataFile = uri;
		const fileData = await PyroDocument.readFile(dataFile);
		return new PyroDocument(uri, fileData.getText(), fileData);
	}

	private static async readFile(uri: vscode.Uri): Promise<vscode.TextDocument> {
		return vscode.workspace.openTextDocument(uri);
	}

	private readonly _uri: vscode.Uri;

	private _documentData: string;

	private readonly _delegate: vscode.TextDocument;

	private constructor(
		uri: vscode.Uri,
		initialContent: string,
		delegate: vscode.TextDocument
	) {
		super();
		this._uri = uri;
		this._documentData = initialContent;
		this._delegate = delegate;
	}

	public get uri() { return this._uri; }

	public get documentData(): string { return this._documentData; }

	public get delegate(): vscode.TextDocument { return this._delegate; }
	

	private readonly _onDidDispose = this._register(new vscode.EventEmitter<void>());
	/**
	 * Fired when the document is disposed of.
	 */
	public readonly onDidDispose = this._onDidDispose.event;

	private readonly _onDidChangeDocument = this._register(new vscode.EventEmitter<{
		readonly content?: string;
	}>());
	/**
	 * Fired to notify webviews that the document has changed.
	 */
	public readonly onDidChangeContent = this._onDidChangeDocument.event;

	private readonly _onDidChange = this._register(new vscode.EventEmitter<{
		readonly label: string,
		undo(): void,
		redo(): void,
	}>());


	async makeEdit() {
		this._onDidChange.fire({
			label: 'Stroke',
			undo: async () => {
				console.log("undo");
			},
			redo: async () => {
				console.log("redo");
			}
		});
		
	}

	async backup(destination: vscode.Uri, cancellation: vscode.CancellationToken): Promise<vscode.CustomDocumentBackup> {
		await this.saveAs(destination, cancellation);

		return {
			id: destination.toString(),
			delete: async () => {
				try {
					await vscode.workspace.fs.delete(destination);
				} catch {
					// noop
				}
			}
		};
	}

	/**
	 * Fired to tell VS Code that an edit has occured in the document.
	 * 
	 * This updates the document's dirty indicator.
	 */
	public readonly onDidChange = this._onDidChange.event;

	/**
	 * Called by VS Code when there are no more references to the document.
	 * 
	 * This happens when all editors for it have been closed.
	 */
	dispose(): void {
		this._onDidDispose.fire();
		super.dispose();
	}

	
	

	/**
	 * Called by VS Code when the user saves the document to a new location.
	 */
	
	async saveAs(targetResource: vscode.Uri, cancellation: vscode.CancellationToken): Promise<void> {
		const delegate = this._delegate;
		if (cancellation.isCancellationRequested) {
			return;
		}
		await vscode.workspace.fs.copy(this._delegate.uri, targetResource,{overwrite:true});
	}
	
	/**
	 * Called by VS Code when the user calls `revert` on a document.
	 */
	async save(_cancellation: vscode.CancellationToken): Promise<void> {
		console.log("SAVED");
	}

	/**
	 * Called by VS Code when the user calls `revert` on a document.
	 */
	async revert(_cancellation: vscode.CancellationToken): Promise<void> {
		vscode.window.showErrorMessage("REVERT");
	}

	
}