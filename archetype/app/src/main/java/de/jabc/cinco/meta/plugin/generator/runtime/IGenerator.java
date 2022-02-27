package de.jabc.cinco.meta.plugin.generator.runtime;

import graphmodel.*;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import info.scce.pyro.auth.SecurityOverrideFilter;

/**
 * Author zweihoff
 */
public abstract class IGenerator<T extends GraphModel> {

	private List<GeneratedFile> files;
	String basePath;
	String staticResourceBase;
	java.util.Map<String,String[]> staticResources;	
	static FileSystem fileSystem = null; 	
	info.scce.pyro.core.FileController fileController;
	
	public IGenerator() {
		files = new LinkedList<>();
	}
	
	public final void generateFiles(T graphModel, String basePath,String staticResourceBase,java.util.Map<String,String[]> staticResources,info.scce.pyro.core.FileController fileController) throws IOException {
		this.basePath = basePath;
		this.fileController = fileController;
		this.staticResourceBase = staticResourceBase;
		this.staticResources = staticResources;
		
		generate(graphModel);
		
		String workspaceStringPath = SecurityOverrideFilter.getWorkspacePath();				
		Path workspaceAbsolutePath = Paths.get(workspaceStringPath);
		Path generationBaseFolderPath = Paths.get(workspaceAbsolutePath.toString(), basePath);
		File dir = new File(generationBaseFolderPath.toString());
		
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		for (GeneratedFile f : files) {
			Path genDirPath = Paths.get(generationBaseFolderPath.toString() , f.getPath());
			File genDir = new File(genDirPath.toString());
			if (!genDir.exists()) {
				genDir.mkdirs();
			}
			Path path = Paths.get(genDirPath.toString(), f.getFilename());
			File file = new File(path.toString());
			if (file.exists() && !file.isDirectory()) {
				file.delete();
			}
			java.nio.file.Files.writeString(path, f.getContent());
		}

	}

    protected abstract void generate(T graphModel);
        
    protected String suffix(String absolutPath, String resource) {
    		return absolutPath.substring(absolutPath.lastIndexOf(resource) + resource.length() + 1);
    }

    protected final void createFile(String filePath, CharSequence content) {
        createFile(filePath, content.toString());
    }

    protected final void createFile(String filename, String path, CharSequence content) {
        createFile(filename, path, content.toString());
    }

    protected final void createFile(String filePath, String content) {
        if(filePath==null) {
            throw new IllegalStateException("All parameters has to be not null");
        }
        if(filePath.isEmpty()) {
            throw new IllegalStateException("Filename has to be given");
        }
        String[] components = filePath.split("/");
        String filename = components[components.length - 1];
        int lastSlash = filePath.lastIndexOf("/");
        String path = filePath.substring(0, lastSlash);
        createFile(filename, path, content);
    }
    
