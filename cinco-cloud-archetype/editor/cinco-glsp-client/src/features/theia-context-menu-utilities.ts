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

import { MenuItem } from '@eclipse-glsp/client';
import { CommandRegistry as PhosphorCommandRegistry } from '@phosphor/commands';
import { Menu as MenuWidget } from '@phosphor/widgets';
import { Command, CommandRegistry, CompoundMenuNodeRole, Disposable, DisposableCollection, MenuAction, MenuPath } from '@theia/core';
import { inject, injectable } from 'inversify';
import { IActionDispatcher, IContextMenuService } from 'sprotty';

import {
    AlternativeHandlerMenuNode,
    CommandMenuNode,
    CompositeMenuNode,
    CompositeMenuNodeWrapper,
    CompoundMenuNode,
    MenuCommandExecutor,
    MenuNode,
    MutableCompoundMenuNode,
    SubMenuOptions
} from '@theia/core';

export class ActionMenuNode implements MenuNode, CommandMenuNode, Partial<AlternativeHandlerMenuNode> {
    readonly altNode: ActionMenuNode | undefined;

    constructor(protected readonly action: MenuAction) {
        if (action.alt) {
            this.altNode = new ActionMenuNode({ commandId: action.alt });
        }
    }

    get command(): string {
        return this.action.commandId;
    }

    get when(): string | undefined {
        return this.action.when;
    }

    get id(): string {
        return this.action.commandId;
    }

    get label(): string {
        if (this.action.label) {
            return this.action.label;
        }
        /* const cmd = this.commands.getCommand(this.action.commandId);
        if (!cmd) {
            console.debug(`No label for action menu node: No command "${this.action.commandId}" exists.`);
            return '';
        }
        return cmd.label || cmd.id;
        */
        return 'TODO: cmd.label || cmd.id';
    }

    get icon(): string | undefined {
        if (this.action.icon) {
            return this.action.icon;
        }
        // const command = this.commands.getCommand(this.action.commandId);
        // return command && command.iconClass;
        return 'TODO: command && command.iconClass';
    }

    get sortString(): string {
        return this.action.order || this.label;
    }
}

/**
 * The MenuModelRegistry allows to register and unregister menus, submenus and actions
 * via strings and {@link MenuAction}s without the need to access the underlying UI
 * representation.
 */
@injectable()
export class MenuModelRegistry {
    protected readonly root = new CompositeMenuNode('');
    protected readonly independentSubmenus = new Map<string, MutableCompoundMenuNode>();

    constructor() {}

    onStart(): void {}

    /**
     * Adds the given menu action to the menu denoted by the given path.
     *
     * @returns a disposable which, when called, will remove the menu action again.
     */
    registerMenuAction(menuPath: MenuPath, item: MenuAction): Disposable {
        const menuNode = new ActionMenuNode(item);
        return this.registerMenuNode(menuPath, menuNode);
    }

    /**
     * Adds the given menu node to the menu denoted by the given path.
     *
     * @returns a disposable which, when called, will remove the menu node again.
     */
    registerMenuNode(menuPath: MenuPath | string, menuNode: MenuNode, group?: string): Disposable {
        const parent = this.getMenuNode(menuPath, group);
        return parent.addNode(menuNode);
    }

    getMenuNode(menuPath: MenuPath | string, group?: string): MutableCompoundMenuNode {
        if (typeof menuPath === 'string') {
            const target = this.independentSubmenus.get(menuPath);
            if (!target) {
                throw new Error(`Could not find submenu with id ${menuPath}`);
            }
            if (group) {
                return this.findSubMenu(target, group);
            }
            return target;
        } else {
            return this.findGroup(group ? menuPath.concat(group) : menuPath);
        }
    }

