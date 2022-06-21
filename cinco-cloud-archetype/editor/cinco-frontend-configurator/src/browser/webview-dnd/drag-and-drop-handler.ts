/* eslint-disable header/header */

import { FileHelper } from '../file-handler/file-helper';
import { isFile, isWebview, isWebviewSibling, isWebviewWrapper } from './drag-and-drop-definitions';
import { DragAndDropFile } from './theia-pyro-protocol';

export let currentFile: DragAndDropFile | undefined;
export let currentView: any;

/**
 * Callbacks
 */

export let dragCB: any | undefined;
export let dragOverCB: any | undefined;
export let dragLeaveCB: any | undefined;
export let dragEndCB: any | undefined;
export let dropCB: any | undefined;

export function setDragCB(cb: any): void {
    dragCB = cb;
}
export function setDragOverCB(cb: any): void {
    dragOverCB = cb;
}
export function setDragLeaveCB(cb: any): void {
    dragLeaveCB = cb;
}
export function setDragEndCB(cb: any): void {
    dragEndCB = cb;
}
export function setDropCB(cb: any): void {
    dropCB = cb;
}

export function registerEventHandler(): void {
    window.addEventListener('drag', function (e) {
        handleDragged(e);
        if (dragCB) {
            dragCB(currentFile, currentView);
        }
    });
    window.addEventListener('dragend', function (e) {
        handleDragged(e);
        if (dragEndCB) {
            dragEndCB(currentFile, currentView);
        }
    });
    window.addEventListener('dragover', function (e) {
        handleView(e);
        const file = currentFile!;
        file.eventType = 'dragover';
        if (dragOverCB) {
            dragOverCB(currentFile, currentView);
        }
    });
    window.addEventListener('dragleave', function (e) {
        handleView(e);
        const file = currentFile!;
        file.eventType = 'dragleave';
        if (dragLeaveCB) {
            dragLeaveCB(currentFile, currentView);
        }
    });
    window.addEventListener('drop', function (e) {
        handleView(e);
        const view = currentView;
        const file = currentFile;
        if (!file) {
            return;
        }
        // if the dragging ends the currentFile is reseted after it was used
        const fileHelper = new FileHelper();// TODO: SAMI - this is only called iff there is a breakpoint...why????
        fileHelper.resolveUriString(file.filePath).then((content: string) => {
            file.content = content;
            file.eventType = 'drop';
            if (dropCB) {
                dropCB(file, view);
            }
        });
    });
}

/**
 * HANDLER
 */

function handleDragged(e: DragEvent): void {
    // Check target-type
    const target: any = e.target;
    // switch on target-type
    if (isFile(target)) {
        /**
         * Creating FileType
         */
        const filePath = target.title;
        const fileName = target.textContent;
        const x = e.clientX;
        const y = e.clientY;
        const eventType = e.type;
        if (eventType === 'drag') {
            currentFile = new DragAndDropFile(fileName, filePath, undefined, x, y, eventType);
        } else if (eventType === 'dragend') {
            reset();
        }
    } else {
        // if the dragged object is not a file
        reset();
    }
}

function reset(): void {
    currentFile = undefined;
    currentView = undefined;
}

function handleView(e: DragEvent): void {
    // Check target-type
    const target: any = e.target;
    handleWebview(target, e);
}

function handleWebview(target: any, e: DragEvent): void {
    // check if it is a webview
    const isWebviewElement = isWebview(target);
    const isWebViewSiblingElement = isWebviewSibling(target);
    const isWebViewWrapperElement = isWebviewWrapper(target);
    if (currentFile) {
        if (isWebviewElement) {
            currentView = target;
            return;
        } else if (isWebViewSiblingElement) {
            const newTarget = target.nextSibling;
            handleWebview(newTarget, e);
            return;
        } else if (isWebViewWrapperElement) {
            const newTarget = target.firstChild();
            handleWebview(newTarget, e);
            return;
        }
    }
    // if the dragged object is not a file
    currentView = undefined;
}
