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
             const index = fileName.lastIndexOf(".");
             const fileExtension = fileName.substring(index + 1);
             const eventType = file.eventType;
             
             const event = new DragEvent(eventType, { dataTransfer: new DataTransfer() });
             event.clientX = file.x;
             event.clientY = file.y;
             event.dataTransfer.setData("typename", fileExtension);
             event.dataTransfer.setData("fileName", fileName);
             event.dataTransfer.setData("filePath", filePath);
 
             if(
                 file.eventType == 'drag'
                 || file.eventType == 'dragover'
             ) {
                 console.log("Pyro received dragged: " + filePath);
                 console.log("content:\n"+ content);
                 confirm_drop_webstory(event);
             } else if(file.eventType == 'drop') {
                 console.log("Pyro received dropped: " + filePath);
                 console.log("content:\n"+ content);
                 create_node_webstory_after_drop(event.clientX, event.clientY, fileExtension);
             } else {
                 console.log("Event type no implemented: "+file.eventType);
             }
 
             break;
         }
         case 'filePicker': {
             const file = message.file;
             const content = file.content;
             console.log("Pyro received fileRef: " + file.filePath);
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
 
 initConnection();