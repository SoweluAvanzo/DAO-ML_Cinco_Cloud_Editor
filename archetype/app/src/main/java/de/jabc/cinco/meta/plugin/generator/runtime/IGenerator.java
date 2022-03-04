package de.jabc.cinco.meta.plugin.generator.runtime;

import graphmodel.*;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import info.scce.pyro.core.FileController;

/**
 * Author zweihoff
 */
public abstract class IGenerator<T extends GraphModel> {

	private List<GeneratedFile> files;
	String workspaceGenerationFolder;
	String staticInternalResourceBase;
	java.util.Map<String,String[]> staticResources;	
	static FileSystem fileSystem = null;
	
	public IGenerator() {
		files = new LinkedList<>();
	}
	
	public final void generateFiles(T graphModel, String workspaceGenerationFolder, String staticResourceBase,java.util.Map<String,String[]> staticResources) throws IOException {
		this.workspaceGenerationFolder = workspaceGenerationFolder;
		this.staticInternalResourceBase = staticResourceBase;
		this.staticResources = staticResources;
		
		/**
		 * The generate() collects files to write, with the use of
		 * the createFile() method.
		 * The generateFiles() writes them afterwards in a collective manner.
		 */
		generate(graphModel);		
		generateFiles();
	}

    protected abstract void generate(T graphModel);
        
    protected String suffix(String absolutPath, String resource) {
    		return absolutPath.substring(absolutPath.lastIndexOf(resource) + resource.length() + 1);
    }

    public final void createFile(String filePath, CharSequence content) {
        createFile(filePath, content.toString());
    }

    public final void createFile(String filename, String path, CharSequence content) {
        createFile(filename, path, content.toString());
    }

    public final void createFile(String filePath, String content) {
        String[] components = filePath.split("/");
        String filename = components[components.length - 1];
        int lastSlash = filePath.lastIndexOf("/");
        String path = filePath.substring(0, lastSlash);
        createFile(filename, path, content);
    }
    
    public final void createFile(String filename, String path, String content) {
        if(filename==null||path==null||content==null) {
            throw new IllegalStateException("All parameters has to be not null");
        }
        if(filename.isEmpty()) {
            throw new IllegalStateException("Filename has to be given");
        }
        if(content.isEmpty()) {
            content = " ";
        }
        files.add(new GeneratedFile(filename,path,content));
    }

    public final void createFile(String filename, String path, File file) {
        if(filename==null||path==null||file==null) {
            throw new IllegalStateException("All parameters has to be not null");
        }
        if(filename.isEmpty()) {
            throw new IllegalStateException("Filename has to be given");
        }
        files.add(new GeneratedFile(filename,path,file));
    }
    
    public final void generateFiles()  throws IOException {
		String generationBaseFolderPath = getWorkspaceBasePath();    	
    	for (GeneratedFile f : files) {
    		Path targetPath = Paths.get(generationBaseFolderPath, f.getPath(), f.getFilename());
    		FileController.writeFile(targetPath.toString(), f.getContent());
		}
    }

    public final void copyStaticResources() {
    	copyStaticResources("");
    }
    
    public final void copyStaticResources(String relativeTargetPath) {
    	try {
    		for (java.util.Map.Entry<String, String[]> staticResource : staticResources.entrySet()) {
    			String[] fileEntries = staticResource.getValue();
    			for (String fileEntry : fileEntries) {
    				Path p = Paths.get(staticResource.getKey() + "/" + fileEntry).normalize();
    				String staticResourceFolder = staticResource.getKey();
    				String resourceFilePath = suffix(p.toString(), staticResourceFolder);
    				copyInternalResource(staticResourceFolder, resourceFilePath, relativeTargetPath);
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public String getWorkspaceBasePath() {
		return FileController.createFolder(workspaceGenerationFolder);
    }
    
    /**
     * see info.scce.pyro.core.FileController.createFolder
     * @param relativeFolderPath
     * @return
     */
    public String createFolder(String relativeFolderPath) {
    	return FileController.createFolder(relativeFolderPath);
    }
    
    /**
     * see info.scce.pyro.core.FileController.copyResource
     * @param relativeFolderPath
     * @return
     */
	public void copyResource(String relativeSourcePath, String relativeTargetPath) throws IOException {
		FileController.copyResource(relativeSourcePath, relativeTargetPath);
	}
	
	/**
     * see info.scce.pyro.core.FileController.copyResource
     * @param relativeFolderPath
     * @return
     */
	public void copyResource(java.io.InputStream resourceStream, String relativeTargetPath) throws IOException {
		FileController.copyResource(resourceStream, relativeTargetPath);
	}
	
	/**
     * see info.scce.pyro.core.FileController.copyInternalResource
     * @param relativeFolderPath
     * @return
     */
	public void copyInternalResource(String staticResourceFolder, String relativeResourcePath, String relativeTargetFolderPath) throws IOException {	
		FileController.copyInternalResource(staticInternalResourceBase, staticResourceFolder, relativeResourcePath, relativeTargetFolderPath);
	}
	
	/**
	 * Provides an InputStream of the resource located inside the jar at: /META-INF/[staticResourceBase, e.g. GraphModelName]/" + staticResourceFilePath,
	 * where staticResourceFilePath, could be a folder, where all the GeneratorResources are located.
     * (see info.scce.pyro.core.FileController.loadInternalResource)
     * 
	 * @param staticResourceFilePath
	 * @return
	 */
	public java.io.InputStream loadInternalResource(String staticResourceFilePath) {
		return FileController.loadInternalResource(staticInternalResourceBase, staticResourceFilePath);
	}
	
	/**
     * see info.scce.pyro.core.FileController.loadStream
     * @param relativeWorkspaceFilePath
     * @return
     */
	public java.io.InputStream loadStream(String relativeWorkspaceFilePath) throws IOException {
    	return FileController.loadStream(relativeWorkspaceFilePath);
	}
}