    /**
     * Register a new menu at the given path with the given label.
     * (If the menu already exists without a label, iconClass or order this method can be used to set them.)
     *
     * @param menuPath the path for which a new submenu shall be registered.
     * @param label the label to be used for the new submenu.
     * @param options optionally allows to set an icon class and specify the order of the new menu.
     *
     * @returns if the menu was successfully created a disposable will be returned which,
     * when called, will remove the menu again. If the menu already existed a no-op disposable
     * will be returned.
     *
     * Note that if the menu already existed and was registered with a different label an error
     * will be thrown.
     */
    registerSubmenu(menuPath: MenuPath, label: string, options?: SubMenuOptions): Disposable {
        if (menuPath.length === 0) {
            throw new Error('The sub menu path cannot be empty.');
        }
        const index = menuPath.length - 1;
        const menuId = menuPath[index];
        const groupPath = index === 0 ? [] : menuPath.slice(0, index);
        const parent = this.findGroup(groupPath, options);
        let groupNode = this.findSubMenu(parent, menuId, options);
        if (!groupNode) {
            groupNode = new CompositeMenuNode(menuId, label, options, parent);
            return parent.addNode(groupNode);
        } else {
            groupNode.updateOptions({ ...options, label });
            return Disposable.NULL;
        }
    }

    registerIndependentSubmenu(id: string, label: string, options?: SubMenuOptions): Disposable {
        if (this.independentSubmenus.has(id)) {
            console.debug(`Independent submenu with path ${id} registered, but given ID already exists.`);
        }
        this.independentSubmenus.set(id, new CompositeMenuNode(id, label, options));
        return { dispose: () => this.independentSubmenus.delete(id) };
    }

    linkSubmenu(parentPath: MenuPath | string, childId: string | MenuPath, options?: SubMenuOptions, group?: string): Disposable {
        const child = this.getMenuNode(childId);
        const parent = this.getMenuNode(parentPath, group);
        const wrapper = new CompositeMenuNodeWrapper(child, parent, options);
        return parent.addNode(wrapper);
    }

    /**
     * Unregister all menu nodes with the same id as the given menu action.
     *
     * @param item the item whose id will be used.
     * @param menuPath if specified only nodes within the path will be unregistered.
     */
    unregisterMenuAction(item: MenuAction, menuPath?: MenuPath): void;
    /**
     * Unregister all menu nodes with the same id as the given command.
     *
     * @param command the command whose id will be used.
     * @param menuPath if specified only nodes within the path will be unregistered.
     */
    unregisterMenuAction(command: Command, menuPath?: MenuPath): void;
    /**
     * Unregister all menu nodes with the given id.
     *
     * @param id the id which shall be removed.
     * @param menuPath if specified only nodes within the path will be unregistered.
     */
    unregisterMenuAction(id: string, menuPath?: MenuPath): void;
    unregisterMenuAction(itemOrCommandOrId: MenuAction | Command | string, menuPath?: MenuPath): void {
        const id = MenuAction.is(itemOrCommandOrId)
            ? itemOrCommandOrId.commandId
            : Command.is(itemOrCommandOrId)
            ? itemOrCommandOrId.id
            : itemOrCommandOrId;

        if (menuPath) {
            const parent = this.findGroup(menuPath);
            parent.removeNode(id);
            return;
        }

        this.unregisterMenuNode(id);
    }

    /**
     * Recurse all menus, removing any menus matching the `id`.
     *
     * @param id technical identifier of the `MenuNode`.
     */
    unregisterMenuNode(id: string): void {
        const recurse = (root: MutableCompoundMenuNode) => {
            root.children.forEach(node => {
                if (CompoundMenuNode.isMutable(node)) {
                    node.removeNode(id);
                    recurse(node);
                }
            });
        };
        recurse(this.root);
    }

    /**
     * Finds a submenu as a descendant of the `root` node.
     * See {@link MenuModelRegistry.findSubMenu findSubMenu}.
     */
    protected findGroup(menuPath: MenuPath, options?: SubMenuOptions): MutableCompoundMenuNode {
        let currentMenu: MutableCompoundMenuNode = this.root;
        for (const segment of menuPath) {
            currentMenu = this.findSubMenu(currentMenu, segment, options);
        }
        return currentMenu;
    }

