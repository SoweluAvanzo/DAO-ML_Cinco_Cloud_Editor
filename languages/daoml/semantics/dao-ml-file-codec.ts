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
    ModelElementContainer,
    FileCodecHandler,
    GraphModel,
    LanguageFilesRegistry,
    ModelElement,
    Node,
    Edge,
    Container
} from '@cinco-glsp/cinco-glsp-api';
import { MetaSpecification, getEnum, isContainer } from '@cinco-glsp/cinco-glsp-common';
import { XMLParser } from 'fast-xml-parser';

/**
 * Language Designer defined example of a FileCodecHandler
 */
export class DaoMLFileCodecHandler extends FileCodecHandler {
    override CHANNEL_NAME: string | undefined = 'DaoML [ CODEC ]';
    readonly attributesPrefix = '@_';
    ENUM_TRANSLATOR = {
        // GovernanceArea.Implementation
        OnChain: 'on-chain',
        'on-chain': 'OnChain',
        Hybrid: 'hybrid',
        hybrid: 'Hybrid',
        OffChain: 'off-chain',
        'off-chain': 'OffChain',
        // Committee.DecisionMakingMethod
        SimpleMajority: 'simple_majority',
        simple_majority: 'SimpleMajority',
        LazyConsensus: 'lazy_consensus',
        lazy_consensus: 'LazyConsensus',
        QuadraticVoting: 'quadratic_voting',
        quadratic_voting: 'QuadraticVoting',
        OptimisticGovernance: 'optimistic_governance',
        optimistic_governance: 'OptimisticGovernance',
        // Permission.PermissionType
        Operational: 'operational',
        operational: 'Operational',
        Structural: 'structural',
        structural: 'Structural',
        Strategic: 'strategic',
        strategic: 'Strategic',
        // AgentType
        human: 'Human',
        Human: 'human',
        Autonomous: ''
    };
    ELEMENT_TYPE_ID_TRANSLATOR = {
        // MODEL
        'dao_ml:dao_model': 'DAO-ML_diagram',
        'DAO-ML_diagram': 'dao_ml:dao_model',
        // DAO
        DAO: 'dao_ml:dao',
        'dao_ml:dao': 'DAO',
        // Role
        Role: 'dao_ml:role',
        'dao_ml:role': 'Role',
        // Committee
        Committee: 'dao_ml:committee',
        'dao_ml:committee': 'Committee',
        // GovernanceArea
        GovernanceArea: 'dao_ml:governancearea',
        'dao_ml:governancearea': 'GovernanceArea',
        // Permission
        Permission: 'dao_ml:permission',
        'dao_ml:permission': 'Permission',
        // IsControlledBy
        'dao_ml:iscontrolledby': 'is_controlled_by',
        is_controlled_by: 'dao_ml:iscontrolledby',
        'dao_ml:federation': 'federates_into',
        federates_into: 'dao_ml:federation',
        'dao_ml:association': 'associated_to',
        associated_to: 'dao_ml:association',
        // aggregation edge-cases
        aggregates: 'dao_ml:aggregation',
        'dao_ml:aggregation': 'aggregates',
        'dao_ml:aggregationrole': 'aggregates',
        'dao_ml:aggregationcommittee': 'aggregates'
    };
    TRANSLATIONS = {
        'DAO-ML_diagram': [
            { xml: 'name', cinco: 'name' },
            { xml: 'uniqueID', cinco: 'id' }, // Special-case id
            { xml: 'xsi:noNamespaceSchemaLocation', xml_default: 'XSD_DAO_ML.xsd' }, // Special-case static
            { xml: 'xmlns:xsi', xml_default: 'http://www.w3.org/2001/XMLSchema-instance' } // Special-case static
        ],
        DAO: [
            { xml: 'DAO_ID', cinco: 'id' },
            { xml: 'DAO_name', cinco: 'name' },
            { xml: 'mission_statement', cinco: 'missionStatement' },
            { xml: 'hierarchical_inheritance', cinco: 'hierarchicalInheritance' }
        ],
        Committee: [
            { xml: 'committee_ID', cinco: 'id' }, // Special-case id
            { xml: 'committee_description', cinco: 'description' },
            { xml: 'decision_making_method', cinco: 'decisionMakingMethod', enum: true }, // Special-case enum
            { xml: 'aggregation_level', cinco: 'aggregationLevel' },
            { xml: 'federation_level', cinco: 'federationLevel' },
            { xml: 'voting_condition', cinco: 'votingCondition' },
            { xml: 'proposalCondition', cinco: 'proposalCondition' }
        ],
        GovernanceArea: [
            { xml: 'gov_area_ID', cinco: 'id' }, // Special-case id
            { xml: 'gov_area_description', cinco: 'description' },
            { xml: 'implementation', cinco: 'implementation', enum: true } // Special-case enum
        ],
        Permission: [
            { xml: 'permission_ID', cinco: 'id' }, // Special-case id
            { xml: 'allowed_action', cinco: 'allowedAction' },
            { xml: 'permission_type', cinco: 'permissionType', enum: true }, // Special-case enum
            { xml: 'ref_gov_area', containment: true } // Special-case containment
        ],
        Role: [
            { xml: 'role_ID', cinco: 'id' }, // Special-case id
            { xml: 'role_name', cinco: 'name' },
            { xml: 'n_agent_min', cinco: 'agentMin' },
            { xml: 'n_agent_max', cinco: 'agentMax' },
            { xml: 'agent_type', cinco: 'agentType', enum: true }, // Special-case enum
            { xml: 'aggregation_level', cinco: 'aggregationLevel' },
            { xml: 'federation_level', cinco: 'federationLevel' }
        ],
        federates_into: [{ xml: '_id', cinco: 'id' }],
        associated_to: [{ xml: '_id', cinco: 'id' }],
        aggregates: [{ xml: '_id', cinco: 'id' }],
        is_controlled_by: [{ xml: '_id', cinco: 'id' }]
    };

