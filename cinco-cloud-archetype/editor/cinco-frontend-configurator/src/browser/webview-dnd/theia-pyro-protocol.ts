/* eslint-disable header/header */

/**
 * TYPES (please document here):
 * - command(cmd, args, cb)
 * - conenct
 * - connected(view)
 * - dnd(dragAndDropFile)
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

export class TheiaPyroMessage {
    type: string;
}

export class TheiaPyroConnectMessage extends TheiaPyroMessage {
    type = 'connect';
}

export class TheiaPyroConnectedMessage extends TheiaPyroMessage {
    type = 'connected';
}

export class TheiaPyroCommandMessage extends TheiaPyroMessage {
    type = 'command';
    cmd: string;
    args: any;
    cb: any;
}

export class TheiaPyroDnDMessage extends TheiaPyroMessage {
    type = 'dnd';
    file: DragAndDropFile;
}

