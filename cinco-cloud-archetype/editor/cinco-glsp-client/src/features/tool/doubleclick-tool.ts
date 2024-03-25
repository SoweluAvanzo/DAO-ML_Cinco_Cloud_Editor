/********************************************************************************
 * Copyright (c) 2020-2022 Cinco Cloud.
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
import { DoubleClickAction } from '@cinco-glsp/cinco-glsp-common';
import { Action, Tool, KeyListener, MouseListener, GModelElement, isSelectable, MouseTool, KeyTool } from '@eclipse-glsp/client';
import { inject, injectable } from 'inversify';
import { toArray } from 'sprotty/lib/utils/iterable';
import { matchesKeystroke } from 'sprotty/lib/utils/keyboard';

@injectable()
export class DoubleClickTool implements Tool {
    static readonly ID = 'doubleclick-tool';

    @inject(MouseTool)
    protected mouseTool: MouseTool;
    @inject(KeyTool)
    protected keyTool: KeyTool;
    protected editLabelKeyListener: KeyListener;
    protected mouseListener: MouseListener;

    get id(): string {
        return DoubleClickTool.ID;
    }

    protected createDoubleClickMouseListener(): MouseListener {
        return new DoubleClickMouseListener();
    }

    protected createDoubleClickKeyListener(): KeyListener {
        return new DoubleClickKeyListener();
    }

    enable(): void {
        this.editLabelKeyListener = this.createDoubleClickKeyListener();
        this.mouseListener = this.createDoubleClickMouseListener();
        this.mouseTool.register(this.mouseListener);
        this.keyTool.register(this.editLabelKeyListener);
    }

    disable(): void {
        this.keyTool.deregister(this.editLabelKeyListener);
        this.mouseTool.deregister(this.mouseListener);
    }
}

/**
 * Listener
 *
 * the listeners will heandle the creation of the action.
 * Theses actions will be handled to other tools (e.g. mouseTool and keyTool),
 * who will dispatch the action further to the backend.
 */

export class DoubleClickMouseListener extends MouseListener {
    override doubleClick(target: GModelElement, event: MouseEvent): (Action | Promise<Action>)[] {
        if (isWithDoubleClickFeature(target)) {
            return [DoubleClickAction.create(target.id)];
        }
        return [];
    }
}

// an alternative for doubleClick by pressing F2
export class DoubleClickKeyListener extends KeyListener {
    override keyDown(element: GModelElement, event: KeyboardEvent): Action[] {
        if (matchesKeystroke(event, 'F2')) {
            const doubleClickable = toArray(element.index.all().filter(e => isSelectable(e) && e.selected)).filter(
                (e): e is GModelElement => e !== undefined && isWithDoubleClickFeature(e)
            );
            if (doubleClickable.length === 1) {
                // only one element can be "doubleClicked"
                return [DoubleClickAction.create(doubleClickable[0].id)];
            }
        }
        return [];
    }
}

/**
 * Attribute Symbol, that corresponds to an annotation on elements, for those the action can be performed
 */

export const withDoubleClickFeature = Symbol('withDoubleClickFeature');

export function isWithDoubleClickFeature<T extends GModelElement>(element: T): element is T {
    return element.hasFeature(withDoubleClickFeature);
}
