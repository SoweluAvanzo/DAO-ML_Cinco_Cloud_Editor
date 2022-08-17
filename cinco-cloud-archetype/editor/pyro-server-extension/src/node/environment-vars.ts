/* eslint-disable header/header */
// Environment vars
export const DATABASE_URL = process.env.DATABASE_URL ?? 'localhost:5432/quarkus_test';
export const DATABASE_USER = process.env.DATABASE_USER ?? 'quarkus_test';
export const DATABASE_PASSWORD = process.env.DATABASE_PASSWORD ?? 'quarkus_test';
export const INTERNAL_PYRO_PORT = process.env.INTERNAL_PYRO_PORT ?? '8000';
export const EXTERNAL_PYRO_SUBPATH = process.env.EXTERNAL_PYRO_SUBPATH ?? '';
export const PYRO_SERVER_BINARIES_FILE = process.env.PYRO_SERVER_BINARIES_FILE ?? '';
export const CINCO_CLOUD_DEBUG = process.env.CINCO_CLOUD_DEBUG ?? 'false';
export const CINCO_CLOUD_HOST = process.env.CINCO_CLOUD_HOST ?? '192.168.65.2';
export const CINCO_CLOUD_PORT = process.env.CINCO_CLOUD_PORT ?? '3000';
export const MINIO_HOST = process.env.MINIO_HOST ?? 'minio-service';
export const MINIO_PORT = process.env.MINIO_PORT ?? '9000';
export const MINIO_ACCESS_KEY = process.env.MINIO_ACCESS_KEY ?? '';
export const MINIO_SECRET_KEY = process.env.MINIO_SECRET_KEY ?? '';
export const EDITOR_TYPE = process.env.EDITOR_TYPE ?? 'MODEL_EDITOR';
