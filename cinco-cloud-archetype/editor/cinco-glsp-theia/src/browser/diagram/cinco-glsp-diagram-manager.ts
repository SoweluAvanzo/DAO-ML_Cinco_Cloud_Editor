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
import { META_FILE_TYPES } from '@cinco-glsp/cinco-glsp-common/lib/meta-resource';
import { CompositionSpecification, MetaSpecification } from '@cinco-glsp/cinco-glsp-common/lib/meta-specification';
import { DiagramWidget, DiagramWidgetOptions, GLSPDiagramManager } from '@eclipse-glsp/theia-integration';
import { GLSPDiagramLanguage } from '@eclipse-glsp/theia-integration/lib/common';
import { CommandService } from '@theia/core';
import { inject, injectable } from 'inversify';

import { getDiagramConfiguration } from '../../common/cinco-language';
import { FileProviderResponse } from '../theia-registration/file-provider';
import { ARGS_PROVIDER_ID } from '@cinco-glsp/cinco-glsp-common';
import * as path from 'path';
import { CincoGLSPDiagramWidget } from './cinco-glsp-diagram-widget';
import { configureServerActions } from '@eclipse-glsp/client';

@injectable()
export class CincoGLSPDiagramMananger extends GLSPDiagramManager {
    @inject(CommandService) protected commandService: CommandService;
    private _diagramType?: string;
    private _label: string;
    private _fileExtensions: string[] = [];
    private _iconClass = 'codicon codicon-type-hierarchy-sub';

    protected override async initialize(): Promise<void> {
        if (this._diagramType) {
            return super.initialize();
        }
    }

    public doConfigure(diagramLanguage: GLSPDiagramLanguage): void {
        // intial update of meta-modell
        this._fileExtensions = diagramLanguage.fileExtensions;
        this._diagramType = diagramLanguage.diagramType;
        this._label = diagramLanguage.label;
        this._iconClass = diagramLanguage.iconClass || this._iconClass;
        this.initialize();
    }

    public loadUpdates(): void {
        this.commandService.executeCommand(ARGS_PROVIDER_ID).then((server_args: any) => {
            const rootFolder = path.resolve(server_args.rootFolder);
            const languageFolder = path.join(rootFolder, server_args.languagePath);
            (
                this.commandService.executeCommand('fileProviderHandler', {
                    directories: [`${languageFolder}`],
                    readFiles: true,
                    filter: META_FILE_TYPES // only read supported files
                }) as Promise<FileProviderResponse>
            ).then((response: FileProviderResponse) => {
                // merge all meta-specification files
                response.items.forEach(item => {
                    try {
                        const metaSpecification = JSON.parse(item.content ?? '{}');
                        if (CompositionSpecification.is(metaSpecification)) {
                            MetaSpecification.merge(metaSpecification);
                        }
                    } catch (err) {
                        console.error(err);
                    }
                });
            });
        });
    }

    get contributionId(): string {
        return getDiagramConfiguration().contributionId;
    }

    get fileExtensions(): string[] {
        this._fileExtensions = getDiagramConfiguration().fileExtensions;
        return this._fileExtensions;
    }

    get diagramType(): string {
        this._diagramType = getDiagramConfiguration().diagramType;
        if (!this._diagramType) {
            throw new Error('No diagramType has been set for this ConfigurableGLSPDiagramManager');
        }
        return this._diagramType;
    }

    get label(): string {
        this._label = getDiagramConfiguration().label;
        return this._label;
    }

    override get iconClass(): string {
        this._iconClass = getDiagramConfiguration().iconClass ?? this._iconClass;
        return this._iconClass;
    }

    override async createWidget(options?: any): Promise<DiagramWidget> {
        if (DiagramWidgetOptions.is(options)) {
            const clientId = this.createClientId();
            const widgetId = this.createWidgetId(options);
            const config = this.getDiagramConfiguration(options);
            const diContainer = config.createContainer(clientId);
            const initializeResult = await this.diagramConnector.initializeResult;
            await configureServerActions(initializeResult, this.diagramType, diContainer);
            const widget = new CincoGLSPDiagramWidget(
                options,
                widgetId,
                diContainer,
                this.editorPreferences,
                this.storage,
                this.theiaSelectionService,
                this.diagramConnector
            );
            widget.listenToFocusState(this.shell);
            return widget;
        }
        throw Error('DiagramWidgetFactory needs DiagramWidgetOptions but got ' + JSON.stringify(options));
    }
}
