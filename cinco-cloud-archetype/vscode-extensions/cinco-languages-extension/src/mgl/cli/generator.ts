import fs from 'fs';
import path from 'path';
import * as vscode from 'vscode';
import { AbsolutePosition, AbstractPosition, AbstractShape, Alignment, Annotation, Color, ContainerShape, Edge, EdgeElementConnection, EdgeStyle, Font, GraphModel, Image, InlineAppearance, MglModel, ModelElement, MultiText, Node, NodeContainer, NodeStyle, Point, Polygon, Polyline, RoundedRectangle, Shape, Size, Text, isAbsolutePosition, isAlignment, isComplexAttribute, isContainerShape, isCustomDataType, isEdge, isEdgeStyle, isEllipse, isEnum, isGraphModel, isImage, isMultiText, isNode, isNodeContainer, isNodeStyle, isPolygon, isPolyline, isPrimitiveAttribute, isRectangle, isRoundedRectangle, isShape, isText } from '../../generated/ast';
import { extractDestinationAndName, mergeArrays, replaceInMapValues, replaceKeyInMap, topologicalSortWithDescendants } from './cli-util';
import { createMslServices } from '../../msl/language-server/msl-module';
import { extractAstNode } from '../../msl/cli/cli-util';
import { Styles } from '../../generated/ast';
import { NodeFileSystem } from 'langium/node';
import { ContainerType, Specification } from '../model/specification-types';

interface ServerArgs {
    metaDevMode: boolean;
    rootFolder: string;
    languagePath: string;
    workspacePath: string;
    port: number;
}

export class MGLGenerator {
    specification: Specification = {
        graphTypes: [],
        nodeTypes: [],
        edgeTypes: [],
        appearances: [],
        styles: []
    };

    abstractModelElements: Specification = {
        graphTypes: [],
        nodeTypes: [],
        edgeTypes: [],
        appearances: [],
        styles: []
    };

    async generateMetaSpecification(model: MglModel, filePath: string, destination: string | undefined): Promise<string> {
        const data = extractDestinationAndName(filePath, destination);
        const generatedFilePath = path.join(data.destination, 'meta-specification.json');

        // Handle appearances and styles first
        // Trim path to MGL to retrieve the project path
        const pathToProject = filePath.substring(0, filePath.lastIndexOf('/') + 1);
        const appearancesAndStyles = await inferAppearancesAndStyles(pathToProject + model.stylePath);
        this.specification.appearances = appearancesAndStyles.appearances;
        this.specification.styles = appearancesAndStyles.styles;
    
        // Handle MGL
        const { sortedModelElements, descendantsMap } = topologicalSortWithDescendants(model.modelElements);
        const abstractElementTypeIds = [];
    
        // Handle all model elements; abstract definitions are handled separately
        for (const modelElement of sortedModelElements) {
            let elementTypeId = '';
            if (!isEnum(modelElement) && modelElement.isAbstract) {
                elementTypeId = this.handleModelElement(modelElement, this.abstractModelElements);
                abstractElementTypeIds.push(elementTypeId);
            } else {
                elementTypeId = this.handleModelElement(modelElement, this.specification);
            }

            // Replace in descendants map to 
            replaceKeyInMap(descendantsMap, modelElement.name, elementTypeId);
            replaceInMapValues(descendantsMap, modelElement.name, elementTypeId);
        }

        this.introduceInheritanceToConstraints(descendantsMap, abstractElementTypeIds);
    
        if (!fs.existsSync(data.destination)) {
            fs.mkdirSync(data.destination, { recursive: true });
        }
    
        const stringifiedSpecification = JSON.stringify(this.specification, null, 4);
        fs.writeFileSync(generatedFilePath, stringifiedSpecification);
    
        vscode.commands.executeCommand( 'cinco.provide.glsp-server-args').then( result => {
            const serverArgs = result as ServerArgs;
            const targetPath = path.join(serverArgs.rootFolder, serverArgs.languagePath, 'meta-specification.json');
            console.log('Integrating meta-specification to: '+ targetPath)
            fs.writeFileSync(targetPath, stringifiedSpecification);
            vscode.commands.executeCommand('cinco.meta-specification.reload')
        });
    
        return generatedFilePath;
    }
    
