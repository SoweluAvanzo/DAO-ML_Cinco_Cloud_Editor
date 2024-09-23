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
    AbsolutePosition,
    AbstractPosition,
    AbstractShape,
    Alignment,
    Annotation,
    Color,
    ComplexModelElement,
    ContainerShape,
    Edge,
    EdgeElementConnection,
    EdgeStyle,
    ExternalReference,
    Font,
    GraphModel,
    GraphicalElementContainment,
    Image,
    InlineAppearance,
    MglModel,
    ModelElement,
    MultiText,
    Node,
    NodeContainer,
    NodeStyle,
    Point,
    Polygon,
    Polyline,
    RoundedRectangle,
    Shape,
    Size,
    Text,
    Wildcard,
    isAbsolutePosition,
    isAlignment,
    isComplexAttribute,
    isContainerShape,
    isEdge,
    isEdgeElementConnection,
    isEdgeStyle,
    isEllipse,
    isEnum,
    isGraphModel,
    isGraphicalElementContainment,
    isImage,
    isMultiText,
    isNode,
    isNodeContainer,
    isNodeStyle,
    isPolygon,
    isPolyline,
    isPrimitiveAttribute,
    isRectangle,
    isReferencedEClass,
    isRoundedRectangle,
    isShape,
    isText,
    isUserDefinedType
} from '../../generated/ast';
import {
    constructElementTypeId,
    getElementTypeId,
    mergeArrays,
    replaceInMapValues,
    replaceKeyInMap,
    topologicalSortWithDescendants
} from './cli-util';
import { createMslServices } from '../../msl/language/msl-module';
import { extractAstNode } from '../../msl/cli/cli-util';
import { Styles } from '../../generated/ast';
import { NodeFileSystem } from 'langium/node';
import { Attribute, ContainerType, Specification } from '../model/specification-types';
import { isWebView } from '../../generated/ast';
import { WebView } from '../../generated/ast';
import * as path from 'path';
import { MglServices } from '../language/mgl-module';

export interface Constraint {
    lowerBound: number | '*';
    upperBound: number | '*';
    elements: string[];
}

export class MGLGenerator {
    specification: Specification = {
        graphTypes: [],
        nodeTypes: [],
        edgeTypes: [],
        customTypes: [],
        appearances: [],
        styles: []
    };

    abstractModelElements: Specification = {
        graphTypes: [],
        nodeTypes: [],
        edgeTypes: [],
        customTypes: [],
        appearances: [],
        styles: []
    };

    async loadExternalModel(filePath: string, services: MglServices): Promise<MglModel> {
        const model = await extractAstNode<MglModel>(filePath, services);
        return model;
    }

    async generateMetaSpecification(model: MglModel, mglPathString: string, services: MglServices): Promise<string> {
        const mglPath = path.parse(mglPathString);
        const mslPathString = path.join(mglPath.dir, model.stylePath);
        const importPaths = model.imports.map(imprt => path.join(mglPath.dir, imprt.importURI));
        const importedModels = [];
        for (const modelPath of importPaths) {
            importedModels.push(await this.loadExternalModel(modelPath, services));
        }

        // Handle appearances and styles first
        const { appearances, styles } = await inferAppearancesAndStyles(mslPathString);
        this.specification.appearances = appearances;
        this.specification.styles = styles;

        // Handle MGL
        const { sortedModelElements, descendantsMap } = topologicalSortWithDescendants(model, importedModels);
        const abstractElementTypeIds = [];

        // Handle all model elements; abstract definitions are handled separately
        const localSortedModelElements = sortedModelElements.filter(e => e.$container === model);

        // only generate for local, not external modelElements
        for (const modelElement of localSortedModelElements) {
            let elementTypeId = '';
            if (!isEnum(modelElement) && modelElement.isAbstract) {
                elementTypeId = this.handleModelElement(modelElement, this.abstractModelElements, importedModels);
                abstractElementTypeIds.push(elementTypeId);
            } else {
                elementTypeId = this.handleModelElement(modelElement, this.specification, importedModels);
            }

            // Replace in descendants map to
            replaceKeyInMap(descendantsMap, modelElement.name, elementTypeId);
            replaceInMapValues(descendantsMap, modelElement.name, elementTypeId);
        }

        this.introduceInheritanceToConstraints(descendantsMap, abstractElementTypeIds);
        return JSON.stringify(this.specification, undefined, 4);
    }

