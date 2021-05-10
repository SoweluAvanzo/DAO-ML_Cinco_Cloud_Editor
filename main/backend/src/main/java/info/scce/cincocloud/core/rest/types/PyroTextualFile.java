package info.scce.cincocloud.core.rest.types;

import info.scce.cincocloud.db.PyroTextualFileDB;

/**
 * Author zweihoff
 */

public class PyroTextualFile extends PyroFile {

    private String content;

    public static PyroTextualFile fromEntity(final PyroTextualFileDB entity, info.scce.cincocloud.rest.ObjectCache objectCache) {

        if (objectCache.containsRestTo(entity)) {
            return objectCache.getRestTo(entity);
        }
        final PyroTextualFile result;
        result = new PyroTextualFile();
        result.setId(entity.id);
        result.set__type(entity.getClass().getSimpleName());

        result.setfilename(entity.filename);
        result.setextension(entity.extension);
        result.setcontent(entity.content);

        objectCache.putRestTo(entity, result);

        return result;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("content")
    public String getcontent() {
        return this.content;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("content")
    public void setcontent(final String content) {
        this.content = content;
    }
}