/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud.
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

import {
    CommandAction,
    ElementType,
    getSpecOf,
    Point,
    ServerDialogAction,
    ServerDialogResponse,
    ServerOutputAction
} from '@cinco-glsp/cinco-glsp-common';
import {
    ActionDispatcher,
    Logger,
    LogLevel,
    SeverityLevel,
    MessageAction,
    SourceModelStorage,
    SaveModelAction,
    ModelSubmissionHandler,
    CreateNodeOperation,
    CreateEdgeOperation
} from '@eclipse-glsp/server';
import { RootPath } from './root-path';
import { Container, GraphModel, ModelElement, Node } from '../model/graph-model';
import { GraphModelState } from '../model/graph-model-state';
import { ServerResponseHandler } from '../tools/server-dialog-response-handler';
import * as fileHelper from '../utils/file-helper';
import { GraphModelStorage } from '../model/graph-storage';

export abstract class APIBaseHandler {
    protected readonly logger: Logger;
    readonly modelState: GraphModelState;
    protected readonly actionDispatcher: ActionDispatcher;
    protected sourceModelStorage: SourceModelStorage;
    protected submissionHandler: ModelSubmissionHandler;
    CHANNEL_NAME: string | undefined;

    constructor(
        // TODO-Sami: Bundle these Clases together
        logger: Logger,
        modelState: GraphModelState,
        actionDispatcher: ActionDispatcher,
        sourceModelStorage: SourceModelStorage,
        submissionHandler: ModelSubmissionHandler
    ) {
        this.logger = logger;
        this.modelState = modelState;
        this.actionDispatcher = actionDispatcher;
        this.sourceModelStorage = sourceModelStorage;
        this.submissionHandler = submissionHandler;
    }

    getElement(modelElementId: string): ModelElement {
        const element = this.modelState.index.findElement(modelElementId) as ModelElement;
        return element;
    }

    getSpecification(type: string): ElementType | undefined {
        return getSpecOf(type);
    }

    /**
     * The ChannelName where the logging will be provided, will be named in the following precedence:
     *
     * options.channelName ?? this.CHANNEL_NAME ?? this.logger.caller?.toString() ?? 'unnamed'
     *
     * @param message
     * @param options
     */
    async log(message: string, options?: { channelName?: string; show?: boolean; logLevel?: LogLevel }): Promise<void> {
        switch (options?.logLevel) {
            case LogLevel.debug:
                this.logger.debug(message);
                break;
            case LogLevel.error:
                this.logger.error(message);
                break;
            case LogLevel.info:
                this.logger.info(message);
                break;
            case LogLevel.warn:
                this.logger.warn(message);
                break;
            default:
                this.logger.info(message);
                break;
        }
        const channelName = options?.channelName ?? this.CHANNEL_NAME ?? this.logger.caller?.toString() ?? 'unnamed';
        const o = {
            show: options?.show ?? false,
            logLevel: options?.logLevel?.toString() ?? LogLevel.info.toString()
        };
        const serverOutputAction = ServerOutputAction.create(channelName, message, o);
        try {
            await this.actionDispatcher.dispatch(serverOutputAction);
        } catch (e) {
            console.log(e);
        }
    }

    /**
     * Execute the vscode/theia command from within the semantics of a language.
     * Can be used to dedicate semantics to a vscode extension.
     * @param commandId id of the theia/vscode command
     * @param args args to execute the command
     */
    executeCommand(commandId: string, args: any[]): Promise<void> {
        const commandAction = CommandAction.create(commandId, args);
        try {
            return this.actionDispatcher.dispatch(commandAction);
        } catch (e) {
            console.log(e);
            return new Promise<void>(resolve => resolve());
        }
    }

    debug(message: Error | string | undefined): void {
        this.log(this.parseMessage(message), { show: true, logLevel: LogLevel.debug });
    }

    info(message: Error | string | undefined): void {
        this.log(this.parseMessage(message), { show: true, logLevel: LogLevel.info });
    }

    warn(message: Error | string | undefined): void {
        this.log(this.parseMessage(message), { show: true, logLevel: LogLevel.warn });
    }

    error(message: Error | string | undefined): void {
        this.log(this.parseMessage(message), { show: true, logLevel: LogLevel.error });
    }

    private parseMessage(message: Error | string | undefined): string {
        if (message === undefined) {
            return '<undefined>';
        }
        if (typeof message == 'string') {
            return message === '' ? '<empty string>' : message;
        }
        if (typeof message == 'object' && message instanceof Error) {
            return message.stack ?? `${message.name}: ${message.message}`;
        }
        return message;
    }

    /**
     *
     * @param message
     * @param severity "NONE" | "INFO" | "WARNING" | "ERROR" | "FATAL" | "OK"
     * @returns
     */
    async notify(message: string, severity?: SeverityLevel, details?: string, timeout?: number): Promise<void> {
        const messageAction = MessageAction.create(message, {
            severity: severity ?? 'INFO',
            details: details ?? ''
        });
        try {
            await this.actionDispatcher.dispatch(messageAction);
        } catch (e) {
            console.log(e);
        }
    }

