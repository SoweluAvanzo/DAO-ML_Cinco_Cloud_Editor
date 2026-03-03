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
import { LanguageFilesRegistry, Node, Container, ModelElement, ValueProvider } from '@cinco-glsp/cinco-glsp-api';
import { Action, ValueUpdateRequestAction } from '@cinco-glsp/cinco-glsp-common';
 
export class DaoMLValueProvider extends ValueProvider {
    override CHANNEL_NAME: string | undefined = 'DAO ML';
    factor = 7;

    override updateValue(action: ValueUpdateRequestAction, ...args: unknown[]): Action[] | Promise<Action[]> {
        const reason = action.reason;
        let currentModelElement: any | undefined = this.getElement(action.modelElementId) as Node;
        try {
            this.update(currentModelElement as ModelElement, reason);
        } catch(e) {
            console.log(e);
        }         
        return [];
    }

    update(modelElement: ModelElement, reason: string | undefined, updateParent = true): void {
        if(Node.is(modelElement)) {
            // update node/container
            let label1 = "";
            let label2 = "";
            let label = "";
            if(modelElement.type == 'dao_ml:permission' && reason !== 'changeContainer') {
                label1 = modelElement.getProperty("allowedAction") ?? '';
                label2 = "(" + modelElement.getProperty("permissionType") + ")"
                label = label1.length > label2.length ? label1 : label2;
                if(modelElement.size.width < label.length * this.factor || modelElement.size.width > label.length * this.factor) {
                    modelElement.size.width = label.length * this.factor;
                }
                modelElement.size.height = this.factor * 7;
                if(reason == 'changeBounds' && modelElement.parent?.type == 'dao_ml:governancearea') {
                    this.update(modelElement.parent, reason);
                }
            } else if(modelElement.type == 'dao_ml:governancearea' && Container.is(modelElement)) {
                label1 = modelElement.getProperty("description") ?? '';
                label2 = "(" + modelElement.getProperty("implementation") + ")"
                label = label1.length > label2.length ? label1 : label2;
                const textWidth = label.length * (this.factor + 1);
                let targetWidth = Math.max(textWidth, 100);

                // find accumulate containments height & find most widest containment
                let targetHeight = 40;  // TODO: see if top margin fits
                modelElement.containments.forEach(c => {
                    c.position = {
                        x: 25,
                        y: targetHeight
                    };
                    targetHeight += c.size.height + 5;
                    targetWidth = Math.max(targetWidth, c.size.width);
                });
                targetHeight += 20; // bottom margin   
                const leftRightMargin = (targetWidth * 0.2);
                targetWidth = targetWidth + leftRightMargin;
                modelElement.size.width = targetWidth;
                modelElement.size = {
                    width: targetWidth,
                    height: targetHeight
                };

                // reposition in relation to width
                modelElement.containments.forEach(c => {
                    c.position = {
                        x: leftRightMargin / 2,
                        y: c.position.y
                    };
                });
                if(updateParent) {
                    this.update(modelElement.parent as ModelElement, reason);                    
                }
            } else if(modelElement.type == 'dao_ml:committee') {
                label1 = modelElement.getProperty("description") ?? '';
                label2 = "(" + modelElement.getProperty("decisionMakingMethod") + ")"
                label = label1.length > label2.length ? label1 : label2;
                modelElement.size.width = label.length * this.factor;
                modelElement.size.height = this.factor * 7;
            }
        }
    }
}
 
LanguageFilesRegistry.register(DaoMLValueProvider);
 