    // If extendable and parent exists, first copy its entire specifications and overwrite customizations afterwards
    resolveParentProperties(modelElement: ComplexModelElement): any {
        const parentName = modelElement.localExtension?.ref ? getElementTypeId(modelElement.localExtension?.ref) : undefined;

        if (parentName) {
            let foundParent = undefined;
            if (isGraphModel(modelElement)) {
                foundParent = [...this.specification.graphTypes, ...this.abstractModelElements.graphTypes].find(
                    type => type.elementTypeId === parentName
                );
            } else if (isNode(modelElement) || isNodeContainer(modelElement)) {
                foundParent = [...this.specification.nodeTypes, ...this.abstractModelElements.nodeTypes].find(
                    type => type.elementTypeId === parentName
                );
            } else if (isEdge(modelElement)) {
                foundParent = [...this.specification.edgeTypes, ...this.abstractModelElements.edgeTypes].find(
                    type => type.elementTypeId === parentName
                );
            } else if (isUserDefinedType(modelElement)) {
                foundParent = [...this.specification.customTypes, ...this.abstractModelElements.customTypes].find(
                    type => type.elementTypeId === parentName
                );
            }
            // Copy the parent deeply
            if (foundParent) {
                const result = JSON.parse(JSON.stringify(foundParent));
                result.superTypes = (result.superTypes as string[]).concat([foundParent.elementTypeId]);
                return result;
            }
        }
        return {
            superTypes: this.getBaseTypes(modelElement)
        };
    }

    getBaseTypes(modelElement: ComplexModelElement): string[] {
        switch (modelElement.$type) {
            case 'GraphModel':
                return ['graphmodel', 'modelelementcontainer', 'modelelement'];
            case 'Node':
                return ['node', 'modelelement'];
            case 'NodeContainer':
                return ['container', 'modelelementcontainer', 'node', 'modelelement'];
            case 'Edge':
                return ['edge', 'modelelement'];
            case 'UserDefinedType':
                return ['userdefinedtype'];
        }
    }

