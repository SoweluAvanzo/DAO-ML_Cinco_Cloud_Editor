package info.scce.cincocloud.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.enterprise.context.ApplicationScoped;
import info.scce.cincocloud.db.BaseFileDB;

@ApplicationScoped
public class FileController {

    public BaseFileDB getFileReference(final long id) {
        final BaseFileDB file = BaseFileDB.findById(id);

        return file;
    }

    public BaseFileDB getBaseFile(String baseFilePath) {
        try {
            java.net.URI uri = new java.net.URI(baseFilePath);
            String[] segments = uri.getPath().split("/");
            String idStr = segments[segments.length - 2];
            long id = Long.parseLong(idStr);
            return BaseFileDB.findById(id);
        } catch (java.net.URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public InputStream loadFile(final BaseFileDB identifier) {
        if (identifier == null) {
            return null;
        }
        try {
            final String path = "uploads" + File.separator + identifier.id + File.separator;
            final String fileName = identifier.fileExtension == null ?
                    identifier.filename
                    : identifier.filename + "." + identifier.fileExtension;
            return new FileInputStream(new File(path + fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BaseFileDB storeFile(final String fileName, final InputStream data) throws IOException {
        final BaseFileDB result = new BaseFileDB();
        result.filename = org.apache.commons.io.FilenameUtils.removeExtension(fileName);

        if (org.apache.commons.io.FilenameUtils.indexOfExtension(fileName) > -1) {
            result.fileExtension = org.apache.commons.io.FilenameUtils.getExtension(fileName);
        } else {
            result.fileExtension = null;
        }
        result.persist();

        OutputStream out = null;
        InputStream filecontent = data;

        try {
            File newFile = new File("uploads" + File.separator + result.id + File.separator + fileName);
            if (!newFile.getParentFile().exists()) {
                newFile.getParentFile().mkdirs();
            }
            newFile.createNewFile();
            out = new FileOutputStream(newFile);

            int read;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
        } catch (FileNotFoundException fne) {
            result.delete();
        } finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
        }

        return result;
    }

    public void deleteFile(String baseFilePath) {
        deleteFile(getBaseFile(baseFilePath));
    }

    public void deleteFile(BaseFileDB identifier) {
        File f = new File("uploads" + File.separator + identifier.id + File.separator
                + identifier.filename + "." + identifier.fileExtension);
        f.delete();
        identifier.delete();
    }
}