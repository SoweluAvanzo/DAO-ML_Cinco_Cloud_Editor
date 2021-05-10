package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class PyroBinaryFile extends PyroFile
{
    
    private FileReference file;

    @com.fasterxml.jackson.annotation.JsonProperty("file")
    public FileReference getfile() {
        return this.file;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("file")
    public void setfile(final FileReference file) {
        this.file = file;
    }

    public static PyroBinaryFile fromEntity(final entity.core.PyroBinaryFileDB entity, info.scce.pyro.rest.ObjectCache objectCache) {

        if(objectCache.containsRestTo(entity)){
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
}