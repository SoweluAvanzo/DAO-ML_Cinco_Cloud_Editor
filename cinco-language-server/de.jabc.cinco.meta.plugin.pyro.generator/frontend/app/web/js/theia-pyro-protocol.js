/**
 * TYPES (please document here):
 * - command(cmd, args, cb)
 * - conenct
 * - connected(view)
 * - dnd(dragAndDropFile)
 */

class DragAndDropFile {
    fileName;
    filePath;
    x;
    y;
    eventType;

    constructor(fileName, filePath, x, y, eventType) {
        this.fileName = fileName;
        this.filePath = filePath;
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
    cb;
}

class TheiaPyroDnDMessage extends TheiaPyroMessage {
    type = 'dnd';
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
            const dndMessage = message;
            const file = dndMessage.file;
            console.log("Pyro received dragged: " + file.fileName);
            break;
        }
    }
};

function sendMessage(msg) {
    window.top.postMessage(msg, '*');
}

function executeCommand(cmdId, args, cb) {
    if (connected) {
        const cmdMessage = new TheiaPyroCommandMessage();
        cmdMessage.cmd = cmdId;
        cmdMessage.args = args;
        cmdMessage.cb = cb;
        sendMessage(cmdMessage);
    }
}

// Init
var connected = false;
function initConnection() {
    console.log("Pyro connecting to Theia...");
    const connectMessage = new TheiaPyroConnectMessage();
    sendMessage(connectMessage);
}
function testTheiaCommand() {
    const cmdMessage = new TheiaPyroCommandMessage();
    cmdMessage.cmd = 'vscode.open';
    cmdMessage.args = [];
    cmdMessage.cb = (value) => {
        console.log('Executed testTheiaCommand: ' + value);
    };
    cmdMessage.cb = JSON.parse(JSON.stringify(cmdMessage.cb)); // clean
    sendMessage(cmdMessage);
}

console.log("PyroTheiaCommunication - loading...");
initConnection();

// TODO: SAMI