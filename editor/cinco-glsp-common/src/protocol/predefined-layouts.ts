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

/**
 * PUT HERE ALL PREDEFINED LAYOUTS
 *
 * These layouts should be patterns and heuristics that work well for certain classes of use-cases.
 * After you added one, also consider adding support into your language supporting application
 * (e.g. `cinco-languages/src/mgl/language/mgl-annotations.ts` - predefinedLayouts)
 */

export interface PREDEFINED_LAYOUT {
    kind: string;
    layout: any;
}

/**
 * Create and add a predefined layout here.
 * Code of conduct: kinds are in lower-case.
 */
export const PREDEFINED_LAYOUTS: PREDEFINED_LAYOUT[] = [
    {
        kind: 'random',
        layout: {
            'elk.algorithm': 'random',
            'elk.spacing.nodeNode': 50
        }
    },
    {
        kind: 'layered',
        layout: {
            'elk.algorithm': 'layered',
            'elk.layered.spacing.baseValue': 100
        }
    }
];

export namespace PredefinedLayouts {
    export function is(layoutType: string): boolean {
        const type = layoutType.toLowerCase();
        return PREDEFINED_LAYOUTS.find(l => l.kind === type) !== undefined;
    }
    export function get(layoutType: string): any | undefined {
        const type = layoutType.toLowerCase();
        return PREDEFINED_LAYOUTS.find(l => l.kind === type)?.layout;
    }
}
