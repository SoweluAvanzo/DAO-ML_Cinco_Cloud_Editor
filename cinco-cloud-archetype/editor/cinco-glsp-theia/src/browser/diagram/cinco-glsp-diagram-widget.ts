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
import { DiagramWidgetOptions, GLSPDiagramWidget, GLSPWidgetOpenerOptions, TheiaGLSPConnector } from '@eclipse-glsp/theia-integration';
import { SelectionService } from '@theia/core';
import { ApplicationShell, StorageService } from '@theia/core/lib/browser';
import { Container } from '@theia/core/shared/inversify';

export class CincoGLSPDiagramWidget extends GLSPDiagramWidget {

    constructor(
        options: DiagramWidgetOptions & GLSPWidgetOpenerOptions,
        override readonly widgetId: string,
        override readonly diContainer: Container,
        override readonly editorPreferences: any,
        override readonly storage: StorageService,
        override readonly theiaSelectionService: SelectionService,
        override readonly connector: TheiaGLSPConnector
    ) {
        super(
            options,
            widgetId,
            diContainer,
            editorPreferences,
            storage,
            theiaSelectionService,
            connector
        );
    }

    protected override onActivateRequest(msg: Message): void {
        this.onActivateRequestConditional(msg, false);
    }

    protected onActivateRequestConditional(msg: Message, focus: boolean): void {
        const svgElement = this.node.querySelector(`#${this.viewerOptions.baseDiv} svg`) as HTMLElement;
        if (svgElement !== undefined) {
            if (focus) {
                svgElement.focus();
            }
        } else {
            const tabindex = this.node.getAttribute('tabindex');
            if (tabindex === undefined) { this.node.setAttribute('tabindex', '-1'); }
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
                    // This line has been commented out to avoid the permanent retrieval of the focus by the editor
                    // this.actionDispatcher.dispatch(FocusStateChangedAction.create(false));
                } else if (!this.hasFocus && this.isThisWidget(focusedWidget)) {
                    // This line has been commented out to avoid the permanent retrieval of the focus by the editor
                    // this.actionDispatcher.dispatch(FocusStateChangedAction.create(true));
                }
            })
        );
    }
}
