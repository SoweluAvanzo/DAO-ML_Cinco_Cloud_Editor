/**
 * TYPES (please document here):
 * - command(cmd, args)
 * - connect
 * - connected
 * - dnd(dragAndDropFile)
 * - filePicker(TheiaFile)
 */
var connected = false;

class TheiaFile {
    fileName;
    filePath;
    content;

    constructor(fileName, filePath, content) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.content = content;
    }
}

class DragAndDropFile extends TheiaFile {
    x;
    y;
    eventType;

    constructor(fileName, filePath, content, x, y, eventType) {
        super(fileName, filePath, content);
        this.x = x;
        this.y = y;
        this.eventType = eventType;
    }
}

class TheiaPyroMessage {
    type;
}

class TheiaPyroConnectMessage extends TheiaPyroMessage {
    type = 'connect';
}

class TheiaPyroConnectedMessage extends TheiaPyroMessage {
    type = 'connected';
}

class TheiaPyroCommandMessage extends TheiaPyroMessage {
    type = 'command';
    cmd;
    args;
}

class TheiaPyroDnDMessage extends TheiaPyroMessage {
    type = 'dnd';
    file;
}

class TheiaPyroFilePickerMessage extends TheiaPyroMessage {
    type = 'filePicker';
    file;
}

// Receive message
window.onmessage = function (e) {
    const message = e.data;
    if (!message.type) {
        return;
    }
    switch (message.type) {
        case 'connected': {
            connected = true;
            console.log("Pyro connected to Theia - you can now call theia commands!");
            break;
        }
        case 'dnd': {
            const file = message.file;
            const filePath = file.filePath;
            const fileName = file.fileName;
            const content = file.content;
            const eventType = file.eventType;
            const graphModelReference = getGraphModelReference(content);
            
            // create event
            const dragEventInit =  {
                dataTransfer: new DataTransfer(),
                clientX: file.x,
                clientY: file.y
            };
            const event = new DragEvent(eventType, dragEventInit);

            if(graphModelReference) {
                // parse graphmodelReference
                const content = JSON.stringify({
                    typename: graphModelReference['modelType'],
                    elementid: graphModelReference['id'],
                    isReference: true,
                    fileExtension: graphModelReference['fileExtension']
                });
                event.dataTransfer.setData("text", content)
            } else {
                // parse fileReference
                event.dataTransfer.setData("fileName", fileName);
                event.dataTransfer.setData("filePath", filePath);
            }

            // switch on event-type
            console.log("content:\n"+ content);
            if(
                file.eventType == 'drag'
                || file.eventType == 'dragover'
            ) {
                console.log("Pyro received dragged: " + filePath);
                confirm_drop(event);
            } else if(file.eventType == 'drop') {
                console.log("Pyro received dropped: " + filePath);
                if(graphModelReference) {
                    drop_on_canvas(event);
                } else {
                    console.log("Event type not implemented for non-graphmodelFiles");
                }
            } else {
                console.log("Event type not implemented: "+file.eventType);
            }

            break;
        }
        case 'filePicker': {
            const file = message.file;
            const content = file.content;
            const filePath = file.filePath;
            const fileName = file.fileName;
            const graphModelReference = getGraphModelReference(content);
            console.log("Pyro received fileRef: " + filePath);
            console.log("content:\n"+ content);
            // TODO: Do Something
            break;
        }
    }
};

function sendMessage(msg, transferable) {
    window.top.postMessage(msg, '*', transferable);
}

function executeCommand(cmdId, args) {
    if (connected) {
        const cmdMessage = new TheiaPyroCommandMessage();
        cmdMessage.cmd = cmdId;
        cmdMessage.args = args;
        sendMessage(cmdMessage);
    }
}

function initConnection() {
    console.log("Pyro connecting to Theia...");
    const connectMessage = new TheiaPyroConnectMessage();
    sendMessage(connectMessage);
}

function callTheiaCommand(cmdId, args) {
    const cmdMessage = new TheiaPyroCommandMessage();
    cmdMessage.cmd = cmdId;
    cmdMessage.args = args;
    sendMessage(cmdMessage);
}

function openFilePicker(types) {
    callTheiaCommand('info.scce.cinco-cloud.open-file-picker', [
        {
            title: 'Open...',
            canSelectFolders: false,
            canSelectFiles: true,
            canSelectMany: false,
            filters: {
                'Text files': types
            }
       }
    ]);
}

function testTheiaCommand() {
    callTheiaCommand('info.scce.cinco-cloud.open-file-picker', [
        {
            title: 'OpenTest...',
            canSelectFolders: false,
            canSelectFiles: true,
            canSelectMany: false,
            filters: {
                'Text files': ['txt']
            }
       }
    ]);
}

function getGraphModelReference(fileContent) {
    try {
        return JSON.parse(fileContent);
    } catch(e) {
        return undefined;
    }
}

initConnection();