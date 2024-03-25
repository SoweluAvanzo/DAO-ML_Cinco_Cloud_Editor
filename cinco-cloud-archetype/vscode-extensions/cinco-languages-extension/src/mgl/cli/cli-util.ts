import chalk from 'chalk';
import path from 'path';
import fs from 'fs';
import { AstNode, LangiumDocument, LangiumServices } from 'langium';
import { URI } from 'vscode-uri';
import { Appearance, MglModel, ModelElement, Style, isEnum } from '../../generated/ast';

export function topologicalSortWithDescendants(model: MglModel, importedModels: MglModel[]): {
    sortedModelElements: ModelElement[],
    descendantsMap: Record<string, Set<string>>
} {
    const graph: Record<string, ModelElement[]> = {};
    const inDegree: Record<string, number> = {};
    const descendantsMap: Record<string, Set<string>> = {};
    const importedModelElements = importedModels.map(model => model.modelElements).flat();
    const allModelElements = model.modelElements.concat(importedModelElements);
    const mglPath = path.parse(model.$document?.uri.fsPath ?? '');
    
    // Initialize the graph, in-degree maps, and descendants map
    for (const element of allModelElements) {
        const id = getElementTypeId(element);
        graph[id] = [];
        inDegree[id] = 0;
        descendantsMap[id] = new Set();
    }

    // Build the graph.
    for (const element of allModelElements) {
        if (!isEnum(element)) {
            if (element.localExtension) {
                if (element.localExtension.ref) {
                    const localExtensionId = getElementTypeId(element.localExtension.ref);
                    if (!graph[localExtensionId]) {
                        throw new Error(`Element ${element.localExtension} not found.`);
                    }
                    graph[localExtensionId].push(element);
                    inDegree[element.name]++;
                } 
            }  else if(element.externalExtension) {
                if(!element.externalExtension.import.ref) {
                    throw new Error("External reference can not be resolved!");
                }
                // find model and modelElements
                const externalPath = path.join(mglPath.dir, element.externalExtension.import.ref?.importURI);
                // add all extended modelElements
                for(const extendedElement of element.externalExtension.elements) {
                    allModelElements.forEach(externalElement => {
                        if(getElementTypeId(externalElement) === constructElementTypeId(extendedElement, externalPath)) {
                            const id = getElementTypeId(externalElement);
                            graph[id].push(element);
                        }
                    });
                }
            }
        }
    }

    // Array for order of elements.
    const sortedElements: ModelElement[] = [];

    // Array for elements with in-degree = 0.
    const queue: ModelElement[] = allModelElements.filter(element => inDegree[getElementTypeId(element)] === 0);

    while (queue.length) {
        const node = queue.pop()!;
        sortedElements.push(node);

        const id = getElementTypeId(node);
        for (const child of graph[id]) {
            const childId = getElementTypeId(child);
            inDegree[childId]--;
            
            // Update descendants map
            descendantsMap[id].add(childId);
            descendantsMap[childId].forEach(descendant => {
                descendantsMap[id].add(descendant);
            });
            
            if (inDegree[childId] === 0) {
                queue.push(child);
            }
        }
    }

    // If not all elements are in sortedElements, then there's a cycle.
    if (sortedElements.length !== allModelElements.length) {
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
        console.error(chalk.yellow(`Please choose a file with one of these extensions: ${extensions}.`));
        process.exit(1);
    }

    if (!fs.existsSync(fileName)) {
        console.error(chalk.red(`File ${fileName} does not exist.`));
        process.exit(1);
    }

    const document = services.shared.workspace.LangiumDocuments.getOrCreateDocument(URI.file(path.resolve(fileName)));
    await services.shared.workspace.DocumentBuilder.build([document], { validationChecks: 'all' });

    const validationErrors = (document.diagnostics ?? []).filter(e => e.severity === 1);
    if (validationErrors.length > 0) {
        console.error(chalk.red('There are validation errors:'));
        for (const validationError of validationErrors) {
            console.error(chalk.red(
                `line ${validationError.range.start.line + 1}: ${validationError.message} [${document.textDocument.getText(validationError.range)}]`
            ));
        }
        process.exit(1);
    }

    return document;
}

export async function extractAstNode<T extends AstNode>(fileName: string, services: LangiumServices): Promise<T> {
    return (await extractDocument(fileName, services)).parseResult?.value as T;
}

interface FilePathData {
    destination: string,
    name: string
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
    if (!baseArray && !dominantArray) return [];
    if (!uniquePropertyName) throw new Error("The unique property name must be defined.");

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
        map[key] = obj;  // Overwrite if exists, or add if it doesn't
    }

    // Convert the map back to an array
    return Object.values(map);
}

export function copyDirectory(source: string, target: string) {
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


export function getElementTypeId(element: ModelElement | Appearance | Style): string {
    const containerPath = element.$container.$document?.uri.fsPath;
    if (containerPath === undefined) {
        throw new Error(
        "Model is not associated with any document. A uri is needed!"
        );
    }
    return constructElementTypeId(element.name, containerPath);
}

export function constructElementTypeId(elementName: string, containerPath: string): string {
    const containerName = path.parse(containerPath).name;
    return containerName.toLocaleLowerCase() + ":" + elementName.toLowerCase();
}
