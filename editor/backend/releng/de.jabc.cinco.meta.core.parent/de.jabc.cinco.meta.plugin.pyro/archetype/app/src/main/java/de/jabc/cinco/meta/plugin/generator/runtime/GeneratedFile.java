package de.jabc.cinco.meta.plugin.generator.runtime;

import java.io.File;

final class GeneratedFile {
    private String filename;
    private String path;
    private String content;
    private File file;

    GeneratedFile(String filename,String path,String content) {
        this.filename = filename;
        this.path = path;
        this.content = content;
    }

    GeneratedFile(String filename,String path,File file) {
        this.filename = filename;
        this.path = path;
        this.file = file;
    }

    public final String getFilename() {
        return filename;
    }

    public final String getPath() {
        return path;
    }

    public final String getContent() {
        return content;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