    // Constructs the modelElementSpec and returns the resulting elementTypeId
    handleModelElement(modelElement: ModelElement, specification: Specification): string {
        var modelElementSpec : any = {};

        // If parent exists, first copy its entire specifications and overwrite customizations afterwards
        if(!isEnum(modelElement)) {
            const parentName = modelElement.localExtension?.ref?.name.toLowerCase();
            if(parentName) {
                let foundParent = undefined;
                if(isGraphModel(modelElement)) {
                    foundParent = [...this.specification.graphTypes, ...this.abstractModelElements.graphTypes].find((graphType) => 
                        graphType.elementTypeId.split(':')[1] === parentName
                    );
                } else if(isNode(modelElement) || isNodeContainer(modelElement)) {
                    foundParent = [...this.specification.nodeTypes, ...this.abstractModelElements.nodeTypes].find((nodeType) => 
                        nodeType.elementTypeId.split(':')[1] === parentName
                    );
                } else if(isEdge(modelElement)) {
                    foundParent = [...this.specification.edgeTypes, ...this.abstractModelElements.edgeTypes].find((edgeType) => 
                        edgeType.elementTypeId.split(':')[1] === parentName
                    );
                }
                // Copy the parent deeply
                if(foundParent) {
                    modelElementSpec = JSON.parse(JSON.stringify(foundParent));
                }
            }
        }
            
        // This is completed later by prepending the modelElement type (see below)
        modelElementSpec.elementTypeId = modelElement.name.toLowerCase();

        modelElementSpec.type = modelElementSpec.label = modelElement.name;
        
        modelElementSpec.annotations = mergeArrays(modelElementSpec.annotations,
            modelElement.annotations.map(annotation => handleAnnotation(annotation)),
            'name');

        if(!isCustomDataType(modelElement)) {
            // Attributes
            modelElementSpec.attributes = mergeArrays(modelElementSpec.attributes,
                modelElement.attributes.map((attribute) => {
                    const result = {
                        'annotations': attribute.annotations.map(annotation => handleAnnotation(annotation)),
                        'final': attribute.notChangeable,
                        'unique': attribute.unique,
                        'name': attribute.name,
                        'defaultValue': attribute.defaultValue ?? "",
                        'bounds': {
                            'lowerBound': attribute.lowerBound ?? 0,
                            'upperBound': attribute.upperBound ?? 1
                        },
                        // Type is filled in below
                        'type': ''
                    };

                    if(isPrimitiveAttribute(attribute)) {
                        result.type = attribute.dataType
                    } else if(isComplexAttribute(attribute)) {
                        result.type = attribute.type.ref?.name ?? ''
                    }

                    return result;
                }),
                'name');
        }

        if (isGraphModel(modelElement) || isNodeContainer(modelElement)) {
            const containerElement : GraphModel | NodeContainer = modelElement;

            modelElementSpec.containments = mergeElementConstraints(modelElementSpec.containments,
                containerElement.containableElements.map(containableElement => {
                return {
                    lowerBound: containableElement.lowerBound ?? -1,
                    upperBound: containableElement.upperBound ?? -1,
                    // TODO Add handling of externalContainments
                    elements: containableElement.localContainments.map(localContainment => {
                        return 'node:' + localContainment.ref?.name.toLowerCase();
                    }) ?? []
                }
            }));
        }

        if (isNode(modelElement) || isEdge(modelElement) || isNodeContainer(modelElement)) {
            const graphicalElement : Node | Edge | NodeContainer = modelElement;
            const usedStyle = graphicalElement.usedStyle;

            modelElementSpec.view = {
                "style": usedStyle
            };
            if (graphicalElement.styleParameters) {
                modelElementSpec.view.styleParameter = graphicalElement.styleParameters;
            }
        }

        if (isGraphModel(modelElement)) {
            const graphModel : GraphModel = modelElement;

            // Prepend modelElement type to complete elementTypeId
            modelElementSpec.elementTypeId = 'graphmodel:' + modelElementSpec.elementTypeId;
            modelElementSpec.diagramExtension = graphModel.fileExtension;

            specification.graphTypes.push(modelElementSpec);
        }

        if (isNode(modelElement) || isNodeContainer(modelElement)) {
            const node = modelElement as Node | NodeContainer;
            // Retrieve the main (container) shape to display width and height properly
            const usedStyleName = node.usedStyle;
            const usedStyle = this.specification.styles.find((style) => style.name === usedStyleName);
            const mainShape = usedStyle?.shape;

            // Prepend modelElement type to complete elementTypeId
            modelElementSpec.elementTypeId = 'node:' + modelElementSpec.elementTypeId;

            // TODO Check for disable annotation for these
            modelElementSpec.deletable = modelElementSpec.reparentable = modelElementSpec.repositionable = modelElementSpec.resizable = true;
            

            modelElementSpec.width = mainShape?.size?.width ?? 100;
            modelElementSpec.height = mainShape?.size?.height ?? 100;

            // TODO check annotations for this
            modelElementSpec.palettes = [];

            modelElementSpec.incomingEdges = mergeElementConstraints(modelElementSpec.incomingEdges,
                node.incomingEdgeConnections.map(incomingEdgeConnection => {
                    return getEdgeElementConnectionObject(incomingEdgeConnection);
                }));

            modelElementSpec.outgoingEdges = mergeElementConstraints(modelElementSpec.outgoingEdges,
                node.outgoingEdgeConnections.map(outgoingEdgeConnection => {
                    return getEdgeElementConnectionObject(outgoingEdgeConnection);
                }));

            specification.nodeTypes.push(modelElementSpec);
        }

        if (isEdge(modelElement)) {
            modelElementSpec.elementTypeId = 'edge:' + modelElementSpec.elementTypeId;

            specification.edgeTypes.push(modelElementSpec);
        }

        return modelElementSpec.elementTypeId;
    }

