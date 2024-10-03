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
import { RenderingContext, ShapeView, svg } from '@eclipse-glsp/client';
import { injectable } from 'inversify';
import { VNode } from 'snabbdom';
import * as conflictMarker from '@mdi/svg/svg/flash.svg';
import { CincoMarker } from '../model/model';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const JSX = { createElement: svg };

@injectable()
export class MarkerEdgeSourceTargetConflictView extends ShapeView {
    render(node: CincoMarker, context: RenderingContext): VNode | undefined {
        if (!this.isVisible(node, context)) {
            return undefined;
        }
        return (
            <g>
                <rect width={node.size.width} height={node.size.height} fill='white' stroke='black' stroke-width={2} />
                <image href={conflictMarker as any} width={node.size.width} height={node.size.height} />
                {context.renderChildren(node) as Iterable<React.ReactNode>}
            </g>
        ) as unknown as VNode;
    }
}
