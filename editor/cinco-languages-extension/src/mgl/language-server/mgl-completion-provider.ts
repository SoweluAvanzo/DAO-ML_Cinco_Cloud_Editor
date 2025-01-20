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
import {
    CompletionAcceptor,
    CompletionContext,
    CstNode,
    DefaultCompletionProvider,
    isCompositeCstNode,
    LangiumDocument,
    LangiumServices,
    NextFeature,
    stream
} from 'langium';
import {
    CompletionParams,
    CancellationToken,
    CompletionList,
    CompletionItem,
    CompletionItemKind,
    InsertTextFormat
} from 'vscode-languageserver';
import { AbstractElement, isKeyword } from 'langium/lib/grammar/generated/ast';
import { Annotation } from '@cinco-glsp/cinco-languages/lib/generated/ast';
import { MglAnnotations } from '@cinco-glsp/cinco-languages';

export class MglCompletionProvider extends DefaultCompletionProvider {
    services: LangiumServices;
    annotations = MglAnnotations.allAnnotationNames;
    hooks = MglAnnotations.HOOK_VALUES;

    entityCompletions: { gate: (node: CstNode) => boolean; completion: CompletionItem }[] = [
        this.createGraphModelCompletion,
        this.createNodeCompletion,
        this.createContainerCompletion,
        this.createEdgeCompletion,
        this.createTypeCompletion,
        this.createEnumCompletion
    ];

    propertyCompletions: { gate: (node: CstNode) => boolean; completion: CompletionItem }[] = [
        this.createAttributeCompletion,
        this.createComplexReferenceCompletion,
        this.createFullAttributeCompletion,
        this.createFullComplexReferenceCompletion,
        this.createAnnotationCompletion,
        this.createHookAnnotationCompletion,
        this.createMoreHookCompletion,
        this.createImportCompletion,
        this.createStealthImportCompletion,
        this.createIdCompletion,
        this.createStylePathCompletion,
        this.createPrimeReferenceCompletion
    ];

    constructor(services: LangiumServices) {
        super(services);
        this.services = services;
    }

    override async getCompletion(
        document: LangiumDocument,
        params: CompletionParams,
        _?: CancellationToken
    ): Promise<CompletionList | undefined> {
        const items: CompletionItem[] = [];
        const contexts = this.buildContexts(document, params.position);

        // add default behaviour items
        const acceptor: CompletionAcceptor = (context, value) => {
            const completionItem = this.fillCompletionItem(context, value);
            const currentToken = context.document.textDocument.getText().substring(context.tokenOffset, context.tokenEndOffset);
            if (value.kind === CompletionItemKind.Keyword && (!value.label?.includes(currentToken) || currentToken.length <= 0)) {
                return;
            }
            if (completionItem) {
                items.push(completionItem);
            }
        };
        const distinctionFunction = (element: NextFeature): string | AbstractElement => {
            if (isKeyword(element.feature)) {
                return element.feature.value;
            } else {
                return element.feature;
            }
        };
        const completedFeatures: NextFeature[] = [];
        for (const context of contexts) {
            await Promise.all(
                stream(context.features)
                    .distinct(distinctionFunction)
                    .exclude(completedFeatures)
                    .map(e => this.completionFor(context, e, acceptor))
            );
            // Do not try to complete the same feature multiple times
            completedFeatures.push(...context.features);

            // add completion snippets for properties
            for (const com of this.propertyCompletions) {
                const cstNodes = this.getCstNodes(context);
                for (const cstNode of cstNodes) {
                    try {
                        if (com.gate(cstNode)) {
                            items.push(com.completion);
                        }
                    } catch (e) {
                        console.log(e);
                    }
                }
            }
            // add completion snippets for structs
            for (const com of this.entityCompletions) {
                const cstNodes = this.getCstNodes(context);
                for (const cstNode of cstNodes) {
                    try {
                        if (com.gate(cstNode)) {
                            items.push(com.completion);
                        }
                    } catch (e) {
                        console.log(e);
                    }
                }
            }
        }
        return CompletionList.create(this.deduplicateItems(items), true);
    }

