/* eslint-disable header/header */

import { CommandRegistry } from '@theia/core';
import { inject } from '@theia/core/shared/inversify';
import { draggingOverWebview } from '../webview-dnd/drag-and-drop-definitions';
import { registerEventHandler, setDragLeaveCB, setDragOverCB, setDropCB } from '../webview-dnd/drag-and-drop-handler';
import { DragAndDropFile, TheiaPyroCommandMessage, TheiaPyroConnectedMessage, TheiaPyroDnDMessage, TheiaPyroMessage }
    from '../webview-dnd/theia-pyro-protocol';

export class FrontendEventController {

    @inject(CommandRegistry)
    static readonly commands: CommandRegistry;
    static pyroWindows: any[] = [];

    static sendMessage(msg: TheiaPyroMessage, window: any): void {
        if (!window) {
            return;
        }
        window.postMessage(msg, '*');
    }

    static sendDnDMessage(dndFile: DragAndDropFile, window: any): void {
        if (!draggingOverWebview()) {
            return;
        }
        const dndMessage = new TheiaPyroDnDMessage();
        dndMessage.file = dndFile;
        FrontendEventController.sendMessage(dndMessage, window);
    }

    static init(): void {
        // register dragAndDropEvents
        registerEventHandler();

        // Set CBs for Send events
        setDragOverCB((file: any, view: any) => {
            const pyroWindow = FrontendEventController.getPyroWindow(view);
            FrontendEventController.sendDnDMessage(file, pyroWindow);
        });
        setDragLeaveCB((file: any, view: any) => {
            const pyroWindow = FrontendEventController.getPyroWindow(view);
            FrontendEventController.sendDnDMessage(file, pyroWindow);
        });
        setDropCB((file: any, view: any) => {
            const pyroWindow = FrontendEventController.getPyroWindow(view);
            FrontendEventController.sendDnDMessage(file, pyroWindow);
        });

        // Receive events
        window.onmessage = (event: any) => {
            const message: TheiaPyroMessage = event.data;
            const type: string = message.type;
            switch (type) {
                case 'connect': {
                    const connectedMessage = new TheiaPyroConnectedMessage();
                    const window = FrontendEventController.getSourceWindow(event);
                    if (!window) {
                        return;
                    }
                    FrontendEventController.registerWindow(window);
                    this.sendMessage(connectedMessage, window);
                    break;
                }
                case 'command': {
                    // protocoll
                    const commandMessage: TheiaPyroCommandMessage = message as TheiaPyroCommandMessage;
                    const cmd = commandMessage.cmd;
                    const args = commandMessage.args;
                    const cb = commandMessage.cb;
                    // executing
                    console.log('Executing Command: ' + cmd);
                    FrontendEventController.commands.executeCommand(
                        cmd, args
                    ).then((value: any) => {
                        if (cb) {
                            cb(value);
                        }
                    });
                    break;
                }
                default:
                    break;
            }
        };
    }

    static registerWindow(window: any): void {
        FrontendEventController.cleanUp();
        FrontendEventController.pyroWindows.push(window);
    }

    static cleanUp(): void {
        const buffer: any[] = [];
        for (let i = 0; i < FrontendEventController.pyroWindows.length; i++) {
            if (FrontendEventController.pyroWindows[i]) {
                const current = FrontendEventController.pyroWindows[i];
                buffer.push(current);
            }
        }
        FrontendEventController.pyroWindows = buffer;
    }

    static getSourceWindow(event: any): any {
        return event.source.window;
    }

    static getPyroWindow(parentView: any): any {
        const parentWindow = parentView?.contentWindow;
        if (!parentWindow) {
            return false;
        }
        for (let i = 0; i < FrontendEventController.pyroWindows.length; i++) {
            const current = FrontendEventController.pyroWindows[i];
            if (FrontendEventController.isParent(parent, current)) {
                return current;
            }
        }
        return undefined;
    }

    static isParent(parentWindow: any, window: any): boolean {
        if (!parentWindow) {
            return false;
        }
        let current = window;
        while (current !== window.top) {
            current = current.parent;
            if (parentWindow === current) {
                return true;
            }
        }
        return parentWindow === current;
    }
}
