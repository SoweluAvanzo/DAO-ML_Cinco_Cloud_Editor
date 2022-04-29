const httpProtocol = window.location.protocol;
const wsProtocol = httpProtocol.endsWith('s:') ? 'wss:' : 'ws:';
const host = window.location.host;

export const environment = {
  production: true,
  baseUrl: `${httpProtocol}//${host}`,
  webSocketUrl: `${wsProtocol}//${host}/api/ws`,
  apiUrl: `${httpProtocol}//${host}/api`
};
