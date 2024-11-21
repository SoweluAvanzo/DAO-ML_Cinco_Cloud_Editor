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

export function resolveParameter(modelElement: any, parameter: string): string {
    const parameterPattern = RegExp('(\\$\\{(.*)\\})', 'g');
    let result: string = parameter;
    let currentFind;
    do {
        currentFind = parameterPattern.exec(result);
        if (currentFind) {
            const startIndex = currentFind.index;
            const endIndex = startIndex + currentFind[0].length;
            const prefix = result.substring(0, startIndex);
            const postfix = result.substring(endIndex, result.length);
            const attributeName = currentFind[2];
            if (attributeName) {
                // resolve parameter from attribute
                const resolvedAttribute = resolveAttribute(modelElement, attributeName) ?? '';
                result = prefix + resolvedAttribute + postfix;
            }
        }
    } while (currentFind);
    return result;
}

export function resolveAttribute(element: any, attributeName: string): string | undefined {
    if (attributeName === 'id') {
        return element.id;
    }
    if (attributeName === 'type') {
        return element.type;
    }
    if (attributeName === 'size.width') {
        return '' + element.size.width;
    }
    if (attributeName === 'size.height') {
        return '' + element.size.height;
    }
    if (attributeName === 'position.x') {
        return '' + element.position.x;
    }
    if (attributeName === 'position.y') {
        return '' + element.position.y;
    }
    try {
        return element.properties ? element.properties[attributeName]
            : element.getProperty(attributeName);
    } catch (e) {
        console.log(e);
        return undefined;
    }
}