    // Constructs the modelElementSpec and returns the resulting elementTypeId
    handleModelElement(modelElement: ModelElement, specification: Specification, importedModels: MglModel[]): string {
        const modelElementSpec: any = !isEnum(modelElement) ? this.resolveParentProperties(modelElement) : {};

        modelElementSpec.elementTypeId = getElementTypeId(modelElement);
        modelElementSpec.label = modelElement.name;

        modelElementSpec.annotations = mergeArrays(
            modelElementSpec.annotations,
            modelElement.annotations.map(annotation => handleAnnotation(annotation)),
            'name'
        );

        if (!isEnum(modelElement)) {
            // Attributes
            // check inherited attributes for override
            if (modelElementSpec.attributes && modelElement.defaultValueOverrides) {
                modelElementSpec.attributes = modelElementSpec.attributes.map((attribute: Attribute) => {
                    const overrides = modelElement.defaultValueOverrides.filter(o => o.attribute === attribute.name);
                    if (overrides.length > 0) {
                        // is overriden
                        const overridenAttribute = attribute;
                        overridenAttribute.defaultValue = '' + overrides[0].defaultValue;
                        return overridenAttribute;
                    } else {
                        return attribute;
                    }
                });
            }
            // add new
            modelElementSpec.attributes = mergeArrays(
                modelElementSpec.attributes,
                modelElement.attributes.map(attribute => {
                    const result = {
                        annotations: attribute.annotations.map(annotation => handleAnnotation(annotation)),
                        final: attribute.notChangeable,
                        unique: attribute.unique,
                        name: attribute.name,
                        defaultValue: attribute.defaultValue,
                        bounds: {
                            lowerBound: attribute.lowerBound ?? 1,
                            upperBound: handleUpperBound(attribute.upperBound, 1)
                        },
                        // Type is filled in below
                        type: ''
                    };

                    if (isPrimitiveAttribute(attribute)) {
                        result.type = attribute.dataType;
                    } else if (isComplexAttribute(attribute)) {
                        const type = attribute.type.ref;
                        if (type === undefined) {
                            throw new Error('Attribute type is undefined!');
                        }
                        result.type = getElementTypeId(type);
                    }
                    return result;
                }),
                'name'
            );
            // normalize (e.g. boolean 'True' -> 'true')
            modelElementSpec.attributes = modelElementSpec.attributes.map((attribute: any) => {
                if (attribute.type === 'boolean' && attribute.defaultValue && typeof attribute.defaultValue == 'string') {
                    attribute.defaultValue = attribute.defaultValue.toLowerCase();
                }
                return attribute;
            });
        }

        // handle containment constraints
        if (isGraphModel(modelElement) || isNodeContainer(modelElement)) {
            modelElementSpec.containments = handleContainmentConstraints(modelElement, modelElementSpec.containments, importedModels);
        }

        // handle GraphicalModelElement specific
        if (isNode(modelElement) || isEdge(modelElement) || isNodeContainer(modelElement)) {
            const graphicalElement: Node | Edge | NodeContainer = modelElement;

            modelElementSpec.view = { style: undefined };
            modelElementSpec.view.style = graphicalElement.usedStyle
                ? constructElementTypeId(graphicalElement.usedStyle, modelElement.$container.stylePath)
                : undefined;
            if (graphicalElement.styleParameters) {
                modelElementSpec.view.styleParameter = graphicalElement.styleParameters;
            }

            // TODO check annotations for this
            modelElementSpec.palettes = [];
        }

        // handle graphModel
        if (isGraphModel(modelElement)) {
            const graphModel: GraphModel = modelElement;
            modelElementSpec.diagramExtension = graphModel.fileExtension;

            specification.graphTypes.push(modelElementSpec);
        }
        // handle Nodes
        else if (isNode(modelElement) || isNodeContainer(modelElement)) {
            const node = modelElement as Node | NodeContainer;
            // Retrieve the main (container) shape to display width and height properly
            const usedStyleName = node.usedStyle;
            const usedStyle = this.specification.styles.find(style => style.name === usedStyleName);
            const mainShape = usedStyle?.shape;

            modelElementSpec.deletable =
                modelElementSpec.reparentable =
                modelElementSpec.repositionable =
                modelElementSpec.resizable =
                    true;

            modelElementSpec.width = mainShape?.size?.width ?? 100;
            modelElementSpec.height = mainShape?.size?.height ?? 100;

            // Handle edges
            {
                modelElementSpec.incomingEdges = handleEdgeConstraints(modelElement, modelElementSpec.incomingEdges, importedModels, true);
                modelElementSpec.outgoingEdges = handleEdgeConstraints(modelElement, modelElementSpec.outgoingEdges, importedModels, false);
            }
            if (modelElement.primeReference) {
                modelElementSpec.primeReference = {};
                modelElementSpec.primeReference.name = modelElement.primeReference.name;
                if (isReferencedEClass(modelElement.primeReference)) {
                    throw new Error(
                        'EClass are currently not supported as prime Refernce. Go to gitlab.com/scce/cinco-cloud and contribute!'
                    );
                } else {
                    const reference = modelElement.primeReference;
                    if (reference.import && reference.referencedModelElement && reference.import.ref && node.$container.$document) {
                        const importedElement = reference.import.ref;
                        const mglPath = path.parse(node.$container.$document!.uri.fsPath);
                        const externalModelElements = getReferencedModelElements(
                            mglPath.dir,
                            importedElement.importURI,
                            importedModels,
                            m => m.name === reference.referencedModelElement
                        );
                        if (externalModelElements.length > 0) {
                            const referencedId = getElementTypeId(externalModelElements.at(0)!);
                            modelElementSpec.primeReference.type = referencedId;
                        }
                    } else if (reference.modelElement) {
                        const referencedElement = reference.modelElement.ref;
                        if (referencedElement) {
                            const referencedId = getElementTypeId(referencedElement);
                            modelElementSpec.primeReference.type = referencedId;
                        }
                    } else if (reference.modelElementBaseType) {
                        // base type reference: GraphModel, Node, Container, Edge, UserDefinedType
                        modelElementSpec.primeReference.type = reference.modelElementBaseType.toLowerCase();
                    }
                }
            }

            specification.nodeTypes.push(modelElementSpec);
        }
        // handle edges
        else if (isEdge(modelElement)) {
            specification.edgeTypes.push(modelElementSpec);
        }
        // handle userDefinedTypes
        else if (isUserDefinedType(modelElement)) {
            specification.customTypes.push(modelElementSpec);
        }
        // handle Enums
        else if (isEnum(modelElement)) {
            modelElementSpec.literals = modelElement.literals;
            specification.customTypes.push(modelElementSpec);
        }

        return modelElementSpec.elementTypeId;
    }

