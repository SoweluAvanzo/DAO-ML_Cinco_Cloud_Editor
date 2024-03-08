import * as path from 'path'
import {
  AbsolutePosition,
  AbstractPosition,
  AbstractShape,
  Alignment,
  Annotation,
  Color,
  ContainerShape,
  Edge,
  EdgeElementConnection,
  EdgeStyle,
  Font,
  GraphModel,
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
  isAbsolutePosition,
  isAlignment,
  isComplexAttribute,
  isContainerShape,
  isEdge,
  isEdgeStyle,
  isEllipse,
  isEnum,
  isGraphModel,
  isImage,
  isMultiText,
  isNode,
  isNodeContainer,
  isNodeStyle,
  isPolygon,
  isPolyline,
  isPrimitiveAttribute,
  isRectangle,
  isRoundedRectangle,
  isShape,
  isText,
  isUserDefinedType,
} from "../../generated/ast";
import {
  mergeArrays,
  replaceInMapValues,
  replaceKeyInMap,
  topologicalSortWithDescendants,
} from "./cli-util";
import { createMslServices } from "../../msl/language-server/msl-module";
import { extractAstNode } from "../../msl/cli/cli-util";
import { Styles } from "../../generated/ast";
import { NodeFileSystem } from "langium/node";
import { ContainerType, Specification } from "../model/specification-types";
import { isWebView } from "../../generated/ast";
import { WebView } from "../../generated/ast";

export class MGLGenerator {
  specification: Specification = {
    graphTypes: [],
    nodeTypes: [],
    edgeTypes: [],
    customTypes: [],
    appearances: [],
    styles: [],
  };

  abstractModelElements: Specification = {
    graphTypes: [],
    nodeTypes: [],
    edgeTypes: [],
    customTypes: [],
    appearances: [],
    styles: [],
  };

  async generateMetaSpecification(
    model: MglModel,
    mglPathString: string
  ): Promise<string> {
    // Handle appearances and styles first
    // Trim path to MGL to retrieve the project path
    const mglPath = path.parse(mglPathString);
    const mglName = mglPath.name;

    const mslPathString = path.join(mglPath.dir, model.stylePath)
    const mslPath = path.parse(mslPathString);
    const mslName = mslPath.name;
    const { appearances, styles } =
      await inferAppearancesAndStyles(mslPathString, mslName);
    this.specification.appearances = appearances;
    this.specification.styles = styles;

    // Handle MGL
    const { sortedModelElements, descendantsMap } =
      topologicalSortWithDescendants(model.modelElements);
    const abstractElementTypeIds = [];

    // Handle all model elements; abstract definitions are handled separately
    for (const modelElement of sortedModelElements) {
      let elementTypeId = "";
      if (!isEnum(modelElement) && modelElement.isAbstract) {
        elementTypeId = this.handleModelElement(
          mglName,
          mslName,
          modelElement,
          this.abstractModelElements
        );
        abstractElementTypeIds.push(elementTypeId);
      } else {
        elementTypeId = this.handleModelElement(
          mglName,
          mslName,
          modelElement,
          this.specification
        );
      }

      // Replace in descendants map to
      replaceKeyInMap(descendantsMap, modelElement.name, elementTypeId);
      replaceInMapValues(descendantsMap, modelElement.name, elementTypeId);
    }

    this.introduceInheritanceToConstraints(
      descendantsMap,
      abstractElementTypeIds
    );
    return JSON.stringify(this.specification, null, 4);
  }