    protected final void createFile(String filename, String path, String content) {
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

    protected final void createFile(String filename, String path, File file) {
        if(filename==null||path==null||file==null) {
            throw new IllegalStateException("All parameters has to be not null");
        }
        if(filename.isEmpty()) {
            throw new IllegalStateException("Filename has to be given");
        }
        files.add(new GeneratedFile(filename,path,file));
    }
    
    protected final void copyStaticResources(String relativeTargetPath) {
    	try {
    		for (java.util.Map.Entry<String, String[]> staticResource : staticResources.entrySet()) {
    			String[] fileEntries = staticResource.getValue();
    			for (String fileEntry : fileEntries) {
    				Path p = Paths.get(staticResource.getKey() + "/" + fileEntry).normalize();
    				String staticResourceFolder = staticResource.getKey();
    				String resourceFilePath = suffix(p.toString(), staticResourceFolder);
    				copyInternalResource(resourceFilePath, staticResourceFolder, relativeTargetPath);
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void copyFileReference(String relativeFilePath, String relativTargetPath) {
    	String workspaceStringPath = SecurityOverrideFilter.getWorkspacePath();				
		Path workspaceAbsolutePath = Paths.get(workspaceStringPath);
		Path generationBaseFolderPath = Paths.get(workspaceAbsolutePath.toString(), basePath);
		File dir = new File(generationBaseFolderPath.toString());
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		Path relativeStaticTargetPath = Paths.get(relativTargetPath).normalize();
		Path staticResourcePath = Paths.get(generationBaseFolderPath.toString(),relativeStaticTargetPath.toString());
		File staticResourcesDest = new File(staticResourcePath.toString());
		if (!staticResourcesDest.exists() || !staticResourcesDest.isDirectory()) {
			staticResourcesDest.mkdirs();
		}
    }
    
    protected String getWorkspaceBasePath() {
    	String workspaceStringPath = SecurityOverrideFilter.getWorkspacePath();				
		String workspaceAbsolutePath = Paths.get(workspaceStringPath).toString();
		String baseFolderPath = Paths.get(workspaceAbsolutePath, basePath).toString();
		File dir = new File(baseFolderPath);
		if (!dir.exists()) {
			try {
				java.nio.file.Files.createDirectories(Paths.get(baseFolderPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return baseFolderPath;
    }
    
    /**
     * if the workspace-path is "/editor/workspace" on the filesystem, and the
     * relativeFolderPath is "gen-folder", the resulting created folder will have the path:
     * 	"/editor/workspace/gen-folder"
     * 
     * @param relativeFolderPath	- this is the relative path, that will be created inside the workspace.
     * 									e.g. "gen-folder"
     * @return						- the concatenation of the workspace-path and the relativeFolderPath as a String:
     * 									e.g. "/editor/workspace/gen-folder" 
     */
    public String createFolder(String relativeFolderPath) {
    	// create workspaceBasePath (if not existing)
    	String absoluteBaseFolderPath = getWorkspaceBasePath();
    	// create folderPath
    	String relativeStaticResourcePath = Paths.get(relativeFolderPath).normalize().toString();
		String absoluteStaticResourcePath = Paths.get(absoluteBaseFolderPath, relativeStaticResourcePath).toString();
		File staticResourcesDest = new File(absoluteStaticResourcePath);
		if (!staticResourcesDest.exists() || !staticResourcesDest.isDirectory()) {
			try {
				if(!staticResourcesDest.isDirectory()) { // staticResourceDest is a File not a Folder
					String sanitizedPath = absoluteStaticResourcePath.replace(File.separator, "/");
					sanitizedPath = sanitizedPath.substring(0, sanitizedPath.lastIndexOf('/'));
					java.nio.file.Files.createDirectories(Paths.get(sanitizedPath));
				} else {
					java.nio.file.Files.createDirectories(Paths.get(absoluteStaticResourcePath));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return absoluteStaticResourcePath;
    }
    
    /**
     * 
     * @param relativeSourcePath		- The path to the sourceFile inside the workspace-path
     * @param relativeTargetFolderPath	- The relativePath to the folder inside the workspace-path, where the resource will be copied to
     * @throws IOException
     */
	protected void copyResource(String relativeSourcePath, String relativeTargetFolderPath) throws IOException {
		String targetResourceName = relativeSourcePath.substring(relativeSourcePath.lastIndexOf('/') + 1);
		copyResource(relativeSourcePath, relativeTargetFolderPath, targetResourceName);
	}
	
    /**
     * 
     * @param relativeSourcePath		- The path to the sourceFile inside the workspace-path
     * @param relativeTargetFolderPath	- The relativePath to the folder inside the workspace-path, where the resource will be copied to
     * @param targetResourceName		- The final name of the resource
     * @throws IOException
     */
	protected void copyResource(String relativeSourcePath, String relativeTargetFolderPath, String targetResourceName) throws IOException {	
		java.io.InputStream resourceStream = loadStream(relativeSourcePath);
		String absoluteTargetFolderPath = createFolder(relativeTargetFolderPath);
		String absoluteTargetResourcePath = absoluteTargetFolderPath + "/" + targetResourceName;
		Path absoluteResourcePath = Paths.get(absoluteTargetResourcePath).normalize();
		java.nio.file.Files.copy(resourceStream, absoluteResourcePath, StandardCopyOption.REPLACE_EXISTING);
	}
    
	/**
	 * 
	 * @param relativeResourcePath			- the relative of the resource-path inside the relativeTargetFolderPath, as well as in the destination-path
	 * 										after generation.
	 * @param relativeTargetFolderPath 		- the relative-path inside the folder defined by "@generatable"-annotation (inside the workspace-folder).
	 * 										Inside that folder, the "resourceFilePath" will be placed.
	 * @param resourceStream				- the resource to copy as a stream.
	 * @throws IOException
	 */
	protected void copyResource(java.io.InputStream resourceStream, String relativeTargetFolderPath) throws IOException {	
		String absoluteTargetFolderPath = createFolder(relativeTargetFolderPath);
		Path absoluteTargetResourceFilePath = Paths.get(absoluteTargetFolderPath + "/").normalize();
		java.nio.file.Files.copy(resourceStream, absoluteTargetResourceFilePath, StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * @param relativeResourcePath		- the relative of the resource-path inside the staticResourceFolder, as well as in the destination-path
	 * 									after generation.
	 * @param staticResourceFolder 		- defined by the "@pyroGeneratorResource"-annotation (That annotation can contain an array of possible folders).
	 * @param relativeTargetFolderPath 	- the relative-path inside the folder defined by "@generatable"-annotation (inside the workspace-folder).
	 * 									Inside that folder, the "resourceFilePath" will be placed.
	 * @throws IOException
	 */
	protected void copyInternalResource(String relativeResourcePath, String staticResourceFolder, String relativeTargetFolderPath) throws IOException {	
		java.io.InputStream resourceStream = loadResourceFromJar(staticResourceFolder + "/" + relativeResourcePath);
		String relativeTargetPath = relativeTargetFolderPath + "/" + relativeResourcePath;
		copyResource(resourceStream, relativeTargetPath);
	}
	
	/**
	 * Provides an InputStream of the resource located inside the jar at: /META-INF/[staticResourceBase, e.g. GraphModelName]/" + staticResourceFilePath,
	 * where staticResourceFilePath, could be a folder, where all the GeneratorResources are located.
	 * @param staticResourceFilePath
	 * @return
	 */
	protected java.io.InputStream loadResourceFromJar(String staticResourceFilePath) {
		String resource = "/META-INF/" + staticResourceBase + "/" + staticResourceFilePath;
		resource = resource.replace(File.separator, "/");
		return IGenerator.class.getResourceAsStream(resource);
	}
	
	protected java.io.InputStream loadStream(String relativeWorkspaceFilePath) {
    	String absoluteBaseFolderPath = getWorkspaceBasePath();
    	String absoluteWorkspaceFilePath = absoluteBaseFolderPath + "/" + relativeWorkspaceFilePath;
    	File workspaceFile = new File(absoluteWorkspaceFilePath);
    	try {
			return new FileInputStream(workspaceFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
