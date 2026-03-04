/********************************************************************************
 * Copyright (c) 2024 The DAO ML Team.
 ********************************************************************************/

import { Node, Container, ModelElement } from '@cinco-glsp/cinco-glsp-api';

const factor = 7;
const MAX_RECURSION_DEPTH = 10;

export function updateModel(modelElement: ModelElement): void {
    updateModelWithDepth(modelElement, 0);
}

/**
 * Automatically sizes and positions DAO ML diagram elements based on their type and content.
 * - Governance areas are resized to fit their contents with margins,
 *   i.e. permissions width and number of permissions specifies their height.
 *   They also reposition their contained permissions to be horizontally centered with a vertical layout.
 * - Permissions are resized with a fixed height and a width based on the longest label (allowedAction or permissionType)
 *   and always update their parent governance area to ensure proper layout.
 * - DAOs do not reposition containments, but resize to fit contained elements.
 *   Make them bigger to contain elements contained but outside their boundaries and make them smaller if they have too much empty space.
 * - Roles do not resize in any way.
 * - Committees resize horizontally based on their longest label (description or decisionMakingMethod) and have a fixed height.
 *
 * @param modelElement The element to update
 * @param depth Current recursion depth for safety
 */
function updateModelWithDepth(
    modelElement: ModelElement | undefined,
    depth = 0
): void {
    // Validate inputs
    if (!modelElement) {
        return;
    }

    // Prevent infinite recursion
    if (depth > MAX_RECURSION_DEPTH) {
        console.warn('Max recursion depth exceeded in DAO ML value update. Possible circular parent relationship.');
        return;
    }

    if (!Node.is(modelElement)) {
        return;
    }

    // Ensure size object exists
    if (modelElement.size === undefined) {
        modelElement.size = { width: 100, height: 100 };
    }

    // Permission element sizing
    if (modelElement.type === 'dao_ml:permission') {
        const allowedAction = modelElement.getProperty('allowedAction') ?? '';
        const permissionType = modelElement.getProperty('permissionType') ?? '';

        const label1 = allowedAction;
        const label2 = '(' + permissionType + ')'; // Now safe to concatenate
        const label = label1.length > label2.length ? label1 : label2;

        // Update width if label length differs significantly
        const expectedWidth = label.length * factor;
        if (Math.abs(modelElement.size.width - expectedWidth) > 2) { // Allow small tolerance
            modelElement.size.width = expectedWidth;
        }
        modelElement.size.height = factor * 7;

        // Cascade update to parent GovernanceArea so it resizes and repositions.
        // Permissions only exist as containments of GovernanceAreas in the graph model
        // (in XML they are siblings inside a DAO, translated via ref_gov_area reference).
        // DAOs never directly contain permissions.
        if (modelElement.parent && modelElement.parent.type === 'dao_ml:governancearea') {
            updateModelWithDepth(modelElement.parent, depth + 1);
        }
    }
    else if (modelElement.type === 'dao_ml:role' && Node.is(modelElement)) {
        // Cascade update to parent Dao so it resizes and repositions.
        // Permissions only exist as containments of GovernanceAreas in the graph model
        // (in XML they are siblings inside a DAO, translated via ref_gov_area reference).
        // DAOs never directly contain permissions.
        if (modelElement.parent && modelElement.parent.type === 'dao_ml:dao') {
            updateModelWithDepth(modelElement.parent, depth + 1);
        }
    }
    // Governance area sizing with child layout
    else if (modelElement.type === 'dao_ml:governancearea' && Container.is(modelElement)) {
        const description = modelElement.getProperty('description') ?? '';
        const implementation = modelElement.getProperty('implementation') ?? '';

        const label1 = description;
        const label2 = '(' + implementation + ')'; // Now safe
        const label = label1.length > label2.length ? label1 : label2;

        const textWidth = label.length * (factor + 1);
        let targetWidth = Math.max(textWidth, 100);

        // Calculate containment layout
        let targetHeight = 40;
        const childrenWidths: number[] = [];
        if (modelElement.containments && modelElement.containments.length > 0) {
            modelElement.containments.forEach(c => {
                targetHeight += c.size.height + 5;
                childrenWidths.push(c.size.width);
                targetWidth = Math.max(targetWidth, c.size.width);
            });
        }

        targetHeight += 20; // bottom margin
        const leftRightMargin = targetWidth * 0.2;
        targetWidth = targetWidth + leftRightMargin;

        modelElement.size = {
            width: targetWidth,
            height: targetHeight
        };

        // Reposition containments based on final width with horizontal centering
        if (modelElement.containments && modelElement.containments.length > 0) {
            let yOffset = 40;
            modelElement.containments.forEach((c, index) => {
                // Horizontally center each permission within the governance area
                const centerX = (modelElement.size.width - c.size.width) / 2;
                c.position = {
                    x: centerX,
                    y: yOffset
                };
                yOffset += c.size.height + 5;
            });
        }

        // Always update parent if it exists - governance areas determine their own sizing
        // based on their children, so parent must be notified of size changes
        if (modelElement.parent) {
            updateModelWithDepth(modelElement.parent, depth + 1);
        }
    }
    // Committee element sizing
    else if (modelElement.type === 'dao_ml:committee') {
        const description = modelElement.getProperty('description') ?? '';
        const decisionMethod = modelElement.getProperty('decisionMakingMethod') ?? '';

        const label1 = description;
        const label2 = '(' + decisionMethod + ')'; // Now safe
        const label = label1.length > label2.length ? label1 : label2;

        modelElement.size.width = label.length * factor;
        modelElement.size.height = factor * 7;
    }
    // Role elements do not resize in any way
    else if (modelElement.type === 'dao_ml:role') {
        // No resizing for roles
    }
    // DAO (root model) element sizing
    // DAOs contain GovernanceAreas and Permissions at the same XML level.
    // In the graph model, permissions are translated as containments of GovernanceAreas
    // via the ref_gov_area reference. DAOs do not directly contain permissions.
    // DAOs do not reposition containments, but resize to fit them.
    else if (modelElement.type === 'dao_ml:dao' && Container.is(modelElement)) {
        const name = modelElement.getProperty('name') ?? '';
        const missionStatement = modelElement.getProperty('missionStatement') ?? '';

        const label1 = name;
        const label2 = missionStatement ? '(' + missionStatement + ')' : '';
        const label = label1.length > label2.length ? label1 : label2;

        const textWidth = label.length * (factor + 1);
        let targetWidth = Math.max(textWidth, 200);
        let targetHeight = 50; // header height

        // Calculate required size to contain all contained elements
        if (modelElement.containments && modelElement.containments.length > 0) {
            modelElement.containments.forEach(c => {
                const elementRight = (c.position?.x ?? 0) + c.size.width;
                const elementBottom = (c.position?.y ?? 0) + c.size.height;
                targetWidth = Math.max(targetWidth, elementRight + 15); // 15 right margin
                targetHeight = Math.max(targetHeight, elementBottom + 30); // 15 bottom margin
            });
        }

        modelElement.size = {
            width: targetWidth,
            height: targetHeight
        };
    }
}

