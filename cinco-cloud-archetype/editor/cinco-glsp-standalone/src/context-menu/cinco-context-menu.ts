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

import { EnvironmentProvider, IEnvironmentProvider } from '@cinco-glsp/cinco-glsp-client';
import { Point } from '@cinco-glsp/cinco-glsp-common';
import { AbstractUIExtension, Anchor, IContextMenuService, MenuItem } from '@eclipse-glsp/client';
import { inject, injectable } from 'inversify';

@injectable()
export class CincoContextMenu extends AbstractUIExtension {
    protected contextMenuService: CincoContextMenuService;
    static readonly ID = 'cinco-context-menu';
    protected bodyDiv: HTMLElement;
    protected _position = { x: 0, y: 0 };
    protected _hidden = true;

    override id(): string {
        return CincoContextMenu.ID;
    }

    override containerClass(): string {
        return CincoContextMenu.ID;
    }

    set position(p: Point) {
        this._position = p;
    }

    get position(): Point {
        return this._position;
    }

    protected override initializeContents(containerElement: HTMLElement): void {
        containerElement.setAttribute('aria-label', 'Context-Menu');
        this.updateBody();
    }

    setService(contextMenuService: CincoContextMenuService): void {
        if (!this.contextMenuService) {
            this.contextMenuService = contextMenuService;
        }
    }

    protected updateBody(): void {
        let bodyDiv;
        if (!this.bodyDiv) {
            bodyDiv = document.createElement('div');
            bodyDiv.classList.add('context-body');
            this.bodyDiv = bodyDiv;
            this.containerElement.appendChild(bodyDiv);
        } else {
            bodyDiv = this.bodyDiv;
            for (const child of Array.from(this.bodyDiv.children)) {
                this.bodyDiv.removeChild(child);
            }
        }

        // render children
        if (this.contextMenuService.groups.size > 0) {
            const noResultsDiv = document.createElement('div');
            noResultsDiv.innerText = 'Has results found.';
            noResultsDiv.classList.add('context-menu-button');
            bodyDiv.appendChild(noResultsDiv);
        } else {
            const noResultsDiv = document.createElement('div');
            noResultsDiv.innerText = 'No results found.';
            noResultsDiv.classList.add('context-menu-button');
            bodyDiv.appendChild(noResultsDiv);
        }

        this._hidden = false;
    }

    initialized(): boolean {
        return this.bodyDiv !== undefined;
    }

    unhide(): void {
        if (this.bodyDiv) {
            window.setTimeout(() => {
                this.setContainerVisible(true);
                this.restoreFocus();
                this.updateBody();
                this.bodyDiv.hidden = false;
                this._hidden = false;
            }, 200);
        }
    }

    override hide(): void {
        if (this.bodyDiv) {
            super.hide();
            this.bodyDiv.hidden = true;
        }
    }

    isHidden(): boolean {
        return this._hidden;
    }
}

@injectable()
export class CincoContextMenuService implements IContextMenuService {
    @inject(CincoContextMenu) protected readonly contextMenu: CincoContextMenu;
    @inject(EnvironmentProvider) protected readonly environmentProvider: IEnvironmentProvider;
    protected timeout?: number;
    protected menuGroups: Map<string, Set<MenuAction>> = new Map(); // <(sub-)menu, action[]>
    protected submenus: Map<string, Set<string>> = new Map(); // <menu, submenues[]>
    static CONTEXT_MENU = ['glsp-context-menu'];

    get menues(): Map<string, Set<string>> {
        return this.submenus;
    }
    get groups(): Map<string, Set<MenuAction>> {
        return this.menuGroups;
    }

    show(items: MenuItem[], anchor: Anchor, onHide?: (() => void) | undefined): void {
        this.register(CincoContextMenuService.CONTEXT_MENU, items);
        const renderOptions = {
            menuPath: CincoContextMenuService.CONTEXT_MENU,
            anchor: anchor,
            onHide: () => {
                if (onHide) {
                    onHide();
                }
                this.scheduleCleanup();
            }
        };
        const resolvedOptions = this.resolve(renderOptions);
        this.doRender(resolvedOptions);
    }

