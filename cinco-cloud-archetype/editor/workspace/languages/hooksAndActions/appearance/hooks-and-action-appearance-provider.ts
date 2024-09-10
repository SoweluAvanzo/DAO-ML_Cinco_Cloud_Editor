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
import { AppearanceProvider, LanguageFilesRegistry, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import {
    AbstractShape,
    Appearance,
    ApplyAppearanceUpdateAction,
    Color,
    Ellipse,
    Font,
    LineStyle,
    NodeStyle,
    Rectangle,
    RequestAppearanceUpdateAction
} from '@cinco-glsp/cinco-glsp-common';

const EXAMPLE_APPEARANCE: Appearance = {
    background: {
        r: 255,
        g: 200,
        b: 200
    } as Color,
    foreground: {
        r: 200,
        g: 255,
        b: 200
    } as Color,
    lineStyle: LineStyle.DOT,
    lineWidth: 5,
    transparency: 1.0,
    name: 'DynamicAppearance',
    font: {
        fontName: 'Arial'
    } as Font,
    filled: true
};

const EXAMPLE_STYLE: NodeStyle = {
    name: '',
    styleType: 'NodeStyle',
    shape: {
        name: '',
        type: AbstractShape.ELLIPSE,
        appearance: EXAMPLE_APPEARANCE
    } as Ellipse
};

/**
 * Language Designer defined example of a DoubleClickHandler
 */
export class HooksAndActionsAppearanceProvider extends AppearanceProvider {
    override CHANNEL_NAME: string | undefined = 'HooksAndActions [' + this.modelState.root.id + ']';

    getAppearance(
        action: RequestAppearanceUpdateAction,
        ...args: unknown[]
    ): ApplyAppearanceUpdateAction[] | Promise<ApplyAppearanceUpdateAction[]> {
        // parse action
        const modelElementId: string = action.modelElementId;
        const element = this.getElement(modelElementId);
        if (element === undefined) {
            return [];
        }

        /**
         * calculate new appearance
         */

        if (!element.style || !(element.style as NodeStyle).shape || !((element.style as NodeStyle).shape as Rectangle).appearance) {
            element.style = EXAMPLE_STYLE;
        }

        // toggle transparency
        const appearance = { ...element.appearance } as Appearance;
        if ((appearance.transparency ?? 1.0) >= 1.0) {
            appearance.transparency = 0.5;
        } else {
            appearance.transparency = 1.0;
        }

        element.appearance = appearance;
        const appearanceUpdate = ApplyAppearanceUpdateAction.create(modelElementId, [], { ...appearance });
        // logging
        const message = 'Element [' + element.type + ', ' + modelElementId + '] is changing appearance.';
        this.log(message, { show: true });

        // save and update gui
        this.saveModel();
        this.submitModel();

        return [appearanceUpdate];
    }
}
// register into app
LanguageFilesRegistry.register(HooksAndActionsAppearanceProvider);
