package info.scce.cincocloud.storage;

import io.minio.ErrorCode;
import io.minio.errors.ErrorResponseException;
import java.util.List;

public class MinioUtils {

  public static boolean objectDoesNotExistByException(ErrorResponseException e) {
    final var errorCodes = List.of(ErrorCode.NO_SUCH_OBJECT, ErrorCode.NO_SUCH_BUCKET);
    return errorCodes.contains(e.errorResponse().errorCode());
  }
}