    toModelElement(type: string, model: GraphModel): ModelElement {
        let modelElement: ModelElement;
        switch (type) {
            case 'DAO-ML_diagram':
                modelElement = model;
                break;
            case 'DAO':
                modelElement = new Container();
                break;
            case 'GovernanceArea':
                modelElement = new Container();
                break;
            case 'Role':
                modelElement = new Node();
                break;
            case 'Committee':
                modelElement = new Node();
                break;
            case 'Permission':
                modelElement = new Node();
                break;
            case 'aggregates':
                modelElement = new Edge();
                break;
            case 'federates_into':
                modelElement = new Edge();
                break;
            case 'associated_to':
                modelElement = new Edge();
                break;
            case 'is_controlled_by':
                modelElement = new Edge();
                break;
            default:
                throw new Error('Unknown dao_ml-type identified: ' + type);
        }
        modelElement.id = this.toDAOId(modelElement.id); // generate correct id
        return modelElement;
    }

    decode(content: string): GraphModel | undefined {
        if (content == '') {
            return undefined; // triggers new creation
        }
        const options = {
            ignoreAttributes: false, // Keep attributes (e.g., id="123")
            attributeNamePrefix: this.attributesPrefix, // Prefix for attributes in the result (e.g., @id)
            preserveOrder: true, // Preserves the order and structure of the original XML
            cdataPropName: 'cdata' // Keeps CDATA sections
        };
        const parser = new XMLParser(options);
        const xmlContent = parser.parse(content);
        const diagram = xmlContent[0];

        const encodedContainments = diagram['DAO-ML_diagram'];
        const attributes = diagram[':@'];
        const graphModel = new GraphModel();
        let result: GraphModel;
        try {
            result = this.decodeSwitch('DAO-ML_diagram', attributes, encodedContainments, graphModel) as GraphModel;
        } catch (e) {
            throw new Error("> Decoding Error >" + e.stack);
        }
        return result;
    }