    /**
     * Finds or creates a submenu as an immediate child of `current`.
     * @throws if a node with the given `menuId` exists but is not a {@link MutableCompoundMenuNode}.
     */
    protected findSubMenu(current: MutableCompoundMenuNode, menuId: string, options?: SubMenuOptions): MutableCompoundMenuNode {
        const sub = current.children.find(e => e.id === menuId);
        if (CompoundMenuNode.isMutable(sub)) {
            return sub;
        }
        if (sub) {
            throw new Error(`'${menuId}' is not a menu group.`);
        }
        const newSub = new CompositeMenuNode(menuId, undefined, options, current);
        current.addNode(newSub);
        return newSub;
    }

    /**
     * Returns the menu at the given path.
     *
     * @param menuPath the path specifying the menu to return. If not given the empty path will be used.
     *
     * @returns the root menu when `menuPath` is empty. If `menuPath` is not empty the specified menu is
     * returned if it exists, otherwise an error is thrown.
     */
    getMenu(menuPath: MenuPath = []): MutableCompoundMenuNode {
        return this.findGroup(menuPath);
    }

    /**
     * Returns the {@link MenuPath path} at which a given menu node can be accessed from this registry, if it can be determined.
     * Returns `undefined` if the `parent` of any node in the chain is unknown.
     */
    getPath(node: MenuNode): MenuPath | undefined {
        const identifiers = [];
        const visited: MenuNode[] = [];
        let next: MenuNode | undefined = node;

        while (next && !visited.includes(next)) {
            if (next === this.root) {
                return identifiers.reverse();
            }
            visited.push(next);
            identifiers.push(next.id);
            next = next.parent;
        }
        return undefined;
    }
}

export interface ContextMatcher extends Disposable {
    /**
     * Whether the expression is satisfied. If `context` provided, the service will attempt to retrieve a context object associated with that element.
     */
    match(expression: string, context?: HTMLElement): boolean;
}

export interface Coordinate {
    x: number;
    y: number;
}
export const Coordinate = Symbol('Coordinate');

export type Anchor = MouseEvent | Coordinate;

export function toAnchor(anchor: HTMLElement | Coordinate): Anchor {
    return anchor instanceof HTMLElement ? { x: anchor.offsetLeft, y: anchor.offsetTop } : anchor;
}

export function coordinateFromAnchor(anchor: Anchor): Coordinate {
    const { x, y } = anchor instanceof MouseEvent ? { x: anchor.clientX, y: anchor.clientY } : anchor;
    return { x, y };
}

export abstract class ContextMenuAccess implements Disposable {
    protected readonly toDispose = new DisposableCollection();
    readonly onDispose = this.toDispose.onDispose;

    constructor(toClose: Disposable) {
        this.toDispose.push(toClose);
    }

    get disposed(): boolean {
        return this.toDispose.disposed;
    }

    dispose(): void {
        this.toDispose.dispose();
    }
}

@injectable()
export abstract class ContextMenuRenderer {
    protected _current: ContextMenuAccess | undefined;
    protected readonly toDisposeOnSetCurrent = new DisposableCollection();
    /**
     * Currently opened context menu.
     * Rendering a new context menu will close the current.
     */
    get current(): ContextMenuAccess | undefined {
        return this._current;
    }
    protected setCurrent(current: ContextMenuAccess | undefined): void {
        if (this._current === current) {
            return;
        }
        this.toDisposeOnSetCurrent.dispose();
        this._current = current;
        if (current) {
            this.toDisposeOnSetCurrent.push(
                current.onDispose(() => {
                    this._current = undefined;
                })
            );
            this.toDisposeOnSetCurrent.push(current);
        }
    }

    render(options: RenderContextMenuOptions): ContextMenuAccess {
        const resolvedOptions = this.resolve(options);
        const access = this.doRender(resolvedOptions);
        this.setCurrent(access);
        return access;
    }

    protected abstract doRender(options: RenderContextMenuOptions): ContextMenuAccess;

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
}

export interface RenderContextMenuOptions {
    menuPath: MenuPath;
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
    contextKeyService?: ContextMatcher;
    onHide?: () => void;
}

export const isWindows = is('Windows', 'win32');
export const isOSX = is('Mac', 'darwin');

export type CMD = [string, string[]];
export function cmd(command: string, ...args: string[]): CMD {
    return [isWindows ? 'cmd' : command, isWindows ? ['/c', command, ...args] : args];
}

