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

import { getFileCodec, getFileExtension, getGraphModelOfFileType, GraphType } from '@cinco-glsp/cinco-glsp-common';
import { ActionDispatcher, MessageAction, SeverityLevel } from '@eclipse-glsp/server';
import { LanguageFilesRegistry } from './language-files-registry';
import { GraphModel } from '../model/graph-model';
import { DefaultFileCodecHandler, FileCodecHandler } from '../api/file-codec-handler';
import { ContextBundle } from '../api/context-bundle';

export class FileCodecManager {
    /**
     * Encodes a graphmodel into a textual file representation
     * @param sourceUri the uri of the model
     * @param model the model that needs to be textually encoded
     * @param contextBundle
     * @returns the textual content that represents the graphmodel
     */
    static encode(model: GraphModel, contextBundle: ContextBundle): string | undefined {
        return this.executeFileCodec((codecInstance: FileCodecHandler) => codecInstance.encode(model), model.getSpec(), contextBundle);
    }

    /**
     * Decodes the textual content of a file to a graphmodel representation
     * @param sourceUri the uri to the graphmodel
     * @param content the textual content that represents the graphmodel
     * @param contextBundle
     * @returns the graphmodel that represents the textual content
     */
    static decode(sourceUri: string, content: string, contextBundle: ContextBundle): GraphModel | undefined {
        // load hook class
        const fileExtension = getFileExtension(sourceUri);
        const graphModelSpec: GraphType | undefined = getGraphModelOfFileType(fileExtension);
        if (!graphModelSpec) {
            throw new Error('No associated graphmodel type found for file extension: ' + fileExtension);
        }
        return this.executeFileCodec<GraphModel>(
            (codecInstance: FileCodecHandler) => codecInstance.decode(content),
            graphModelSpec,
            contextBundle
        );
    }

    private static executeFileCodec<T>(
        procedure: (codecInstance: FileCodecHandler) => T | undefined,
        graphModelSpec: GraphType,
        contextBundle: ContextBundle
    ): T | undefined {
        let className: string | undefined;
        let codecClass: any | undefined;
        try {
            className = this.getFileCodecClass(graphModelSpec.elementTypeId);
            codecClass = this.loadAnnotatedClass(className);
        } catch (error: any) {
            contextBundle.logger.error(error);
        }
        try {
            const codecInstance = codecClass ? new codecClass(contextBundle) : new DefaultFileCodecHandler(contextBundle);
            return procedure(codecInstance);
        } catch (error: any) {
            contextBundle.logger.error(error);
            const errorMsg =
                'Errors execution CodecClass for Element of type "' +
                graphModelSpec?.elementTypeId +
                '" with Class "' +
                className +
                '" found.';
            this.notify(contextBundle.actionDispatcher, errorMsg, 'ERROR');
        }
        return undefined;
    }

    private static getFileCodecClass(elementTypeId: string): string {
        const fileCodecAnnotations = getFileCodec(elementTypeId);
        if (fileCodecAnnotations.length < 0) {
            throw new Error("Graphmodel of type '" + elementTypeId + "' has no FileCodec-Annotation!");
        }
        const fileCodecAnnotation = fileCodecAnnotations[0];
        if (fileCodecAnnotation.length <= 0) {
            throw new Error("FileCodec-Annotation of '" + elementTypeId + "' has no semantics specified!");
        }
        return fileCodecAnnotation[0];
    }

    private static loadAnnotatedClass(className: string): any {
        const classes = LanguageFilesRegistry.getRegisteredSync().filter((c: any) => c.name === className);
        return classes.length > 0 ? classes[0] : undefined;
    }

    /**
     *
     * @param message
     * @param severity "NONE" | "INFO" | "WARNING" | "ERROR" | "FATAL" | "OK"
     * @returns
     */
    static notify(actionDispatcher: ActionDispatcher, message: string, severity?: SeverityLevel, details?: string, timeout?: number): void {
        const messageAction = MessageAction.create(message, {
            severity: severity ?? 'INFO',
            details: details ?? ''
        });
        actionDispatcher.dispatch(messageAction);
    }
}
