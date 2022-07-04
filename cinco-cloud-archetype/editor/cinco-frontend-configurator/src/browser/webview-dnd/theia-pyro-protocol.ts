/* eslint-disable header/header */

/**
 * TYPES (please document here):
 * - command(cmd, args)
 * - connect
 * - connected
 * - dnd(dragAndDropFile)
 * - filePicker(TheiaFile)
 */
export class TheiaFile {
    fileName: string;
    filePath: string;
    content: string | undefined;

    constructor(fileName: string, filePath: string, content: string | undefined) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.content = content;
    }
}

export class DragAndDropFile extends TheiaFile {
    x: number;
    y: number;
    eventType: string;

    constructor(fileName: string, filePath: string, content: string | undefined, x: number, y: number, eventType: string) {
        super(fileName, filePath, content);
        this.x = x;
        this.y = y;
        this.eventType = eventType;
    }
}

export class TheiaPyroMessage {
    type: string;
    cbId: string;

    constructor() {
        this.cbId = makeid(16);
    }
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
    files: TheiaFile[];
}

function makeid(length: number): string {
    let result = '';
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const charactersLength = characters.length;
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
}
