package de.jabc.cinco.meta.plugin.generator.runtime;

import graphmodel.*;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import info.scce.pyro.auth.SecurityOverrideFilter;
import jdk.internal.reflect.ReflectionFactory.GetReflectionFactoryAction;

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
        
    protected String suffix(String absolutPath, String resourceFolder) {
    		return absolutPath.substring(absolutPath.lastIndexOf(resourceFolder) + resourceFolder.length() + 1);
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
    
    protected final void copyStaticResources( String target) {
    	try {
    		for (java.util.Map.Entry<String, String[]> staticResource : staticResources.entrySet()) {
    			String[] fileEntries = staticResource.getValue();
    			for (String fileEntry : fileEntries) {
    				Path p = Paths.get(staticResource.getKey() + "/" + fileEntry).normalize();
    				copyResource(p.toString(), target, IGenerator.class, staticResource.getKey() );
    			}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}    
	
	protected void copyResource(String res, String dest, Class<?> mainClass, String folder) throws IOException {
		
		String workspaceStringPath = SecurityOverrideFilter.getWorkspacePath();				
		Path workspaceAbsolutePath = Paths.get(workspaceStringPath);
		Path generationBaseFolderPath = Paths.get(workspaceAbsolutePath.toString(), basePath);
		File dir = new File(generationBaseFolderPath.toString());
		
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		Path relativeStaticResourcePath = Paths.get(dest).normalize();
		Path staticResourcePath = Paths.get(generationBaseFolderPath.toString(),relativeStaticResourcePath.toString());
		File staticResourcesDest = new File(staticResourcePath.toString());
		if (!staticResourcesDest.exists() || !staticResourcesDest.isDirectory()) {
			staticResourcesDest.mkdirs();
		}
		
		ClassLoader clsLoader = mainClass.getClassLoader();
		String resource = "/META-INF/"+staticResourceBase +"/"+res;
		
		// windows-path workaround
		resource = resource.replace(File.separator, "/");
		
		java.io.InputStream src = IGenerator.class.getResourceAsStream(resource);
		
		// windows-path workaround
		String finalPath = staticResourcesDest.toString() +"/"+ res;
		String sanitizedPath = finalPath.replace(File.separator, "/");
		Path destPath = Paths.get(staticResourcesDest.toString(), suffix(res, folder)).normalize();
		//Path destPath = Paths.get(sanitizedPath);
		
		// create Folder holding the final file
		int lastIndex = destPath.getNameCount() - 1;
		// lastIndex = lastIndex > 0 ? lastIndex : 0
		if (lastIndex > 0) {
			lastIndex = 0;
		}
		// val folderPath = Paths.get(File.separator + destPath.subpath(0, lastIndex))
		createFolder(destPath.toString());
		// create/copy file not directory
		
		java.nio.file.Files.copy(src, destPath, StandardCopyOption.REPLACE_EXISTING);
		
	}
	

	protected static boolean createFolder(String folderPath) throws IOException {
		File file = new File(folderPath + File.separator);
		if (file.exists()) {
			return false;
		}
		java.nio.file.Files.createDirectories(Paths.get(folderPath));
		return true;
	}
}
