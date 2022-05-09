export const httpProtocol = window.location.protocol;
export const wsProtocol = httpProtocol.endsWith('s:') ? 'wss:' : 'ws:';
export const host = window.location.host;
