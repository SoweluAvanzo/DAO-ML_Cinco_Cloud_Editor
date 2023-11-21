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
export class CincoGLSPDiagramWidget extends GLSPDiagramWidget {
    static _cincoDiagramExtension?: string;
    @inject(ApplicationShell) protected shell: ApplicationShell;
    @inject(CincoGLSPDiagramContextKeyService) protected contextKeyService: CincoGLSPDiagramContextKeyService;

    protected override onActivateRequest(msg: Message): void {
        this.onActivateRequestConditional(msg, false);
    }

    protected onActivateRequestConditional(msg: Message, focus: boolean): void {
        const svgElement = this.node.querySelector(`#${this.viewerOptions.baseDiv} svg`) as HTMLElement;
        if (svgElement !== undefined) {
            if (focus) {
                svgElement.focus();
            }
            this.updateTabBarButtons();
        } else {
            const tabindex = this.node.getAttribute('tabindex');
            if (tabindex === undefined) {
                this.node.setAttribute('tabindex', '-1');
            }
            if (focus) {
                this.node.focus();
            }
        }
        this.updateGlobalSelection();
    }

    override listenToFocusState(shell: ApplicationShell): void {
        this.toDispose.push(
            shell.onDidChangeActiveWidget(event => {
                const focusedWidget = event.newValue;
                if (this.hasFocus && focusedWidget && !this.isThisWidget(focusedWidget)) {
                    // this.actionDispatcher.dispatch(FocusStateChangedAction.create(false));
                    // This line has been commented out to avoid the permanent retrieval of the focus by the editor
                } else if (!this.hasFocus && this.isThisWidget(focusedWidget)) {
                    // This line has been commented out to avoid the permanent retrieval of the focus by the editor
                    // this.actionDispatcher.dispatch(FocusStateChangedAction.create(true));
                }
            })
        );
    }

    protected override onResize(msg: Widget.ResizeMessage): void {
        super.onResize(msg);
        this.updateTabBarButtons(500); // after resize the gui needs some delay to update the tabBar
    }

    protected override onAfterShow(msg: Widget.ResizeMessage): void {
        super.onAfterShow(msg);
        this.updateTabBarButtons();
    }

    /**
     * Updating the tabBar. Theia and GLSP result in some race conditions. These need to be tackled by delay.
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
                this.reloadModel();
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
}