    private getCstNodes(context: CompletionContext): CstNode[] {
        if (!context || (!context.node?.$cstNode?.root && !context.document.parseResult.value.$cstNode)) {
            return [];
        }
        // check node
        const positionOffset = context.offset;
        const rootCstNode = context.document.parseResult.value.$cstNode ?? context.node!.$cstNode!.root!;
        const result: CstNode[] = [];

        // search at actual position
        let foundNode = this.findLeafNodeAtOffset(rootCstNode, positionOffset);
        if (foundNode) {
            result.push(foundNode);
            return result;
        }
        let currentOffset = positionOffset - 1;
        // search to start
        do {
            foundNode = this.findLeafNodeAtOffset(rootCstNode, currentOffset);
            if (foundNode && result.indexOf(foundNode) < 0) {
                result.push(foundNode);
                break;
            }
            currentOffset -= 1;
        } while (currentOffset > 0);
        // search to end
        currentOffset = positionOffset + 1;
        do {
            foundNode = this.findLeafNodeAtOffset(rootCstNode, currentOffset);
            if (foundNode && result.indexOf(foundNode) < 0) {
                result.push(foundNode);
                break;
            }
            currentOffset += 1;
        } while (currentOffset < context.document.textDocument.getText().length);

        return result;
    }

    private findLeafNodeAtOffset(node: CstNode, offset: number): CstNode | undefined {
        // If the node has no children, it is a leaf node
        if (!isCompositeCstNode(node)) {
            return node;
        }
        // Otherwise, search through the children to find the correct node at the offset
        for (const child of node.children) {
            if (child.offset <= offset && child.end >= offset) {
                // Recursively search for the correct leaf node within this child
                return this.findLeafNodeAtOffset(child, offset);
            }
        }
        return node;
    }

    private createMglStructCompletion(type: string, detail: string, content: string): CompletionItem {
        return {
            label: 'Create ' + type,
            kind: CompletionItemKind.Struct,
            detail: detail,
            insertText: type + ' ${1:' + type + 'Name} {\n' + content + '\n}',
            insertTextFormat: InsertTextFormat.Snippet // Use Snippet to allow placeholders
        };
    }

    private createMglPropertyCompletion(keyword: string, detail: string, content: string): CompletionItem {
        return {
            label: keyword,
            kind: CompletionItemKind.Struct,
            detail: detail,
            insertText: content,
            insertTextFormat: InsertTextFormat.Snippet // Use Snippet to allow placeholders
        };
    }

