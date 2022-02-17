package de.jabc.cinco.meta.plugin.generator.runtime;

import graphmodel.*;

import java.io.*;
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
	
	info.scce.pyro.core.FileController fileController;
	
	public IGenerator() {
		files = new LinkedList<>();
	}
	
	public final void generateFiles(T graphModel, String basePath,String staticResourceBase,java.util.Map<String,String[]> staticResources,info.scce.pyro.core.FileController fileController) throws IOException {
		this.basePath = basePath;
		this.fileController = fileController;
		
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
		
		// TODO: Joel ab hier
		
		String staticResourceJarPath = "javaPath/"; // da was anderes rein
		Path staticResourcePath = Paths.get(staticResourceJarPath, staticResourceBase).normalize();
		File staticResourcesDest = new File(staticResourcePath.toString());
		if (!staticResourcesDest.exists() || !staticResourcesDest.isDirectory()) {
			staticResourcesDest.mkdirs();
		}
		
		//copy and overwrite with static resources
		for (java.util.Map.Entry<String, String[]> staticResource : staticResources.entrySet()) {
			String[] fileEntries = staticResource.getValue();
			for (String fileEntry : fileEntries) {
				Path p = Paths.get(staticResource.getKey() + "/" + fileEntry).normalize();
				File f = new File(p.toString());
				// if (f.exists() && !f.isDirectory()) {
				// f.delete();
				// }
				try {
					Path copyDest = Paths.get(staticResourcesDest.toString(), suffix(f.toString(), staticResource.getKey())).normalize();
					File fileTocopy = new File(copyDest.toString());
					if (!fileTocopy.getParentFile().exists()) {
								fileTocopy.getParentFile().mkdirs();
					}
					java.nio.file.Files.copy(f.toPath(), fileTocopy.toPath().normalize(),
									StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
}
