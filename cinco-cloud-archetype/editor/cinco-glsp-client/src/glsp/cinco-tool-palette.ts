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
    ToolPalette,
    ICommand,
    EnableDefaultToolsAction,
    SetUIExtensionVisibilityAction,
    SetContextActions,
    RequestContextActions,
    createIcon,
    changeCodiconClass,
    IActionDispatcher,
    TYPES,
    IDiagramOptions
} from '@eclipse-glsp/client';
import { KeyboardToolPalette } from '@eclipse-glsp/client/lib/features/accessibility/keyboard-tool-palette/keyboard-tool-palette';
import { Action, PaletteItem } from '@eclipse-glsp/protocol';
import { inject, injectable } from 'inversify';
import { CincoCustomTool, EnvironmentProvider, IEnvironmentProvider } from '../api/environment-provider';
import { GraphModelProvider } from '../model/graph-model-provider';
import { UPDATING_RACE_CONDITION_INDICATOR } from '@cinco-glsp/cinco-glsp-common';

// imported from: '@eclipse-glsp/client/lib/features/accessibility/keyboard-tool-palette/keyboard-tool-palette'
const PALETTE_ICON_ID = 'symbol-color';
const CHEVRON_DOWN_ICON_ID = 'chevron-right';
const PALETTE_HEIGHT = '500px';

@injectable()
export class CincoToolPalette extends KeyboardToolPalette {
    @inject(TYPES.IDiagramOptions)
    protected diagramOptions: IDiagramOptions;
    @inject(GraphModelProvider)
    protected readonly graphModelProvider: GraphModelProvider;
    @inject(EnvironmentProvider) readonly environmentProvider: IEnvironmentProvider;
    protected lastFilter = '';
    CHANGED = false;

    getHeadToolsId(): string {
        return 'cinco-tool-palette-header-' + this.diagramOptions.clientId;
    }

    static async requestPalette(actionDispatcher: IActionDispatcher): Promise<void> {
        const requestAction = RequestContextActions.create({
            contextId: CincoToolPalette.ID,
            editorContext: {
                selectedElementIds: []
            }
        });
        actionDispatcher.dispatch(requestAction);
    }

    override handle(action: Action): void | Action | ICommand {
        if (action.kind === EnableDefaultToolsAction.KIND) {
            if (this.lastActiveButton || this.defaultToolsButton) {
                this.changeActiveButton();
                this.restoreFocus();
            }
        } else if (SetContextActions.is(action)
                && action.actions.filter(s => (s as PaletteItem).id === UPDATING_RACE_CONDITION_INDICATOR.id).length <= 0
            ) {
            // TODO: SAMI - action.actions.length > 0 is wrong, but at somepoint actions is sent empty, find the reason why
            // store and backup new palette
            const newPaletteItems = action.actions.map(e => e as PaletteItem);
            if (this.palettesHaveChanged(this.paletteItems ?? [], newPaletteItems)) {
                this.CHANGED = true;
            }
            this.paletteItems = newPaletteItems;
            this.backupPaletteCopy();
            // make
            /*
            this.actionDispatcher.dispatch(
                SetUIExtensionVisibilityAction.create({
                    extensionId: ToolPalette.ID,
                    visible: !this.editorContext.isReadonly
                })
            );
            */
            // update palette view
            this.requestFilterUpdate(this.lastFilter);
            // update header tools
            const headerTools = document.getElementById(this.getHeadToolsId());
            if (headerTools) {
                this.updateHeaderTools(headerTools);
            }
        }
    }

    protected override requestFilterUpdate(filter: string): void {
        if (!this.containerElement) {
            // palette can not yet be updated
            this.initialize();
        }
        // cache last filter
        this.lastFilter = filter;
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
        if (this.CHANGED) {
            this.createBody();
            this.CHANGED = false;
        }
    }

    palettesHaveChanged(oldPaletteItems: PaletteItem[], newPaletteItems: PaletteItem[], depth = 0): boolean {
        let pairs = 0; // detects differences in the ids (too much means there are new, too few means some are gone/changed)
        for (const n of newPaletteItems) {
            for (const o of oldPaletteItems) {
                if (o.id === n.id) {
                    pairs++;
                    if (n.label !== o.label || o.children?.length !== n.children?.length || o.actions.length !== n.actions.length) {
                        return true;
                    } else {
                        if (this.palettesHaveChanged(o.children ?? [], n.children ?? [])) {
                            return true;
                        }
                    }
                }
            }
        }
        return pairs !== oldPaletteItems.length || oldPaletteItems.length !== newPaletteItems.length;
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
        headerTools.id = this.getHeadToolsId();
        headerTools.classList.add('header-tools');
        this.updateHeaderTools(headerTools);
        return headerTools;
    }

    protected updateHeaderTools(headerTools: HTMLElement): void {
        const currentModel = this.graphModelProvider.getGraphModelFrom(
            this.diagramOptions.sourceUri ?? ''
        );
        // fetch custom tools for model
        const tools = this.environmentProvider.provideTools(currentModel);

        headerTools.replaceChildren(...([] as (string | Node)[]));
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
