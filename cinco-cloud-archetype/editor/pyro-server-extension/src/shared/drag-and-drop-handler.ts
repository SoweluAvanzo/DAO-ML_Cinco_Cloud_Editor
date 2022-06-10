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
        currentFile = new DragAndDropFile(fileName, filePath, x, y, eventType);

        // propagate the currentFile
        // TODO:

        // if the dragging ends the currentFile is reseted after it was used
        if (eventType === 'dragend') {
            console.log('EVENT: ' + eventType);
            console.log('DRAG FILE:' + '\nFileName: ' + currentFile.fileName + '\nFilePath: ' + currentFile.filePath);
            console.log('DRAG FILE ON POSITION:' + '\nx: ' + currentFile.x + '\ny: ' + currentFile.y);
            currentFile = undefined;
        }
        return;
    } else {
        // if the dragged object is not a file
        currentFile = undefined;
    }
}

function handleView(e: DragEvent): void {
    const eventType = e.type;
    // Check target-type
    const target: any = e.target;
    // check if it is a webview
    if (isWebview(target)) {
        currentView = target;
        console.log('EVENT: ' + eventType);
        console.log('DRAGGING ' + currentFile?.fileName + ' OVER/FROM WEBVIEW!');
        return;
    } else {
        // if the dragged object is not a file
        currentView = undefined;
    }
}

/**
 * DEFINITIONS
 */

function isFile(target: any): boolean {
    const classList: DOMTokenList = target.classList;
    return classList.contains('theia-FileStatNode');
}

function isWebview(target: any): boolean {
    const classList: DOMTokenList = target.classList;
    return classList.contains('webview');
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

    constructor(fileName: string, filePath: string, x: number, y: number, eventType: string) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.x = x;
        this.y = y;
        this.eventType = eventType;
    }
}
