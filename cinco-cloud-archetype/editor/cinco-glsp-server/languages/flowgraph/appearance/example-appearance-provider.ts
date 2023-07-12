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
    AbstractShape,
    Appearance,
    Color,
    Ellipse,
    Font,
    LineStyle,
    NodeStyle,
    Rectangle,
    View
} from '../../../src/shared/meta-specification';
import { ApplyAppearanceUpdateAction, RequestAppearanceUpdateAction } from '../../../src/shared/protocol/appearance-provider-protocol';
import { AppearanceProvider } from '../../../src/tools/api/appearance-provider';
import { LanguageFilesRegistry } from '../../../src/tools/language-files-registry';
import { ModelElement } from '../../model/graph-model';

// eslint-disable-next-line no-shadow
enum CHANGE_APPEARANCE_MODE {
    CSS,
    APPEARANCE
}

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
export class ExampleAppearanceProvider extends AppearanceProvider {
    CHANGE_MODE: CHANGE_APPEARANCE_MODE = CHANGE_APPEARANCE_MODE.APPEARANCE;
    defaultCSSClass = 'flowgraph-activity';
    toggledCSSClass = 'flowgraph-activity-toggled';
    override CHANNEL_NAME: string | undefined = 'Flowgraph [' + this.modelState.root.id + ']';

    getAppearance(
        action: RequestAppearanceUpdateAction,
        ...args: unknown[]
    ): ApplyAppearanceUpdateAction[] | Promise<ApplyAppearanceUpdateAction[]> {
        // parse action
        const modelElementId: string = action.modelElementId;
        const element = this.modelState.index.findElement(modelElementId) as ModelElement;
        if (element === undefined) {
            return [];
        }
        let appearanceUpdate: ApplyAppearanceUpdateAction;

        /**
         * calculate new appearance
         */
        switch (this.CHANGE_MODE) {
            // change by appearance (Legacy of CINCO)
            case CHANGE_APPEARANCE_MODE.APPEARANCE:
                {
                    if (
                        !element.style ||
                        !(element.style as NodeStyle).shape ||
                        !((element.style as NodeStyle).shape as Rectangle).appearance
                    ) {
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
                    appearanceUpdate = ApplyAppearanceUpdateAction.create(modelElementId, [], { ...appearance });
                }
                break;
            // Change by a CSS Class (new method)
            case CHANGE_APPEARANCE_MODE.CSS: {
                if (!element.view) {
                    element.view = { cssClass: [] } as View;
                }
                // change element's style and propagate this style to the frontend
                const currentView = element.view;
                let currentCSSClasses = currentView.cssClass;
                // toggle css style
                if (currentCSSClasses) {
                    if (currentCSSClasses.indexOf(this.defaultCSSClass) >= 0) {
                        currentCSSClasses = currentCSSClasses.filter(css => css !== this.defaultCSSClass);
                        currentCSSClasses.push(this.toggledCSSClass);
                    } else {
                        currentCSSClasses = currentCSSClasses.filter(css => css !== this.toggledCSSClass);
                        currentCSSClasses.push(this.defaultCSSClass);
                    }
                    currentView.cssClass = currentCSSClasses;
                }
                appearanceUpdate = ApplyAppearanceUpdateAction.create(modelElementId, currentCSSClasses);
            }
        }
        // logging
        const message = 'Element [' + element.type + ', ' + modelElementId + '] is changing appearance.';
        this.log(message, { show: true });
        return [appearanceUpdate];
    }
}
// register into app
LanguageFilesRegistry.register(ExampleAppearanceProvider);
