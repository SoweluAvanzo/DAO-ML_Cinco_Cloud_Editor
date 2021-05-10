package info.scce.cincocloud.core.rest.types;

import info.scce.cincocloud.db.PyroURLFileDB;

public class PyroURLFile extends PyroFile {

    private String url;

    public static PyroURLFile fromEntity(final PyroURLFileDB entity, info.scce.cincocloud.rest.ObjectCache objectCache) {

        if (objectCache.containsRestTo(entity)) {
            return objectCache.getRestTo(entity);
        }
        final PyroURLFile result;
        result = new PyroURLFile();
        result.setId(entity.id);
        result.set__type(entity.getClass().getSimpleName());

        result.setfilename(entity.filename);
        result.setextension(entity.extension);
        result.seturl(entity.url);

        objectCache.putRestTo(entity, result);

        return result;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("url")
    public String geturl() {
        return this.url;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("url")
    public void seturl(final String url) {
        this.url = url;
    }
}