    dialog(title: string, message: string, args: any = {}): Promise<string> {
        return new Promise((resolve, reject) => {
            const callback: (response: any) => void = (response: ServerDialogResponse) => resolve(response.result);
            const messageId = ServerResponseHandler.registerResponseHandling(callback);
            const serverDialog = ServerDialogAction.create(messageId, title, message, { args });
            try {
                this.actionDispatcher.dispatch(serverDialog);
            } catch (e) {
                console.log(e);
            }
        });
    }

    saveModel(): Promise<void> {
        return new Promise<void>(resolve => {
            const result = this.sourceModelStorage.saveSourceModel(SaveModelAction.create({ fileUri: this.modelState.sourceUri }));
            if (result instanceof Promise) {
                result.then(_ => resolve());
            } else {
                resolve();
            }
        });
    }

    submitModel(): Promise<void> {
        return new Promise<void>(resolve => {
            const result = this.submissionHandler.submitModel();
            if (result instanceof Promise) {
                result.then(_ => resolve());
            } else {
                resolve();
            }
        });
    }

    createNode(type: string, location: Point, container?: Container | string): Promise<void> {
        const operation: CreateNodeOperation = {
            kind: CreateNodeOperation.KIND,
            elementTypeId: type,
            isOperation: true,
            containerId: typeof container == 'string' ? container : (container?.id ?? this.modelState.index.getRoot().id),
            location: location
        };
        return this.actionDispatcher.dispatch(operation);
    }

    createEdge(type: string, source: Node | string, target: Node | string): Promise<void> {
        const operation: CreateEdgeOperation = {
            kind: CreateEdgeOperation.KIND,
            elementTypeId: type,
            isOperation: true,
            sourceElementId: typeof source === 'string' ? source : source.id,
            targetElementId: typeof target === 'string' ? target : target.id
        };
        return this.actionDispatcher.dispatch(operation);
    }

    getParentDirectory(fileOrDirPath: string): string {
        return fileHelper.getParentDirectory(fileOrDirPath);
    }

    getDirectoryName(dirPath: string): string {
        return fileHelper.getDirectoryName(dirPath);
    }

    getFileName(filePath: string): string {
        return fileHelper.getFileName(filePath);
    }

    getFileExtension(filePath: string): string {
        return fileHelper.getFileExtension(filePath);
    }

    exists(relativePath: string, root = RootPath.WORKSPACE): boolean {
        const targetPath = root.join(relativePath);
        return fileHelper.existsSync(targetPath);
    }

    existsFile(relativePath: string, root = RootPath.WORKSPACE): boolean {
        const targetPath = root.join(relativePath);
        return fileHelper.existsFileSync(targetPath);
    }

    existsDirectory(relativePath: string, root = RootPath.WORKSPACE): boolean {
        const targetPath = root.join(relativePath);
        return fileHelper.existsDirectorySync(targetPath);
    }

    readFile(relativePath: string, root = RootPath.WORKSPACE, encoding: NodeJS.BufferEncoding = 'utf-8'): string | undefined {
        const targetPath = root.join(relativePath);
        return fileHelper.readFileSync(targetPath, encoding);
    }

    readModelFromFile(relativePath: string, root = RootPath.WORKSPACE): GraphModel | undefined {
        const targetPath = root.join(relativePath);
        return (this.sourceModelStorage as GraphModelStorage).readModelFromURI(targetPath);
    }

    readDirectory(relativePath: string, root = RootPath.WORKSPACE): string[] {
        const targetPath = root.join(relativePath);
        return fileHelper.readDirectorySync(targetPath);
    }

    deleteFile(relativePath: string, force = false): void {
        const targetPath = RootPath.WORKSPACE.join(relativePath);
        fileHelper.deleteFileSync(targetPath, force);
    }

    deleteDirectory(relativePath: string, recursive = true, force = false): void {
        const targetPath = RootPath.WORKSPACE.join(relativePath);
        fileHelper.deleteDirectorySync(targetPath, recursive, force);
    }

    createFile(relativePath: string, content: string, overwriteExistingFile = true, encoding = 'utf-8'): void {
        const targetPath = RootPath.WORKSPACE.join(relativePath);
        fileHelper.writeFileSync(targetPath, content, overwriteExistingFile, encoding);
    }

    createDirectory(relativePath: string, deleteExistingDirectory = false): void {
        const targetPath = RootPath.WORKSPACE.join(relativePath);
        fileHelper.createDirectorySync(targetPath, deleteExistingDirectory);
    }

    copyFile(relativeSourcePath: string, relativeTargetPath: string, overwriteExistingFile = true, sourceRoot = RootPath.WORKSPACE): void {
        const sourcePath = sourceRoot.join(relativeSourcePath);
        const targetPath = RootPath.WORKSPACE.join(relativeTargetPath);
        fileHelper.copyFileSync(sourcePath, targetPath, overwriteExistingFile);
    }

    copyDirectory(
        relativeSourcePath: string,
        relativeTargetPath: string,
        deleteExistingDirectories = false,
        overwriteExistingFiles = true,
        sourceRoot = RootPath.WORKSPACE
    ): void {
        const sourcePath = sourceRoot.join(relativeSourcePath);
        const targetPath = RootPath.WORKSPACE.join(relativeTargetPath);
        fileHelper.copyDirectorySync(sourcePath, targetPath, deleteExistingDirectories, overwriteExistingFiles);
    }
}
