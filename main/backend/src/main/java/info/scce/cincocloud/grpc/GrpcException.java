package info.scce.cincocloud.grpc;

import com.google.rpc.Status;

public class GrpcException extends RuntimeException {
    Status status;

    public GrpcException(Status status) {
        this.status = status;
    }
}