    get createGraphModelCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateStruct(cstNode),
            completion: this.createMglStructCompletion(
                'graphmodel',
                'Create a graphmodel',
                //    '\t// Path to an icon for representation (currently not supported)\n'
                // +   '\t// iconPath "./someIcon.png"\n'
                '\t// fileExtension\n' +
                    '\tdiagramExtension ${2:"ext"}\n' +
                    '\t// containments\n' +
                    '\tcontainableElements(${3:/* <outgoingEdgeType>[<lowerBound or *>, <UpperBound or *>], ... */})\n\n' +
                    '\t// attributes\n' +
                    '\t${4:attr}'
            )
        };
    }

    get createNodeCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateStruct(cstNode),
            completion: this.createMglStructCompletion(
                'node',
                'Create a node',
                '\tstyle ${2:styleName}\n' +
                    '\t// edges\n' +
                    '\tincomingEdges(${3:/* <incomingEdgeType>[<lowerBound or *>, <UpperBound or *>], ... */})\n' +
                    '\toutgoingEdges(${4:/* <outgoingEdgeType>[<lowerBound or *>, <UpperBound or *>], ... */})\n\n' +
                    '\t// attributes\n' +
                    '\t${5:attr}'
            )
        };
    }

    get createContainerCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateStruct(cstNode),
            completion: this.createMglStructCompletion(
                'container',
                'Create a container',
                '\t// style\n' +
                    '\tstyle ${2:styleName}\n' +
                    '\t// containments\n' +
                    '\tcontainableElements(${3:/* <outgoingEdgeType>[<lowerBound or *>, <UpperBound or *>], ... */})\n' +
                    '\t// edges\n' +
                    '\tincomingEdges(${4:/* <incomingEdgeType>[<lowerBound or *>, <UpperBound or *>], ... */})\n' +
                    '\toutgoingEdges(${5:/* <outgoingEdgeType>[<lowerBound or *>, <UpperBound or *>], ... */})\n\n' +
                    '\t// attributes\n' +
                    '\t${6:attr}'
            )
        };
    }

    get createEdgeCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateStruct(cstNode),
            completion: this.createMglStructCompletion(
                'edge',
                'Create an edge',
                '\t// style\n' + '\tstyle ${2:styleName}\n\n' + '\t// attributes\n' + '\t${3:attr}'
            )
        };
    }

    get createEnumCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateStruct(cstNode),
            completion: this.createMglStructCompletion('enum', 'Create an enum', '\t${1:Value1}\n\t${2:Value2}')
        };
    }

    get createTypeCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateStruct(cstNode),
            completion: this.createMglStructCompletion('type', 'Create a type', '\t$0')
        };
    }

    get createIdCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateId(cstNode),
            completion: this.createMglPropertyCompletion('Specify id', 'Specify the MGLs id', 'id ${1:package.name}')
        };
    }

    get createStylePathCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateStylePath(cstNode),
            completion: this.createMglPropertyCompletion('Specify id', 'Specify the MGLs id', 'stylePath "${1:relativePathToMSL}"')
        };
    }

    get createImportCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateImport(cstNode),
            completion: this.createMglPropertyCompletion(
                'Declare import',
                'Declare an import',
                'import "${1:relativePath}" as ${2:importName}'
            )
        };
    }

    get createStealthImportCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateImport(cstNode),
            completion: this.createMglPropertyCompletion(
                'Declare stealth import',
                'Declare stealth import',
                'stealth import "${1:relativePath}" as ${2:importName}'
            )
        };
    }

    get createAttributeCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateAttribute(cstNode),
            completion: this.createMglPropertyCompletion(
                'Primitive Attribute',
                'Create attribute: attr string as stringAttribute = "defaultValue"',
                'attr ${1|string,number,boolean,Date|} as ${2:attributeName} = ${3|"defaultValue",true,false,42|}'
            )
        };
    }

    get createFullAttributeCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateAttribute(cstNode),
            completion: this.createMglPropertyCompletion(
                'Full Primitive Attribute',
                'Create full primitive attribute: final unique attr string as stringAttribute = "defaultValue"',
                // eslint-disable-next-line max-len
                'final unique attr ${1|string,number,boolean,Date|} as ${2:attributeName} = ${3|"defaultValue",true,false,42|}'
            )
        };
    }

    get createPrimeReferenceCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateAttribute(cstNode),
            completion: this.createMglPropertyCompletion(
                'PrimeReference Attribute',
                'Create full primitive attribute: final unique attr string as stringAttribute = "defaultValue"',
                // eslint-disable-next-line max-len
                'prime ${1|this::ReferencedType,importName::ReferencedType,Node,Edge,Container,ModelElement,ModelElementContainer,GraphModel|} as ${2:referenceName}'
            )
        };
    }

    get createComplexReferenceCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateAttribute(cstNode),
            completion: this.createMglPropertyCompletion(
                'Complex Reference Attribute',
                'Create complex reference: attr Node1 as nodeReference',
                // eslint-disable-next-line max-len
                'attr ${1:ModelElementType} as ${2:attributeName}'
            )
        };
    }

    get createFullComplexReferenceCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateAttribute(cstNode),
            completion: this.createMglPropertyCompletion(
                'Full Complex Reference Attribute',
                'Create full complex reference: final unique override attr Node1 as nodeReference',
                // eslint-disable-next-line max-len
                'final unique override attr ${1:ModelElementType} as ${2:attributeName}'
            )
        };
    }

    get createAnnotationCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.canCreateAnnotation(cstNode),
            completion: this.createMglPropertyCompletion(
                'Annotation',
                'Choose an Annotation out of these supported ones',
                // eslint-disable-next-line max-len
                '@${1|' + this.annotations.join(',') + '|}(${2:/*hookParameter*/}) '
            )
        };
    }

    get createHookAnnotationCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        const hookString = this.hooks.join(',');
        return {
            gate: (cstNode: CstNode) => this.canCreateHookAnnotation(cstNode),
            completion: this.createMglPropertyCompletion(
                'Hook-Annotation',
                'Create a Hook Annotation',
                'Hooks(${1:ClassName}, ${2|' + hookString + '|}, ${0})'
            )
        };
    }

    get createMoreHookCompletion(): { gate: (cstNode: CstNode) => boolean; completion: CompletionItem } {
        const hookString = this.hooks.join(',');
        return {
            gate: (cstNode: CstNode) => this.isInsideHookAnnotation(cstNode),
            completion: this.createMglPropertyCompletion('Add Hook', 'Add a Hook', '${1|' + hookString + '|}, ${0}')
        };
    }

    canCreateId(node: CstNode): boolean {
        return this.isInsideMGL(node) && !this.isInsideStruct(node);
    }

    canCreateStylePath(node: CstNode): boolean {
        return this.isInsideMGL(node) && !this.isInsideStruct(node);
    }

    canCreateImport(node: CstNode): boolean {
        return this.isInsideMGL(node) && !this.isInsideStruct(node);
    }

    canCreateAttribute(node: CstNode): boolean {
        return this.isInsideModelElement(node);
    }

    canCreateHookAnnotation(node: CstNode): boolean {
        return (
            !this.isInsideAnnotation(node) && !this.isInsideAttribute(node) && (this.isInsideModelElement(node) || this.isInsideMGL(node))
        );
    }

    canCreateAnnotation(node: CstNode): boolean {
        return (
            !this.isInsideAnnotation(node) && (this.isInsideAttribute(node) || this.isInsideModelElement(node) || this.isInsideMGL(node))
        );
    }

    canCreateStruct(node: CstNode): boolean {
        return this.isInsideMGL(node) && !this.isInsideStruct(node) && !this.isInsideAnnotation(node);
    }

    isInsideMGL(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isMGL(type)) !== undefined;
    }

    isInsideStruct(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isStruct(type)) !== undefined;
    }

    isInsideEnum(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isEnumType(type)) !== undefined;
    }

    isInsideModelElement(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isModelElementType(type)) !== undefined;
    }

    isInsideHookAnnotation(node: CstNode): boolean {
        const result = this.isInside(node, (type: string | undefined) => this.isAnnotation(type));
        return result !== undefined && (result.element as Annotation).name === 'Hooks';
    }

    isInsideAnnotation(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isAnnotation(type)) !== undefined;
    }

    isInsideAttribute(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isAttribute(type)) !== undefined;
    }

    isMGL(type: string | undefined): boolean {
        return ['MglModel'].includes(type ?? '');
    }

    isStruct(type: string | undefined): boolean {
        return (type !== undefined && this.isModelElementType(type)) || this.isEnumType(type);
    }

    isModelElementType(type: string | undefined): boolean {
        return ['GraphModel', 'Node', 'NodeContainer', 'Edge', 'UserdefinedType'].includes(type ?? '');
    }

    isEnumType(type: string | undefined): boolean {
        return ['Enum'].includes(type ?? '');
    }

    isAnnotation(type: string | undefined): boolean {
        return ['Annotation'].includes(type ?? '');
    }

    isAttribute(type: string | undefined): boolean {
        return ['PrimitiveAttribute', 'ComplexAttribute', 'ReferencedModelElement'].includes(type ?? '');
    }

    isInside(node: CstNode, gate: (type: string) => boolean): CstNode | undefined {
        if (gate(node.element.$type)) {
            return node;
        }
        if (node.parent) {
            this.isInside(node.parent, gate);
        }
        return undefined;
    }
}