    introduceInheritanceToConstraints(descendantMap: Record<string, Set<string>>, abstractElementTypeIds: string[]): void {
        // Helper function to recursively replace elements in a constraints array
        const replaceInConstraints = (
            constraintsArray?: {
                lowerBound: number | '*';
                upperBound: number | '*';
                elements: string[];
            }[]
        ) => {
            if (!constraintsArray) {
                return;
            }

            for (const constraint of constraintsArray) {
                const newElements: string[] = [];

                const recursivelyReplace = (element: string) => {
                    if (descendantMap[element].size > 0) {
                        if (!abstractElementTypeIds.includes(element) && !newElements.includes(element)) {
                            newElements.push(element);
                        }
                        for (const descendant of descendantMap[element]) {
                            recursivelyReplace(descendant);
                        }
                    } else {
                        // If the element doesn't have descendants, add it to the newElements list
                        if (!newElements.includes(element)) {
                            newElements.push(element);
                        }
                    }
                };

                for (const element of constraint.elements) {
                    recursivelyReplace(element);
                }

                constraint.elements = newElements;
            }
        };

        // Replace in graphTypes
        if (this.specification.graphTypes) {
            for (const graphType of this.specification.graphTypes) {
                replaceInConstraints(graphType.containments);
            }
        }

        // Replace in nodeTypes
        if (this.specification.nodeTypes) {
            for (const nodeType of this.specification.nodeTypes) {
                if ((nodeType as ContainerType).containments !== undefined) {
                    replaceInConstraints((nodeType as ContainerType).containments);
                }
                replaceInConstraints(nodeType.incomingEdges);
                replaceInConstraints(nodeType.outgoingEdges);
            }
        }
    }
}

function getReferencedModelElements(
    root: string,
    modelRefUri: string,
    importedModels: MglModel[],
    modelElementFilter: (modelElement: ModelElement) => boolean
) {
    // identify nodes of referenced model
    const referencedModelPath = path.parse(path.join(root, modelRefUri));
    const referencedModels = importedModels.filter(model => {
        const modelPath = path.parse(model.$document?.uri.fsPath ?? '');
        return modelPath.dir === referencedModelPath.dir && modelPath.base === referencedModelPath.base;
    });
    return referencedModels.map(model => model.modelElements.filter(modelElementFilter)).flat();
}

function mapExternalReferenceId(externalReference: ExternalReference | undefined): string[] {
    if (!externalReference || !externalReference.import || !externalReference.import.ref) {
        throw new Error('External reference can not be resolved!');
    }
    const externalContainerName = externalReference.import.ref.importURI;
    const externalReferenceElements = externalReference.elements; // names of the containments
    return externalReferenceElements.map(e => constructElementTypeId(e, externalContainerName));
}

