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
    EnableToolPaletteAction,
    ToolPalette,
    ICommand,
    EnableDefaultToolsAction,
    SetUIExtensionVisibilityAction,
    SetContextActions,
    RequestContextActions,
    Ranked,
    createIcon,
    changeCodiconClass
} from '@eclipse-glsp/client';
import { KeyboardToolPalette } from '@eclipse-glsp/client/lib/features/accessibility/keyboard-tool-palette/keyboard-tool-palette';
import { Action, PaletteItem } from '@eclipse-glsp/protocol';
import { inject, injectable } from 'inversify';
import { CINCO_STARTUP_RANK } from '@cinco-glsp/cinco-glsp-common';
import { CincoCustomTool, EnvironmentProvider, IEnvironmentProvider } from '../api/environment-provider';

// imported from: '@eclipse-glsp/client/lib/features/accessibility/keyboard-tool-palette/keyboard-tool-palette'
const PALETTE_ICON_ID = 'symbol-color';
const CHEVRON_DOWN_ICON_ID = 'chevron-right';
const PALETTE_HEIGHT = '500px';

@injectable()
export class CincoToolPalette extends KeyboardToolPalette implements Ranked {
    @inject(EnvironmentProvider) readonly environmentProvider: IEnvironmentProvider;
    static _rank: number = CINCO_STARTUP_RANK - 1; // needs to be before CincoPreparationsStartup
    rank: number = CincoToolPalette._rank;
    protected lastFilter = '';

    override async preRequestModel(): Promise<void> {}

    async postRequestModel?(): Promise<void> {
        const requestAction = RequestContextActions.create({
            contextId: ToolPalette.ID,
            editorContext: {
                selectedElementIds: []
            }
        });
        const response = await this.actionDispatcher.request<SetContextActions>(requestAction);
        this.paletteItems = response.actions.map(e => e as PaletteItem);
        if (!this.editorContext.isReadonly) {
            this.show(this.editorContext.modelRoot);
        }
    }

    async requestPalette(requestFilterUpdate = true): Promise<void> {
        const requestAction = RequestContextActions.create({
            contextId: CincoToolPalette.ID,
            editorContext: {
                selectedElementIds: []
            }
        });
        const response = await this.actionDispatcher.request(requestAction);
        if (SetContextActions.is(response)) {
            // store and backup new palette
            this.paletteItems = response.actions.map(e => e as PaletteItem);
            this.backupPaletteCopy();
            // make
            this.actionDispatcher.dispatch(
                SetUIExtensionVisibilityAction.create({
                    extensionId: ToolPalette.ID,
                    visible: !this.editorContext.isReadonly
                })
            );
            // update palette view
            if (requestFilterUpdate) {
                this.requestFilterUpdate(this.lastFilter);
            }
        }
    }

    override handle(action: Action): ICommand | Action | void {
        if (action.kind === EnableToolPaletteAction.KIND) {
            this.requestPalette();
        } else if (action.kind === EnableDefaultToolsAction.KIND) {
            if (this.lastActiveButton || this.defaultToolsButton) {
                this.changeActiveButton();
                this.restoreFocus();
            }
        }
    }

    protected override requestFilterUpdate(filter: string): void {
        if (!this.containerElement) {
            // palette can not yet be updated
            return;
        }
        // cache last filter
        this.lastFilter = filter;

        this.requestPalette(false).then(_ => {
            // Reset the paletteItems before searching
            this.paletteItems = JSON.parse(JSON.stringify(this.paletteItemsCopy));
            // Filter the entries
            const filteredPaletteItems: PaletteItem[] = [];
            for (const itemGroup of this.paletteItems) {
                if (itemGroup.children) {
                    // Fetch the labels according to the filter
                    const matchingChildren = itemGroup.children.filter(child => child.label.toLowerCase().includes(filter.toLowerCase()));
                    // Add the itemgroup containing the correct entries
                    if (matchingChildren.length > 0) {
                        // Clear existing children
                        itemGroup.children.splice(0, itemGroup.children.length);
                        // Push the matching children
                        matchingChildren.forEach(child => itemGroup.children!.push(child));
                        filteredPaletteItems.push(itemGroup);
                    }
                }
            }
            this.paletteItems = filteredPaletteItems;
            this.createBody();
        });
    }