function is(userAgent: string, platform: string): boolean {
    if (typeof navigator !== 'undefined') {
        if (navigator.userAgent && navigator.userAgent.indexOf(userAgent) >= 0) {
            return true;
        }
    }
    if (typeof process !== 'undefined') {
        return process.platform === platform;
    }
    return false;
}

export namespace OS {
    /**
     * Enumeration of the supported operating systems.
     */
    export enum Type {
        Windows = 'Windows',
        Linux = 'Linux',
        OSX = 'OSX'
    }

    /**
     * Returns with the type of the operating system. If it is neither [Windows](isWindows) nor [OS X](isOSX), then
     * it always return with the `Linux` OS type.
     */
    export function type(): OS.Type {
        if (isWindows) {
            return Type.Windows;
        }
        if (isOSX) {
            return Type.OSX;
        }
        return Type.Linux;
    }

    export const backend = {
        type,
        isWindows,
        isOSX
    };
}

@injectable()
export class ContextMenuContext {
    protected _altPressed = false;
    get altPressed(): boolean {
        return this._altPressed;
    }

    protected setAltPressed(altPressed: boolean): void {
        this._altPressed = altPressed;
    }

    resetAltPressed(): void {
        this.setAltPressed(false);
    }

    constructor() {
        document.addEventListener('keydown', e => this.setAltPressed(e.altKey || (OS.type() !== OS.Type.OSX && e.shiftKey)), true);
        document.addEventListener('keyup', () => this.resetAltPressed(), true);
    }
}

export class MenuServices {
    readonly commandRegistry: CommandRegistry;
    readonly context: ContextMenuContext;
    readonly menuWidgetFactory: MenuWidgetFactory;
    readonly commandExecutor: MenuCommandExecutor;
}

export interface MenuWidgetFactory {
    createMenuWidget(menu: MenuNode & Required<Pick<MenuNode, 'children'>>, options: BrowserMenuOptions): MenuWidget;
}

export class MenuCommandRegistry extends PhosphorCommandRegistry {
    protected actions = new Map<string, [MenuNode & CommandMenuNode, unknown[]]>();
    protected toDispose = new DisposableCollection();

    constructor(protected services: MenuServices) {
        super();
    }

    registerActionMenu(menu: MenuNode & CommandMenuNode, args: unknown[]): void {
        const { commandRegistry } = this.services;
        const command = commandRegistry.getCommand(menu.command);
        if (!command) {
            return;
        }
        const { id } = command;
        if (this.actions.has(id)) {
            return;
        }
        this.actions.set(id, [menu, args]);
    }

    snapshot(menuPath: MenuPath): this {
        this.toDispose.dispose();
        for (const [menu, args] of this.actions.values()) {
            this.toDispose.push(this.registerCommand(menu, args, menuPath));
        }
        return this;
    }

    protected registerCommand(menu: MenuNode & CommandMenuNode, args: unknown[], menuPath: MenuPath): Disposable {
        const { commandRegistry, commandExecutor } = this.services;
        const command = commandRegistry.getCommand(menu.command);
        if (!command) {
            return Disposable.NULL;
        }
        const { id } = command;
        if (this.hasCommand(id)) {
            // several menu items can be registered for the same command in different contexts
            return Disposable.NULL;
        }

        // We freeze the `isEnabled`, `isVisible`, and `isToggled` states so they won't change.
        const enabled = commandExecutor.isEnabled(menuPath, id, ...args);
        const visible = commandExecutor.isVisible(menuPath, id, ...args);
        const toggled = commandExecutor.isToggled(menuPath, id, ...args);
        const unregisterCommand = this.addCommand(id, {
            execute: () => commandExecutor.executeCommand(menuPath, id, ...args),
            label: menu.label,
            icon: menu.icon,
            isEnabled: () => enabled,
            isVisible: () => visible,
            isToggled: () => toggled
        });
        /*
        TODO: KEYBINDING DISABLED
        const bindings = keybindingRegistry.getKeybindingsForCommand(id);
        // Only consider the first keybinding.
        if (bindings.length) {
            const binding = bindings[0];
            const keys = keybindingRegistry.acceleratorFor(binding, ' ', true);
            this.addKeyBinding({
                command: id,
                keys,
                selector: '.p-Widget' // We have the PhosphorJS dependency anyway.
            });
        }
        */
        return Disposable.create(() => unregisterCommand.dispose());
    }
}
/**
 * A menu widget that would recompute its items on update.
 */