    decodeSwitch(
        type: string,
        attributes: Map<string, any>,
        encodedContainments: Map<string, any>[],
        model: GraphModel,
        edge_references: ((model: GraphModel) => void)[] = [],
        containment_references: ((model: GraphModel) => void)[] = []
    ): ModelElement | undefined {
        let modelElement: ModelElement = this.toModelElement(type, model);
        modelElement.type = this.ELEMENT_TYPE_ID_TRANSLATOR[type];
        modelElement.initializeProperties();

        this.log("decoding: "+ modelElement.type + "(" + modelElement.id + ")");
        // set attributes
        let attrs: any[] = this.TRANSLATIONS[type];
        for (const at of attrs) {
            if (at['cinco']) {
                const propKey = this.attributesPrefix + at['xml'];
                let propValue = attributes[propKey];
                if (at['enum']) {
                    if (at['xml'] == 'agent_type' && (propValue == '' || propValue == undefined)) {
                        propValue = 'Autonomous';
                    } else {
                        propValue = this.ENUM_TRANSLATOR[propValue];
                    }
                }
                if (at['cinco'] == 'id') {
                    if (propValue) {
                        modelElement.id = propValue;
                    }
                } else {
                    if (!propValue) {
                        const def = modelElement.getPropertyDefinition(at['cinco']);
                        let defaultValue = def?.defaultValue;
                        if (!def) {
                            throw new Error("Property definition not found: " + at['cinco']);
                        } else if (!def.defaultValue) {
                            const enumDef = getEnum(def.type);
                            if (enumDef) {
                                defaultValue = enumDef.literals[0];
                            } else {
                                console.log("Property definition has no defaultValue for: " + at['cinco']);
                                defaultValue = "";
                            }
                        }
                        modelElement.setProperty(at['cinco'], defaultValue);
                    } else {
                        modelElement.setProperty(at['cinco'], propValue);
                    }
                }
            } else if (at['containment']) {
                // e.g. {xml: 'ref_gov_area', containment: true}
                const propKey = this.attributesPrefix + at['xml'];
                let containmentReferenceId = attributes[propKey];
                containment_references.push((model: GraphModel) => {
                    const container = model.getAllContainedElements().concat(model).find(m => m.id === containmentReferenceId);
                    if (!container || (!Container.is(container) && !GraphModel.is(container))) {
                        throw new Error("Could not resolve containment '" + containmentReferenceId + "' of type " + type);
                    }
                    if (!Node.is(modelElement)) {
                        throw new Error("ModelElement type '" + type + "' needs to be a Node");
                    }
                    // Detach from old container
                    const containerDetach = (container: Container | GraphModel) => {
                        let toDetach = false;
                        for (const containment of container._containments as Node[]) {
                            if (containment.id == modelElement.id) {
                                toDetach = true;
                            }
                            if (Container.is(containment)) {
                                containerDetach(containment);
                            }
                        }
                        container.containments = (container.containments as Node[]).filter(c => c.id !== modelElement.id);
                    };
                    containerDetach(model);
                    // attach to new container
                    container.containments.push(modelElement as Node);
                });
            }
        }

        // correct existing ids if they are uuids (length 36 and 4 '-') - should be sufficient
        if (modelElement.id.length == 36 && this.countSymbol(modelElement.id, '-') == 4) {
            this.log("Recognized unsupported id! Migrating id: "+modelElement.id);
            modelElement.id = this.toDAOId(modelElement.id);
            this.log("New id: "+modelElement.id);
        }
        

        // set containments
        for (const encodedContainment of encodedContainments) {
            const containmentAttributes = encodedContainment[':@'] ?? {};
            const containmentType = Object.entries(encodedContainment)[0][0];
            const children = encodedContainment[containmentType];
            if (containmentType == '#text') {
                edge_references.push((model: GraphModel) => {
                    const edgeTargetId = encodedContainment[containmentType];
                    this.log("edgeTargetId: " + edgeTargetId);
                    const potentialTarget = model.getAllContainedElements().find(e => e.id == edgeTargetId);
                    this.log("edgeTarget: " + JSON.stringify(potentialTarget));
                    if (potentialTarget) {
                        (modelElement as Edge).targetID = potentialTarget.id;
                    } else { // added mechanism to remove broken edges
                        this.log('Could not resolve edge target: ' + edgeTargetId);
                        this.log('Removing dangling edge: ' + modelElement.type + ' (' + modelElement.id + ')');
                        model.edges = model.edges.filter(e => (e as Edge).id !== modelElement.id);
                    }
                });
            } else {
                const childModelElement = this.decodeSwitch(
                    containmentType,
                    containmentAttributes,
                    children,
                    model,
                    edge_references,
                    containment_references
                );
                if (ModelElementContainer.is(modelElement) && Node.is(childModelElement) && !this.isExternalContainment(containmentType)) {
                    modelElement.containments?.push(childModelElement as Node);
                } else if (Edge.is(childModelElement)) {
                    // edges from source
                    if (childModelElement.type == 'dao_ml:aggregation') {
                        // interpolate aggregation type
                        childModelElement.type += modelElement.type == 'dao_ml:role' ? 'role' : 'committee';
                    }
                    childModelElement.sourceID = modelElement.id;
                    model._edges.push(childModelElement);
                }
            }
        }
        if (modelElement.id == model.id) {
            containment_references.forEach(r => r(model));
            try {
                edge_references.forEach(r => r(model));
            } catch(e) {
                this.log("fuck2");
                throw new Error("shit2");
            }
        } else if (Node.is(modelElement)) {
            // handle position and sizes
            try {
                const propKeyX = this.attributesPrefix + 'x';
                const propKeyY = this.attributesPrefix + 'y';
                let propValueX = attributes[propKeyX] ?? '0';
                let propValueY = attributes[propKeyY] ?? '0';
                if (propValueX && propValueY) {
                    modelElement.position = { x: Number.parseFloat(propValueX), y: Number.parseFloat(propValueY) };
                } else {
                    // initialize with random value in view square
                }
            } catch (e) {
                this.log(e);
                modelElement.position = { x: 0, y: 0 };
            }
            try {
                const propKeyWidth = this.attributesPrefix + 'width';
                const propKeyHeight = this.attributesPrefix + 'height';
                let propValueWidth = attributes[propKeyWidth];
                let propValueHeight = attributes[propKeyHeight];
                if (propValueWidth && propValueHeight) {
                    modelElement.size = { width: Number.parseFloat(propValueWidth), height: Number.parseFloat(propValueHeight) };
                } else {
                    // if modelElementContainer in relation to containments
                    // otherwise should be predefined
                }
            } catch (e) {
                this.log(e);
            }
        }
        this.log("finished: " + modelElement.type + "(" + modelElement.id + ")");
        return modelElement;
    }