function handleContainmentConstraints(
    modelElement: GraphModel | NodeContainer,
    parentConstraints: any,
    importedModels: MglModel[]
): Constraint[] {
    const typeFilter = (modelElement: ModelElement) => isNode(modelElement) || isNodeContainer(modelElement);
    const wildcards: Wildcard[] = modelElement.containmentWildcards;

    // build constrain sets
    const localConstraintSet = modelElement.containableElements.filter(c => c.localContainments.length >= 0);
    const externalConstraintSet = modelElement.containableElements.filter(c => c.externalContainment !== undefined);

    // Id mappings
    const localConstraintElementIdMapping = (constraint: GraphicalElementContainment | EdgeElementConnection): string[] => {
        if (isEdgeElementConnection(constraint)) {
            throw new Error('Wrong type of constraint: EdgeElementConnection');
        }
        return (
            constraint.localContainments.map(constraintElement => {
                if (!constraintElement.ref) {
                    throw new Error('Referenced containment is undefined');
                }
                return getElementTypeId(constraintElement.ref);
            }) ?? []
        );
    };
    const externalConstraintElementIdMapping = (constraint: GraphicalElementContainment | EdgeElementConnection) => {
        if (isEdgeElementConnection(constraint)) {
            throw new Error('Wrong type of constraint: EdgeElementConnection');
        }
        const externalReference = constraint.externalContainment;
        return mapExternalReferenceId(externalReference);
    };
    const container = modelElement.$container;

    // actual constraints handling
    return handleConstraints(
        container,
        parentConstraints,
        importedModels,
        typeFilter,
        wildcards,
        localConstraintSet,
        externalConstraintSet,
        localConstraintElementIdMapping,
        externalConstraintElementIdMapping
    );
}

function handleEdgeConstraints(
    modelElement: Node | NodeContainer,
    parentConstraints: any,
    importedModels: MglModel[],
    handleIncoming: boolean
): Constraint[] {
    const typeFilter = (modelElement: ModelElement) => isEdge(modelElement);
    const wildcards: Wildcard[] = handleIncoming ? modelElement.incomingWildcards : modelElement.outgoingWildcards;

    // build constrain sets
    const localConstraintSet = handleIncoming
        ? modelElement.incomingEdgeConnections.filter(c => c.localConnection.length >= 0)
        : modelElement.outgoingEdgeConnections.filter(c => c.localConnection.length >= 0);
    const externalConstraintSet = handleIncoming
        ? modelElement.incomingEdgeConnections.filter(c => c.externalConnection !== undefined)
        : modelElement.outgoingEdgeConnections.filter(c => c.externalConnection !== undefined);

    // Id mappings
    const localConstraintElementIdMapping = (constraint: GraphicalElementContainment | EdgeElementConnection): string[] => {
        if (isGraphicalElementContainment(constraint)) {
            throw new Error('Wrong type of constraint: GraphicalElementContainment');
        }
        return (
            constraint.localConnection.map(constraintElement => {
                if (!constraintElement.ref) {
                    throw new Error('Referenced containment is undefined');
                }
                return getElementTypeId(constraintElement.ref);
            }) ?? []
        );
    };
    const externalConstraintElementIdMapping = (constraint: GraphicalElementContainment | EdgeElementConnection) => {
        if (isGraphicalElementContainment(constraint)) {
            throw new Error('Wrong type of constraint: GraphicalElementContainment');
        }
        const externalReference = constraint.externalConnection;
        return mapExternalReferenceId(externalReference);
    };
    const container = modelElement.$container;

    // actual constraints handling
    return handleConstraints(
        container,
        parentConstraints,
        importedModels,
        typeFilter,
        wildcards,
        localConstraintSet,
        externalConstraintSet,
        localConstraintElementIdMapping,
        externalConstraintElementIdMapping
    );
}

