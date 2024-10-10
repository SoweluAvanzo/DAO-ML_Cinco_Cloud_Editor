/********************************************************************************
 * Copyright (c) 2019-2022 EclipseSource and others.
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
import { Message } from '@phosphor/messaging/lib';
import { GLSPDiagramWidget } from '@eclipse-glsp/theia-integration';
import { ApplicationShell, Widget } from '@theia/core/lib/browser';
import { getGraphModelOfFileType } from '@cinco-glsp/cinco-glsp-common';
import { CincoGLSPDiagramContextKeyService } from './cinco-glsp-diagram-manager';
import { wait } from '@theia/core/lib/common/promise-util';
import { inject } from '@theia/core/shared/inversify';
import { DiagramLoader, ICopyPasteHandler, IDiagramOptions, TYPES } from '@eclipse-glsp/client';

export class CincoGLSPDiagramWidget extends GLSPDiagramWidget {
    static _cincoDiagramExtension?: string;
    @inject(ApplicationShell) protected shell: ApplicationShell;
    @inject(CincoGLSPDiagramContextKeyService) protected contextKeyService: CincoGLSPDiagramContextKeyService;
    protected _clientId: string | undefined;
    protected _diagramOptions: IDiagramOptions | undefined;

    protected override onActivateRequest(msg: Message): void {
        super.onActivateRequest(msg);
    }

    protected override onResize(msg: Widget.ResizeMessage): void {
        super.onResize(msg);
        this.updateTabBarButtons(500); // after resize the gui needs some delay to update the tabBar
    }

    protected override onAfterShow(msg: Widget.ResizeMessage): void {
        super.onAfterShow(msg);
        this.updateTabBarButtons();
    }

    protected override onAfterAttach(msg: Message): void {
        if (!this.diagramContainer) {
            // Create the container and initialize its content upon first attachment
            this.createContainer();
            this.initializeDiagram().then(_ => {
                this._clientId = super.clientId;
                this._diagramOptions = this.diagramOptions;
            });
        }
        super.onAfterAttach(msg);

        this.disposed.connect(() => {
            this.diContainer.unbindAll();
        });

        this.node.dataset['uri'] = this.uri.toString();
        if (this.diContainer.isBound(TYPES.ICopyPasteHandler)) {
            this.copyPasteHandler = this.diContainer.get<ICopyPasteHandler>(TYPES.ICopyPasteHandler);
            this.addClipboardListener(this.node, 'copy', e => this.handleCopy(e));
            this.addClipboardListener(this.node, 'paste', e => this.handlePaste(e));
            this.addClipboardListener(this.node, 'cut', e => this.handleCut(e));
        }
        this.node.addEventListener('mouseenter', e => this.handleMouseEnter(e));
        this.node.addEventListener('mouseleave', e => this.handleMouseLeave(e));
    }

    /**
     * Updating the tabBar. Theia and GLSP result in some race conditions (And still in GLSP 2.0).
     * These need to be tackled by delay.
     * @param delay
     */
    updateTabBarButtons(delay = 50): void {
        wait(delay).then(_ => {
            // 20ms wait to tackle a race-condition
            const fileUriString = this.uri.toString(true);
            this.cincoDiagramExtension = fileUriString.substring(fileUriString.lastIndexOf('.') + 1);
            this.contextKeyService.doUpdateStaticContextKeys(this);
            const tabBar = this.shell.getTabBarFor(this);
            if (tabBar) {
                tabBar.activate();
                tabBar.show();
                tabBar.update();
                this.update();
            }
        });
    }

    resetDiagramExtension(): void {
        this.cincoDiagramExtension = undefined;
    }

    set cincoDiagramExtension(cincoDiagramExtension: string | undefined) {
        CincoGLSPDiagramWidget._cincoDiagramExtension = cincoDiagramExtension;
    }

    get cincoDiagramExtension(): string | undefined {
        return CincoGLSPDiagramWidget._cincoDiagramExtension;
    }

    get cincoGraphModelType(): string | undefined {
        return getGraphModelOfFileType(this.cincoDiagramExtension ?? '')?.elementTypeId;
    }

    get diagramLoader(): DiagramLoader {
        return this.diContainer.get(DiagramLoader);
    }

    get diagramOptions(): IDiagramOptions {
        return this._diagramOptions ?? this.diContainer.get(TYPES.IDiagramOptions);
    }

    override get clientId(): string {
        return this._clientId ?? super.clientId;
    }
}
