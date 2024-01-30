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
            bodyDiv.classList.add('cinco-context-menu');
            this.bodyDiv = bodyDiv;
            this.containerElement.appendChild(bodyDiv);
        } else {
            bodyDiv = this.bodyDiv;
            for (const child of Array.from(this.bodyDiv.children)) {
                this.bodyDiv.removeChild(child);
            }
        }

        // set position
        bodyDiv.style.left = `${this.position.x}px`;
        bodyDiv.style.top = `${this.position.y}px`;

        // render children
        const menuContainer = document.createElement('div');
        menuContainer.className =
            'cinco-context-menu-container bg-white w-60 border border-gray-300 ' +
            'rounded-lg flex flex-col text-sm py-4 px-2 text-gray-500 shadow-lg';
        menuContainer.style.width = 'fit-content';
        bodyDiv.appendChild(menuContainer);
        const root = this.contextMenuService.root;
        this.createMenu(root, menuContainer);
        bodyDiv.hidden = false;
        this._hidden = false;
    }

    createMenu(structure: ContextMenu, parent: HTMLElement, createDivider = false): void {
        const submenues = structure.subMenues;
        const actions = structure.menuActions;

        if (actions) {
            for (const action of actions) {
                this.createMenuAction(action, parent);
            }
        }
        if (submenues) {
            let i = 0;
            for (const submenu of submenues) {
                i++;
                this.createMenu(submenu, parent, i < submenues.length);
            }
        }
        if (createDivider) {
            this.createDivider(parent);
        }
    }

    createMenuAction(action: MenuAction, parent: HTMLElement): void {
        const menuNode = document.createElement('div');
        menuNode.className = 'cinco-context-menu-item flex hover:bg-gray-100 py-1 px-2 rounded cursor-pointer';
        parent.appendChild(menuNode);

        const menuAction = document.createElement('div');
        menuAction.className = 'cinco-context-menu-item-action ml-4';
        menuAction.innerHTML = action.label;
        menuAction.id = action.commandId;
        addEventListener('cinco-context-menu-fired', e => {
            this.contextMenuService.handleContextMenuAction(action);
        });
        if (menuNode.onclick) {
            menuNode.onclick(new MouseEvent('cinco-context-menu-fired'));
        }
        menuNode.appendChild(menuAction);
    }

    createDivider(bodyDiv: HTMLElement): void {
        const divider = document.createElement('hr');
        divider.className = 'cinco-context-menu-divider my-3 border-gray-300';
        bodyDiv.appendChild(divider);
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

class ContextMenu {
    id: string;
    protected _subMenues: ContextMenu[] = [];
    protected _menuActions: MenuAction[] = [];

    constructor(id: string) {
        this.id = id;
    }

    get subMenues(): ContextMenu[] {
        return this._subMenues.reverse();
    }

    get menuActions(): MenuAction[] {
        return this._menuActions.reverse();
    }

    addSubMenu(submenu: ContextMenu): boolean {
        if (this._subMenues.filter(s => s.id === submenu.id).length <= 0) {
            this._subMenues.push(submenu);
            return true;
        }
        return false;
    }

    addAction(action: MenuAction): boolean {
        if (this._menuActions.filter(a => a.label === action.label && a.commandId === action.commandId).length <= 0) {
            this._menuActions.push(action);
            return true;
        }
        return false;
    }

    getSubMenu(id: string): ContextMenu | undefined {
        const result = this._subMenues.filter(e => e.id === id);
        return result.length > 0 ? result[0] : undefined;
    }

    getAction(commandId: string): MenuAction | undefined {
        const result = this._menuActions.filter(e => e.commandId === commandId);
        return result.length > 0 ? result[0] : undefined;
    }

    clear(): void {
        this._menuActions = [];
        this._subMenues = [];
    }
}

@injectable()
export class CincoContextMenuService implements IContextMenuService {
    @inject(CincoContextMenu) protected readonly contextMenu: CincoContextMenu;
    @inject(EnvironmentProvider) protected readonly environmentProvider: IEnvironmentProvider;
    protected timeout?: number;
    protected contextMenuStructure: ContextMenu = new ContextMenu('root');
    static CONTEXT_MENU = ['glsp-context-menu'];

    get root(): ContextMenu {
        return this.contextMenuStructure;
    }

    handleContextMenuAction(action: MenuAction): void {
        console.log('Menuaction triggered: ' + action.commandId);
    }

    show(items: MenuItem[], anchor: Anchor, onHide?: (() => void) | undefined): void {
        this.registerAction(CincoContextMenuService.CONTEXT_MENU, items);
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

    protected registerAction(menuPath: string[], items: MenuItem[]): void {
        this.contextMenuStructure.clear();
        for (const item of items) {
            const menuPathOfItem = item.group ? [...menuPath, item.group] : menuPath;
            this.registerContextMenu(menuPathOfItem, item, this.contextMenuStructure);
        }
    }

    protected registerContextMenu(menuPath: string[], item: MenuItem, parent: ContextMenu): void {
        if (menuPath.length > 0) {
            // exhaust menuPath stepwise -> add submenu
            const subMenuId = menuPath[0];
            const menuPathOfItem = menuPath.slice(1);

            let subContextMenu = new ContextMenu(subMenuId);
            const added = parent.addSubMenu(subContextMenu);
            subContextMenu = added ? subContextMenu : parent.getSubMenu(subMenuId)!;
            if (subContextMenu) {
                this.registerContextMenu(menuPathOfItem, item, subContextMenu);
            }
        } else {
            // menuPath exhausted -> add menuAction
            const menuAction: MenuAction = { label: item.label, order: item.sortString, commandId: this.commandId(menuPath, item) };
            parent.addAction(menuAction);
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
