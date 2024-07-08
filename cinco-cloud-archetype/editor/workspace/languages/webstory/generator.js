"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.WebstoryGenerator = void 0;
const cinco_glsp_api_1 = require("@cinco-glsp/cinco-glsp-api");
const cinco_glsp_common_1 = require("@cinco-glsp/cinco-glsp-common");
/**
 * Generator for the Webstory
 */
class WebstoryGenerator extends cinco_glsp_api_1.GeneratorHandler {
    constructor() {
        super(...arguments);
        this.CHANNEL_NAME = 'Webstory [' + this.modelState.root.id + ']';
    }
    execute(action, ...args) {
        // parse action
        const model = this.getElement(action.modelElementId);
        //  logging
        var message = 'Element [' + model.type + '] generation process started';
        this.log(message, { show: true });
        // generate
        this.generate(model);
        //  logging
        message = 'Element [' + model.type + '] generation process finished';
        this.log(message, { show: true });
        return [];
    }
    canExecute(action, ...args) {
        const element = this.getElement(action.modelElementId);
        return element !== undefined;
    }
    /**
     * generate files
     */
    generate(model) {
        const targetPath = "webstory-output/src-gen/";
        this.log('Collecting information', { show: true });
        // Collect starting information
        const graphModel = model;
        const startMarker = graphModel.containments.find((node) => node.type === 'webstory:startmarker');
        // Somehow, this doesn't word but the line after it works; I have therefore used the second version throughout the generator - this should be changes if it has been fixed
        // const firstScreen = startMarker?.successors[0];
        const firstActivity = startMarker === null || startMarker === void 0 ? void 0 : startMarker.outgoingEdges[0].index.findNode(startMarker === null || startMarker === void 0 ? void 0 : startMarker.outgoingEdges[0].targetID);
        if (!firstActivity) {
            this.notify('Start marker is not connected to an activity!', 'ERROR');
            throw new Error('Start marker is not connected to an activity!');
        }
        const trackedActivities = [firstActivity];
        const generatorActivities = [];
        const generatorVariables = [];
        // Collect all activities
        for (var currentActivityIndex = 0; currentActivityIndex < trackedActivities.length; currentActivityIndex++) {
            this.log(`Processing activity ${currentActivityIndex}`, { show: true });
            const currentActivity = trackedActivities[currentActivityIndex];
            if (currentActivity.type === 'webstory:screen') {
                const imagePath = currentActivity.getProperty('backgroundImage');
                const generatorScreen = {
                    id: currentActivity.id,
                    image: imagePath,
                    areas: []
                };
                // copy image (TODO: make this folder management api in createFile)
                if (imagePath && imagePath.length > 0) {
                    if (imagePath.includes('/')) {
                        const components = imagePath.split('/');
                        let path = targetPath;
                        for (var i = 0; i < components.length - 1; i++) {
                            path += components[i] + "/";
                            if (!this.existsDirectory(path)) {
                                this.createDirectory(path);
                            }
                        }
                    }
                    this.copyFile(imagePath, targetPath + imagePath, true, cinco_glsp_api_1.RootPath.LANGUAGES);
                }
                // handle areas
                currentActivity.containments.forEach((area) => {
                    const successor = area.outgoingEdges[0].index.findNode(area.outgoingEdges[0].targetID);
                    if (!successor) {
                        this.notify(`Area ${JSON.stringify(area.id)} has no successor!`, 'ERROR');
                        throw new Error(`Area ${JSON.stringify(area.id)} has no successor!`);
                    }
                    else {
                        const trackedActivity = trackedActivities.find((el) => el.id === successor.id);
                        // Add if not already exists in screens
                        if (!trackedActivity) {
                            trackedActivities.push(successor);
                        }
                    }
                    // Create area and add it
                    const relativePositions = this.computeRelativePositions(currentActivity, area);
                    generatorScreen.areas.push({
                        id: area.id,
                        originX: relativePositions.relativeX,
                        originY: relativePositions.relativeY,
                        width: relativePositions.relativeWidth,
                        height: relativePositions.relativeHeight,
                        targetActivityId: successor.id
                    });
                });
                // Add WebstoryScreen object to collecting array
                generatorActivities.push(generatorScreen);
            }
            else if (currentActivity.type === 'webstory:variable') {
                generatorVariables.push({
                    name: currentActivity.getProperty('name')
                });
            }
            else if (currentActivity.type === 'webstory:condition') {
                // get dataflow source
                const dataFlows = currentActivity.incomingEdges.filter(e => e.type === 'webstory:dataflow');
                const dataFlowSourceId = dataFlows.length > 0 ? dataFlows[0].sourceID : undefined;
                let variableName;
                if (dataFlowSourceId) {
                    const variable = model.getGraphModel().containments.find(e => e.id === dataFlowSourceId);
                    if (!variable) {
                        this.notify('Condition is not connected to a variable!', 'ERROR');
                        throw new Error('Condition is not connected to a variable!');
                    }
                    variableName = variable.getProperty('name');
                }
                // get true successor
                const trueTransitions = currentActivity.outgoingEdges.filter(e => e.type === 'webstory:ttransition');
                const trueTransitionsTargetId = trueTransitions.length > 0 ? trueTransitions[0].targetID : undefined;
                const trueSuccessor = graphModel.containments.find(e => e.id === trueTransitionsTargetId);
                if (!trueSuccessor) {
                    this.notify(`Condition has no true successor!`, 'ERROR');
                    throw new Error(`Condition has no true successor!`);
                }
                else {
                    const trackedActivity = trackedActivities.find((el) => el.id === trueSuccessor.id);
                    // Add if not already exists in screens
                    if (!trackedActivity) {
                        trackedActivities.push(trueSuccessor);
                    }
                }
                // get false successor
                const falseTransitions = currentActivity.outgoingEdges.filter(e => e.type === 'webstory:ftransition');
                const falseTransitionsTargetId = falseTransitions.length > 0 ? falseTransitions[0].targetID : undefined;
                const falseSuccessor = graphModel.containments.find(e => e.id === falseTransitionsTargetId);
                if (!falseSuccessor) {
                    this.notify(`Condition has no false successor!`, 'ERROR');
                    throw new Error(`Condition has no false successor!`);
                }
                else {
                    const trackedActivity = trackedActivities.find((el) => el.id === falseSuccessor.id);
                    // Add if not already exists in screens
                    if (!trackedActivity) {
                        trackedActivities.push(falseSuccessor);
                    }
                }
                generatorActivities.push({
                    id: currentActivity.id,
                    dataSource: variableName,
                    trueSuccessor: trueTransitionsTargetId,
                    falseSuccessor: falseTransitionsTargetId
                });
            }
            else if (currentActivity.type === 'webstory:modifyvariable') {
                // get dataflow target
                const dataFlows = currentActivity.outgoingEdges.filter(e => e.type === 'webstory:dataflow');
                const dataFlowTargetId = dataFlows.length > 0 ? dataFlows[0].targetID : undefined;
                let variableName;
                if (dataFlowTargetId) {
                    const variable = model.getGraphModel().containments.find(e => e.id === dataFlowTargetId);
                    if (!variable) {
                        this.notify('Condition is not connected to a variable!', 'ERROR');
                        throw new Error('Condition is not connected to a variable!');
                    }
                    variableName = variable.getProperty('name');
                }
                // get true successor
                const transitions = currentActivity.outgoingEdges.filter(e => e.type === 'webstory:transition');
                const transitionTarget = transitions.length > 0 ? transitions[0].targetID : undefined;
                const successor = graphModel.containments.find(e => e.id === transitionTarget);
                if (!successor) {
                    this.notify(`ModifyVariable has no successor!`, 'ERROR');
                    throw new Error(`ModifyVariable has no successor!`);
                }
                else {
                    const trackedActivity = trackedActivities.find((el) => el.id === successor.id);
                    // Add if not already exists in screens
                    if (!trackedActivity) {
                        trackedActivities.push(successor);
                    }
                }
                generatorActivities.push({
                    id: currentActivity.id,
                    dataTarget: variableName,
                    successor: transitionTarget,
                    value: '' + currentActivity.getProperty('value')
                });
            }
        }
        // Generate resulting HTML file
        this.createFile(targetPath + 'webstory.html', this.getContent(generatorActivities, generatorVariables));
        this.notify('Generated Webstory successfully');
    }
    computeRelativePositions(container, child) {
        const containerWidth = container.size.width;
        const containerHeight = container.size.height;
        const childX = child.position.x;
        const childY = child.position.y;
        const childWidth = child.size.width;
        const childHeight = child.size.height;
        // Returns in percent relative to parent
        return {
            relativeX: childX / containerWidth * 100,
            relativeY: childY / containerHeight * 100,
            relativeWidth: childWidth / containerWidth * 100,
            relativeHeight: childHeight / containerHeight * 100
        };
    }
    getContent(activities, variables) {
        // handle variables
        const javascriptVariables = [];
        variables.forEach(v => {
            javascriptVariables.push(`
                var ${v.name} = false;
            `);
        });
        // handle screens
        const cssRules = [];
        const htmlEntities = [];
        const screens = activities.filter(a => WebstoryScreen.is(a));
        screens.forEach((webstoryScreen, index) => {
            var _a;
            const areasHtml = [];
            cssRules.push(`
                 #activity-${webstoryScreen.id} {
                    ${index === 0 ? 'display: block;' : ''}
                     background-image: url('${webstoryScreen.image}');
                 }`);
            (_a = webstoryScreen.areas) === null || _a === void 0 ? void 0 : _a.forEach((area, index) => {
                cssRules.push(`
                     #activity-${webstoryScreen.id}-area-${index} {
                         position: absolute;
                         left: ${area.originX}%;
                         top: ${area.originY}%;
                         width: ${area.width}%;
                         height: ${area.width}%;
                     }
                 `);
                areasHtml.push(`
                     <div id="activity-${webstoryScreen.id}-area-${index}" onclick="toggleActivities('activity-${webstoryScreen.id}', 'activity-${area.targetActivityId}')"></div>
                 `);
            });
            htmlEntities.push(`
                 <div id="activity-${webstoryScreen.id}">
                     ${areasHtml.join('\n')}
                 </div>
             `);
        });
        // handle conditions and modifyVariables
        const conditions = activities.filter(a => Condition.is(a));
        const modifyVariables = activities.filter(a => ModifyVariable.is(a));
        const conditionsHtml = conditions.map(c => `
            if(newActivityId === 'activity-${c.id}') {
                if(${c.dataSource}) {
                    newActivityId = 'activity-${c.trueSuccessor}';
                } else {
                    newActivityId = 'activity-${c.falseSuccessor}';
                }
            }    
        `);
        const modifyVariablesHtml = modifyVariables.map(m => `
            if(newActivityId === 'activity-${m.id}') {
                ${m.dataTarget} = ${m.value};
                newActivityId = 'activity-${m.successor}';
            }    
        `);
        return `
 <!DOCTYPE html>
 <html>
     <head>
         <title>Webstory</title>
         <style>
             /* Set the screens to take full viewport height and width */
             body > [id^="activity-"] {
                 position: relative;
                 width: 100vw;
                 height: 100vh;
                 display: none; /* By default, set all screens to be invisible */
                 background-size: cover;
             }
 
             /* Place the area in the lower right quarter of its parent screen */
             [id^="activity-"][id$="-area-0"] {
                 position: absolute;
                 cursor: pointer;
             }
 
             ${cssRules.join('\n\n')}
         </style>
     </head>
     <body>
         ${htmlEntities.join('\n')}
 
         <script>
            // variables 
            ${javascriptVariables.join('\n')}

             function toggleActivities(oldActivityId, targetActivityId) {
                var oldActivity = document.getElementById(oldActivityId);
                oldActivity.style.display = "none";
                var newActivityId = targetActivityId;

                // handle conditions
                ${conditionsHtml.join('\n')}
                
                // handle modify variables
                ${modifyVariablesHtml.join('\n')}

                var newActivity = document.getElementById(newActivityId);
                newActivity.style.display = "block";
             }
         </script>
     </body>
 </html>`;
    }
}
exports.WebstoryGenerator = WebstoryGenerator;
var WebstoryScreen;
(function (WebstoryScreen) {
    function is(object) {
        return cinco_glsp_common_1.AnyObject.is(object) && (0, cinco_glsp_common_1.hasArrayProp)(object, 'areas');
    }
    WebstoryScreen.is = is;
})(WebstoryScreen || (WebstoryScreen = {}));
var Condition;
(function (Condition) {
    function is(object) {
        return cinco_glsp_common_1.AnyObject.is(object)
            && (0, cinco_glsp_common_1.hasStringProp)(object, 'dataSource')
            && (0, cinco_glsp_common_1.hasStringProp)(object, 'trueSuccessor')
            && (0, cinco_glsp_common_1.hasStringProp)(object, 'falseSuccessor');
    }
    Condition.is = is;
})(Condition || (Condition = {}));
var ModifyVariable;
(function (ModifyVariable) {
    function is(object) {
        return cinco_glsp_common_1.AnyObject.is(object)
            && (0, cinco_glsp_common_1.hasStringProp)(object, 'successor')
            && (0, cinco_glsp_common_1.hasStringProp)(object, 'dataTarget')
            && (0, cinco_glsp_common_1.hasStringProp)(object, 'value');
    }
    ModifyVariable.is = is;
})(ModifyVariable || (ModifyVariable = {}));
var Variable;
(function (Variable) {
    function is(object) {
        return cinco_glsp_common_1.AnyObject.is(object)
            && (0, cinco_glsp_common_1.hasStringProp)(object, 'name');
    }
    Variable.is = is;
})(Variable || (Variable = {}));
// register into app
cinco_glsp_api_1.LanguageFilesRegistry.register(WebstoryGenerator);
