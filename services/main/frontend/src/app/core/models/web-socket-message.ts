import { WebSocketEvent } from '../enums/web-socket-event';

export class WebSocketMessage {
  senderId: number;
  event: WebSocketEvent;
  content: any;

  static fromJson(json: string): WebSocketMessage {
    const obj = JSON.parse(json);
    const message = new WebSocketMessage();
    message.senderId = obj['senderId'];
    message.event = obj['event'];
    message.content = obj['content'];
    return message;
  }
}
