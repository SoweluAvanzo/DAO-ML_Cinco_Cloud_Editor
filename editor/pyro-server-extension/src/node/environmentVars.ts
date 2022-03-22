/* eslint-disable header/header */
// Environment vars
export const DATABASE_URL = process.env.DATABASE_URL ? process.env.DATABASE_URL : 'localhost:5432/quarkus_test';
export const DATABASE_USER = process.env.DATABASE_USER ? process.env.DATABASE_USER : 'quarkus_test';
export const DATABASE_PASSWORD = process.env.DATABASE_PASSWORD ? process.env.DATABASE_PASSWORD : 'quarkus_test';
export const PYRO_PORT = process.env.PYRO_PORT ? process.env.PYRO_PORT : '8000';
export const CINCO_CLOUD_DEBUG = process.env.CINCO_CLOUD_DEBUG ? process.env.CINCO_CLOUD_DEBUG : 'false';
export const CINCO_CLOUD_HOST = process.env.CINCO_CLOUD_HOST ? process.env.CINCO_CLOUD_HOST : '192.168.65.2';
export const CINCO_CLOUD_PORT = process.env.CINCO_CLOUD_PORT ? process.env.CINCO_CLOUD_PORT : '3000';