export class DynamicMenuWidget extends MenuWidget {
    /**
     * We want to restore the focus after the menu closes.
     */
    protected previousFocusedElement: HTMLElement | undefined;

    constructor(protected menu: CompoundMenuNode, protected options: any, protected services: MenuServices) {
        super(options);
        if (menu.label) {
            this.title.label = menu.label;
        }
        if (menu.icon) {
            this.title.iconClass = menu.icon;
        }
        this.updateSubMenus(this, this.menu, this.options.commands);
    }

    public aboutToShow({ previousFocusedElement }: { previousFocusedElement: HTMLElement | undefined }): void {
        this.preserveFocusedElement(previousFocusedElement);
        this.clearItems();
        this.runWithPreservedFocusContext(() => {
            this.options.commands.snapshot(this.options.rootMenuPath);
            this.updateSubMenus(this, this.menu, this.options.commands);
        });
    }

    public override open(x: number, y: number, options?: MenuWidget.IOpenOptions): void {
        const cb = () => {
            this.restoreFocusedElement();
            this.aboutToClose.disconnect(cb);
        };
        this.aboutToClose.connect(cb);
        this.preserveFocusedElement();
        super.open(x, y, options);
    }

    protected updateSubMenus(parent: MenuWidget, menu: CompoundMenuNode, commands: MenuCommandRegistry): void {
        const items = this.buildSubMenus([], menu, commands);
        while (items[items.length - 1]?.type === 'separator') {
            items.pop();
        }
        for (const item of items) {
            parent.addItem(item);
        }
    }

    protected buildSubMenus(
        parentItems: MenuWidget.IItemOptions[],
        menu: MenuNode,
        commands: MenuCommandRegistry
    ): MenuWidget.IItemOptions[] {
        if (
            CompoundMenuNode.is(menu) &&
            menu.children.length &&
            this.undefinedOrMatch(this.options.contextKeyService, menu.when, this.options.context)
        ) {
            const role = menu === this.menu ? CompoundMenuNodeRole.Group : CompoundMenuNode.getRole(menu);
            if (role === CompoundMenuNodeRole.Submenu) {
                const submenu = this.services.menuWidgetFactory.createMenuWidget(menu, this.options);
                if (submenu.items.length > 0) {
                    parentItems.push({ type: 'submenu', submenu });
                }
            } else if (role === CompoundMenuNodeRole.Group && menu.id !== 'inline') {
                const children = CompoundMenuNode.getFlatChildren(menu.children);
                const myItems: MenuWidget.IItemOptions[] = [];
                children.forEach(child => this.buildSubMenus(myItems, child, commands));
                if (myItems.length) {
                    if (parentItems.length && parentItems[parentItems.length - 1].type !== 'separator') {
                        parentItems.push({ type: 'separator' });
                    }
                    parentItems.push(...myItems);
                    parentItems.push({ type: 'separator' });
                }
            }
        } else if (menu.command) {
            const node = menu.altNode && this.services.context.altPressed ? menu.altNode : (menu as MenuNode & CommandMenuNode);
            if (
                commands.isVisible(node.command)
                // TODO: && this.undefinedOrMatch(this.options.contextKeyService ?? this.services.contextKeyService, node.when, this.options.context)
            ) {
                parentItems.push({
                    command: node.command,
                    type: 'command'
                });
            }
        }
        return parentItems;
    }

    protected undefinedOrMatch(contextKeyService: ContextMatcher, expression?: string, context?: HTMLElement): boolean {
        if (expression) {
            return contextKeyService.match(expression, context);
        }
        return true;
    }

    protected preserveFocusedElement(previousFocusedElement: Element | null = document.activeElement): boolean {
        if (!this.previousFocusedElement && previousFocusedElement instanceof HTMLElement) {
            this.previousFocusedElement = previousFocusedElement;
            return true;
        }
        return false;
    }

