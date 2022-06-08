package info.scce.cincocloud.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

  public static void writeFileToZip(File file, String fileName, File zip) throws IOException {
    final var fos = new FileOutputStream(zip);
    final var zout = new ZipOutputStream(fos);
    final var fis = new FileInputStream(file);
    final var entry = new ZipEntry(fileName);
    zout.putNextEntry(entry);
    final var bytes = new byte[1024];
    int length;
    while ((length = fis.read(bytes)) >= 0) {
      zout.write(bytes, 0, length);
    }
    zout.close();
    fis.close();
    fos.close();
  }

  public static void readFileFromZip(InputStream zipInputStream, String fileName, OutputStream os) throws IOException {
    final var zis = new ZipInputStream(zipInputStream);
    var entry = zis.getNextEntry();
    while (entry != null) {
      if (!entry.isDirectory() && entry.getName().equals(fileName)) {
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = zis.read(buffer)) > 0) {
          os.write(buffer, 0, length);
        }
      }
      entry = zis.getNextEntry();
    }
    zis.closeEntry();
    zis.close();
  }
}