function handleConstraints(
    container: MglModel,
    parentConstraint: any,
    importedModels: MglModel[],
    typeFilter: (modelElement: ModelElement) => boolean,
    wildcards: Wildcard[],
    localConstraintSet: (GraphicalElementContainment | EdgeElementConnection)[],
    externalConstraintSet: (GraphicalElementContainment | EdgeElementConnection)[],
    localConstraintElementIdMapping: (constraint: GraphicalElementContainment | EdgeElementConnection) => string[],
    externalConstraintElementIdMapping: (constraint: GraphicalElementContainment | EdgeElementConnection) => string[]
): Constraint[] {
    if (!container.$document) {
        throw new Error('Container of modelElement could not be resolved!');
    }

    // prepare
    const mglPath = path.parse(container.$document.uri.fsPath);
    const allLocalConstrainableElements = container.modelElements.filter(typeFilter);
    const localWildcards: Wildcard[] = wildcards.filter(wildcard => wildcard.selfWildcard);
    const externalWildcards: Wildcard[] = wildcards.filter(wildcard => !wildcard.selfWildcard);

    // local wildcards
    const localWildcardConstraints = localWildcards.map(wildcard => ({
        lowerBound: wildcard.lowerBound ?? 0,
        upperBound: handleUpperBound(wildcard.upperBound, -1),
        elements: allLocalConstrainableElements.map(modelElement => getElementTypeId(modelElement))
    }));
    // external Wildcards
    const externalWildcardConstraints = externalWildcards.map(wildcard => {
        if (!wildcard.referencedImport?.ref) {
            throw new Error('Referenced wildcard could not be resolved!');
        }
        const externalModelElements = getReferencedModelElements(
            mglPath.dir,
            wildcard.referencedImport.ref.importURI,
            importedModels,
            typeFilter
        );
        return {
            lowerBound: wildcard.lowerBound ?? 0,
            upperBound: handleUpperBound(wildcard.upperBound, -1),
            elements: externalModelElements.map(element => getElementTypeId(element))
        };
    });
    // local containments
    const localConstraintElements = localConstraintSet.map(constraint => ({
        lowerBound: constraint.lowerBound ?? 0,
        upperBound: handleUpperBound(constraint.upperBound, -1),
        elements: localConstraintElementIdMapping(constraint)
    }));
    // external containments
    const externalConstraintElements = externalConstraintSet.map(constraint => ({
        lowerBound: constraint.lowerBound ?? 0,
        upperBound: handleUpperBound(constraint.upperBound, -1),
        elements: externalConstraintElementIdMapping(constraint)
    }));
    const allContainments = localWildcardConstraints
        .concat(localConstraintElements)
        .concat(externalWildcardConstraints)
        .concat(externalConstraintElements);
    return mergeElementConstraints(parentConstraint, allContainments);
}

async function inferAppearancesAndStyles(stylePath: string): Promise<{ appearances: any[]; styles: any[] }> {
    const services = createMslServices(NodeFileSystem).Msl;
    const model = await extractAstNode<Styles>(stylePath, services);

    // TODO Type properly
    const result = {
        appearances: [] as any[],
        styles: [] as any[]
    };

    for (const appearance of model.appearances) {
        const appearanceConfiguration: any = {};
        const parentId = appearance.parent?.ref ? getElementTypeId(appearance.parent.ref) : undefined;
        appearanceConfiguration.name = getElementTypeId(appearance);
        appearanceConfiguration.parent = parentId;
        appearanceConfiguration.lineWidth = appearance.lineWidth;
        appearanceConfiguration.lineStyle = appearance.lineStyle?.lineType;
        appearanceConfiguration.filled = appearance.filled;
        appearanceConfiguration.font = appearance.font
            ? {
                  fontName: appearance.font.fontName,
                  isBold: appearance.font.isBold,
                  isItalic: appearance.font.isItalic,
                  size: appearance.font.size
              }
            : undefined;
        appearanceConfiguration.imagePath = appearance.imagePath;
        appearanceConfiguration.transparency = appearance.transparency;

        const background = appearance.background;
        if (background) {
            appearanceConfiguration.background = handleColor(background);
        }

        const foreground = appearance.foreground;
        if (foreground) {
            appearanceConfiguration.foreground = handleColor(foreground);
        }

        result.appearances.push(appearanceConfiguration);
    }

    for (const style of model.styles) {
        // TODO Type properly
        const styleConfiguration: any = {};

        styleConfiguration.name = getElementTypeId(style);
        styleConfiguration.appearanceProvider = style.appearanceProvider;
        styleConfiguration.parameterCount = style.parameterCount;

        if (isEdgeStyle(style)) {
            const edgeStyle = style as EdgeStyle;

            styleConfiguration.connectionType = edgeStyle.connectionType?.FreeForm;

            // Appereance
            const inlineAppearance = edgeStyle.inlineAppearance;
            if (inlineAppearance) {
                styleConfiguration.appearance = handleInlineAppearance(inlineAppearance);
            } else {
                styleConfiguration.appearance = edgeStyle.referencedAppearance?.ref
                    ? getElementTypeId(edgeStyle.referencedAppearance?.ref)
                    : undefined;
            }

            // Decorators
            styleConfiguration.decorator = edgeStyle.decorator.map(decorator => {
                // TODO type properly
                const decoratorConfiguration: any = {
                    location: decorator.location,
                    movable: decorator.movable
                };

                const predefinedDecorator = decorator.predefinedDecorator;
                const decoratorShape = decorator.decoratorShape;
                if (predefinedDecorator) {
                    const predefinedDecoratorConfiguration: any = {};

                    // Shape
                    const shapeType = predefinedDecorator.shape.shapeType;
                    if (shapeType) {
                        predefinedDecoratorConfiguration.shape = `${shapeType.toUpperCase()}`;
                    }

                    // Appereance
                    const inlineAppearance = predefinedDecorator.inlineAppearance;
                    if (inlineAppearance) {
                        predefinedDecoratorConfiguration.appearance = handleInlineAppearance(inlineAppearance);
                    } else {
                        predefinedDecoratorConfiguration.appearance = predefinedDecorator.referencedAppearance?.ref
                            ? getElementTypeId(predefinedDecorator.referencedAppearance?.ref)
                            : undefined;
                    }

                    decoratorConfiguration.predefinedDecorator = predefinedDecoratorConfiguration;
                } else if (decoratorShape) {
                    // Decorator Shape
                    // This casting is possible because of the union types and because AbstractShape is a superset of GraphicsAlgorithm
                    decoratorConfiguration.decoratorShape = handleAbstractShape(decoratorShape as AbstractShape);
                }

                return decoratorConfiguration;
            });

            styleConfiguration.styleType = 'EdgeStyle';
        } else if (isNodeStyle(style)) {
            const nodeStyle = style as NodeStyle;
            styleConfiguration.styleType = 'NodeStyle';
            styleConfiguration.fixed = nodeStyle.fixed;
            styleConfiguration.shape = handleAbstractShape(style.mainShape);
        }

        result.styles.push(styleConfiguration);
    }

    return result;
}

