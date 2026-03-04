/********************************************************************************
 * Copyright (c) 2024 The DAO ML Team.
 ********************************************************************************/

import {
    ModelElementContainer,
    GraphModel,
    ModelElement,
    Node,
    Edge,
    Container,
    IdentifiableElement
} from '@cinco-glsp/cinco-glsp-api';
import { getEnum } from '@cinco-glsp/cinco-glsp-common';
import { XMLParser } from 'fast-xml-parser';

export const attributesPrefix = '@_';
const ENUM_TRANSLATOR: { [key: string]: string } = {
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
const ELEMENT_TYPE_ID_TRANSLATOR: { [key: string]: string } = {
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
const TRANSLATIONS: {
    [key: string]: {
        xml: string, // xml attribute name
        cinco?: string, // cinco property name (if different from xml)
        enum?: boolean, // whether the attribute is an enum that needs translation
        containment?: boolean, // whether the attribute is a containment reference that needs special handling
        xml_default?: string // default value for static attributes that are not stored in the model
    }[]
} = {
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

export function parseXMLContent(content: string): {
    attributes: Record<string, any>,
    encodedContainments: Record<string, any>[]
} {
    const options = {
        ignoreAttributes: false, // Keep attributes (e.g., id="123")
        attributeNamePrefix: attributesPrefix, // Prefix for attributes in the result (e.g., @id)
        preserveOrder: true, // Preserves the order and structure of the original XML
        cdataPropName: 'cdata' // Keeps CDATA sections
    };
    const parser = new XMLParser(options);
    const xmlContent = parser.parse(content);
    const diagram = xmlContent[0];
    const encodedContainments = diagram['DAO-ML_diagram'];
    const attributes = diagram[':@'];
    return { attributes, encodedContainments };
}

export function toModelElement(type: string, model: GraphModel): ModelElement {
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
    modelElement.id = toDAOId(modelElement.id); // generate correct id
    return modelElement;
}

export function decodeSwitch(
    type: string,
    content: string | {
        attributes: Record<string, any>,
        encodedContainments: Record<string, any>[]
    },
    model: GraphModel,
    options: {
        edge_references: ((model: GraphModel) => void)[],
        containment_references: ((model: GraphModel) => void)[]
        log: (message: any) => void
    }
): ModelElement | undefined {
    const edge_references = options?.edge_references ?? [];
    const containment_references = options?.containment_references ?? [];
    const log = options?.log ?? console.log;

    const { attributes, encodedContainments } = typeof content === 'string' ? parseXMLContent(content) : content;
    const modelElement: ModelElement = toModelElement(type, model);
    modelElement.type = ELEMENT_TYPE_ID_TRANSLATOR[type];
    modelElement.initializeProperties();

    log('decoding: ' + modelElement.type + '(' + modelElement.id + ')');
    // set attributes
    const attrs: any[] = TRANSLATIONS[type];
    for (const at of attrs) {
        if (at['cinco']) {
            const propKey = attributesPrefix + at['xml'];
            let propValue = attributes[propKey];
            if (at['enum']) {
                if (at['xml'] === 'agent_type' && (propValue === '' || propValue === undefined)) {
                    propValue = 'Autonomous';
                } else {
                    propValue = ENUM_TRANSLATOR[propValue];
                }
            }
            if (at['cinco'] === 'id') {
                if (propValue) {
                    modelElement.id = propValue;
                }
            } else {
                if (!propValue) {
                    const def = modelElement.getPropertyDefinition(at['cinco']);
                    let defaultValue = def?.defaultValue;
                    if (!def) {
                        throw new Error('Property definition not found: ' + at['cinco']);
                    } else if (!def.defaultValue) {
                        const enumDef = getEnum(def.type);
                        if (enumDef) {
                            defaultValue = enumDef.literals[0];
                        } else {
                            console.log('Property definition has no defaultValue for: ' + at['cinco']);
                            defaultValue = '';
                        }
                    }
                    modelElement.setProperty(at['cinco'], defaultValue);
                } else {
                    modelElement.setProperty(at['cinco'], propValue);
                }
            }
        } else if (at['containment']) {
            // e.g. {xml: 'ref_gov_area', containment: true}
            const propKey = attributesPrefix + at['xml'];
            const containmentReferenceId = attributes[propKey];
            containment_references.push((mdl: GraphModel) => {
                const container = mdl.getAllContainedElements().concat(mdl).find(m => m.id === containmentReferenceId);
                if (!container || (!Container.is(container) && !GraphModel.is(container))) {
                    throw new Error("Could not resolve containment '" + containmentReferenceId + "' of type " + type);
                }
                if (!Node.is(modelElement)) {
                    throw new Error("ModelElement type '" + type + "' needs to be a Node");
                }
                // Detach from old container
                const containerDetach = (ctn: Container | GraphModel): void => {
                    for (const containment of ctn._containments as Node[]) {
                        if (Container.is(containment)) {
                            containerDetach(containment);
                        }
                    }
                    ctn.containments = (ctn.containments as Node[]).filter(c => c.id !== modelElement.id);
                };
                containerDetach(mdl);
                // attach to new container
                container.containments.push(modelElement as Node);
            });
        }
    }

    // correct existing ids if they are uuids (length 36 and 4 '-') - should be sufficient
    if (modelElement.id.length === 36 && countSymbol(modelElement.id, '-') === 4) {
        log('Recognized unsupported id! Migrating id: ' + modelElement.id);
        modelElement.id = toDAOId(modelElement.id);
        log('New id: ' + modelElement.id);
    }

    // set containments
    for (const encodedContainment of encodedContainments) {
        const containmentAttributes = encodedContainment[':@'] ?? {};
        const containmentType = Object.entries(encodedContainment)[0][0];
        const children = encodedContainment[containmentType];
        if (containmentType === '#text') {
            edge_references.push((mdl: GraphModel) => {
                const edgeTargetId = encodedContainment[containmentType];
                log('edgeTargetId: ' + edgeTargetId);
                const potentialTarget = mdl.getAllContainedElements().find(e => e.id === edgeTargetId);
                log('edgeTarget: ' + JSON.stringify(potentialTarget));
                if (potentialTarget) {
                    (modelElement as Edge).targetID = potentialTarget.id;
                } else { // added mechanism to remove broken edges
                    log('Could not resolve edge target: ' + edgeTargetId);
                    log('Removing dangling edge: ' + modelElement.type + ' (' + modelElement.id + ')');
                    mdl.edges = mdl.edges.filter(e => (e as Edge).id !== modelElement.id);
                }
            });
        } else {
            const childModelElement = decodeSwitch(
                containmentType,
                {
                    attributes: containmentAttributes,
                    encodedContainments: children
                },
                model,
                {
                    edge_references,
                    containment_references,
                    log
                }
            );
            if (ModelElementContainer.is(modelElement) && Node.is(childModelElement) && !isExternalContainment(containmentType)) {
                modelElement.containments?.push(childModelElement as Node);
            } else if (Edge.is(childModelElement)) {
                // edges from source
                if (childModelElement.type === 'dao_ml:aggregation') {
                    // interpolate aggregation type
                    childModelElement.type += modelElement.type === 'dao_ml:role' ? 'role' : 'committee';
                }
                childModelElement.sourceID = modelElement.id;
                model._edges.push(childModelElement);
            }
        }
    }
    if (modelElement.id === model.id) {
        containment_references.forEach(r => r(model));
        try {
            edge_references.forEach(r => r(model));
        } catch (e: any) {
            throw new Error('something went wrong during edge reference resolution: ' + e.stack);
        }
    } else if (Node.is(modelElement)) {
        // handle position and sizes
        try {
            const propKeyX = attributesPrefix + 'x';
            const propKeyY = attributesPrefix + 'y';
            const propValueX = attributes[propKeyX] ?? '0';
            const propValueY = attributes[propKeyY] ?? '0';
            if (propValueX && propValueY) {
                modelElement.position = { x: Number.parseFloat(propValueX), y: Number.parseFloat(propValueY) };
            } else {
                // initialize with random value in view square
            }
        } catch (e: any) {
            log(e);
            modelElement.position = { x: 0, y: 0 };
        }
        try {
            const propKeyWidth = attributesPrefix + 'width';
            const propKeyHeight = attributesPrefix + 'height';
            const propValueWidth = attributes[propKeyWidth];
            const propValueHeight = attributes[propKeyHeight];
            if (propValueWidth && propValueHeight) {
                modelElement.size = { width: Number.parseFloat(propValueWidth), height: Number.parseFloat(propValueHeight) };
            } else {
                // if modelElementContainer in relation to containments
                // otherwise should be predefined
            }
        } catch (e: any) {
            log(e);
        }
    }
    log('finished: ' + modelElement.type + '(' + modelElement.id + ')');
    return modelElement;
}

export function isExternalContainment(type: string): boolean {
    const attrs: any[] = TRANSLATIONS[type];
    const externalContainmentReference = attrs.find(at => at['containment']);
    return externalContainmentReference !== undefined;
}

export function encodeSwitch(
    modelElement: Node | Edge | GraphModel,
    model: GraphModel,
    options?: {
        indent: string,
        containmentProcedures: ((indent2: string) => string)[]
    }
): string | undefined {
    const indent = options?.indent ?? '';
    const containmentProcedures = options?.containmentProcedures ?? [];

    const xmlType = ELEMENT_TYPE_ID_TRANSLATOR[modelElement.type];
    let externalContainmentProc: ((indent2: string) => string)[];
    if (modelElement.type === 'dao_ml:dao') {
        externalContainmentProc = []; // new context opened => collect elements
    } else {
        externalContainmentProc = containmentProcedures; // use same context
    }
    const attributesProc = (m: Node | Edge | GraphModel): string =>
        TRANSLATIONS[ELEMENT_TYPE_ID_TRANSLATOR[m.type]]?.map((attrMap: any) => {
            const attrKey = attrMap['cinco'];
            if (attrKey) {
                if (attrKey === 'id') {
                    return attrMap['xml'] + '="' + m.id + '"';
                }
                let attrVal;
                try {
                    attrVal = m.getProperty(attrKey);
                } catch (e) {
                    console.log('Error: ' + e);
                    attrVal = '';
                }
                if (attrMap['enum']) {
                    attrVal = ENUM_TRANSLATOR[attrVal];
                }
                if (attrVal) {
                    return attrMap['xml'] + '=' + '"' + attrVal + '"';
                }
                return undefined;
            } else if (attrMap['containment']) {
                // e.g. {xml: 'ref_gov_area', containment: true}, // Special-case containment
                const foundContainer = findContainer(modelElement, model);
                if (foundContainer) {
                    const attrVal = foundContainer!.id;
                    return attrMap['xml'] + '=' + '"' + attrVal + '"';
                } else {
                    throw new Error('Could not find container for element: ' + modelElement.id);
                }
            } else {
                // static edge-case
                return attrMap['xml'] + '=' + '"' + attrMap['xml_default'] + '"';
            }
        }).filter((a: string | undefined) => a !== undefined).join(' ');
    const attributes = attributesProc(modelElement);

    // containments
    let containments = '';
    if (ModelElementContainer.is(modelElement) && modelElement.containments.length > 0) {
        containments = (modelElement.containments as Node[])
            .map((m: Node) => {
                const xmlChildType = ELEMENT_TYPE_ID_TRANSLATOR[m.type];
                if (!isExternalContainment(xmlChildType)) {
                    return indent + '\t' + encodeSwitch(m, model,
                        { indent: indent + '\t', containmentProcedures: externalContainmentProc });
                }
                // external containment will be placed somewhere else
                externalContainmentProc.push((indent2: string) =>
                    encodeSwitch(m, model, { indent: indent2 + '\t', containmentProcedures: externalContainmentProc }) ?? '');
                return '';
            })
            .filter(e => e !== '')
            .join('\n');
    }

    // edges
    let edgeContainments = '';
    if (Node.is(modelElement)) {
        const outgoingEdges = (model.edges as Edge[]).filter((e: Edge) => e.sourceID === modelElement.id);
        edgeContainments = outgoingEdges
            .map(e => {
                const edgeType = ELEMENT_TYPE_ID_TRANSLATOR[e.type];
                const edgeAttributes = attributesProc(e);
                return `${indent + '\t'}<${edgeType} ${edgeAttributes}>${e.targetID}</${edgeType}>`;
            })
            .join('\n');
    }

    // position and size
    const dimensions = `x="${modelElement.position.x}" y="${modelElement.position.y
        }" width="${modelElement.size.width}" height="${modelElement.size.height}"`;

    if (modelElement.type === 'dao_ml:dao') {
        // add external containments
        containments += '\n' + indent + '\t' + externalContainmentProc.map(p => p(indent + '\t')).join('\n\t' + indent);
        externalContainmentProc = [];
    }
    // <DAO-ML_diagram xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    // xsi:noNamespaceSchemaLocation="XSD_DAO_ML.xsd" name="Group Currency DAO Diagram" uniqueID="GCDAOdiagram" >
    if (['GovernanceArea', 'Permission'].includes(xmlType)) {
        return `<${xmlType} ${attributes} ${dimensions} />`;
    }
    return `<${xmlType} ${attributes} ${dimensions}>${containments !== '' ? '\n' + containments : ''
        }${edgeContainments !== '' ? '\n' + edgeContainments : ''}\n${indent}</${xmlType}>`;
}

export function findContainer(modelElement: IdentifiableElement, container: Container | GraphModel): Container | GraphModel | undefined {
    for (const containment of container._containments as Node[]) {
        if (containment.id === modelElement.id) {
            return container;
        }
        if (Container.is(containment)) {
            const result = findContainer(modelElement, containment);
            if (result) {
                return result;
            }
        }
    }
    return undefined;
}

export function countSymbol(str: string, symbol: string): number {
    return str.match(new RegExp(`\\${symbol}`, 'g'))?.length ?? 0;
}

export function toDAOId(id: string): string {
    return 'dao_' + id.replace(/-/g, '_');
}

