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
import { EnableToolPaletteAction, ToolPalette } from '@eclipse-glsp/client';
import { Action, PaletteItem, RequestContextActions, SetContextActions } from '@eclipse-glsp/protocol';
import { injectable } from 'inversify';
import { EnableDefaultToolsAction, ICommand, SetUIExtensionVisibilityAction } from 'sprotty';

@injectable()
export class DynamicToolPalette extends ToolPalette {
    protected lastFilter = '';

    override handle(action: Action): ICommand | Action | void {
        if (action.kind === EnableToolPaletteAction.KIND) {
            const requestAction = RequestContextActions.create({
                contextId: DynamicToolPalette.ID,
                editorContext: {
                    selectedElementIds: []
                }
            });
            this.actionDispatcher.requestUntil(requestAction).then(response => {
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
                    this.requestFilterUpdate(this.lastFilter);
                }
            });
        } else if (action.kind === EnableDefaultToolsAction.KIND) {
            this.changeActiveButton();
            this.restoreFocus();
        }
    }

    protected override requestFilterUpdate(filter: string): void {
        if (!this.containerElement) {
            // palette can not yet be updated
            return;
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
        this.createBody();
    }

    /**
     * Create a backup copy. Needed to restore the palette after a search.
     */
    backupPaletteCopy(): void {
        // create a deep copy
        this.paletteItemsCopy = JSON.parse(JSON.stringify(this.paletteItems));
    }
}
