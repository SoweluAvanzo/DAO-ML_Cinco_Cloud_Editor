import * as grpc from '@grpc/grpc-js';
import { MainServiceClient } from '../../generated/cinco-cloud_grpc_pb';

export const client = new MainServiceClient(`${process.env.CINCO_CLOUD_HOST}:9000`, grpc.credentials.createInsecure());
