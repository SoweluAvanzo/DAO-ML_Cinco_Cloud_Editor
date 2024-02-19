package info.scce.cincocloud.core.services;

import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.storage.MinioBuckets;
import info.scce.cincocloud.storage.MinioService;
import io.minio.GetObjectArgs;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.io.FilenameUtils;

@ApplicationScoped
public class FileService {

  private static final Logger LOGGER = Logger.getLogger(FileService.class.getName());

  @Inject
  MinioService minio;

  public BaseFileDB getFileReference(final long id) {
    return BaseFileDB.findById(id);
  }

  public InputStream loadFile(final BaseFileDB identifier) {
    if (identifier == null) {
      return null;
    }

    try {
      return minio.getClient().getObject(GetObjectArgs.builder()
          .bucket(MinioBuckets.FILE_UPLOADS_KEY)
          .object(identifier.getFullFilename())
          .build());
    } catch (Exception e) {
      LOGGER.log(Level.INFO, "Failed to load file.", e);
      return null;
    }
  }

  public BaseFileDB storeFile(final String fileName, final InputStream fileContent, final String contentType) throws IOException {
    final BaseFileDB result = new BaseFileDB();
    result.persist();

    // append BaseFileDB id to filename to avoid name collisions
    result.filename = FilenameUtils.removeExtension(fileName) + "-" + result.id;

    if (FilenameUtils.indexOfExtension(fileName) > -1) {
      result.fileExtension = FilenameUtils.getExtension(fileName);
    } else {
      result.fileExtension = null;
    }

    PutObjectArgs uArgs = PutObjectArgs.builder()
        .bucket(MinioBuckets.FILE_UPLOADS_KEY)
        .object(result.getFullFilename())
        .stream(fileContent, -1, 5242880)
        .contentType(contentType)
        .build();

    try {
      minio.getClient().putObject(uArgs);
    } catch (Exception e) {
      LOGGER.log(Level.INFO, "Failed to upload file.", e);
      result.delete();
    } finally {
      if (fileContent != null) {
        fileContent.close();
      }
    }

    return result;
  }

  public void deleteFile(BaseFileDB identifier) {
    try {
      minio.getClient().removeObject(RemoveObjectArgs.builder()
          .bucket(MinioBuckets.FILE_UPLOADS_KEY)
          .object(identifier.getFullFilename())
          .build());
    } catch (Exception e) {
      LOGGER.log(Level.INFO, "Failed to delete file " + identifier.id);
    } finally {
      identifier.delete();
    }
  }
}