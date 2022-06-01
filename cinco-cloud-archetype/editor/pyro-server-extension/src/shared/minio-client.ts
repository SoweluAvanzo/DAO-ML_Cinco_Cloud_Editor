/* eslint-disable header/header */
import { Client } from 'minio';
import { MINIO_HOST, MINIO_PORT, MINIO_ACCESS_KEY, MINIO_SECRET_KEY } from '../node/environment-vars';

let clientInstance: Client;

export function createClient(): Client {
    if (clientInstance === undefined) {
        clientInstance = new Client({
            endPoint: MINIO_HOST,
            port: Number(MINIO_PORT),
            useSSL: false,
            accessKey: MINIO_ACCESS_KEY,
            secretKey: MINIO_SECRET_KEY
        });
    }

    return clientInstance;
}
