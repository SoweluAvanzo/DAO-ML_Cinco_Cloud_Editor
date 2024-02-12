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
import * as path from 'path';
import * as fs from 'fs';
import { AstNode, BuildOptions, LangiumDocument, LangiumServices } from 'langium';
import { URI } from 'vscode-uri';
import { ModelElement, isEnum } from '../../generated/ast.js';

export function topologicalSortWithDescendants(modelElements: ModelElement[]): {
    sortedModelElements: ModelElement[];
    descendantsMap: Record<string, Set<string>>;
} {
    const graph: Record<string, ModelElement[]> = {};
    const inDegree: Record<string, number> = {};
    const descendantsMap: Record<string, Set<string>> = {};

    // Initialize the graph, in-degree maps, and descendants map
    for (const element of modelElements) {
        graph[element.name] = [];
        inDegree[element.name] = 0;
        descendantsMap[element.name] = new Set();
    }

    // Build the graph.
    for (const element of modelElements) {
        if (!isEnum(element) && element.localExtension) {
            const localExtensionName = element.localExtension.ref?.name;
            if (localExtensionName) {
                if (!graph[localExtensionName]) {
                    throw new Error(`Element ${element.localExtension} not found.`);
                }
                graph[localExtensionName].push(element);
                inDegree[element.name]++;
            }
        }
    }

    // Array for order of elements.
    const sortedElements: ModelElement[] = [];

    // Array for elements with in-degree = 0.
    const queue: ModelElement[] = modelElements.filter(element => inDegree[element.name] === 0);

    while (queue.length) {
        const node = queue.pop()!;
        sortedElements.push(node);

        for (const child of graph[node.name]) {
            inDegree[child.name]--;

            // Update descendants map
            descendantsMap[node.name].add(child.name);
            descendantsMap[child.name].forEach(descendant => {
                descendantsMap[node.name].add(descendant);
            });

            if (inDegree[child.name] === 0) {
                queue.push(child);
            }
        }
    }

    // If not all elements are in sortedElements, then there's a cycle.
    if (sortedElements.length !== modelElements.length) {
        throw new Error("There's a cycle in the inheritance graph.");
    }

    return {
        sortedModelElements: sortedElements,
        descendantsMap: descendantsMap
    };
}

export function replaceKeyInMap(map: Record<string, Set<string>>, oldKey: string, newKey: string): void {
    if (map[oldKey]) {
        map[newKey] = map[oldKey];
        delete map[oldKey];
    }
}

export function replaceInMapValues(map: Record<string, Set<string>>, target: string, replacement: string): void {
    for (const key in map) {
        if (map[key].has(target)) {
            map[key].delete(target);
            map[key].add(replacement);
        }
    }
}

export async function extractDocument(fileName: string, services: LangiumServices): Promise<LangiumDocument> {
    const extensions = services.LanguageMetaData.fileExtensions;
    if (!extensions.includes(path.extname(fileName))) {
        console.error(`Please choose a file with one of these extensions: ${extensions}.`);
        process.exit(1);
    }

    if (!fs.existsSync(fileName)) {
        console.error(`File ${fileName} does not exist.`);
        process.exit(1);
    }

    const document = services.shared.workspace.LangiumDocuments.getOrCreateDocument(URI.file(path.resolve(fileName)));
    await services.shared.workspace.DocumentBuilder.build([document], { validation: true } as BuildOptions);

    const validationErrors = (document.diagnostics ?? []).filter(e => e.severity === 1);
    if (validationErrors.length > 0) {
        console.error('There are validation errors:');
        for (const validationError of validationErrors) {
            console.error(
                `line ${validationError.range.start.line + 1}: ${validationError.message} [${document.textDocument.getText(
                    validationError.range
                )}]`
            );
        }
        process.exit(1);
    }

    return document;
}

export async function extractAstNode<T extends AstNode>(fileName: string, services: LangiumServices): Promise<T> {
    return (await extractDocument(fileName, services)).parseResult?.value as T;
}

interface FilePathData {
    destination: string;
    name: string;
}

export function extractDestinationAndName(filePath: string, destination: string | undefined): FilePathData {
    filePath = path.basename(filePath, path.extname(filePath)).replace(/[.-]/g, '');
    return {
        destination: destination ?? path.join(path.dirname(filePath), 'generated'),
        name: path.basename(filePath)
    };
}

export function mergeArrays(baseArray: any[], dominantArray: any[], uniquePropertyName: string): any[] {
    // Handle undefined arrays and specialProperty
    if (!baseArray && !dominantArray) {
        return [];
    }
    if (!uniquePropertyName) {
        throw new Error('The unique property name must be defined.');
    }

    baseArray = baseArray || [];
    dominantArray = dominantArray || [];

    const map: Record<string, any> = {};

    // Convert the first array into a map
    for (const obj of baseArray) {
        const key = String(obj[uniquePropertyName]);
        map[key] = obj;
    }

    // Iterate through the second array to merge
    for (const obj of dominantArray) {
        const key = String(obj[uniquePropertyName]);
        map[key] = obj; // Overwrite if exists, or add if it doesn't
    }

    // Convert the map back to an array
    return Object.values(map);
}

export function copyDirectory(source: string, target: string): void {
    if (!fs.existsSync(target)) {
        fs.mkdirSync(target, { recursive: true });
    }

    const files = fs.readdirSync(source, { withFileTypes: true });

    for (const file of files) {
        const sourcePath = path.join(source, file.name);
        const targetPath = path.join(target, file.name);

        if (file.isDirectory()) {
            copyDirectory(sourcePath, targetPath);
        } else if (file.isFile()) {
            fs.copyFileSync(sourcePath, targetPath);
        }
    }
}