    introduceInheritanceToConstraints(descendantMap: Record<string, Set<string>>, abstractElementTypeIds: string[]): void {
        // Helper function to recursively replace elements in a constraints array
        const replaceInConstraints = (constraintsArray?: {lowerBound: number | '*', upperBound: number | '*', elements: string[]}[]) => {
            if (!constraintsArray) return;

            for (let constraint of constraintsArray) {
                let newElements: string[] = [];

                const recursivelyReplace = (element: string) => {
                    if (descendantMap[element]) {
                        if (!abstractElementTypeIds.includes(element)) {
                            newElements.push(element);
                        }
                        for (let descendant of descendantMap[element]) {
                            recursivelyReplace(descendant);
                        }
                    } else {
                        // If the element doesn't have descendants, add it to the newElements list
                        newElements.push(element);
                    }
                };

                for (let element of constraint.elements) {
                    recursivelyReplace(element);
                }

                constraint.elements = newElements;
            }
        };

        // Replace in graphTypes
        if (this.specification.graphTypes) {
            for (let graphType of this.specification.graphTypes) {
                replaceInConstraints(graphType.containments);
            }
        }

        // Replace in nodeTypes
        if (this.specification.nodeTypes) {
            for (let nodeType of this.specification.nodeTypes) {
                if ((nodeType as ContainerType).containments !== undefined) {
                    replaceInConstraints((nodeType as ContainerType).containments);
                }
                replaceInConstraints(nodeType.incomingEdges);
                replaceInConstraints(nodeType.outgoingEdges);
            }
        }
    }
}

async function inferAppearancesAndStyles(stylePath: string): Promise<{appearances: any[], styles: any[]}> {
    const services = createMslServices(NodeFileSystem).Msl;
    const model = await extractAstNode<Styles>(stylePath, services);

    // TODO Type properly
    const result = {
        appearances: [] as any[],
        styles: [] as any[]
    }

    for (const appearance of model.appearances) {
        // TODO Type properly
        let appearanceConfiguration : any = {};
        
        appearanceConfiguration.name = appearance.name;
        appearanceConfiguration.parent = appearance.parent?.ref?.name;
        appearanceConfiguration.lineWidth = appearance.lineWidth;
        appearanceConfiguration.lineStyle = appearance.lineStyle?.lineType;
        appearanceConfiguration.filled = appearance.filled;
        appearanceConfiguration.font = appearance.font?.fontName;
        appearanceConfiguration.imagePath = appearance.imagePath;
        appearanceConfiguration.transparency = appearance.transparency;
        
        const background = appearance.background;
        if(background) {
            appearanceConfiguration.background = handleColor(background);
        }

        const foreground = appearance.foreground;
        if(foreground) {
            appearanceConfiguration.foreground = handleColor(foreground);
        }

        result.appearances.push(appearanceConfiguration);
    }

    for (const style of model.styles) {
        // TODO Type properly
        let styleConfiguration : any = {}

        styleConfiguration.name = style.name;
        styleConfiguration.appearanceProvider = style.appearanceProvider;
        styleConfiguration.parameterCount = style.parameterCount;

        if(isEdgeStyle(style)) {
            const edgeStyle = style as EdgeStyle;

            styleConfiguration.connectionType = edgeStyle.connectionType?.FreeForm;

            // Appereance
            const inlineAppearance = edgeStyle.inlineAppearance;
            if(inlineAppearance) {
                styleConfiguration.appearance = handleInlineAppearance(inlineAppearance);
            } else {
                styleConfiguration.appearance = edgeStyle.referencedAppearance?.ref?.name;
            }

            // Decorators
            styleConfiguration.decorator = edgeStyle.decorator.map(decorator => {
                // TODO type properly
                let decoratorConfiguration : any = {
                    'location': decorator.location,
                    'movable': decorator.movable
                };

                const predefinedDecorator = decorator.predefinedDecorator;
                const decoratorShape = decorator.decoratorShape;
                if(predefinedDecorator) {
                    const predefinedDecoratorConfiguration : any = {};

                    // Shape
                    const shapeType = predefinedDecorator.shape.shapeType;
                    if(shapeType) {
                        predefinedDecoratorConfiguration.shape = 'DecoratorShape.' + shapeType;
                    }

                    // Appereance
                    const inlineAppearance = predefinedDecorator.inlineAppearance;
                    if(inlineAppearance) {
                        predefinedDecoratorConfiguration.appearance = handleInlineAppearance(inlineAppearance);
                    } else {
                        predefinedDecoratorConfiguration.appearance = predefinedDecorator.referencedAppearance?.ref?.name;
                    }

                    decoratorConfiguration.predefinedDecorator = predefinedDecoratorConfiguration;
                } else if(decoratorShape) {
                    // Decorator Shape
                    // This casting is possible because of the union types and because AbstractShape is a superset of GraphicsAlgorithm
                    decoratorConfiguration.decoratorShape = handleAbstractShape(decoratorShape as AbstractShape);
                }

                return decoratorConfiguration;
            });

            styleConfiguration.styleType = 'EdgeStyle';
        }

        if(isNodeStyle(style)) {
            const nodeStyle = style as NodeStyle;
            
            styleConfiguration.styleType = 'NodeStyle';
            
            styleConfiguration.fixed = nodeStyle.fixed;

            styleConfiguration.shape = handleAbstractShape(style.mainShape);
            
        }

        result.styles.push(styleConfiguration);
    }

    return result;
}