  // Constructs the modelElementSpec and returns the resulting elementTypeId
  handleModelElement(
    mglName: string,
    mslName: string,
    modelElement: ModelElement,
    specification: Specification
  ): string {
    var modelElementSpec: any = {};

    // If parent exists, first copy its entire specifications and overwrite customizations afterwards
    if (!isEnum(modelElement) && modelElement.localExtension !== undefined && modelElement.localExtension.ref !== undefined) {
      const parentName = modelElement.localExtension.ref.name;
      const parentId = buildIdentifier(mglName, parentName);
      if (parentName) {
        let foundParent;
        if (isGraphModel(modelElement)) {
          foundParent = [
            ...this.specification.graphTypes,
            ...this.abstractModelElements.graphTypes,
          ].find(
            (graphType) => graphType.elementTypeId === parentId
          );
        } else if (isNode(modelElement) || isNodeContainer(modelElement)) {
          foundParent = [
            ...this.specification.nodeTypes,
            ...this.abstractModelElements.nodeTypes,
          ].find(
            (nodeType) => nodeType.elementTypeId === parentId
          );
        } else if (isEdge(modelElement)) {
          foundParent = [
            ...this.specification.edgeTypes,
            ...this.abstractModelElements.edgeTypes,
          ].find(
            (edgeType) => edgeType.elementTypeId === parentId
          );
        }
        // Copy the parent deeply
        if (foundParent) {
          modelElementSpec = JSON.parse(JSON.stringify(foundParent));
        }
      }
    }

    modelElementSpec.elementTypeId =
      buildIdentifier(mglName, modelElement.name);

    modelElementSpec.label = modelElement.name;

    modelElementSpec.annotations = mergeArrays(
      modelElementSpec.annotations,
      modelElement.annotations.map((annotation) =>
        handleAnnotation(annotation)
      ),
      "name"
    );

    if (!isEnum(modelElement)) {
      // Attributes
      modelElementSpec.attributes = mergeArrays(
        modelElementSpec.attributes,
        modelElement.attributes.map((attribute) => {
          const result = {
            annotations: attribute.annotations.map((annotation) =>
              handleAnnotation(annotation)
            ),
            final: attribute.notChangeable,
            unique: attribute.unique,
            name: attribute.name,
            defaultValue: attribute.defaultValue,
            bounds: {
              lowerBound: attribute.lowerBound ?? 1,
              upperBound: handleUpperBound(attribute.upperBound, 1),
            },
            // Type is filled in below
            type: "",
          };

          if (isPrimitiveAttribute(attribute)) {
            result.type = attribute.dataType;
          } else if (isComplexAttribute(attribute)) {
            const { name } = attribute.type.ref!
            result.type = buildIdentifier(mglName, name)
          }

          return result;
        }),
        "name"
      );
    }

    if (isGraphModel(modelElement) || isNodeContainer(modelElement)) {
      const containerElement: GraphModel | NodeContainer = modelElement;

      modelElementSpec.containments = mergeElementConstraints(
        modelElementSpec.containments,
        containerElement.containableElements.map((containableElement) => {
          return {
            lowerBound: containableElement.lowerBound ?? 0,
            upperBound: handleUpperBound(containableElement.upperBound, -1),
            // TODO Add handling of externalContainments
            elements:
              containableElement.localContainments.map((localContainment) => {
                const { name } = localContainment.ref!;
                return buildIdentifier(mglName, name)
              }) ?? [],
          };
        })
      );
    }

    if (
      isNode(modelElement) ||
      isEdge(modelElement) ||
      isNodeContainer(modelElement)
    ) {
      const { usedStyle, styleParameters } =
        modelElement as Node | Edge | NodeContainer;

      modelElementSpec.view = {};

      if (usedStyle !== undefined) {
        modelElementSpec.view.style = buildIdentifier(mslName, usedStyle);
      }

      modelElementSpec.view.styleParameter = styleParameters;
    }

    if (isGraphModel(modelElement)) {
      const graphModel: GraphModel = modelElement;

      modelElementSpec.diagramExtension = graphModel.fileExtension;

      specification.graphTypes.push(modelElementSpec);
    }

    if (isNode(modelElement) || isNodeContainer(modelElement)) {
      const node = modelElement as Node | NodeContainer;
      // Retrieve the main (container) shape to display width and height properly
      const usedStyleName = node.usedStyle;
      const usedStyle = this.specification.styles.find(
        (style) => style.name === usedStyleName
      );
      const mainShape = usedStyle?.shape;

      // TODO Check for disable annotation for these
      modelElementSpec.deletable =
        modelElementSpec.reparentable =
        modelElementSpec.repositionable =
        modelElementSpec.resizable =
          true;

      modelElementSpec.width = mainShape?.size?.width ?? 100;
      modelElementSpec.height = mainShape?.size?.height ?? 100;

      // TODO check annotations for this
      modelElementSpec.palettes = [];

      modelElementSpec.incomingEdges = mergeElementConstraints(
        modelElementSpec.incomingEdges,
        node.incomingEdgeConnections.map((incomingEdgeConnection) => {
          return getEdgeElementConnectionObject(mglName, incomingEdgeConnection);
        })
      );

      modelElementSpec.outgoingEdges = mergeElementConstraints(
        modelElementSpec.outgoingEdges,
        node.outgoingEdgeConnections.map((outgoingEdgeConnection) => {
          return getEdgeElementConnectionObject(mglName, outgoingEdgeConnection);
        })
      );

      specification.nodeTypes.push(modelElementSpec);
    }

    if (isEdge(modelElement)) {
      specification.edgeTypes.push(modelElementSpec);
    }

    if (isUserDefinedType(modelElement)) {
        specification.customTypes.push(modelElementSpec);
    }

    if (isEnum(modelElement)) {
        modelElementSpec.literals = modelElement.literals;
        specification.customTypes.push(modelElementSpec);
    }

    return modelElementSpec.elementTypeId;
  }

