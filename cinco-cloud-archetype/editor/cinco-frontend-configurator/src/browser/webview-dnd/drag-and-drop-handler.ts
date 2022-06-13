/* eslint-disable header/header */

import { isFile, isWebview, isWebviewSibling, isWebviewWrapper } from './drag-and-drop-definitions';
import { DragAndDropFile } from './drag-and-drop-file';

export let currentFile: DragAndDropFile | undefined;
export let currentView: any;

export function registerEventHandler(): void {
    window.addEventListener('drag', function (e) {
        handleDragged(e);
    });
    window.addEventListener('dragend', function (e) {
        handleDragged(e);
    });
    window.addEventListener('dragover', function (e) {
        handleView(e);
    });
    window.addEventListener('dragleave', function (e) {
        handleView(e);
    });
    window.addEventListener('drop', function (e) {
        handleView(e);
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
        currentFile = new DragAndDropFile(fileName, filePath, x, y, eventType, e);

        // if the dragging ends the currentFile is reseted after it was used
        if (eventType === 'dragend') {
            reset();
        }
        return;
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
            console.log('DRAGGING ' + currentFile?.fileName + ' OVER/FROM WEBVIEW!');
            console.log('FILE:' + '\nFileName: ' + currentFile.fileName + '\nFilePath: ' + currentFile.filePath);
            console.log('FILE ON POSITION:' + '\nx: ' + currentFile.x + '\ny: ' + currentFile.y);
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

/**
 * PROTOCOL
 */