    protected restoreFocusedElement(): boolean {
        if (this.previousFocusedElement) {
            this.previousFocusedElement.focus({ preventScroll: true });
            this.previousFocusedElement = undefined;
            return true;
        }
        return false;
    }

    protected runWithPreservedFocusContext(what: () => void): void {
        let focusToRestore: HTMLElement | undefined = undefined;
        const { activeElement } = document;
        if (this.previousFocusedElement && activeElement instanceof HTMLElement && this.previousFocusedElement !== activeElement) {
            focusToRestore = activeElement;
            this.previousFocusedElement.focus({ preventScroll: true });
        }
        try {
            what();
        } finally {
            if (focusToRestore) {
                focusToRestore.focus({ preventScroll: true });
            }
        }
    }
}

export interface BrowserMenuOptions extends MenuWidget.IOptions {
    commands: MenuCommandRegistry;
    context?: HTMLElement;
    contextKeyService?: ContextMatcher;
    rootMenuPath: MenuPath;
}

export class BrowserContextMenuAccess extends ContextMenuAccess {
    constructor(public readonly menu: MenuWidget) {
        super(menu);
    }
}

@injectable()
export class BrowserContextMenuRenderer extends ContextMenuRenderer {
    @inject(MenuModelRegistry)
    protected readonly menuProvider: MenuModelRegistry;

    @inject(ContextMenuContext)
    protected readonly context: ContextMenuContext;

    @inject(CommandRegistry)
    protected readonly commandRegistry: CommandRegistry;

    @inject(MenuCommandExecutor)
    protected readonly menuCommandExecutor: MenuCommandExecutor;

    protected doRender({ menuPath, anchor, args, onHide, context, contextKeyService }: RenderContextMenuOptions): ContextMenuAccess {
        const contextMenu = this.createContextMenu(menuPath, args, context, contextKeyService);
        const { x, y } = coordinateFromAnchor(anchor);
        if (onHide) {
            contextMenu.aboutToClose.connect(() => onHide!());
        }
        contextMenu.open(x, y);
        return new BrowserContextMenuAccess(contextMenu);
    }

    createContextMenu(path: MenuPath, args?: unknown[], context?: HTMLElement, contextKeyService?: ContextMatcher): MenuWidget {
        const menuModel = this.menuProvider.getMenu(path);
        const menuCommandRegistry = this.createMenuCommandRegistry(menuModel, args).snapshot(path);
        const contextMenu = this.createMenuWidget(menuModel, {
            commands: menuCommandRegistry,
            context,
            rootMenuPath: path,
            contextKeyService
        });
        return contextMenu;
    }

    protected createMenuCommandRegistry(menu: CompoundMenuNode, args: unknown[] = []): MenuCommandRegistry {
        const menuCommandRegistry = new MenuCommandRegistry(this.services);
        this.registerMenu(menuCommandRegistry, menu, args);
        return menuCommandRegistry;
    }

    protected registerMenu(menuCommandRegistry: MenuCommandRegistry, menu: MenuNode, args: unknown[]): void {
        if (CompoundMenuNode.is(menu)) {
            menu.children.forEach(child => this.registerMenu(menuCommandRegistry, child, args));
        } else if (CommandMenuNode.is(menu)) {
            menuCommandRegistry.registerActionMenu(menu, args);
            if (CommandMenuNode.hasAltHandler(menu)) {
                menuCommandRegistry.registerActionMenu(menu.altNode, args);
            }
        }
    }

    createMenuWidget(menu: CompoundMenuNode, options: BrowserMenuOptions): DynamicMenuWidget {
        return new DynamicMenuWidget(menu, options, this.services);
    }

    protected get services(): MenuServices {
        return {
            context: this.context,
            commandRegistry: this.commandRegistry,
            menuWidgetFactory: this,
            commandExecutor: this.menuCommandExecutor
        };
    }
}

export namespace CincoContextMenu {
    export const CONTEXT_MENU: string[] = ['glsp-context-menu'];
}

export namespace TheiaSprottyContextMenu {
    export const CONTEXT_MENU: MenuPath = ['theia-sprotty-context-menu'];
}

