"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.TransitionHooks = void 0;
const cinco_glsp_api_1 = require("@cinco-glsp/cinco-glsp-api");
const cinco_glsp_common_1 = require("@cinco-glsp/cinco-glsp-common");
class TransitionHooks extends cinco_glsp_api_1.AbstractEdgeHooks {
    constructor() {
        super(...arguments);
        this.CHANNEL_NAME = 'TransitionHooks [' + this.modelState.root.id + ']';
    }
    canCreate(operation) {
        this.log("Triggered canCreate. Can create edge of type: " + operation.elementTypeId);
        return true;
    }
    preCreate(source, target) {
        this.log("Triggered preCreate. Creating edge for source (" + source.id + ") and target (" + target.id + ")");
    }
    postCreate(edge) {
        this.log("Triggered postCreate on edge (" + edge.id + ")");
    }
    canDelete(edge) {
        this.log("Triggered canDelete on edge (" + edge.id + ")");
        return true;
    }
    preDelete(edge) {
        this.log("Triggered preDelete on edge (" + edge.id + ")");
    }
    postDelete(edge) {
        this.log("Triggered postDelete on edge (" + edge.id + ")");
    }
    /**
     * Change Attribute
     */
    canChangeAttribute(edge, operation) {
        this.log("Triggered canChangeAttribute on edge (" + edge.id + ")");
        return operation.change.kind === 'assignValue';
    }
    preAttributeChange(edge, operation) {
        this.log("Triggered preAttributeChange on edge (" + edge.id + ")");
        this.log('Changing: ' + operation.name
            + ' from: ' + edge.getProperty(operation.name)
            + " to: " +
            (cinco_glsp_common_1.AssignValue.is(operation.change) ? operation.change.value : 'undefined'));
    }
    postAttributeChange(edge, attributeName, oldValue) {
        this.log("Triggered postAttributeChange on edge (" + edge.id + ")");
        this.log('Changed: ' + attributeName + ' from: ' + oldValue + " to: " + edge.getProperty(attributeName));
    }
    /**
     * The following are not yet implemented
     */
    /**
     * Double Click
     */
    canDoubleClick(edge) {
        this.log("Triggered canDoubleClick on edge (" + edge.id + ")");
        return true;
    }
    postDoubleClick(edge) {
        this.log("Triggered postDoubleClick on edge (" + edge.id + ")");
    }
    /**
     * Select
     */
    canSelect(edge) {
        this.log("Triggered canSelect on edge (" + edge.id + ")");
        return true;
    }
    postSelect(edge) {
        this.log("Triggered postSelect on edge (" + edge.id + ")");
        return true;
    }
}
exports.TransitionHooks = TransitionHooks;
cinco_glsp_api_1.LanguageFilesRegistry.register(TransitionHooks);
