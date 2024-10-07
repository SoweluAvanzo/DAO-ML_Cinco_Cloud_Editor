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

import { ShapeView, RenderingContext, svg } from '@eclipse-glsp/client';
import { injectable } from 'inversify';
import { VNode } from 'snabbdom';
import { CincoButtonDelete } from '../model/model';
import * as trashCan from '@mdi/svg/svg/trash-can.svg';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const JSX = { createElement: svg };

@injectable()
export class ButtonDeleteView extends ShapeView {
    render(button: Readonly<CincoButtonDelete>, context: RenderingContext): VNode | undefined {
        if (!this.isVisible(button, context)) {
            return undefined;
        }

        return (
            <g>
                <rect width={button.size.width} height={button.size.height} fill='white' stroke='black' stroke-width={2} cursor='pointer' />
                <image href={trashCan as any} width={button.size.width} height={button.size.height} />
            </g>
        ) as unknown as VNode;
    }
}
