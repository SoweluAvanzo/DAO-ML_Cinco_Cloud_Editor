/* eslint-disable header/header */

import { CustomFileNavigatorWidget } from './webview-dnd/custom-file-navigator-widget';
import { registerEventHandler } from './webview-dnd/drag-and-drop-handler';

export class FrontendEventController {

    static init(): void {
        // register dragAndDropEvents
        registerEventHandler();
        // setup webview DnD callback
        CustomFileNavigatorWidget.onWebviewDnD = (): void => {
            console.log();
        };
    }

}