@injectable()
export class TheiaContextMenuService implements IContextMenuService {
    protected timeout?: number;
    protected disposables?: DisposableItem[];

    @inject(MenuModelRegistry)
    protected readonly menuProvider: MenuModelRegistry;

    @inject(ContextMenuRenderer)
    protected readonly contextMenuRenderer: ContextMenuRenderer;

    @inject(CommandRegistry)
    protected readonly commandRegistry: CommandRegistry;

    protected actionDispatcher?: IActionDispatcher;

    connect(actionDispatcher: IActionDispatcher) {
        this.actionDispatcher = actionDispatcher;
    }

    show(items: MenuItem[], anchor: Anchor, onHide?: () => void): void {
        this.cleanUpNow();
        this.disposables = this.register(TheiaSprottyContextMenu.CONTEXT_MENU, items);
        const renderOptions = {
            menuPath: TheiaSprottyContextMenu.CONTEXT_MENU,
            anchor: anchor,
            onHide: () => {
                if (onHide) onHide();
                this.scheduleCleanup();
            }
        };
        this.contextMenuRenderer.render(renderOptions);
    }

    protected register(menuPath: string[], items: MenuItem[]): DisposableItem[] {
        const disposables: DisposableItem[] = [];
        for (const item of items) {
            if (item.children && item.children.length > 0) {
                const menuPathOfItem = item.group ? [...menuPath, item.group] : menuPath;
                disposables.push(this.registerSubmenu(menuPathOfItem, item));
                disposables.push(...this.register([...menuPathOfItem, item.id], item.children));
            } else {
                disposables.push(this.registerCommand(menuPath, item));
                disposables.push(this.registerMenuAction(menuPath, item));
            }
        }
        return disposables;
    }

    protected registerSubmenu(menuPath: string[], item: MenuItem): DisposableItem {
        return this.menuProvider.registerSubmenu([...menuPath, item.id], item.label);
    }

    protected registerCommand(menuPath: string[], item: MenuItem): DisposableItem {
        const command: Command = { id: commandId(menuPath, item), label: item.label, iconClass: item.icon };
        const disposable = {
            dispose: () => {
                return;
            }
        }; // this.commandRegistry.registerCommand(command, new SprottyCommandHandler(item, this.actionDispatcher));
        return new DisposableCommand(command, disposable);
    }

    protected registerMenuAction(menuPath: string[], item: MenuItem): DisposableItem {
        const menuAction = { label: item.label, order: item.sortString, commandId: commandId(menuPath, item) };
        const menuPathOfItem = item.group ? [...menuPath, item.group] : menuPath;
        const disposable = this.menuProvider.registerMenuAction(menuPathOfItem, menuAction);
        return new DisposableMenuAction(menuAction, disposable);
    }

    protected cleanUpNow() {
        window.clearTimeout(this.timeout);
        this.cleanUp();
    }

    protected scheduleCleanup() {
        this.timeout = window.setTimeout(() => {
            this.cleanUp();
        }, 200);
    }

    protected cleanUp() {
        if (this.disposables) {
            this.disposables.forEach(disposable => disposable.dispose(this.menuProvider, this.commandRegistry));
            this.disposables = undefined;
        }
    }
}

interface DisposableItem {
    dispose(menuProvider: MenuModelRegistry, commandRegistry: CommandRegistry): void;
}

class DisposableMenuAction implements DisposableItem {
    constructor(protected readonly menuAction: MenuAction, protected readonly disposable: Disposable) {}
    dispose(menuProvider: MenuModelRegistry, commandRegistry: CommandRegistry): void {
        menuProvider.unregisterMenuAction(this.menuAction);
        this.disposable.dispose();
    }
}

class DisposableCommand implements DisposableItem {
    constructor(protected readonly command: Command, protected readonly disposable: Disposable) {}
    dispose(menuProvider: MenuModelRegistry, commandRegistry: CommandRegistry): void {
        commandRegistry.unregisterCommand(this.command);
        this.disposable.dispose();
    }
}

function commandId(menuPath: string[], item: any): string {
    return menuPath.join('.') + '.' + item.id;
}
