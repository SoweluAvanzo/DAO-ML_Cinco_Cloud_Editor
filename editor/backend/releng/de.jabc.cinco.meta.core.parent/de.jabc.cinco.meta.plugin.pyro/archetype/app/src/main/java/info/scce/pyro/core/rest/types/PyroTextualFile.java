package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class PyroTextualFile extends PyroFile {

    private String content;

    @com.fasterxml.jackson.annotation.JsonProperty("content")
    public String getcontent() {
        return this.content;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("content")
    public void setcontent(final String content) {
        this.content = content;
    }

    public static PyroTextualFile fromEntity(final entity.core.PyroTextualFileDB entity, info.scce.pyro.rest.ObjectCache objectCache) {

        if(objectCache.containsRestTo(entity)){
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
}