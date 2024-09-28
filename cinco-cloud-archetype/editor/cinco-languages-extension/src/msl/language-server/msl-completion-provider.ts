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
    CompletionAcceptor, CompletionContext, CstNode, DefaultCompletionProvider,
    isCompositeCstNode, LangiumDocument, LangiumServices, NextFeature, stream
} from 'langium';
import {
    CompletionParams, CancellationToken, CompletionList, CompletionItem, CompletionItemKind,
    InsertTextFormat
} from 'vscode-languageserver';
import { AbstractElement, isKeyword } from 'langium/lib/grammar/generated/ast';

export class MslCompletionProvider extends DefaultCompletionProvider {
    services: LangiumServices;

    propertyCompletions: {gate: (node: CstNode) => boolean, completion: CompletionItem}[] = [
        // nodeStyle properties
        this.createNodeStyleCompletion,
        this.createNodeFragmentCompletion,
        this.createFixedFragmentCompletion,
        this.createShapeFragmentCompletion,
        // edgeStyle properties
        this.createEdgeStyleCompletion,
        this.createEdgeFragmentCompletion,
        this.createDecoratorCompletion,
        this.createPredefinedDecoratorCompletion,
        this.createDecoratorShapeCompletion,
        this.createLocationCompletion,
        // appearance properties
        this.createAppearanceCompletion,
        this.createInlineAppearanceCompletion,
        this.createInlineAppearanceReferenceCompletion,
        this.createAppearanceProviderFragmentCompletion,
        this.createAppearanceFragmetCompletion,
        this.createBackgroundCompletion,
        this.createForegroundCompletion,
        this.createFilledCompletion,
        this.createImagePathCompletion,
        this.createLineStyleCompletion,
        this.createLineWidthCompletion,
        this.createTransparencyCompletion,
        this.createFontCompletion,
        // shapes
        this.createRectangleShapeCompletion, // TODO: Evaluate HERE WEITERMAKEN
        this.createRoundedRectangleShapeCompletion,
        this.createEllipseShapeCompletion,
        this.createPolygonShapeCompletion,
        this.createTextShapeCompletion,
        this.createMultiTextShapeCompletion,
        this.createImageShapeCompletion,
        this.createWebViewShapeCompletion,
        this.createPolylineShapeCompletion,
        // shape properties
        this.createSizeFragmentCompletion,
        this.createPositionFragmentCompletion,
        this.createAlignmentFragmentCompletion,
        this.createCornerFragmentCompletion,
        this.createPointsFragmentCompletion,
        this.createTextValueFragmentCompletion,
        this.createPathFragmentCompletion,
        this.createScrollableFragmentCompletion,
        this.createPaddingFragmentCompletion,
        this.createContentFragmentCompletion
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
            if(value.kind === CompletionItemKind.Keyword
                && (!value.label?.includes(currentToken)
                    || currentToken.length <= 0
                )
            ) {
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
            for(const com of this.propertyCompletions) {
                const cstNodes = this.getCstNodes(context);
                for(const cstNode of cstNodes)
                {
                    if(com.gate(cstNode)) {
                        items.push(com.completion);
                    }
                }
            }
        }
        return CompletionList.create(this.deduplicateItems(items), true);
    }

