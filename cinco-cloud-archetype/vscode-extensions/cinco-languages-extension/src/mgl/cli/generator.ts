import fs from 'fs';
import path from 'path';
import { Appearance, EdgeElementConnection, EdgeStyle, GraphModel, MglModel, Node, NodeContainer, NodeStyle, isEdge, isEdgeStyle, isGraphModel, isNode, isNodeContainer, isNodeStyle } from '../../generated/ast';
import { extractDestinationAndName } from './cli-util';
import { createMslServices } from '../../msl/language-server/msl-module';
import { extractAstNode } from '../../msl/cli/cli-util';
import { Styles } from '../../generated/ast';
import { NodeFileSystem } from 'langium/node';

export async function generateMetaSpecification(model: MglModel, filePath: string, destination: string | undefined): Promise<string> {
    const data = extractDestinationAndName(filePath, destination);
    const generatedFilePath = `${path.join(data.destination, data.name)}.json`;

    // TODO Type properly
    const specification = {
        graphTypes: [] as any[],
        nodeTypes: [] as any[],
        edgeTypes: [] as any[],
        appearances: [] as any[],
        styles: [] as any[]
    };

    for (const modelElement of model.modelElements) {
        const modelElementSpec : any = {};
        
        // This has to be completed by prepending the modelElement type (see below)
        modelElementSpec.elementTypeId = modelElement.name.toLowerCase();

        modelElementSpec.type = modelElementSpec.label = modelElement.name;
        
        modelElementSpec.annotations = modelElement.annotations.map(annotation => {
            return {
                name : annotation.name,
                values : annotation.value ?? []
            };
        });

        if (isGraphModel(modelElement) || isNodeContainer(modelElement)) {
            const containerElement : GraphModel | NodeContainer = modelElement;

            modelElementSpec.containments = containerElement.containableElements.map(containableElement => {
                return {
                    lowerBound: containableElement.lowerBound ?? -1,
                    upperBound: containableElement.upperBound ?? -1,
                    // TODO Add handling of externalContainments
                    elements: containableElement.localContainments.map(localContainment => {
                        return 'node:' + localContainment.ref?.name;
                    }) ?? []
                }
            });
        }

        if (isGraphModel(modelElement)) {
            // Prepend modelElement type to complete elementTypeId
            modelElementSpec.elementTypeId = 'graphmodel:' + modelElementSpec.elementTypeId;

            specification.graphTypes.push(modelElementSpec);
        }

        if (isNode(modelElement)) {
            const node = modelElement as Node;

            // Prepend modelElement type to complete elementTypeId
            modelElementSpec.elementTypeId = 'node:' + modelElementSpec.elementTypeId;

            // TODO Check for disable annotation for these
            modelElementSpec.deletable = modelElementSpec.reparentable = modelElementSpec.repositionable = modelElementSpec.resizeable = true;
            
            // TODO Use style for these
            modelElementSpec.width = 100;
            modelElementSpec.height = 100;
            modelElementSpec.view = {};

            // TODO check annotations for this
            modelElementSpec.palettes = [];

            modelElementSpec.incomingEdges = node.incomingEdgeConnections.map(incomingEdgeConnection => {
                return getEdgeElementConnectionObject(incomingEdgeConnection);
            });

            modelElementSpec.outgoingEdges = node.outgoingEdgeConnections.map(outgoingEdgeConnection => {
                return getEdgeElementConnectionObject(outgoingEdgeConnection);
            });

            specification.nodeTypes.push(modelElementSpec);
        }

        if (isEdge(modelElement)) {
            modelElementSpec.elementTypeId = 'edge:' + modelElementSpec.elementTypeId;

            specification.edgeTypes.push(modelElementSpec);
        }
    }

    // Trim path to MGL to retrieve the project path
    const pathToProject = filePath.substring(0, filePath.lastIndexOf('/') + 1);
    const appearancesAndStyles = await inferAppearancesAndStyles(pathToProject + model.stylePath);
    specification.appearances = appearancesAndStyles.appearances;
    specification.styles = appearancesAndStyles.styles;

    if (!fs.existsSync(data.destination)) {
        fs.mkdirSync(data.destination, { recursive: true });
    }
    fs.writeFileSync(generatedFilePath, JSON.stringify(specification, null, 4));
    return generatedFilePath;
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
        appearanceConfiguration.parent = appearance.parent;
        appearanceConfiguration.lineWidth = appearance.lineWidth;
        appearanceConfiguration.lineStyle = appearance.lineStyle;
        appearanceConfiguration.filled = appearance.filled;
        appearanceConfiguration.font = appearance.font?.fontName;
        appearanceConfiguration.imagePath = appearance.imagePath;
        appearanceConfiguration.transparency = appearance.transparency;
        
        const background = appearance.background;
        if(background) {
            appearanceConfiguration.background = {
                r: background.red,
                g: background.green,
                b: background.blue
            }
        }

        const foreground = appearance.foreground;
        if(foreground) {
            appearanceConfiguration.foreground = {
                r: foreground.red,
                g: foreground.green,
                b: foreground.blue
            }
        }

        result.appearances.push(appearanceConfiguration);
    }

    // TODO add support for inline appearances
    for (const style of model.styles) {
        // TODO Type properly
        let styleConfiguration : any = {}

        styleConfiguration.name = style.name;
        styleConfiguration.appearance = style.appearanceProvider;
        styleConfiguration.parameterCount = style.parameterCount;

        if(isEdgeStyle(style)) {
            const edgeStyle = style as EdgeStyle;

            styleConfiguration.connectionType = edgeStyle.connectionType?.FreeForm;

            // TODO add support for every possible decorator
            styleConfiguration.decorator = edgeStyle.decorator.map(decorator => {
                // TODO type properly
                let decoratorConfiguration : any = {
                    location: decorator.location
                };

                const predefinedDecorator = decorator.predefinedDecorator;
                if(predefinedDecorator) {
                    const predefinedDecoratorConfiguration : any = {
                        appearance: (decorator.predefinedDecorator?.referencedAppearance?.$refNode?.element as Appearance)?.name
                    };

                    // Identify set shape
                    if(predefinedDecorator.shape.ARROW) {
                        predefinedDecoratorConfiguration.shape = 'DecoratorShape.ARROW';
                    } else if(predefinedDecorator.shape.CIRCLE) {
                        predefinedDecoratorConfiguration.shape = 'DecoratorShape.CIRCLE';
                    } else if(predefinedDecorator.shape.DIAMOND) {
                        predefinedDecoratorConfiguration.shape = 'DecoratorShape.DIAMOND';
                    } else if(predefinedDecorator.shape.TRIANGLE) {
                        predefinedDecoratorConfiguration.shape = 'DecoratorShape.TRIANGLE';
                    }

                    styleConfiguration.predefinedDecorator = predefinedDecoratorConfiguration;
                }

                return decoratorConfiguration;
            });

            styleConfiguration.styleType = 'EdgeStyle';
        }

        if(isNodeStyle(style)) {
            const nodeStyle = style as NodeStyle;
            
            styleConfiguration.styleType = 'NodeStyle';
            
            styleConfiguration.fixed = nodeStyle.fixed;

            // TODO add support for shapes
            // styleConfiguration.mainShape = 
        }

        result.styles.push(styleConfiguration);
    }

    return result;
}

function getEdgeElementConnectionObject(edgeElementConnection: EdgeElementConnection) {
    return {
        lowerBound: edgeElementConnection.lowerBound ?? -1,
        upperBound: edgeElementConnection.upperBound ?? -1,
        // TODO Add handling of externalConnections
        elements: edgeElementConnection.localConnection.map(localContainment => {
            return 'node:' + localContainment.ref?.name;
        }) ?? []
    }
}
