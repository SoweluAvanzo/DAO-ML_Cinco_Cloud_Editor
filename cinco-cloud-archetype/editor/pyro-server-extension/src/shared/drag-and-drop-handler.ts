/*!
 * Copyright (c) 2019-2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */

export let currentFile: DragAndDropFile | undefined;
export let currentView: any;
export let currentDragEvent: DragEvent | undefined; // TODO:

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
    const eventType = e.type;
    // Check target-type
    const target: any = e.target;
    // switch on target-type
    if (isFile(target)) {
        const filePath = target.title;
        const fileName = target.textContent;
        const x = e.clientX;
        const y = e.clientY;
        /**
         * Creating FileType
         */
        currentFile = new DragAndDropFile(fileName, filePath, x, y, eventType, target);

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
 * DEFINITIONS
 */

export function isFile(target: any): boolean {
    if (!target) {
        return false;
    }
    const classList: DOMTokenList = target.classList;
    return classList.contains('theia-FileStatNode');
}

export function isWebview(target: any): boolean {
    if (!target) {
        return false;
    }
    const classList: DOMTokenList = target.classList;
    return classList.contains('webview');
}

export function isWebviewWrapper(target: any): boolean {
    if (!target) {
        return false;
    }
    const classList: DOMTokenList = target.classList;
    return classList.contains('theia-webview');
}

export function isWebviewSibling(target: any): boolean {
    if (!target) {
        return false;
    }
    const classList: DOMTokenList = target.classList;
    return classList.contains('theia-transparent-overlay');
}

/**
 * PROTOCOL
 */

export class DragAndDropFile {
    fileName: string;
    filePath: string;
    x: number;
    y: number;
    eventType: string;
    target: any;

    constructor(fileName: string, filePath: string, x: number, y: number, eventType: string, target: any) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.x = x;
        this.y = y;
        this.eventType = eventType;
        this.target = target;
    }
}