function mergeElementConstraints(baseConstraints: Constraint[], dominantConstraints: Constraint[]): Constraint[] {
    if (!baseConstraints && !dominantConstraints) {
        return [];
    }

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
        name: annotation.name,
        values: annotation.value ?? []
    };
}

function handleAbstractShape(abstractShape: AbstractShape) {
    const result: any = {
        anchorShape: abstractShape.anchorShape
    };

    // Distinguish between ContainerShapes and Shapes and handle specific properties separately
    if (isContainerShape(abstractShape)) {
        const containerShape = abstractShape as ContainerShape;

        // Basic type
        if (isEllipse(containerShape)) {
            result.type = 'ELLIPSE';
        } else if (isPolygon(containerShape)) {
            result.type = 'POLYGON';
        } else if (isRectangle(containerShape)) {
            result.type = 'RECTANGLE';
        } else if (isRoundedRectangle(containerShape)) {
            result.type = 'ROUNDEDRECTANGLE';
        }

        // Appereance
        const inlineAppearance = containerShape.inlineAppearance;
        if (inlineAppearance) {
            result.appearance = handleInlineAppearance(inlineAppearance);
        } else {
            result.appearance = containerShape.referencedAppearance?.ref
                ? getElementTypeId(containerShape.referencedAppearance?.ref)
                : undefined;
        }

        // Position
        const position = containerShape.position;
        if (position) {
            result.position = handlePosition(position);
        }

        // Size
        const size = containerShape.size;
        if (size) {
            result.size = handleSize(size);
        }

        // Children
        result.children = containerShape.children.map(child => handleAbstractShape(child));

        if (isRoundedRectangle(containerShape)) {
            const roundedRectangle = containerShape as RoundedRectangle;

            // CornerWidth & CornerHeight
            result.cornerWidth = roundedRectangle.cornerWidth;
            result.cornerHeight = roundedRectangle.cornerHeight;
        }

        if (isPolygon(containerShape)) {
            const polygon = containerShape as Polygon;

            // Points
            result.points = handlePoints(polygon.points);
        }
    } else if (isShape(abstractShape)) {
        const shape = abstractShape as Shape;

        // Basic type
        if (isText(shape)) {
            result.type = 'TEXT';
        } else if (isMultiText(shape)) {
            result.type = 'MULTITEXT';
        } else if (isImage(shape)) {
            result.type = 'IMAGE';
        } else if (isWebView(shape)) {
            result.type = 'WEBVIEW';
        } else if (isPolyline(shape)) {
            result.type = 'POLYLINE';
        }

        // These are treated weirdly because they are almost disjunctive; should be optimized at language level
        if (!isImage(shape) && !isWebView(shape)) {
            const nonImage = shape as Text | MultiText | Polyline;

            // Appearance
            const inlineAppearance = nonImage.inlineAppearance;
            if (inlineAppearance) {
                result.appearance = handleInlineAppearance(inlineAppearance);
            } else {
                result.appearance = nonImage.referencedAppearance?.ref ? getElementTypeId(nonImage.referencedAppearance?.ref) : undefined;
            }
        }
        if (!isPolyline(shape)) {
            const positionedShape = shape as Text | MultiText | Image | WebView;

            // Position
            const position = positionedShape.position;
            if (position) {
                result.position = handlePosition(position);
            }
        }
        if (isText(shape) || isMultiText(shape)) {
            const someText = shape as Text | MultiText;

            // Value
            result.value = someText.value;
        }
        if (isImage(shape) || isWebView(shape) || isPolyline(shape)) {
            const sizedShape = shape as Image | WebView | Polyline;

            // Size
            const size = sizedShape.size;
            if (size) {
                result.size = handleSize(size);
            }
        }
        if (isImage(shape)) {
            const image = shape as Image;

            // Path
            result.path = image.path;
        }
        if (isWebView(shape)) {
            const webview = shape as WebView;

            // Content
            result.content = webview.content;
            // Scrollbar activated
            result.scrollbar = webview.scrollable?.value;
            // padding
            result.padding = webview.padding ?? 10;
        }
        if (isPolyline(shape)) {
            const polyline = shape as Polyline;

            // Points
            result.points = handlePoints(polyline.points);
        }
    }
    return result;
}

