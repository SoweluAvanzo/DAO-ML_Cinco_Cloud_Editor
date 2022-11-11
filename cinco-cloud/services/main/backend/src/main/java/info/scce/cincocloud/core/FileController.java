package info.scce.cincocloud.core;

import info.scce.cincocloud.config.Properties;
import info.scce.cincocloud.db.BaseFileDB;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;

@ApplicationScoped
public class FileController {

  @Inject
  Properties properties;

  public BaseFileDB getFileReference(final long id) {
    return BaseFileDB.findById(id);
  }

  public BaseFileDB getBaseFile(String baseFilePath) {
    try {
      final var uri = new URI(baseFilePath);
      String[] segments = uri.getPath().split("/");
      String idStr = segments[segments.length - 2];
      long id = Long.parseLong(idStr);
      return BaseFileDB.findById(id);
    } catch (URISyntaxException e) {
      e.printStackTrace();
      return null;
    }
  }

  public InputStream loadFile(final BaseFileDB identifier) {
    if (identifier == null) {
      return null;
    }
    try {
      final var fileName = identifier.fileExtension == null
          ? identifier.filename
          : identifier.filename + "." + identifier.fileExtension;

      final var file = Paths.get(getUploadDir().toString(), String.valueOf(identifier.id), fileName);
      return new FileInputStream(file.toFile());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public BaseFileDB storeFile(final String fileName, final InputStream fileContent) throws IOException {
    final BaseFileDB result = new BaseFileDB();
    result.filename = FilenameUtils.removeExtension(fileName);

    if (FilenameUtils.indexOfExtension(fileName) > -1) {
      result.fileExtension = FilenameUtils.getExtension(fileName);
    } else {
      result.fileExtension = null;
    }
    result.persist();

    OutputStream out = null;

    try {
      final var newFile = Paths.get(getUploadDir().toString(), String.valueOf(result.id), fileName).toFile();
      if (!newFile.getParentFile().exists()) {
        newFile.getParentFile().mkdirs();
      }
      newFile.createNewFile();
      out = new FileOutputStream(newFile);

      int read;
      final byte[] bytes = new byte[1024];

      while ((read = fileContent.read(bytes)) != -1) {
        out.write(bytes, 0, read);
      }
    } catch (FileNotFoundException fne) {
      result.delete();
    } finally {
      if (out != null) {
        out.close();
      }
      if (fileContent != null) {
        fileContent.close();
      }
    }

    return result;
  }

  public void deleteFile(String baseFilePath) {
    deleteFile(getBaseFile(baseFilePath));
  }

  public void deleteFile(BaseFileDB identifier) {
    final var filename = identifier.filename + "." + identifier.fileExtension;
    final var file = Paths.get(getUploadDir().toString(), String.valueOf(identifier.id), filename).toFile();
    file.delete();
    identifier.delete();
  }

  private Path getUploadDir() {
    return Path.of(properties.getDataDir(), "uploads");
  }
}