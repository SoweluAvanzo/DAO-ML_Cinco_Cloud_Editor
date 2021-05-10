package info.scce.cincocloud.core.rest.types;

import info.scce.cincocloud.db.PyroBinaryFileDB;

/**
 * Author zweihoff
 */

public class PyroBinaryFile extends PyroFile {

    private FileReference file;

    public static PyroBinaryFile fromEntity(final PyroBinaryFileDB entity, info.scce.cincocloud.rest.ObjectCache objectCache) {

        if (objectCache.containsRestTo(entity)) {
            return objectCache.getRestTo(entity);
        }
        final PyroBinaryFile result;
        result = new PyroBinaryFile();
        result.setId(entity.id);
        result.set__type(entity.getClass().getSimpleName());

        result.setfilename(entity.filename);
        result.setextension(entity.extension);
        result.setfile(new FileReference(entity.file));

        objectCache.putRestTo(entity, result);

        return result;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("file")
    public FileReference getfile() {
        return this.file;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("file")
    public void setfile(final FileReference file) {
        this.file = file;
    }
}