function mergeElementConstraints(baseConstraints: {lowerBound: number | '*', upperBound: number | '*', elements: string[]}[], dominantConstraints: {lowerBound: number | '*', upperBound: number | '*', elements: string[]}[]): {lowerBound: number | '*', upperBound: number | '*', elements: string[]}[] {
    if (!baseConstraints && !dominantConstraints) return [];

    baseConstraints = baseConstraints || [];
    dominantConstraints = dominantConstraints || [];

    // Create a set of all unique elements in dominantConstraints
    const dominantConstraintsElements = new Set<string>();
    for (const entry of dominantConstraints) {
        for (const element of entry.elements) {
            dominantConstraintsElements.add(element);
        }
    }

    // Remove elements in baseConstraints that exist in dominantConstraints
    for (const entry of baseConstraints) {
        entry.elements = entry.elements.filter(element => !dominantConstraintsElements.has(element));
    }

    // Concatenate the arrays and filter out entries with empty elements
    return [...baseConstraints, ...dominantConstraints].filter(entry => entry.elements.length > 0);
}


function handleAnnotation(annotation: Annotation) {
    return {
        name : annotation.name,
        values : annotation.value ?? []
    };
}

function handleAbstractShape(abstractShape: AbstractShape) {
    let result: any = {
        'anchorShape': abstractShape.anchorShape
    };

    // Distinguish between ContainerShapes and Shapes and handle specific properties separately
    if(isContainerShape(abstractShape)) {
        const containerShape = abstractShape as ContainerShape;

        // Basic type
        if(isEllipse(containerShape)) {
            result.type = 'ELLIPSE';
        } else if(isPolygon(containerShape)) {
            result.type = 'POLYGON';
        } else if(isRectangle(containerShape)) {
            result.type = 'RECTANGLE';
        } else if(isRoundedRectangle(containerShape)) {
            result.type = 'ROUNDEDRECTANGLE';
        }

        // Appereance
        const inlineAppearance = containerShape.inlineAppearance;
        if(inlineAppearance) {
            result.appearance = handleInlineAppearance(inlineAppearance);
        } else {
            result.appearance = containerShape.referencedAppearance?.ref?.name;
        }

        // Position
        const position = containerShape.position;
        if(position) {
            result.position = handlePosition(position);
        }

        // Size
        const size = containerShape.size;
        if(size) {
            result.size = handleSize(size);
        }

        // Children
        result.children = containerShape.children.map((child) => handleAbstractShape(child));

        if(isRoundedRectangle(containerShape)) {
            const roundedRectangle = containerShape as RoundedRectangle;

            // CornerWidth & CornerHeight
            result.cornerWidth = roundedRectangle.cornerWidth;
            result.cornerHeight = roundedRectangle.cornerHeight;
        }

        if(isPolygon(containerShape)) {
            const polygon = containerShape as Polygon;

            // Points
            result.points = handlePoints(polygon.points);
        }
    } else if(isShape(abstractShape)) {
        const shape = abstractShape as Shape;

        // Basic type
        if(isText(shape)) {
            result.type = 'TEXT';
        } else if(isMultiText(shape)) {
            result.type = 'MULTITEXT';
        } else if(isImage(shape)) {
            result.type = 'IMAGE';
        } else if(isPolyline(shape)) {
            result.type = 'POLYLINE';
        }

        // These are treated weirdly because they are almost disjunctive; should be optimized at language level
        if(!isImage(shape)) {
            const nonImage = shape as Text | MultiText | Polyline;

            // Appearance
            const inlineAppearance = nonImage.inlineAppearance;
            if(inlineAppearance) {
                result.appearance = handleInlineAppearance(inlineAppearance);
            } else {
                result.appearance = nonImage.referencedAppearance?.ref?.name;
            }
        }
        if(!isPolyline(shape)) {
            const nonPolyline = shape as Text | MultiText | Image;

            // Position
            const position = nonPolyline.position;
            if(position) {
                result.position = handlePosition(position);
            }
        }
        if(isText(shape) || isMultiText(shape)) {
            const someText = shape as Text | MultiText;

            // Value
            result.value = someText.value;
        }
        if(isImage(shape) || isPolyline(shape)) {
            const imageOrPolyLine = shape as Image | Polyline;

            // Size
            const size = imageOrPolyLine.size;
            if(size) {
                result.size = handleSize(size);
            }
        }
        if(isImage(shape)) {
            const image = shape as Image;

            // Path
            result.path = image.path;
        }
        if(isPolyline(shape)) {
            const polyline = shape as Polyline;

            // Points
            result.points = handlePoints(polyline.points);
        }
    }
    return result;
}

