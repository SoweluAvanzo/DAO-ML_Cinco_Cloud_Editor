/* eslint-disable header/header */

/**
 * TYPES (please document here):
 * - command(cmd, args, cb)
 * - conenct
 * - connected(view)
 * - dnd(dragAndDropFile)
 */
export class TheiaFile {
    fileName: string;
    filePath: string;

    constructor(fileName: string, filePath: string) {
        this.fileName = fileName;
        this.filePath = filePath;
    }
}

export class DragAndDropFile extends TheiaFile {
    x: number;
    y: number;
    eventType: string;

    constructor(fileName: string, filePath: string, x: number, y: number, eventType: string) {
        super(fileName, filePath);
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
}

export class TheiaPyroDnDMessage extends TheiaPyroMessage {
    type = 'dnd';
    file: DragAndDropFile;
}

export class TheiaPyroFilePickerMessage extends TheiaPyroMessage {
    type = 'filePicker';
    file: TheiaFile;
}