  introduceInheritanceToConstraints(
    descendantMap: Record<string, Set<string>>,
    abstractElementTypeIds: string[]
  ): void {
    // Helper function to recursively replace elements in a constraints array
    const replaceInConstraints = (
      constraintsArray?: {
        lowerBound: number | "*";
        upperBound: number | "*";
        elements: string[];
      }[]
    ) => {
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

async function inferAppearancesAndStyles(
  mslPathString: string,
  mslName: string
): Promise<{ appearances: any[]; styles: any[] }> {
  const services = createMslServices(NodeFileSystem).Msl;
  const model = await extractAstNode<Styles>(mslPathString, services);

  // TODO Type properly
  const result = {
    appearances: [] as any[],
    styles: [] as any[],
  };

  for (const appearance of model.appearances) {
    // TODO Type properly
    let appearanceConfiguration: any = {};

    appearanceConfiguration.name = buildIdentifier(mslName, appearance.name);
    appearanceConfiguration.parent = appearance.parent?.ref?.name;
    appearanceConfiguration.lineWidth = appearance.lineWidth;
    appearanceConfiguration.lineStyle = appearance.lineStyle?.lineType;
    appearanceConfiguration.filled = appearance.filled;
    appearanceConfiguration.font = appearance.font
      ? {
          fontName: appearance.font.fontName,
          isBold: appearance.font.isBold,
          isItalic: appearance.font.isItalic,
          size: appearance.font.size,
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
    let styleConfiguration: any = {};

    styleConfiguration.name = buildIdentifier(mslName, style.name);
    styleConfiguration.appearanceProvider = style.appearanceProvider;
    styleConfiguration.parameterCount = style.parameterCount;

    if (isEdgeStyle(style)) {
      const edgeStyle = style as EdgeStyle;

      styleConfiguration.connectionType = edgeStyle.connectionType?.FreeForm;

      // Appereance
      const inlineAppearance = edgeStyle.inlineAppearance;
      if (inlineAppearance) {
        styleConfiguration.appearance =
          handleInlineAppearance(inlineAppearance);
      } else if (edgeStyle.referencedAppearance !== undefined && edgeStyle.referencedAppearance.ref !== undefined) {
        styleConfiguration.appearance =
          buildIdentifier(mslName, edgeStyle.referencedAppearance.ref.name);
      }

      // Decorators
      styleConfiguration.decorator = edgeStyle.decorator.map((decorator) => {
        // TODO type properly
        let decoratorConfiguration: any = {
          location: decorator.location,
          movable: decorator.movable,
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
            predefinedDecoratorConfiguration.appearance =
              handleInlineAppearance(inlineAppearance);
          } else {
            predefinedDecoratorConfiguration.appearance =
              predefinedDecorator.referencedAppearance?.ref?.name;
          }

          decoratorConfiguration.predefinedDecorator =
            predefinedDecoratorConfiguration;
        } else if (decoratorShape) {
          // Decorator Shape
          // This casting is possible because of the union types and because AbstractShape is a superset of GraphicsAlgorithm
          decoratorConfiguration.decoratorShape = handleAbstractShape(
            decoratorShape as AbstractShape
          );
        }

        return decoratorConfiguration;
      });

      styleConfiguration.styleType = "EdgeStyle";
    }

    if (isNodeStyle(style)) {
      const nodeStyle = style as NodeStyle;

      styleConfiguration.styleType = "NodeStyle";

      styleConfiguration.fixed = nodeStyle.fixed;

      styleConfiguration.shape = handleAbstractShape(style.mainShape);
    }

    result.styles.push(styleConfiguration);
  }

  return result;
}

function mergeElementConstraints(
  baseConstraints: {
    lowerBound: number | "*";
    upperBound: number | "*";
    elements: string[];
  }[],
  dominantConstraints: {
    lowerBound: number | "*";
    upperBound: number | "*";
    elements: string[];
  }[]
): {
  lowerBound: number | "*";
  upperBound: number | "*";
  elements: string[];
}[] {
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
    entry.elements = entry.elements.filter(
      (element) => !dominantConstraintsElements.has(element)
    );
  }

  // Concatenate the arrays and filter out entries with empty elements
  return [...baseConstraints, ...dominantConstraints].filter(
    (entry) => entry.elements.length > 0
  );
}

function handleAnnotation(annotation: Annotation) {
  return {
    name: annotation.name,
    values: annotation.value ?? [],
  };
}

function handleAbstractShape(abstractShape: AbstractShape) {
  let result: any = {
    anchorShape: abstractShape.anchorShape,
  };

  // Distinguish between ContainerShapes and Shapes and handle specific properties separately
  if (isContainerShape(abstractShape)) {
    const containerShape = abstractShape as ContainerShape;

    // Basic type
    if (isEllipse(containerShape)) {
      result.type = "ELLIPSE";
    } else if (isPolygon(containerShape)) {
      result.type = "POLYGON";
    } else if (isRectangle(containerShape)) {
      result.type = "RECTANGLE";
    } else if (isRoundedRectangle(containerShape)) {
      result.type = "ROUNDEDRECTANGLE";
    }

    // Appereance
    const inlineAppearance = containerShape.inlineAppearance;
    if (inlineAppearance) {
      result.appearance = handleInlineAppearance(inlineAppearance);
    } else {
      result.appearance = containerShape.referencedAppearance?.ref?.name;
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
    result.children = containerShape.children.map((child) =>
      handleAbstractShape(child)
    );

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
      result.type = "TEXT";
    } else if (isMultiText(shape)) {
      result.type = "MULTITEXT";
    } else if (isImage(shape)) {
      result.type = "IMAGE";
    } else if (isWebView(shape)) {
      result.type = "WEBVIEW";
    } else if (isPolyline(shape)) {
      result.type = "POLYLINE";
    }

    // These are treated weirdly because they are almost disjunctive; should be optimized at language level
    if (!isImage(shape) && !isWebView(shape)) {
      const nonImage = shape as Text | MultiText | Polyline;

      // Appearance
      const inlineAppearance = nonImage.inlineAppearance;
      if (inlineAppearance) {
        result.appearance = handleInlineAppearance(inlineAppearance);
      } else {
        result.appearance = nonImage.referencedAppearance?.ref?.name;
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
    background: inlineAppearance.background
      ? handleColor(inlineAppearance.background)
      : null,
    parent: inlineAppearance.parent?.ref?.name,
    foreground: inlineAppearance.foreground
      ? handleColor(inlineAppearance.foreground)
      : null,
    font: inlineAppearance.font ? handleFont(inlineAppearance.font) : null,
    lineStyle: inlineAppearance.lineStyle?.lineType,
    lineWidth: inlineAppearance.lineWidth,
    transparency: inlineAppearance.transparency,
    filled: inlineAppearance.filled?.value,
    imagePath: inlineAppearance.imagePath,
  };
}

function handlePoints(points: Point[]) {
  return points.map((point) => {
    return {
      x: point.x,
      y: point.y,
    };
  });
}

function handleSize(size: Size) {
  return {
    width: size.width,
    height: size.height,
    widthFixed: size.widthFixed,
    heightFixed: size.heightFixed,
  };
}

function handlePosition(abstractPosition: AbstractPosition) {
  let result = {};
  if (isAbsolutePosition(abstractPosition)) {
    const absolutePosition = abstractPosition as AbsolutePosition;
    result = {
      xPos: absolutePosition.xPos,
      yPos: absolutePosition.yPos,
    };
  } else if (isAlignment(abstractPosition)) {
    const alignment = abstractPosition as Alignment;
    result = {
      horizontal: alignment.horizontal.alignmentType,
      xMargin: alignment.xMargin,
      vertical: alignment.vertical.alignmentType,
      yMargin: alignment.yMargin,
    };
  }
  return result;
}

function handleColor(color: Color) {
  return {
    r: color.r ?? 0,
    g: color.g ?? 0,
    b: color.b ?? 0,
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
      b: 0,
    },
    background: {
      r: 255,
      g: 255,
      b: 255,
    },
  };
}

function getEdgeElementConnectionObject(
  mglName: string,
  edgeElementConnection: EdgeElementConnection
) {
  return {
    lowerBound: edgeElementConnection.lowerBound ?? 0,
    upperBound: handleUpperBound(edgeElementConnection.upperBound, -1),
    // TODO Add handling of externalConnections
    elements:
      edgeElementConnection.localConnection.map((localContainment) => {
        const { name } = localContainment.ref!;
        return buildIdentifier(mglName, name);
      }) ?? [],
  };
}

function handleUpperBound(
  specification: number | "*" | undefined,
  defaultValue: number
): number {
  if (specification === undefined) {
    return defaultValue;
  }
  if (specification === "*") {
    return -1;
  }
  return specification;
}

function buildIdentifier(namespace: string, name: string) {
  return `${namespace.toLowerCase()}:${name.toLowerCase()}`
}