    isExternalContainment(type: string) {
        let attrs: any[] = this.TRANSLATIONS[type];
        const externalContainmentReference = attrs.find(at => at['containment']);
        return externalContainmentReference !== undefined;
    }

    encode(model: GraphModel): string {
        let result: string | undefined;
        try {
            result = this.encodeSwitch(model, model);
            if (!result) {
                throw new Error('Could not encode model!');
            }
        } catch (e) {
            this.log(e);
            throw new Error('> Encoding Error > ' + e.stack);
        }
        return result;
    }

    encodeSwitch(
        modelElement: ModelElement,
        model: GraphModel,
        indent = '',
        containmentProcedures: ((indent2: string) => string)[] = []
    ): string | undefined {
        const xmlType = this.ELEMENT_TYPE_ID_TRANSLATOR[modelElement.type];
        let externalContainmentProc;
        if (modelElement.type == 'dao_ml:dao') {
            externalContainmentProc = []; // new context opened => collect elements
        } else {
            externalContainmentProc = containmentProcedures; // use same context
        }
        const attributesProc = (m: ModelElement) =>
            this.TRANSLATIONS[this.ELEMENT_TYPE_ID_TRANSLATOR[m.type]]
                .map((attrMap: any) => {
                    const attrKey = attrMap['cinco'];
                    if (attrKey) {
                        if (attrKey == 'id') {
                            return attrMap['xml'] + '="' + m.id + '"';
                        }
                        let attrVal;
                        try {
                            attrVal = m.getProperty(attrKey);
                        } catch (e) {
                            console.log("Error: " + e);
                            attrVal = '';
                        }
                        if (attrMap['enum']) {
                            attrVal = this.ENUM_TRANSLATOR[attrVal];
                        }
                        if (attrVal) {
                            return attrMap['xml'] + '=' + '"' + attrVal + '"';
                        }
                        return undefined;
                    } else if (attrMap['containment']) {
                        // e.g. {xml: 'ref_gov_area', containment: true}, // Special-case containment
                        const foundContainer = this.findContainer(modelElement, model);
                        if (foundContainer) {
                            let attrVal = foundContainer!.id;
                            return attrMap['xml'] + '=' + '"' + attrVal + '"';
                        } else {
                            throw new Error('Could not find container for element: ' + modelElement.id);
                        }
                    } else {
                        // static edge-case
                        return attrMap['xml'] + '=' + '"' + attrMap['xml_default'] + '"';
                    }
                })
                .filter((a: string) => a !== undefined)
                .join(' ');
        const attributes = attributesProc(modelElement);

        // containments
        let containments = '';
        if (ModelElementContainer.is(modelElement) && modelElement.containments.length > 0) {
            containments = (modelElement.containments as Node[])
                .map((m: Node) => {
                    const xmlChildType = this.ELEMENT_TYPE_ID_TRANSLATOR[m.type];
                    if (!this.isExternalContainment(xmlChildType)) {
                        return indent + '\t' + this.encodeSwitch(m, model, indent + '\t', externalContainmentProc);
                    }
                    // external containment will be placed somewhere else
                    externalContainmentProc.push((indent2: string) => {
                        return this.encodeSwitch(m, model, indent2 + '\t', externalContainmentProc) ?? '';
                    });
                    return '';
                })
                .filter(e => e !== '')
                .join('\n');
        }

        // edges
        let edgeContainments = '';
        if (Node.is(modelElement)) {
            const outgoingEdges = (model.edges as Edge[]).filter((e: Edge) => e.sourceID == modelElement.id);
            edgeContainments = outgoingEdges
                .map(e => {
                    const edgeType = this.ELEMENT_TYPE_ID_TRANSLATOR[e.type];
                    const edgeAttributes = attributesProc(e);
                    return `${indent + '\t'}<${edgeType} ${edgeAttributes}>${e.targetID}</${edgeType}>`;
                })
                .join('\n');
        }

        // position and size
        const dimensions = `x="${modelElement.position.x}" y="${modelElement.position.y}" width="${modelElement.size.width}" height="${modelElement.size.height}"`;

        if (modelElement.type == 'dao_ml:dao') {
            // add external containments
            containments += '\n' + indent + '\t' + externalContainmentProc.map(p => p(indent + '\t')).join('\n\t' + indent);
            externalContainmentProc = [];
        }
        // <DAO-ML_diagram xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="XSD_DAO_ML.xsd" name="Group Currency DAO Diagram" uniqueID="GCDAOdiagram" >
        if (['GovernanceArea', 'Permission'].includes(xmlType)) {
            return `<${xmlType} ${attributes} ${dimensions} />`;
        }
        return `<${xmlType} ${attributes} ${dimensions}>${containments !== '' ? '\n' + containments : ''}${edgeContainments !== '' ? '\n' + edgeContainments : ''}\n${indent}</${xmlType}>`;
    }

    findContainer(modelElement: ModelElement, container: Container | GraphModel): Container | GraphModel | undefined {
        for (const containment of container._containments as Node[]) {
            if (containment.id == modelElement.id) {
                return container;
            }
            if (Container.is(containment)) {
                const result = this.findContainer(modelElement, containment);
                if (result) {
                    return result;
                }
            }
        }
        return undefined;
    };

    countSymbol(str: string, symbol: string): number {
        return str.match(new RegExp(`\\${symbol}`, "g"))?.length ?? 0;
    }

    toDAOId(id: string): string {
        return 'dao_' + id.replace(/-/g, '_');
    }
}

// register into app
LanguageFilesRegistry.register(DaoMLFileCodecHandler);
