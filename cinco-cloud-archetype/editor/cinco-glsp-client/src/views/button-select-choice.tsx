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

import { ShapeView, RenderingContext, isEdgeLayoutable, svg } from '@eclipse-glsp/client';
import { injectable } from 'inversify';
import { VNode } from 'snabbdom';
import { CincoEdgeButtonSelectChoice } from '../model/model';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const JSX = { createElement: svg };

@injectable()
export class ButtonSelectChoiceView extends ShapeView {
    render(button: Readonly<CincoEdgeButtonSelectChoice>, context: RenderingContext): VNode | undefined {
        if (!isEdgeLayoutable(button) && !this.isVisible(button, context)) {
            return undefined;
        }

        return (
            <g>
                <rect
                    x={-button.size.width}
                    y={-button.size.height}
                    width={button.size.width}
                    height={button.size.height}
                    fill='white'
                    stroke='black'
                    stroke-width={2}
                    cursor='pointer'
                />
                <text
                    x={-button.size.width / 2}
                    y={-button.size.height / 2 + 4}
                    text-anchor='middle'
                    font-size={12}
                    fill='#333'
                    stroke-width={1}
                    cursor='pointer'
                >
                    ✓
                </text>
            </g>
        ) as unknown as VNode;
    }
}
