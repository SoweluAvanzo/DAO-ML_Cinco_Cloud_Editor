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
 import { LanguageFilesRegistry, Node, AbstractNodeHook, Container, ModelElement } from '@cinco-glsp/cinco-glsp-api';
 import { Point } from '@cinco-glsp/cinco-glsp-common';
 
 export class GameObjectHook extends AbstractNodeHook {
     override CHANNEL_NAME: string | undefined = 'GameObject [' + this.modelState.graphModel.id + ']';
 
    postCreate(node: Node): void {
        this.fixPosition(node);
    }
    postMove(node: Node, oldPosition?: Point): void {
        this.fixPosition(node);
    }

    async fixPosition(node: Node) {
        const parent = node.parent;
        if(parent && ModelElement.is(parent)) {
            const size = parent.size;
            const lastTileX = parent.size.width - 96;
            const lastTileY = parent.size.height - 96;

            // fix tiles
            let normalizedX = Math.floor(node.position.x / 48) * 48;
            let normalizedY = Math.floor(node.position.y / 48) * 48;

            // fix boundaries
            if(normalizedX < 48) {
                normalizedX = 48;
            } else if (normalizedX > lastTileX) {
                normalizedX = lastTileX;
            }
            if(normalizedY < 48) {
                normalizedY = 48;
            } else if (normalizedY > lastTileY) {
                normalizedY = lastTileY;
            }

            node.position = { x: normalizedX, y: normalizedY };
            await this.saveModel();
            await this.submitModel();
        }
    }
 }
 
 LanguageFilesRegistry.register(GameObjectHook);
 