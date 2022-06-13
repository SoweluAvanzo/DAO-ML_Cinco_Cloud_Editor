/* eslint-disable header/header */

export class DragAndDropFile {
    fileName: string;
    filePath: string;
    x: number;
    y: number;
    eventType: string;
    e: any;

    constructor(fileName: string, filePath: string, x: number, y: number, eventType: string, dragEvent: any) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.x = x;
        this.y = y;
        this.eventType = eventType;
        this.e = dragEvent;
    }
}