function handleInlineAppearance(inlineAppearance: InlineAppearance) {
    return {
        'background': inlineAppearance.background ? handleColor(inlineAppearance.background) : null,
        'parent': inlineAppearance.parent?.ref?.name,
        'foreground': inlineAppearance.foreground ? handleColor(inlineAppearance.foreground) : null,
        'font': inlineAppearance.font ? handleFont(inlineAppearance.font) : null,
        'lineStyle': inlineAppearance.lineStyle?.lineType,
        'lineWidth': inlineAppearance.lineWidth,
        'transparency': inlineAppearance.transparency,
        'filled': inlineAppearance.filled?.value,
        'imagePath': inlineAppearance.imagePath
    }
}

function handlePoints(points: Point[]) {
    return points.map((point) => {
        return {
            'x': point.x,
            'y': point.y
        }
    });
}

function handleSize(size: Size) {
    return {
        'width': size.width,
        'height': size.height,
        'widthFixed': size.widthFixed,
        'heightFixed': size.heightFixed
    };
}

function handlePosition(abstractPosition: AbstractPosition) {
    let result = {};
    if(isAbsolutePosition(abstractPosition)) {
        const absolutePosition = abstractPosition as AbsolutePosition;
        result = {
            'xPos': absolutePosition.xPos,
            'yPos': absolutePosition.yPos
        }
    } else if(isAlignment(abstractPosition)) {
        const alignment = abstractPosition as Alignment;
        result = {
            'horizontal': alignment.horizontal.alignmentType,
            'xMargin': alignment.xMargin,
            'vertical': alignment.vertical.alignmentType,
            'yMargin': alignment.yMargin
        }
    }
    return result;
}

function handleColor(color: Color) {
    return {
        'r': color.r ?? 0,
        'g': color.g ?? 0,
        'b': color.b ?? 0
    }
}

function handleFont(font: Font) {
    return {
        'fontName': font.fontName,
        'bold': font.isBold,
        'italic': font.isItalic,
        'size': font.size,
        // TODO Implement color handling (here and in msl.langium)
        'foreground': {
            'r': 0,
            'g': 0,
            'b': 0
        },
        'background': {
            'r': 255,
            'g': 255,
            'b': 255
        }
    }
}

function getEdgeElementConnectionObject(edgeElementConnection: EdgeElementConnection) {
    return {
        lowerBound: edgeElementConnection.lowerBound ?? -1,
        upperBound: edgeElementConnection.upperBound ?? -1,
        // TODO Add handling of externalConnections
        elements: edgeElementConnection.localConnection.map(localContainment => {
            return 'edge:' + localContainment.ref?.name.toLowerCase();
        }) ?? []
    }
}