function handleInlineAppearance(inlineAppearance: InlineAppearance) {
    return {
        background: inlineAppearance.background ? handleColor(inlineAppearance.background) : undefined,
        parent: inlineAppearance.parent?.ref ? getElementTypeId(inlineAppearance.parent?.ref) : undefined,
        foreground: inlineAppearance.foreground ? handleColor(inlineAppearance.foreground) : undefined,
        font: inlineAppearance.font ? handleFont(inlineAppearance.font) : undefined,
        lineStyle: inlineAppearance.lineStyle?.lineType,
        lineWidth: inlineAppearance.lineWidth,
        transparency: inlineAppearance.transparency,
        filled: inlineAppearance.filled?.value,
        imagePath: inlineAppearance.imagePath
    };
}

function handlePoints(points: Point[]) {
    return points.map(point => ({
        x: point.x,
        y: point.y
    }));
}

function handleSize(size: Size) {
    return {
        width: size.width,
        height: size.height,
        widthFixed: size.widthFixed,
        heightFixed: size.heightFixed
    };
}

function handlePosition(abstractPosition: AbstractPosition) {
    let result = {};
    if (isAbsolutePosition(abstractPosition)) {
        const absolutePosition = abstractPosition as AbsolutePosition;
        result = {
            xPos: absolutePosition.xPos,
            yPos: absolutePosition.yPos
        };
    } else if (isAlignment(abstractPosition)) {
        const alignment = abstractPosition as Alignment;
        result = {
            horizontal: alignment.horizontal.alignmentType,
            xMargin: alignment.xMargin,
            vertical: alignment.vertical.alignmentType,
            yMargin: alignment.yMargin
        };
    }
    return result;
}

function handleColor(color: Color) {
    return {
        r: color.r ?? 0,
        g: color.g ?? 0,
        b: color.b ?? 0
    };
}

function handleFont(font: Font) {
    return {
        fontName: font.fontName,
        bold: font.isBold,
        italic: font.isItalic,
        size: font.size,
        // TODO Implement color handling (here and in msl.langium)
        foreground: {
            r: 0,
            g: 0,
            b: 0
        },
        background: {
            r: 255,
            g: 255,
            b: 255
        }
    };
}

function handleUpperBound(specification: number | '*' | undefined, defaultValue: number): number {
    if (specification === undefined) {
        return defaultValue;
    }
    if (specification === '*') {
        return -1;
    }
    return specification;
}
