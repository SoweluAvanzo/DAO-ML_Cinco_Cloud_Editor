/* eslint-disable header/header */

import { Path } from '@theia/core/lib/common/path';
import URI from '@theia/core/lib/common/uri';
import { FileHelper } from '../file-handler/file-helper';
import { cmdRegistry } from '../menu-command-removal-contribution';
import { registerEventHandler, setDragLeaveCB, setDragOverCB, setDropCB } from '../webview-dnd/drag-and-drop-handler';
import { DragAndDropFile, TheiaFile, TheiaPyroCommandMessage, TheiaPyroConnectedMessage, TheiaPyroDnDMessage, TheiaPyroFilePickerMessage, TheiaPyroMessage }
    from '../webview-dnd/theia-pyro-protocol';

export class FrontendEventController {
    static pyroWindows: any[] = [];

    static sendMessage(msg: TheiaPyroMessage, window: any): void {
        if (!window) {
            return;
        }
        window.postMessage(msg, '*');
    }

    static sendDnDMessage(dndFile: DragAndDropFile, window: any): void {
        if (!window) {
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
                    // executing
                    console.log('Executing Command: ' + cmd);
                    if (cmdRegistry) {
                        if (cmd === 'info.scce.cinco-cloud.open-file-picker') {
                            const window = FrontendEventController.getSourceWindow(event);
                            cmdRegistry.executeCommand(
                                cmd, args[0]
                            ).then((value: any) => {
                                if (value) {
                                    // response to command
                                    const pickedMessage = new TheiaPyroFilePickerMessage();
                                    const fileHelper = new FileHelper();
                                    const uri: URI = value as URI;
                                    const fileName = uri.path.base;
                                    const filePath = uri.path.fsPath(Path.Format.Posix);
                                    fileHelper.resolveUriString(filePath).then((content: string) => {
                                        const pickedFile: TheiaFile = new TheiaFile(
                                            fileName,
                                            filePath,
                                            content
                                        );
                                        pickedMessage.file = pickedFile;
                                        this.sendMessage(pickedMessage, window);
                                    });
                                }
                            });
                        }
                    }
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
