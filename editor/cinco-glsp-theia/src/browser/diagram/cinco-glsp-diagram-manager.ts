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
import { GLSPDiagramContextKeyService, GLSPDiagramManager } from '@eclipse-glsp/theia-integration';
import { GLSPDiagramLanguage } from '@eclipse-glsp/theia-integration/lib/common';
import { CommandService, URI } from '@theia/core';
import { inject, injectable } from 'inversify';

import { getDiagramConfiguration } from '../../common/cinco-language';
import { CincoGLSPDiagramWidget } from './cinco-glsp-diagram-widget';
import { WidgetOpenerOptions } from '@theia/core/lib/browser';
import { ContextKey } from '@theia/core/lib/browser/context-key-service';
import * as uuid from 'uuid';
import { DIAGRAM_TYPE } from '@cinco-glsp/cinco-glsp-common';

export class CincoGLSPDiagramContextKeyService extends GLSPDiagramContextKeyService {
    protected _cincoDiagramExtension: ContextKey<string>;
    get cincoDiagramExtension(): ContextKey<string> {
        return this._cincoDiagramExtension;
    }

    protected _cincoGraphModelType: ContextKey<string>;
    get cincoGraphModelType(): ContextKey<string> {
        return this._cincoGraphModelType;
    }

    protected override registerContextKeys(): void {
        super.registerContextKeys();
        this._cincoDiagramExtension = this.contextKeyService.createKey<string>('cincoDiagramExtension', undefined);
        this._cincoGraphModelType = this.contextKeyService.createKey<string>('cincoGraphModelType', undefined);
    }

    override doUpdateStaticContextKeys(glspDiagramWidget: CincoGLSPDiagramWidget): void {
        super.doUpdateStaticContextKeys(glspDiagramWidget);
        this.cincoDiagramExtension.set(glspDiagramWidget.cincoDiagramExtension);
        this.cincoGraphModelType.set(glspDiagramWidget.cincoGraphModelType);
    }

    protected override doResetStaticContextKeys(): void {
        super.doResetStaticContextKeys();
        this.cincoDiagramExtension.reset();
        this.cincoGraphModelType.reset();
    }
}

@injectable()
export class CincoGLSPDiagramMananger extends GLSPDiagramManager {
    @inject(GLSPDiagramContextKeyService)
    protected override readonly contextKeyService: CincoGLSPDiagramContextKeyService;
    @inject(CommandService) protected commandService: CommandService;
    private _diagramType?: string;
    private _label: string;
    private _fileExtensions: string[] = [];
    private _iconClass = 'codicon codicon-type-hierarchy-sub';

    public doConfigure(diagramLanguage: GLSPDiagramLanguage): void {
        // intial update of meta-modell
        this._fileExtensions = diagramLanguage.fileExtensions;
        this._diagramType = diagramLanguage.diagramType;
        this._label = diagramLanguage.label;
        this._iconClass = diagramLanguage.iconClass || this._iconClass;
    }

    override async doOpen(widget: CincoGLSPDiagramWidget, options?: WidgetOpenerOptions): Promise<void> {
        const fileUriString = widget.uri.toString(true);
        widget.cincoDiagramExtension = fileUriString.substring(fileUriString.lastIndexOf('.') + 1);
        super.doOpen(widget, options);
    }

    get contributionId(): string {
        return getDiagramConfiguration().contributionId;
    }

    get fileExtensions(): string[] {
        this._fileExtensions = getDiagramConfiguration().fileExtensions.map(e => (e.startsWith('.') ? e : '.' + e));
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

    get currentURI(): URI | undefined {
        const widgetEntries = this.widgetManager.getWidgets('cinco-diagram-diagram-manager');
        const widgets = [...widgetEntries.values()] as CincoGLSPDiagramWidget[];
        for (const widget of widgets) {
            if (widget.hasFocus) {
                return widget.uri;
            }
        }
        return undefined;
    }

    protected override createClientId(): string {
        return DIAGRAM_TYPE + '_' + uuid.v4();
    }
}