    /**
     * Create a backup copy. Needed to restore the palette after a search.
     */
    backupPaletteCopy(): void {
        // create a deep copy
        this.paletteItemsCopy = JSON.parse(JSON.stringify(this.paletteItems));
    }

    protected override addMinimizePaletteButton(): void {
        const baseDiv = document.getElementById(this.options.baseDiv);
        const minPaletteDiv = document.createElement('div');
        minPaletteDiv.classList.add('minimize-palette-button');
        this.containerElement.classList.add('collapsible-palette');
        if (baseDiv) {
            const insertedDiv = baseDiv.insertBefore(minPaletteDiv, baseDiv.firstChild);
            this.updateMinimizePaletteButtonTooltip(minPaletteDiv);
            const minimizeIcon = createIcon(CHEVRON_DOWN_ICON_ID);
            minimizeIcon.onclick = _event => {
                this.setPalette(minPaletteDiv, minimizeIcon);
            };
            insertedDiv.appendChild(minimizeIcon);
            this.setPalette(minPaletteDiv, minimizeIcon, true); // workaround for missing scrollbar at start
        }
    }

    setPalette(minPaletteDiv: HTMLDivElement, minimizeIcon: Element, setOpen = false): void {
        if (!setOpen && this.isPaletteMaximized()) {
            this.containerElement.style.overflow = 'hidden';
            this.containerElement.style.maxHeight = '0px';
        } else {
            this.containerElement.style.overflow = 'scroll'; // fix to scroll
            this.containerElement.style.maxHeight = PALETTE_HEIGHT;
        }
        this.updateMinimizePaletteButtonTooltip(minPaletteDiv);
        changeCodiconClass(minimizeIcon, PALETTE_ICON_ID);
        changeCodiconClass(minimizeIcon, CHEVRON_DOWN_ICON_ID);
    }

    protected override createHeaderTools(): HTMLElement {
        this.headerToolsButtonMapping.clear();

        const headerTools = document.createElement('div');
        headerTools.classList.add('header-tools');

        // fetch custom tools
        const tools = this.environmentProvider.provideTools();

        let index = 0;
        for (const tool of tools) {
            if (tool.id === '_default') {
                this.defaultToolsButton = this.createDefaultToolButton();
                this.headerToolsButtonMapping.set(0, this.defaultToolsButton);
                headerTools.appendChild(this.defaultToolsButton);
            } else if (tool.id === '_delete') {
                this.deleteToolButton = this.createMouseDeleteToolButton();
                this.headerToolsButtonMapping.set(1, this.deleteToolButton);
                headerTools.appendChild(this.deleteToolButton);
            } else if (tool.id === '_marquee') {
                this.marqueeToolButton = this.createMarqueeToolButton();
                this.headerToolsButtonMapping.set(2, this.marqueeToolButton);
                headerTools.appendChild(this.marqueeToolButton);
            } else if (tool.id === '_validate') {
                this.validateToolButton = this.createValidateButton();
                this.headerToolsButtonMapping.set(3, this.validateToolButton);
                headerTools.appendChild(this.validateToolButton);
            } else if (tool.id === '_search') {
                this.searchToolButton = this.createSearchButton();
                this.headerToolsButtonMapping.set(4, this.searchToolButton);
                headerTools.appendChild(this.searchToolButton);
            } else if (CincoCustomTool.is(tool)) {
                const customToolButton = this.createCustomTool(tool);
                this.headerToolsButtonMapping.set(index, customToolButton);
                headerTools.appendChild(customToolButton);
            }
            index++;
        }

        return headerTools;
    }

    protected createCustomTool(tool: CincoCustomTool): HTMLElement {
        const toolButton = createIcon(tool.codicon ?? 'beaker');
        toolButton.id = tool.id;
        toolButton.title = tool.title;
        toolButton.onclick = tool.action ?? ((_: any) => console.log('Triggered: ' + tool.id));
        if (tool.shortcut && tool.shortcut.length > 0) {
            toolButton.appendChild(this.createKeyboardShotcut(tool.shortcut[0]));
        }
        return toolButton;
    }
}
