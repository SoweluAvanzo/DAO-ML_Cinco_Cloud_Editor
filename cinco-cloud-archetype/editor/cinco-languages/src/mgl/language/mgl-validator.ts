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
import { /* ValidationAcceptor,*/ AstNode, ValidationAcceptor, ValidationChecks } from 'langium';
import {
    CincoAstType,
    Edge,
    EdgeElementConnection,
    ModelElement,
    ComplexModelElement,
    NodeType,
    MglModel,
    Styles,
    Annotation,
    isAttribute,
    isModelElement,
    isComplexModelElement
} from '../../generated/ast';
import type { MglServices } from './mgl-module';
import { getConnectingEdges } from '../util/mgl-util';
import * as path from 'path';
import { existsFileSync } from '@cinco-glsp/cinco-glsp-api';
import { createMslServices } from '../../msl/language/msl-module';
import { extractAstNode } from '../cli/cli-util';
import { NodeFileSystem } from 'langium/node';
import { MglAnnotations } from './mgl-annotations';

/**
 * Register custom validation checks.
 */
export function registerValidationChecks(services: MglServices): void {
    const registry = services.validation.ValidationRegistry;
    const validator = services.validation.MglValidator;
    const checks: ValidationChecks<CincoAstType> = {
        ModelElement: [validator.checkModelElementNameNotUnique, validator.checkInheritedAttributes],
        NodeType: [
            validator.checkStyle
            // validator.checkIncomingEdgesUnique,
            // validator.checkOutgoingEdgesUnique
        ],
        MglModel: [validator.checkStylePath],
        Edge: [validator.checkStyle],
        Annotation: [validator.checkAnnotation],
        ComplexModelElement: [validator.checkInheritanceCircle]
    };
    registry.register(checks, validator);
}

/**
 * Implementation of custom validations.
 */
export class MglValidator {
    // TODO: imports validation

    checkInheritanceCircle(element: ComplexModelElement, acceptor: ValidationAcceptor): void {
        const circledElements = this.isInheritanceCircle(element);
        if (circledElements) {
            const circle = circledElements.map(c => c.name).join(' extends ');
            acceptor('error', 'Inheritance Circle detected: ' + circle, {
                node: element,
                property: 'localExtension'
            });
        }
    }

    isInheritanceCircle(element: AstNode | undefined, seen: ComplexModelElement[] = []): ComplexModelElement[] | undefined {
        if (!isComplexModelElement(element)) {
            return undefined;
        } else if (seen.includes(element)) {
            return seen.concat(element);
        }
        const ancestor = element.localExtension?.ref;
        return this.isInheritanceCircle(ancestor, seen.concat([element]));
    }

    checkAnnotation(annotation: Annotation, acceptor: ValidationAcceptor): void {
        if (isAttribute(annotation.$container)) {
            this.checkAnnotationSupport(MglAnnotations.attributeAnnotations, annotation, acceptor);
        } else if (isModelElement(annotation.$container)) {
            this.checkAnnotationSupport(MglAnnotations.modelElementAnnotations, annotation, acceptor);
        } else {
            const message = 'Unknown Annotation "' + annotation.name + '"!';
            acceptor('warning', message, {
                node: annotation,
                property: 'name'
            });
        }
    }

    checkAnnotationSupport(supportedAnnotations: string[], annotation: Annotation, acceptor: ValidationAcceptor): void {
        if (!supportedAnnotations.includes(annotation.name)) {
            const message = 'Unknown Annotation "' + annotation.name + '"! Supported are: ' + supportedAnnotations.join(', ');
            acceptor('error', message, {
                node: annotation,
                property: 'name'
            });
        }
    }

    async checkStyle(element: Edge | NodeType, acceptor: ValidationAcceptor): Promise<void> {
        if (element.isAbstract) {
            return;
        }
        const style = element.usedStyle;
        const msl = await this.getStyles(element.$container, acceptor);
        const foundStyles = msl?.styles.filter(s => s.name === style);
        if (!msl || (foundStyles && foundStyles.length <= 0)) {
            acceptor('warning', 'Style not found' + (element.usedStyle ? `: "${element.usedStyle}"` : ': (no specified)'), {
                node: element,
                property: 'usedStyle'
            });
        }
        if (foundStyles && foundStyles.length > 0) {
            const foundStyle = foundStyles[0];
            const styleParameterCount = element.styleParameters?.length;
            const exactParameterCount = foundStyle.parameterCount;
            if (styleParameterCount && exactParameterCount !== styleParameterCount) {
                acceptor(
                    'error',
                    'Style parameter does not fit! Style "' +
                        element.usedStyle +
                        '" needs ' +
                        (exactParameterCount ?? 'no') +
                        ' parameter(s)!',
                    {
                        node: element,
                        property: 'styleParameters'
                    }
                );
            }
        }
    }

    checkStylePath(mgl: MglModel, acceptor: ValidationAcceptor): void {
        const absoluteStylePath = this.getAbsoluteStylePath(mgl, acceptor);
        if (!absoluteStylePath) {
            return;
        }
        const exists = existsFileSync(absoluteStylePath);
        if (!exists) {
            acceptor('error', 'MSL/Style file could not be found: ' + mgl.stylePath, { node: mgl, property: 'stylePath' });
        }
    }

