/********************************************************************************
 * Copyright (c) 2024 The DAO ML Team.
 ********************************************************************************/

import { Node, Container, ModelElement } from '@cinco-glsp/cinco-glsp-api';

const factor = 7;
export function updateValue(modelElement: ModelElement, reason: string | undefined, updateParent = true): void {
    if (Node.is(modelElement)) {
        // update node/container
        let label1 = '';
        let label2 = '';
        let label = '';
        if (modelElement.type === 'dao_ml:permission' && reason !== 'changeContainer') {
            label1 = modelElement.getProperty('allowedAction') ?? '';
            label2 = '(' + modelElement.getProperty('permissionType') + ')';
            label = label1.length > label2.length ? label1 : label2;
            if (modelElement.size.width < label.length * factor || modelElement.size.width > label.length * factor) {
                modelElement.size.width = label.length * factor;
            }
            modelElement.size.height = factor * 7;
            if (reason === 'changeBounds' && modelElement.parent?.type === 'dao_ml:governancearea') {
                updateValue(modelElement.parent, reason);
            }
        } else if (modelElement.type === 'dao_ml:governancearea' && Container.is(modelElement)) {
            label1 = modelElement.getProperty('description') ?? '';
            label2 = '(' + modelElement.getProperty('implementation') + ')';
            label = label1.length > label2.length ? label1 : label2;
            const textWidth = label.length * (factor + 1);
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
            if (updateParent) {
                updateValue(modelElement.parent as ModelElement, reason);
            }
        } else if (modelElement.type === 'dao_ml:committee') {
            label1 = modelElement.getProperty('description') ?? '';
            label2 = '(' + modelElement.getProperty('decisionMakingMethod') + ')';
            label = label1.length > label2.length ? label1 : label2;
            modelElement.size.width = label.length * factor;
            modelElement.size.height = factor * 7;
        }
    }
}
