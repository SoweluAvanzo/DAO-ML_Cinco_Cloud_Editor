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
import { Action, MousePositionTracker, Point, GModelElement } from '@eclipse-glsp/client';
import { injectable } from 'inversify';

@injectable()
export class MouseContextTracker extends MousePositionTracker {
    protected _lastMousePosition: Point | undefined;
    protected _lastMouseTarget: GModelElement | undefined;

    override mouseMove(target: GModelElement, event: MouseEvent): (Action | Promise<Action>)[] {
        this._lastMousePosition = { x: event.offsetX, y: event.offsetY };
        this._lastMouseTarget = target;
        this.lastPosition = target.root.parentToLocal({ x: event.offsetX, y: event.offsetY });
        return [];
    }

    get lastMousePosition(): Point | undefined {
        return this._lastMousePosition;
    }

    get lastMouseTarget(): GModelElement | undefined {
        return this._lastMouseTarget;
    }
}
