import { httpProtocol, wsProtocol, host } from './vars';

export const environment = {
  production: true,
  baseUrl: `${httpProtocol}//${host}`,
  webSocketUrl: `${wsProtocol}//${host}/api/ws`,
  apiUrl: `${httpProtocol}//${host}/api`
};