    private getCstNodes(context: CompletionContext): CstNode[] {
        if(!context || (!context.node?.$cstNode?.root && !context.document.parseResult.value.$cstNode)) {
            return [];
        }
        // check node
        const positionOffset = context.offset;
        const rootCstNode = context.document.parseResult.value.$cstNode ?? context.node!.$cstNode!.root!;
        const result: CstNode[] = [];

        // search at actual position
        let foundNode = this.findLeafNodeAtOffset(rootCstNode, positionOffset);
        if(foundNode) {
            result.push(foundNode);
            return result;
        }
        let currentOffset = positionOffset - 1;
        // search to start
        do {
            foundNode = this.findLeafNodeAtOffset(rootCstNode, currentOffset);
            if(foundNode && result.indexOf(foundNode) < 0) {
                result.push(foundNode);
                break;
            }
            currentOffset-=1;
        } while(currentOffset > 0);
        // search to end
        currentOffset = positionOffset + 1;
        do {
            foundNode = this.findLeafNodeAtOffset(rootCstNode, currentOffset);
            if(foundNode && result.indexOf(foundNode) < 0) {
                result.push(foundNode);
                break;
            }
            currentOffset+=1;
        } while(currentOffset < context.document.textDocument.getText().length);

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

    private createPropertyCompletion(keyword: string, detail: string, content: string): CompletionItem {
        return {
            label: keyword,
            kind: CompletionItemKind.Struct,
            detail: detail,
            insertText: content,
            insertTextFormat: InsertTextFormat.Snippet // Use Snippet to allow placeholders
        };
    }

    /**
     * Appearance Completion
     */

    get createAppearanceCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateStyleStruct(cstNode),
            completion: this.createPropertyCompletion('Create appearance', 'Create an appearance',
                'appearance ${'+getNextIndex()+':appearanceName}' + `{\n${this.getAppearanceFragment(getNextIndex,'\t')}\n}`
            )
        };
    }

    get createInlineAppearanceCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateInlineAppearanceStruct(cstNode),
            completion: this.createPropertyCompletion('Create inline-appearance', 'Create an inline-appearance',
                this.getInlineAppearanceFragment(getNextIndex)
            )
        };
    }

    get createInlineAppearanceReferenceCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateInlineAppearanceStruct(cstNode),
            completion: this.createPropertyCompletion('Reference an appearance', 'Reference an appearance',
                this.getInlineAppearanceReferenceFragment(getNextIndex)
            )
        };
    }

    get createAppearanceProviderFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateNodeFragment(cstNode) || this.canCreateEdgeFragment(cstNode),
            completion: this.createPropertyCompletion('Specify appearanceProvider', 'Specify appearanceProvider',
                this.getAppearanceProviderFragmet(getNextIndex)
            )
        };
    }

    getInlineAppearanceFragment(getNextIndex: () => number, indentations: string = ''): string {
        return indentations + 'appearance {\n'
        +       this.getAppearanceFragment(getNextIndex, indentations + '\t') + '\n'
        +       indentations + '}';
    }

    getInlineAppearanceReferenceFragment(getNextIndex: () => number): string {
        return 'appearance ${'+getNextIndex()+':referencedAppearance}';
    }

    getAppearanceProviderFragmet(getNextIndex: () => number): string {
        return '${'+getNextIndex()+'|/* no appearanceProvider */,appearanceProvider("appearanceProviderClassName")|}'
    }

    get createBackgroundCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.isInsideAppearance(cstNode),
            completion: this.createPropertyCompletion('Specify background', 'Specify background',
                'background (${1:255},${2:255},${3:255})'
            )
        };
    }

    get createForegroundCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.isInsideAppearance(cstNode),
            completion: this.createPropertyCompletion('Specify foreground', 'Specify foreground',
                'foreground (${1:255},${2:255},${3:255})'
            )
        };
    }

    get createFilledCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.isInsideAppearance(cstNode),
            completion: this.createPropertyCompletion('Specify filled', 'Specify filled',
                'filled ${1|true,false|}'
            )
        };
    }

    get createImagePathCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.isInsideAppearance(cstNode),
            completion: this.createPropertyCompletion('Specify imagePath', 'Specify imagePath',
                'imagePath ("${1:relativePath}")'
            )
        };
    }

    get createLineStyleCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.isInsideAppearance(cstNode),
            completion: this.createPropertyCompletion('Specify lineStyle', 'Specify lineStyle',
                'lineStyle ${1|DASH,DASHDOT,DASHDOTDOT,DOT,SOLID|}'
            )
        };
    }

    get createLineWidthCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.isInsideAppearance(cstNode),
            completion: this.createPropertyCompletion('Specify lineWidth', 'Specify lineWidth',
                'lineWidth ${1:3}'
            )
        };
    }

    get createTransparencyCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.isInsideAppearance(cstNode),
            completion: this.createPropertyCompletion('Specify transparency', 'Specify transparency',
                'transparency ${1:1.0}'
            )
        };
    }

    get createFontCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        return {
            gate: (cstNode: CstNode) => this.isInsideAppearance(cstNode),
            completion: this.createPropertyCompletion('Specify font', 'Specify font',
                'font "${1|Verdana,TimesNewRoman|}"(${2|BOLD,/* not BOLD */|} ${3|ITALIC,/* not ITALIC */|} ${4:25})'
            )
        };
    }

    get createAppearanceFragmetCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.isInsideAppearance(cstNode),
            completion: this.createPropertyCompletion('Specify all appearance-properties', 'Specify all appearance-properties',
                this.getAppearanceFragment(getNextIndex)
            )
        };
    }

    getAppearanceFragment(getNextIndex: () => number, indentations = ''): string {
        return    indentations+'background (${'+getNextIndex()+':255},${'+(getNextIndex())+':255},${'+(getNextIndex())+':255})\n'
        +   indentations+'foreground (${'+(getNextIndex())+':0},${'+(getNextIndex())+':0},${'+(getNextIndex())+':0})\n'
        +   indentations+'filled ${'+(getNextIndex())+'|true,false|}\n'
        +   indentations+'imagePath ("${'+(getNextIndex())+':relativePath}")\n'
        +   indentations+'lineStyle ${'+(getNextIndex())+'|DASH,DASHDOT,DASHDOTDOT,DOT,SOLID|}\n'
        +   indentations+'lineWidth ${'+(getNextIndex())+':3}\n'
        +   indentations+'transparency ${'+(getNextIndex())+':1.0}\n'
        +   indentations+'font "${'+(getNextIndex())+'|Verdana,TimesNewRoman|}"(${'+(getNextIndex())+'|BOLD,/* not BOLD */|} ${'+(getNextIndex())+'|ITALIC,/* not ITALIC */|} ${'+(getNextIndex())+':25})'
    }

    getStyleHeaderFragmet(getNextIndex: () => number): String {
        return '${'+getNextIndex()+':styleName} (${'+getNextIndex()+'|/* No parameter*/,0 /*parameterCount*/|})'
    }

    /**
     * NodeStyle Completion
     */

    get createNodeStyleCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateStyleStruct(cstNode),
            completion: this.createPropertyCompletion('Create nodeStyle', 'Create a nodeStyle',
                `nodeStyle ${this.getStyleHeaderFragmet(getNextIndex)} {\n${this.getNodeFragment(getNextIndex,'\t')}\n}`
            )
        };
    }

    get createNodeFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateNodeFragment(cstNode),
            completion: this.createPropertyCompletion('Specify all node-properties', 'Specify all node-properties',
                this.getNodeFragment(getNextIndex)
            )
        };
    }

    get createFixedFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateNodeFragment(cstNode),
            completion: this.createPropertyCompletion('Specify fixed', 'Specify if node is fixed',
                this.getFixedFragment(getNextIndex)
            )
        };
    }

    get createShapeFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateNodeFragment(cstNode),
            completion: this.createPropertyCompletion('Specify visual shape of style', 'Specify visual shape of style',
                this.getShapeFragment(getNextIndex)
            )
        };
    }

    getNodeFragment(getNextIndex: () => number, indentations = ''): string {
        return  indentations + this.getAppearanceProviderFragmet(getNextIndex) + '\n'
            +   indentations + this.getFixedFragment(getNextIndex) + '\n'
            +   this.getShapeFragment(getNextIndex, indentations)
    }
    getFixedFragment(getNextIndex: () => number): string {
        return '${'+getNextIndex()+'|/* not fixed */,fixed|}';
    }

    getShapeFragment(getNextIndex: () => number, indentations = ''): string {
        return  indentations + '${'+getNextIndex()+'|ellipse,rectangle,roundedRectangle,polygon,text,multiText,image,polyline,webView|} {\n'
            +   indentations + '\t// Shape content\n'
            +   indentations + '}'
    }

    /**
     * EdgeStyle Completion
     */

    get createEdgeStyleCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateStyleStruct(cstNode),
            completion: this.createPropertyCompletion('Create edgeStyle', 'Create an edgeStyle',
                `edgeStyle ${this.getStyleHeaderFragmet(getNextIndex)} {\n${this.getEdgeFragment(getNextIndex,'\t')}\n}`
            )
        };
    }

    get createEdgeFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateEdgeFragment(cstNode),
            completion: this.createPropertyCompletion('Specify all edge-properties', 'Specify all edge-properties',
                this.getEdgeFragment(getNextIndex)
            )
        };
    }

    get createDecoratorCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateEdgeFragment(cstNode),
            completion: this.createPropertyCompletion('Specify decorator', 'Specify decorator',
                this.getDecoratorFragment(getNextIndex)
            )
        };
    }

    get createPredefinedDecoratorCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.isInsideConnectionDecorator(cstNode),
            completion: this.createPropertyCompletion('Specify predefined decorator', 'Specify predefined decorator',
                this.getPredefinedDecoratorFragment(getNextIndex)
            )
        };
    }

    get createDecoratorShapeCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.isInsideConnectionDecorator(cstNode),
            completion: this.createPropertyCompletion('Specify decorator shape', 'Specify decorator shape',
                this.getDecoratorShapeFragment(getNextIndex)
            )
        };
    }

    get createLocationCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.isInsideConnectionDecorator(cstNode),
            completion: this.createPropertyCompletion('Specify location', 'Specify location of decorator',
                this.getLocationFragment(getNextIndex)
            )
        };
    }

    getEdgeFragment(getNextIndex: () => number, indentations = ''): string {
        return  indentations + `${this.getAppearanceProviderFragmet(getNextIndex)}\n`
            +   indentations + 'appearance ${'+(getNextIndex())+'|appearanceName,\{\n\t// inline-appearance content\n\}|}\n'
            // +   indentations+'type ${'+(getNextIndex())+':freeform // currently there are no other supported}\n'
            +   this.getDecoratorFragment(getNextIndex, indentations)
    }

    getDecoratorFragment(getNextIndex: () => number, indentations = ''): string {
        return    indentations + 'decorator ${'+(getNextIndex())+':decoratorName} {\n'
                + indentations +  '\t// decorator content\n'
                + indentations +  '\t' + this.getLocationFragment(getNextIndex) + '\n'
                // + indentations +  '\t' + this.getMovableFragment(getNextIndex) + '\n' // TODO: currently not supported
                + indentations +  '\t${'+getNextIndex()+'|ARROW,DIAMOND,CIRCLE,TRIANGLE,'
                    + 'text {\n\t// shape content\n},'
                    + 'multiText {\n\t// shape content\n},'
                    + 'image {\n\t// shape content\n},'
                    + 'polyline {\n\t// shape content\n},'
                    + 'ellipse {\n\t// shape content\n},'
                    + 'polygon {\n\t// shape content\n},'
                    + 'webView {\n\t// shape content\n}'
                + '|}\n'
                + indentations +  '}';
    }

    getLocationFragment(getNextIndex: () => number): string {
        return 'location (${'+getNextIndex()+':1.0 /* position on the edge as value between 0 and 1 */})'
    }

    getMovableFragment(getNextIndex: () => number): string {
        return '${'+getNextIndex()+'|movable,/* not movable */|}'
    }

    getPredefinedDecoratorFragment(getNextIndex: () => number): string {
        return '${'+getNextIndex()+'|ARROW,DIAMOND,CIRCLE,TRIANGLE|} ${'
            +getNextIndex()+':/* put appearance for decorator here */}';
    }

    getDecoratorShapeFragment(getNextIndex: () => number, indentations = ''): string {
        return indentations + '${'+getNextIndex()+'|'
                    + 'text,'
                    + 'multiText,'
                    + 'image,'
                    + 'polyline,'
                    + 'ellipse,'
                    + 'polygon,'
                    + 'webView'
                + '|} {\n\t// shape content\n}';
    }

    /**
     * Shape Completion
     */

    get createSizeFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateSize(cstNode),
            completion: this.createPropertyCompletion('Specify size', 'specify size',
                this.getSizeFragment(getNextIndex)
            )
        };
    }

    get createPositionFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreatePosition(cstNode),
            completion: this.createPropertyCompletion('Specify position', 'specify position',
                this.getPositionFragment(getNextIndex)
            )
        };
    }

    get createAlignmentFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreatePosition(cstNode),
            completion: this.createPropertyCompletion('Specify alignment', 'specify alignment',
                this.getAlignmentFragment(getNextIndex)
            )
        };
    }

    get createCornerFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateCorner(cstNode),
            completion: this.createPropertyCompletion('Specify corners', 'specify corners',
                this.getCornerFragment(getNextIndex)
            )
        };
    }

    get createPointsFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreatePoints(cstNode),
            completion: this.createPropertyCompletion('Specify points', 'specify points',
                this.getPointsFragment(getNextIndex)
            )
        };
    }

    get createTextValueFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateTextValue(cstNode),
            completion: this.createPropertyCompletion('Specify value (text)', 'specify text',
                this.getTextValueFragment(getNextIndex)
            )
        };
    }

    get createPathFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateImagePath(cstNode),
            completion: this.createPropertyCompletion('Specify path', 'specify path to an Image',
                this.getPathFragment(getNextIndex)
            )
        };
    }

    get createScrollableFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateScrollable(cstNode),
            completion: this.createPropertyCompletion('Specify scrollability', 'specify scrollability',
                this.getScrollableFragment(getNextIndex)
            )
        };
    }

    get createPaddingFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreatePadding(cstNode),
            completion: this.createPropertyCompletion('Specify padding', 'specify padding',
                this.getPaddingFragment(getNextIndex)
            )
        };
    }

    get createContentFragmentCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateWebViewContent(cstNode),
            completion: this.createPropertyCompletion('Specify webview content', 'specify content of webview',
                this.getContentFragment(getNextIndex)
            )
        };
    }

    get createRectangleShapeCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateShapeStruct(cstNode),
            completion: this.createPropertyCompletion('Create rectangle', 'Create a rectangle shape',
                this.getRectangleShapeFragment(getNextIndex)
            )
        };
    }

    get createRoundedRectangleShapeCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateShapeStruct(cstNode),
            completion: this.createPropertyCompletion('Create rounded rectangle', 'Create a rounded rectangle shape',
                this.getRoundedRectangleShapeFragment(getNextIndex)
            )
        };
    }

    get createEllipseShapeCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateShapeStruct(cstNode),
            completion: this.createPropertyCompletion('Create ellipse', 'Create an ellipse shape',
                this.getEllipseShapeFragment(getNextIndex)
            )
        };
    }

    get createPolygonShapeCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateShapeStruct(cstNode),
            completion: this.createPropertyCompletion('Create polygon', 'Create a polygon shape',
                this.getPolygonShapeFragment(getNextIndex)
            )
        };
    }

    get createTextShapeCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateShapeStruct(cstNode),
            completion: this.createPropertyCompletion('Create text', 'Create a text shape',
                this.getTextShapeFragment(getNextIndex)
            )
        };
    }

    get createMultiTextShapeCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateShapeStruct(cstNode),
            completion: this.createPropertyCompletion('Create multiText (multiLine)', 'Create a multiline text shape',
                this.getMultiTextShapeFragment(getNextIndex)
            )
        };
    }

    get createImageShapeCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateShapeStruct(cstNode),
            completion: this.createPropertyCompletion('Create image', 'Create an image shape',
                this.getImageShapeFragment(getNextIndex)
            )
        };
    }

    get createWebViewShapeCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateShapeStruct(cstNode),
            completion: this.createPropertyCompletion('Create webView', 'Create a webView shape',
                this.getWebViewShapeFragment(getNextIndex)
            )
        };
    }

    get createPolylineShapeCompletion(): { gate: (cstNode: CstNode) => boolean, completion: CompletionItem } {
        var index = 1;
        const getNextIndex = () => { const result = index; index+= 1; return result; }
        return {
            gate: (cstNode: CstNode) => this.canCreateShapeStruct(cstNode),
            completion: this.createPropertyCompletion('Create polyline', 'Create a polyline shape',
                this.getPolylineShapeFragment(getNextIndex)
            )
        };
    }

    getRectangleShapeFragment(getNextIndex: () => number, indentations = ''): string {
        return    indentations + 'rectangle {\n'
                + indentations +  '\t// shape content\n'
                + this.getInlineAppearanceFragment(getNextIndex, indentations + '\t') + '\n'
                + indentations +  '\t' + this.getPositionFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getSizeFragment(getNextIndex) + '\n'
                + this.getChildShapeFragment(getNextIndex, indentations + '\t') + '\n'
                + indentations +  '}';
    }

    getRoundedRectangleShapeFragment(getNextIndex: () => number, indentations = ''): string {
        return    indentations + 'roundedRectangle {\n'
                + indentations +  '\t// shape content\n'
                + this.getInlineAppearanceFragment(getNextIndex, indentations + '\t') + '\n'
                + indentations +  '\t' + this.getPositionFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getSizeFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getCornerFragment(getNextIndex) + '\n'
                + this.getChildShapeFragment(getNextIndex, indentations + '\t') + '\n'
                + indentations +  '}';
    }

    getEllipseShapeFragment(getNextIndex: () => number, indentations = ''): string {
        return    indentations + 'ellipse {\n'
                + indentations +  '\t// shape content\n'
                + this.getInlineAppearanceFragment(getNextIndex, indentations + '\t') + '\n'
                + indentations +  '\t' + this.getPositionFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getSizeFragment(getNextIndex) + '\n'
                + this.getChildShapeFragment(getNextIndex, indentations + '\t') + '\n'
                + indentations +  '}';
    }

    getPolygonShapeFragment(getNextIndex: () => number, indentations = ''): string {
        return    indentations + 'polygon {\n'
                + indentations +  '\t// shape content\n'
                + this.getInlineAppearanceFragment(getNextIndex, indentations + '\t') + '\n'
                + indentations +  '\t' + this.getPositionFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getSizeFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getPointsFragment(getNextIndex) + '\n'
                + this.getChildShapeFragment(getNextIndex, indentations + '\t') + '\n'
                + indentations +  '}';
    }

    getTextShapeFragment(getNextIndex: () => number, indentations = ''): string {
        return    indentations + 'text {\n'
                + indentations +  '\t// shape content\n'
                + this.getInlineAppearanceFragment(getNextIndex, indentations + '\t') + '\n'
                + indentations +  '\t' + this.getPositionFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getTextValueFragment(getNextIndex) + '\n'
                + indentations +  '}';
    }

    getMultiTextShapeFragment(getNextIndex: () => number, indentations = ''): string {
        return    indentations + 'multiText {\n'
                + indentations +  '\t// shape content\n'
                + this.getInlineAppearanceFragment(getNextIndex, indentations + '\t') + '\n'
                + indentations +  '\t' + this.getPositionFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getTextValueFragment(getNextIndex) + '\n'
                + indentations +  '}';
    }

    getImageShapeFragment(getNextIndex: () => number, indentations = ''): string {
        return    indentations + 'image {\n'
                + indentations +  '\t// shape content\n'
                + indentations +  '\t' + this.getPositionFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getSizeFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getPathFragment(getNextIndex) + '\n'
                + indentations +  '}';
    }

    getWebViewShapeFragment(getNextIndex: () => number, indentations = ''): string {
        return    indentations + 'webView {\n'
                + indentations +  '\t// shape content\n'
                + indentations +  '\t' + this.getPositionFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getSizeFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getScrollableFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getPaddingFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getContentFragment(getNextIndex) + '\n'
                + indentations +  '}';
    }

    getPolylineShapeFragment(getNextIndex: () => number, indentations = ''): string {
        return    indentations + 'polyline {\n'
                + indentations +  '\t// shape content\n'
                + this.getInlineAppearanceFragment(getNextIndex, indentations + '\t') + '\n'
                + indentations +  '\t' + this.getSizeFragment(getNextIndex) + '\n'
                + indentations +  '\t' + this.getPointsFragment(getNextIndex) + '\n'
                + indentations +  '}';
    }

    getChildShapeFragment(getNextIndex: () => number, indentations = ''): string {
        return indentations + '${'+getNextIndex()+'|'
            + 'rectangle,'
            + 'roundedRectangle,'
            + 'ellipse,'
            + 'polygon,'
            + 'text,'
            + 'multiText,'
            + 'image,'
            + 'webView,'
            + 'polyline'
            + '|} {\n'
            + indentations + '\t// shape content\n'
            + indentations + '}';
    }

    getSizeFragment(getNextIndex: () => number): string {
        return 'size (${'+getNextIndex()+'|/* Width not fixed */,fix /* fixed Width */|} ${'+getNextIndex()+':50 /* Width */}, ${'+getNextIndex()+'|/* Height not fixed */,fix /* fixed Height */|} ${'+getNextIndex()+':50 /* Height */})';
    }

    getPositionFragment(getNextIndex: () => number): string {
        return 'position (${'+getNextIndex()+':0 /* X offset-position */} , ${'+getNextIndex()+':0 /* Y offset-position */})';
    }

    getAlignmentFragment(getNextIndex: () => number): string {
        return 'position (${'+getNextIndex()+'|LEFT,CENTER,RIGHT|} ${'+getNextIndex()+'|0 /* X offset */,/* no X offset */|}, ${'+getNextIndex()+'|TOP,MIDDLE,BOTTOM|} ${'+getNextIndex()+'|0 /* X offset */,/* no X offset */|})';
    }

    getCornerFragment(getNextIndex: () => number): string {
        return 'corner (${'+getNextIndex()+':50 /* CornerWidth */}, ${'+getNextIndex()+':50 /* CornerHeight */})';
    }

    getPointsFragment(getNextIndex: () => number): string {
        return 'points [ ( ${'+getNextIndex()+':0 /* X position */}, ${'+getNextIndex()+':0 /* Y position */} ) /* Point 1 */ ( ${'+getNextIndex()+':0 /* X position */}, ${'+getNextIndex()+':0 /* Y position */} ) /* Point 2 */]';
    }

    getTextValueFragment(getNextIndex: () => number): string {
        return 'value "${'+getNextIndex()+':content}"';
    }

    getPathFragment(getNextIndex: () => number): string {
        return 'path ("${'+getNextIndex()+':relativePath}")';
    }

    getScrollableFragment(getNextIndex: () => number): string {
        return 'scrollable ${'+getNextIndex()+'|true,false|}';
    }

    getPaddingFragment(getNextIndex: () => number): string {
        return 'padding ${'+getNextIndex()+':10}';
    }

    getContentFragment(getNextIndex: () => number): string {
        return 'content ("${'+getNextIndex()+':RelativePathOrHTMLCode}")';
    }

    /**
     * Decorator
     */
    
    /**
     * Can create gates
     */

    canCreateStyleStruct(node: CstNode): boolean {
        return this.isInsideMSL(node) && !this.isInsideStruct(node);
    }

    canCreateNodeFragment(node: CstNode): boolean {
        return this.isInsideNodeStyle(node);
    }

    canCreateEdgeFragment(node: CstNode): boolean {
        return this.isInsideEdgeStyle(node);
    }

    canCreateAppearanceStruct(node: CstNode): boolean {
        return this.isInsideMSL(node) && !this.isInsideStruct(node);
    }

    canCreateInlineAppearanceStruct(node: CstNode): boolean {
        return this.isInsideEdgeStyle(node)
            || (
                // all shapes that have appearance
                this.isInsideShape(node)
                && !this.isInsideImageShape(node)
                && !this.isInsideWebViewShape(node)
            )
            || (
                (this.isInsideAppearanceShape(node)
                || this.isInsideConnectionDecorator(node))
                && (node.element as any)?.predefinedDecorator !== undefined
            );
    }

    canCreateShapeStruct(node: CstNode): boolean {
        return this.isInsideNodeStyle(node)
        || this.isInsideContainerShape(node);
    }

    canCreateGraphicsAlgorithm(node: CstNode): boolean {
        return this.isInsideMSL(node) && 
            (
                this.isInsideConnectionDecorator(node)
            );
    }

    canCreatePredefinedShape(node: CstNode): boolean {
        return this.isInsideMSL(node) && 
            (
                this.isInsideConnectionDecorator(node)
            );
    }

    isInsideMSL(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isMSL(type)) !== undefined;
    }

    isInsideStruct(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isStruct(type)) !== undefined;
    }

    isInsideStyle(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isStyle(type)) !== undefined;
    }

    isInsideNodeStyle(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isNodeStyle(type)) !== undefined;
    }

    isInsideEdgeStyle(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isEdgeStyle(type)) !== undefined;
    }

    isInsideAppearance(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isAppearance(type)) !== undefined;
    }

    isInsideShape(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isShape(type)) !== undefined;
    }

    isInsideContainerShape(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isContainerShape(type)) !== undefined;
    }

    isInsidePredefinedDecorator(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isPredefinedDecorator(type)) !== undefined;
    }

    isInsideConnectionDecorator(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isConnectionDecorator(type)) !== undefined;
    }

    isMSL(type: string | undefined): boolean {
        return ['Styles'].includes(type ?? '');
    }

    isShape(type: string | undefined): boolean {
        return [
            "Text" , "MultiText" , "Image" , "Polyline" , "WebView"
        ].includes(type ?? '') || this.isContainerShape(type ?? '');
    }

    isContainerShape(type: string | undefined): boolean {
        return ["Rectangle", "RoundedRectangle", "Ellipse", "Polygon",].includes(type ?? '');
    }

    isStruct(type: string | undefined): boolean {
        return type !== undefined && this.isStyle(type) || this.isAppearance(type) || this.isShape(type);
    }

    isStyle(type: string | undefined): boolean {
        return this.isNodeStyle(type ?? '') || this.isEdgeStyle(type ?? '');
    }

    isNodeStyle(type: string | undefined): boolean {
        return ['NodeStyle'].includes(type ?? '');
    }

    isEdgeStyle(type: string | undefined): boolean {
        return ['EdgeStyle'].includes(type ?? '');
    }

    isAppearance(type: string | undefined): boolean {
        return ['Appearance', 'InlineAppearance'].includes(type ?? '');
    }

    isPredefinedDecorator(type: string | undefined): boolean {
        return ['PredefinedDecorator'].includes(type ?? '');
    }

    isConnectionDecorator(type: string | undefined): boolean {
        return ['ConnectionDecorator'].includes(type ?? '');
    }

    isInside(node: CstNode, gate: (type: string) => boolean): CstNode | undefined {
        if(gate(node.element.$type)) {
            return node;
        }
        if(node.parent) {
            this.isInside(node.parent, gate);
        }
        return undefined;
    }

    // Shape gates

    canCreateSize(node: CstNode): boolean {
        return this.isInsidePolylineShape(node)
        || this.isInsideWebViewShape(node)
        || this.isInsideImageShape(node)
        || this.isInsideContainerShape(node)
    }

    canCreatePosition(node: CstNode): boolean {
        return this.isInsideShape(node) && !this.isInsidePolylineShape(node)
    }

    canCreateCorner(node: CstNode): boolean {
        return this.isInsideRoundedRectangleShape(node)
    }

    canCreatePoints(node: CstNode): boolean {
        return this.isInsidePolygonShape(node) || this.isInsidePolylineShape(node);
    }

    canCreateTextValue(node: CstNode): boolean {
        return this.isInsideTextShape(node) || this.isInsideMultiTextShape(node);
    }

    canCreateImagePath(node: CstNode): boolean {
        return this.isInsideImageShape(node);
    }

    canCreateScrollable(node: CstNode): boolean {
        return this.isInsideWebViewShape(node);
    }

    canCreatePadding(node: CstNode): boolean {
        return this.isInsideWebViewShape(node);
    }

    canCreateWebViewContent(node: CstNode): boolean {
        return this.isInsideWebViewShape(node);
    }

    isInsideAppearanceShape(node: CstNode): boolean {
        return this.isInsideShape(node)
        && !(
            this.isInsideWebViewShape(node)
            || this.isInsideImageShape(node)
        )
    }

    isInsideRectangleShape(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isRectangleShape(type)) !== undefined;
    }

    isRectangleShape(type: string | undefined): boolean {
        return ['Rectangle'].includes(type ?? '');
    }

    isInsideRoundedRectangleShape(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isRoundedRectangleShape(type)) !== undefined;
    }

    isRoundedRectangleShape(type: string | undefined): boolean {
        return ['RoundedRectangle'].includes(type ?? '');
    }
    
    isInsideEllipseShape(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isEllipseShape(type)) !== undefined;
    }

    isEllipseShape(type: string | undefined): boolean {
        return ['Ellipse'].includes(type ?? '');
    }
    
    isInsidePolygonShape(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isPolygonShape(type)) !== undefined;
    }

    isPolygonShape(type: string | undefined): boolean {
        return ['Polygon'].includes(type ?? '');
    }
    
    isInsideTextShape(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isTextShape(type)) !== undefined;
    }

    isTextShape(type: string | undefined): boolean {
        return ['Text'].includes(type ?? '');
    }
    
    isInsideMultiTextShape(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isMultiTextShape(type)) !== undefined;
    }

    isMultiTextShape(type: string | undefined): boolean {
        return ['MultiText'].includes(type ?? '');
    }
    
    isInsideImageShape(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isImageShape(type)) !== undefined;
    }

    isImageShape(type: string | undefined): boolean {
        return ['Image'].includes(type ?? '');
    }
    
    isInsideWebViewShape(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isWebViewShape(type)) !== undefined;
    }

    isWebViewShape(type: string | undefined): boolean {
        return ['WebView'].includes(type ?? '');
    }
    
    isInsidePolylineShape(node: CstNode): boolean {
        return this.isInside(node, (type: string | undefined) => this.isPolylineShape(type)) !== undefined;
    }

    isPolylineShape(type: string | undefined): boolean {
        return ['Polyline'].includes(type ?? '');
    }
}