    async getStyles(mgl: MglModel, acceptor: ValidationAcceptor): Promise<Styles | undefined> {
        const stylePath = this.getAbsoluteStylePath(mgl, acceptor);
        if (!stylePath) {
            return undefined;
        }
        const services = createMslServices(NodeFileSystem).Msl;
        return extractAstNode<Styles>(stylePath, services);
    }

    getAbsoluteStylePath(mgl: MglModel, acceptor: ValidationAcceptor): string | undefined {
        const mglUri = mgl.$document?.uri.fsPath;
        const mglFolder = mglUri?.substring(0, mglUri?.lastIndexOf('/'));
        if (!mglFolder) {
            acceptor('error', 'No MglFolder found', { node: mgl });
            return undefined;
        }
        const stylePath = mgl.stylePath;
        return path.join(mglFolder, stylePath);
    }

    checkModelElementNameNotUnique(modelElement: ModelElement, acceptor: ValidationAcceptor): void {
        for (const otherElement of modelElement.$container.modelElements) {
            if (modelElement !== otherElement && modelElement.name === otherElement.name) {
                acceptor('error', 'Names of model elements have to be unique', { node: modelElement, property: 'name' });
            }
        }
    }

    checkIncomingEdgesUnique(nodeType: NodeType, acceptor: ValidationAcceptor): void {
        const incomingEdges = nodeType.incomingEdgeConnections.flatMap((incomingConnection: EdgeElementConnection) =>
            getConnectingEdges(incomingConnection)
        );
        for (const incomingEdge of incomingEdges) {
            const filteredList = incomingEdges.filter((edge: Edge) => incomingEdge.name === edge.name);
            if (filteredList.length > 1) {
                acceptor('error', 'Incoming edges should be unique', { node: incomingEdge, property: 'name' });
            }
        }
    }

    checkOutgoingEdgesUnique(nodeType: NodeType, acceptor: ValidationAcceptor): void {
        const outgoingEdges = nodeType.outgoingEdgeConnections.flatMap((outgoingConnection: EdgeElementConnection) =>
            getConnectingEdges(outgoingConnection)
        );
        for (const outgoingEdge of outgoingEdges) {
            if (outgoingEdges.filter((edge: Edge) => outgoingEdge.name === edge.name).length > 1) {
                acceptor('error', 'Outgoing edges should be unique', { node: outgoingEdge, property: 'name' });
            }
        }
    }

    checkInheritedAttributes(modelElement: ComplexModelElement, acceptor: ValidationAcceptor): void {
        const isInheritanceCirclic = this.isInheritanceCircle(modelElement);
        if (modelElement.defaultValueOverrides) {
            for (const defaultValueOverride of modelElement.defaultValueOverrides) {
                // Check if DefaultValueOverride references an AttributeDefinition from an inherited node
                const attributeName = defaultValueOverride.attribute;
                const isDefined = !isInheritanceCirclic && this.isDefinedAttribute(modelElement, attributeName);
                if (!isDefined) {
                    acceptor('error', 'Overriding attribute "' + attributeName + '" is not a valid local or inherited attribute.', {
                        node: defaultValueOverride,
                        property: 'attribute'
                    });
                }
                if (this.isDefinedAttributeLocal(modelElement, attributeName)) {
                    acceptor(
                        'warning',
                        'Duplicate attribute! Overriding attribute "' +
                            attributeName +
                            '" is already defined in "' +
                            modelElement.name +
                            '"',
                        {
                            node: defaultValueOverride,
                            property: 'attribute'
                        }
                    );
                }
                if (modelElement.defaultValueOverrides.filter(d => d.attribute === attributeName).length > 1) {
                    acceptor('error', 'Overriding attribute "' + attributeName + '" is a duplicate.', {
                        node: defaultValueOverride,
                        property: 'attribute'
                    });
                }
            }
            for (const attribute of modelElement.attributes) {
                // Check if DefaultValueOverride references an AttributeDefinition from an inherited node
                const attributeName = attribute.name;
                if (isInheritanceCirclic || this.isDefinedAttribute(modelElement, attributeName, true)) {
                    if (modelElement.attributes.filter(a => a.name === attributeName).length > 1) {
                        acceptor('error', 'Duplicate attribute! "' + attributeName + '" is already defined in ' + modelElement.name, {
                            node: attribute,
                            property: 'name'
                        });
                    } else {
                        acceptor(
                            'error',
                            'Duplicate attribute! "' +
                                attributeName +
                                '" is already inherited. ' +
                                "Use 'override <attributeName> := <defaultValue>' to change the default value.",
                            { node: attribute, property: 'name' }
                        );
                    }
                }
            }
        }
    }

    isDefinedAttributeLocal(modelElement: ComplexModelElement, attributeName: string, checkDuplicate = false): boolean {
        // Check if the attribute is defined locally in this node
        return modelElement.attributes.filter(a => a.name === attributeName).length > (checkDuplicate ? 1 : 0);
    }

    isDefinedAttribute(modelElement: ComplexModelElement, attributeName: string, checkDuplicate = false): boolean {
        // Check if the attribute is defined locally in this node
        if (this.isDefinedAttributeLocal(modelElement, attributeName, checkDuplicate)) {
            return true;
        }
        // Check if the attribute is inherited from parent nodes
        if (modelElement.localExtension) {
            const parent = modelElement.localExtension.ref;
            if (parent) {
                return this.isDefinedAttribute(parent, attributeName);
            }
        } else if (modelElement.externalExtension) {
            // TODO: resolve
            return true;
        }
        // not extending, no attributes with the name
        return false;
    }
}
