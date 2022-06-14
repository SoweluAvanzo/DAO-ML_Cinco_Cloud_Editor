/* eslint-disable header/header */

import { currentView } from './drag-and-drop-handler';

export function isFile(target: any): boolean {
    if (!target) {
        return false;
    }
    const classList: DOMTokenList = target.classList;
    return classList.contains('theia-FileStatNode');
}

export function draggingOverWebview(): boolean {
    return isWebview(currentView);
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
