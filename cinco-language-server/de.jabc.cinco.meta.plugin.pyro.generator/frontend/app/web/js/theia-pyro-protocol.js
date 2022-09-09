/**
 * PROTOCOL
 * 
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
     cbId;
 
     constructor() {
         this.cbId = makeid(16);
     }
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
     files;
 }
 
 function makeid(length) {
     var result = '';
     var characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
     var charactersLength = characters.length;
     for (var i = 0; i < length; i++) {
         result += characters.charAt(Math.floor(Math.random() * charactersLength));
     }
     return result;
 }
 
 /**
  * PROCEDURES AND HANDLING
  */
 
 var callbackMap = {
     // ID: Callback
 };
 
 // Receive message
 window.onmessage = function (e) {
     const message = e.data;
     if (!message.type) {
         return;
     }
     switch (message.type) {
         case 'connected': {
             handleConnected();
             break;
         }
         case 'dnd': {
             handleDnD(message);
             break;
         }
         case 'filePicker': {
             handleFilePicker(message);
             break;
         }
     }
 };
 
 /**
  * CONNECTED
  */
 
 function handleConnected() {
     connected = true;
     console.log("Pyro connected to Theia - you can now call theia commands!");
 }
 
 function initConnection() {
     console.log("Pyro connecting to Theia...");
     const connectMessage = new TheiaPyroConnectMessage();
     sendMessage(connectMessage);
 }
 
 function connectedToTheia() {
     return connected;
 }
 
 /**
  * DND
  */
 
 function handleDnD(message) {
     const file = message.file;
     const filePath = file.filePath;
     const fileName = file.fileName;
     const content = file.content;
     const eventType = file.eventType;
     const graphModelReference = getGraphModelReference(content);
     const isGraphModelReference = graphModelReference != undefined;
     // create event
     const dragEventInit = {
         dataTransfer: new DataTransfer(),
         clientX: file.x,
         clientY: file.y
     };
     const event = new DragEvent(eventType, dragEventInit);
     event.dataTransfer.setData("fileName", fileName);
     event.dataTransfer.setData("filePath", filePath);
 
     // switch on event-type
     if (file.eventType == 'drop') {
         // fire event to file-drop-areas
         var fired = false;
         const dropAreas = document.getElementsByClassName('file-drop-area');
         for (var dropArea of dropAreas) {
             // check if any file-drop-area was the target of the drop
             if (isOnElement(file.x, file.y, dropArea)) {
                 fireCustomEvent('drop', dropArea, { dataTransfer: file });
                 fired = true;
                 break;
             }
         }
         if (!fired) {
             // if no file-drop-area was target of the drop
             // check if the dropped file was a GraphModelReference
             // and if so, drop it on the canvas 
             if (isGraphModelReference) {
                 // parse graphmodelReference
                 const content = JSON.stringify({
                     typename: graphModelReference['modelType'],
                     elementid: graphModelReference['id'],
                     isReference: true,
                     fileExtension: graphModelReference['fileExtension']
                 });
                 event.dataTransfer.setData("text", content);
                 drop_on_canvas(event);
             }
         }
     }
 }
 
 function isOnElement(clientX, clientY, element) {
     const offset = getOffset(element);
     const H = offset.left < clientX && clientX < offset.right;
     const V = offset.top < clientY && clientY < offset.bottom;
     return H && V;
 }
 
 function getOffset(el) {
     const rect = el.getBoundingClientRect();
     return {
         left: rect.left + window.scrollX,
         right: rect.right + window.scrollX,
         top: rect.top + window.scrollY,
         bottom: rect.bottom + window.scrollY
     };
 }
 
 function getGraphModelReference(fileContent) {
     try {
         return JSON.parse(fileContent);
     } catch (e) {
         return undefined;
     }
 }
 
 /**
  * FILEPICKER
  */
 
 /**
  * For the Callback-Function 'cb' to be triggered, the sending message must have the
  * same cbId (callbackId) as the received message, thus it's a message, that is passed around. 
  */
 function handleFilePicker(message) {
     const files = message.files;
     for (var file of files) {
         const filePath = file.filePath;
         const content = file.content;
         console.log("Pyro received fileRef: " + filePath);
     }
     console.log("cross-domain-message-id: " + message.cbId);
     // callback
     var cb = callbackMap[message.cbId];
     if (cb) {
         delete callbackMap[message.cbId];
         cb(files);
     }
 }
 
 /**
  * COMMUNICATION WITH THEIA
  */
 
 function sendMessage(msg, transferable) {
    let targetWindow = window.top;
    try{
        if (window.top.document.title.startsWith('CincoCloud')) {
            targetWindow = window.top.document.querySelector('iframe').contentWindow;
        }
    } catch(e) {
        console.log(e);
    }
    targetWindow.postMessage(msg, '*', transferable);
 }
 
 function executeCommand(cmdId, args) {
     if (connected) {
         const cmdMessage = new TheiaPyroCommandMessage();
         cmdMessage.cmd = cmdId;
         cmdMessage.args = args;
         sendMessage(cmdMessage);
     }
 }
 
 function callTheiaCommand(cmdId, args, callBackFunction) {
     const cmdMessage = new TheiaPyroCommandMessage();
     cmdMessage.cmd = cmdId;
     cmdMessage.args = args;
     sendMessage(cmdMessage);
     if (callBackFunction) {
         callbackMap[cmdMessage.cbId] = callBackFunction;
     }
 }
 
 function openFilePicker(types, multi, callbackFunction) {
     var title = 'Select ' + (multi ? 'File(s)' : 'File');
     callTheiaCommand('info.scce.cinco-cloud.open-file-picker', [
         {
             title: title,
             canSelectFolders: false,
             canSelectFiles: true,
             canSelectMany: multi,
             filters: {
                 'Allowed Files': types
             }
         }
     ], callbackFunction);
 }

 function openPrimeNode(uri, viewType){
    callTheiaCommand('vscode.openWith', [
        {
            resource: uri,
            viewType: viewType
        }
    ]
    );
 }
 /**
  * UTILS
  */
 
 function fireCustomEvent(eventName, element, data) {
     var event;
     data = data || {};
     if (document.createEvent) {
         event = document.createEvent("HTMLEvents");
         event.initEvent(eventName, true, true);
     } else {
         event = document.createEventObject();
         event.eventType = eventName;
     }
 
     event.eventName = eventName;
     event = $.extend(event, data);
 
     if (document.createEvent) {
         element.dispatchEvent(event);
     } else {
         element.fireEvent("on" + event.eventType, event);
     }
 }
 
 $(document).on('change', '.file-input', function () {
     var filesCount = $(this)[0].files.length;
     var textbox = $(this).prev();
     if (filesCount === 1) {
         var fileName = $(this).val().split('\\').pop();
         textbox.text(fileName);
     } else {
         textbox.text(filesCount + ' files selected');
     }
 });
 
 initConnection();
 
 