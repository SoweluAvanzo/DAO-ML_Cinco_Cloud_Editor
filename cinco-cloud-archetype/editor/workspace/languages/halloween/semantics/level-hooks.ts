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
 import { LanguageFilesRegistry, Node, AbstractNodeHook, ResizeBounds } from '@cinco-glsp/cinco-glsp-api';
 
 export class LevelHooks extends AbstractNodeHook {
     override CHANNEL_NAME: string | undefined = 'Level [' + this.modelState.graphModel.id + ']';
 
    postCreate(node: Node): void {
        this.fixSize(node); 
    }
    
    postResize(node: Node, resizeBounds: ResizeBounds): void {
        this.fixSize(node);
    }

    async fixSize(node: Node) {
        // fix tiles
        let normalizedX = Math.floor(node.size.width / 48) * 48;
        let normalizedY = Math.floor(node.size.height / 48) * 48;
        node.size.width = normalizedX < 48 ? 48 : normalizedX;
        node.size.height = normalizedY < 48 ? 48 : normalizedY;
        await this.saveModel();
        await this.submitModel();
        this.log("Updated level", {show: true})
    }
 }
 
 LanguageFilesRegistry.register(LevelHooks);
 