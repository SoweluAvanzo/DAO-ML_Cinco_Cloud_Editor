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
 
     constructor(fileName, filePath) {
         this.fileName = fileName;
         this.filePath = filePath;
     }
 }
 
 class DragAndDropFile extends TheiaFile {
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
             console.log("Pyro received dragged: " + file.filePath);
             // TODO: Do something
             break;
         }
         case 'filePicker': {
             const file = message.file;
             console.log("Pyro received fileRef: " + file.filePath);
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
 
 initConnection();