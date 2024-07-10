"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.HooksAndActionsExampleAppearanceProvider = void 0;
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
const cinco_glsp_api_1 = require("@cinco-glsp/cinco-glsp-api");
const cinco_glsp_common_1 = require("@cinco-glsp/cinco-glsp-common");
// eslint-disable-next-line no-shadow
var CHANGE_APPEARANCE_MODE;
(function (CHANGE_APPEARANCE_MODE) {
    CHANGE_APPEARANCE_MODE[CHANGE_APPEARANCE_MODE["CSS"] = 0] = "CSS";
    CHANGE_APPEARANCE_MODE[CHANGE_APPEARANCE_MODE["APPEARANCE"] = 1] = "APPEARANCE";
})(CHANGE_APPEARANCE_MODE || (CHANGE_APPEARANCE_MODE = {}));
const EXAMPLE_APPEARANCE = {
    background: {
        r: 255,
        g: 200,
        b: 200
    },
    foreground: {
        r: 200,
        g: 255,
        b: 200
    },
    lineStyle: cinco_glsp_common_1.LineStyle.DOT,
    lineWidth: 5,
    transparency: 1.0,
    name: 'DynamicAppearance',
    font: {
        fontName: 'Arial'
    },
    filled: true
};
const EXAMPLE_STYLE = {
    name: '',
    styleType: 'NodeStyle',
    shape: {
        name: '',
        type: cinco_glsp_common_1.AbstractShape.ELLIPSE,
        appearance: EXAMPLE_APPEARANCE
    }
};
/**
 * Language Designer defined example of a DoubleClickHandler
 */
class HooksAndActionsExampleAppearanceProvider extends cinco_glsp_api_1.AppearanceProvider {
    constructor() {
        super(...arguments);
        this.CHANGE_MODE = CHANGE_APPEARANCE_MODE.APPEARANCE;
        this.defaultCSSClass = 'flowgraph-activity';
        this.toggledCSSClass = 'flowgraph-activity-toggled';
        this.CHANNEL_NAME = 'Flowgraph [' + this.modelState.root.id + ']';
    }
    getAppearance(action, ...args) {
        var _a;
        // parse action
        const modelElementId = action.modelElementId;
        const element = this.modelState.index.findElement(modelElementId);
        if (element === undefined) {
            return [];
        }
        let appearanceUpdate;
        /**
         * calculate new appearance
         */
        switch (this.CHANGE_MODE) {
            // change by appearance (Legacy of CINCO)
            case CHANGE_APPEARANCE_MODE.APPEARANCE:
                {
                    if (!element.style ||
                        !element.style.shape ||
                        !element.style.shape.appearance) {
                        element.style = EXAMPLE_STYLE;
                    }
                    // toggle transparency
                    const appearance = Object.assign({}, element.appearance);
                    if (((_a = appearance.transparency) !== null && _a !== void 0 ? _a : 1.0) >= 1.0) {
                        appearance.transparency = 0.5;
                    }
                    else {
                        appearance.transparency = 1.0;
                    }
                    element.appearance = appearance;
                    appearanceUpdate = cinco_glsp_common_1.ApplyAppearanceUpdateAction.create(modelElementId, [], Object.assign({}, appearance));
                }
                break;
            // Change by a CSS Class (new method)
            case CHANGE_APPEARANCE_MODE.CSS: {
                if (!element.view) {
                    element.view = { cssClass: [] };
                }
                // change element's style and propagate this style to the frontend
                const currentView = element.view;
                let currentCSSClasses = currentView.cssClass;
                // toggle css style
                if (currentCSSClasses) {
                    if (currentCSSClasses.indexOf(this.defaultCSSClass) >= 0) {
                        currentCSSClasses = currentCSSClasses.filter(css => css !== this.defaultCSSClass);
                        currentCSSClasses.push(this.toggledCSSClass);
                    }
                    else {
                        currentCSSClasses = currentCSSClasses.filter(css => css !== this.toggledCSSClass);
                        currentCSSClasses.push(this.defaultCSSClass);
                    }
                    currentView.cssClass = currentCSSClasses;
                }
                appearanceUpdate = cinco_glsp_common_1.ApplyAppearanceUpdateAction.create(modelElementId, currentCSSClasses);
            }
        }
        // logging
        const message = 'Element [' + element.type + ', ' + modelElementId + '] is changing appearance.';
        this.log(message, { show: true });
        return [appearanceUpdate];
    }
}
exports.HooksAndActionsExampleAppearanceProvider = HooksAndActionsExampleAppearanceProvider;
// register into app
cinco_glsp_api_1.LanguageFilesRegistry.register(HooksAndActionsExampleAppearanceProvider);