    protected doRender({ menuPath, anchor, args, onHide, context }: RenderContextMenuOptions): void {
        this.contextMenu.setService(this);
        const contextMenu = this.createContextMenu(menuPath, args, context);
        const { x, y } = this.coordinateFromAnchor(anchor);
        if (onHide) {
            contextMenu.aboutToClose(() => onHide!());
        }
        contextMenu.open(x, y);
    }

    protected resolve(options: RenderContextMenuOptions): RenderContextMenuOptions {
        const args: any[] = options.args ? options.args.slice() : [];
        if (options.includeAnchorArg !== false) {
            args.push(options.anchor);
        }
        return {
            ...options,
            args
        };
    }

    protected register(menuPath: string[], items: MenuItem[]): void {
        for (const item of items) {
            if (item.children && item.children.length > 0) {
                // is a submenu entry
                const menuPathOfItem = item.group ? [...menuPath, item.group] : menuPath;
                this.registerSubmenu(menuPathOfItem, item);
                this.register([...menuPathOfItem, item.id], item.children);
            } else {
                this.registerMenuAction(menuPath, item);
            }
        }
    }

    protected registerSubmenu(menuPath: string[], item: MenuItem): void {
        for (let i = 0; i < menuPath.length - 1; i++) {
            if (!this.submenus.has(menuPath[i])) {
                this.submenus.set(menuPath[i], new Set());
            }
            const submenues = this.submenus.get(menuPath[i]);
            this.submenus.set(menuPath[i], submenues!.add(menuPath[i + 1]));
        }
    }

    protected registerMenuAction(menuPath: string[], item: MenuItem): void {
        const menuAction: MenuAction = { label: item.label, order: item.sortString, commandId: this.commandId(menuPath, item) };
        const menuPathOfItem = item.group ? [...menuPath, item.group] : menuPath;
        const menu = menuPathOfItem.join('_');
        if (!this.menuGroups.has(menu)) {
            this.menuGroups.set(menu, new Set());
        }
        const group = this.menuGroups.get(menu);
        if (group) {
            this.menuGroups.set(menu, group.add(menuAction));
        }
    }

    protected cleanUpNow(): void {
        window.clearTimeout(this.timeout);
        this.cleanUp();
    }

    protected scheduleCleanup(): void {
        this.timeout = window.setTimeout(() => {
            this.cleanUp();
        }, 200);
    }

    protected commandId(menuPath: string[], item: any): string {
        return menuPath.join('.') + '.' + item.id;
    }

    protected coordinateFromAnchor(anchor: Anchor): { x: number; y: number } {
        const { x, y } = anchor instanceof MouseEvent ? { x: anchor.clientX, y: anchor.clientY } : anchor;
        return { x, y };
    }

    protected cleanUp(): void {}

    protected createContextMenu(
        menuPath: string[],
        args: any[] | undefined,
        context: HTMLElement | undefined
    ): { open: (x: number, y: number) => void; aboutToClose: (cb: () => void) => void } {
        if (!this.contextMenu) {
            throw new Error('Function not implemented. Please provide rendering.');
        }
        return {
            open: (x: number, y: number) => {
                this.contextMenu.position = { x: x, y: y };
                if (this.contextMenu.initialized()) {
                    this.contextMenu.unhide();
                } else {
                    this.contextMenu.show(this.environmentProvider.getCurrentModel());
                    addEventListener('mousedown', e => {
                        if (!this.contextMenu.isHidden()) {
                            this.contextMenu.hide();
                        }
                    });
                }
            },
            aboutToClose: (cb: () => void) => {
                cb();
                this.contextMenu.hide();
            }
        };
    }
}

interface MenuAction {
    label: string;
    order: string | undefined;
    commandId: string;
}

export interface RenderContextMenuOptions {
    menuPath: string[];
    anchor: Anchor;
    args?: any[];
    /**
     * Whether the anchor should be passed as an argument to the handlers of commands for this context menu.
     * If true, the anchor will be appended to the list of arguments or passed as the only argument if no other
     * arguments are supplied.
     * Default is `true`.
     */
    includeAnchorArg?: boolean;
    /**
     * A DOM context to use when evaluating any `when` clauses
     * of menu items registered for this item.
     */
    context?: HTMLElement;
    onHide?: () => void;
}
