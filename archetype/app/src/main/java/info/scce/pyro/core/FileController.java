package info.scce.pyro.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import de.jabc.cinco.meta.plugin.generator.runtime.IGenerator;
import info.scce.pyro.auth.SecurityOverrideFilter;

public class FileController {
	
	/*
	 *	INTERNAL DATABASE - API 
	 */
    protected final static String WORKSPACE_CONFIG = Paths.get(".cinco-cloud").toString();
    protected final static String UPLOAD_FOLDER = Paths.get(WORKSPACE_CONFIG, "uploads").toString();
	private static java.util.Set<Path> FILES_TO_CLEAN = new java.util.HashSet<>();
    
	public static InputStream loadFile(String baseFilePath) {
		entity.core.BaseFileDB baseFile = getBaseFile(baseFilePath);
		return loadFile(baseFile);
	}
	
	public static InputStream loadFile(final long id) {
		entity.core.BaseFileDB fileReference = getFileReference(id);
		return loadFile(fileReference);
	}
	
	public static entity.core.BaseFileDB getFileReference(final long id) {
		final entity.core.BaseFileDB file = entity.core.BaseFileDB.findById(id);
		return file;
	}
	
	public static entity.core.BaseFileDB getBaseFile(String relativeFilePath) {
		long id = getIdFromFilePath(relativeFilePath);
		return entity.core.BaseFileDB.findById(id);
	}
	
    public static long getIdFromFilePath(String relativeFilePath) {
    	try {
			java.net.URI uri = new java.net.URI(relativeFilePath);
			String[] segments = uri.getPath().split("/");
			String idStr = segments[segments.length-2];
			return Long.parseLong(idStr);
		} catch (java.net.URISyntaxException e) {
			e.printStackTrace();
			return -1;
		}
    }

	public static InputStream loadFile(final entity.core.BaseFileDB identifier) {
		try {
			String fileName = identifier.getFileName();
			String relativeUploadPath = Paths.get(UPLOAD_FOLDER, ""+identifier.id, fileName).toString();
			return loadStream(relativeUploadPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static entity.core.BaseFileDB storeFile(final String fileName, final InputStream data) throws IOException {
		// persist file-information
		final entity.core.BaseFileDB result = new entity.core.BaseFileDB();
		result.filename = org.apache.commons.io.FilenameUtils.removeExtension(fileName);
		if(org.apache.commons.io.FilenameUtils.indexOfExtension(fileName) > -1) {
			result.fileExtension = org.apache.commons.io.FilenameUtils.getExtension(fileName);
		} else {
			result.fileExtension = null;
		} 
		result.persist();
		
		// persist fileContent
	    Path relativeTargetFilePath = Paths.get(
	    		UPLOAD_FOLDER,
	    		""+result.id,
	    		fileName
	    	);
	    try {
	    	writeFile(relativeTargetFilePath.toString(), data);
	    	System.out.println("New file \"" + fileName + "\" created");
	    } catch (FileNotFoundException fne) {
			result.delete();
			System.out.println("You either did not specify a file to upload or are "
			        + "trying to upload a file to a protected or nonexistent "
			        + "location.");
			System.out.println("<br/> ERROR: " + fne.getMessage());
	    }
		return result;
	}
	
	public static void deleteBaseFile(String baseFilePath) {
		entity.core.BaseFileDB baseFile = getBaseFile(baseFilePath);
		deleteBaseFile(baseFile);
	}

	public static void deleteBaseFile(entity.core.BaseFileDB identifier) {
		if(identifier == null)
			return;
		String relativeFolderPath = Paths.get(UPLOAD_FOLDER, ""+identifier.id).toString();
		String relativePath = Paths.get(relativeFolderPath, identifier.getFileName()).toString();
		deleteFile(relativePath);
		deleteFile(relativeFolderPath);
		identifier.delete();
	}
	
	public static boolean deleteFile(String relativePath) {
		cleanUp();
		String workspaceRoot = getWorkspaceRoot();
		Path absoluteFilePath = Paths.get(workspaceRoot, relativePath);
		File f = absoluteFilePath.toFile();
		if(f.exists()) {
			try {
				java.nio.file.Files.delete(absoluteFilePath);
			} catch (IOException e) {
				e.printStackTrace();
				synchronized(FILES_TO_CLEAN) {
					FILES_TO_CLEAN.add(absoluteFilePath);
				}
				return false;
			}
		}
		return true;
	}
	
	// TODO: this is rudimentary and should be rather based on the BaseFileDB
	public static void cleanUp() {
		synchronized(FILES_TO_CLEAN) {
			java.util.Set<Path> removedFiles = new java.util.HashSet<>();
			for(Path p : FILES_TO_CLEAN) {
				try {
					java.nio.file.Files.delete(p);
					removedFiles.add(p);
				} catch (IOException e) {
					System.out.println("Still could not clean: "+p);
				}
			}
			FILES_TO_CLEAN.removeAll(removedFiles);
		}
	}
	
	/*
	 *	FILE - API 
	 */
    
    public static String getWorkspaceRoot() {
    	String workspaceStringPath = SecurityOverrideFilter.getWorkspacePath();				
		String workspaceAbsolutePath = Paths.get(workspaceStringPath).toString();
		String baseFolderPath = Paths.get(workspaceAbsolutePath).toString();
		File dir = new File(baseFolderPath);
		if (!dir.exists()) {
			try {
				Path workspacePath = Paths.get(baseFolderPath);
				java.nio.file.Files.createDirectories(workspacePath);
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
     * 									e.g. "gen-folder". If it is a file, the containing folder will be derived.
     * @return						- the concatenation of the workspace-path and the relativeFolderPath as a String:
     * 									e.g. "/editor/workspace/gen-folder"
     * 								If it is a file, the containing folder will be derived:
     * 									e.g. "/editor/workspace/gen-folder/test.txt"
     * 									will return:
     * 										 "/editor/workspace/gen-folder/"
     */
    public static String createFolder(String relativeFolderPath) {
    	String workspaceStringPath = SecurityOverrideFilter.getWorkspacePath();				
		String workspaceAbsolutePath = Paths.get(workspaceStringPath).toString();
		String targetFolderPath = Paths.get(workspaceAbsolutePath, relativeFolderPath).toString();
		File dir = new File(targetFolderPath);
		String sanitizedPath = sanitizePath(targetFolderPath);
		if (!dir.exists()) {			
			try {
				java.nio.file.Files.createDirectories(Paths.get(sanitizedPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sanitizedPath;
    }

    /**
     * @param relativeSourcePath		- The path to the sourceFile inside the workspace-path
     * @param relativeTargetFolderPath	- The relativePath to the folder inside the workspace-path, where the resource will be copied to
     * @throws IOException
     */
	public static void copyResource(String relativeSourcePath, String relativeTargetPath) throws IOException {	
		java.io.InputStream resourceStream = loadStream(relativeSourcePath);
		copyResource(resourceStream, relativeTargetPath);
	}
    
	/**
	 * @param resourceStream			- the resource to copy as a stream.
 	 * @param relativeTargetPath 		- the relative-path inside the folder defined by "@generatable"-annotation (inside the workspace-folder).
 	 * 									Inside that folder, the "resourceFilePath" will be placed.
	 * @throws IOException
	 */
	public static void copyResource(java.io.InputStream resourceStream, String relativeTargetPath) throws IOException {
		String targetResourceName = getFileName(relativeTargetPath);
		int index = relativeTargetPath.lastIndexOf(targetResourceName);
		String folderPath = relativeTargetPath.substring(0, index <= 0 ? relativeTargetPath.length() : index);
		String absoluteTargetFolderPath = createFolder(folderPath);
		
		String absoluteTargetResourcePath = Paths.get(absoluteTargetFolderPath, targetResourceName).toString();
		Path absoluteResourcePath = Paths.get(absoluteTargetResourcePath).normalize();
		java.nio.file.Files.copy(resourceStream, absoluteResourcePath, StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * @param staticResourceBase		- the folder predefined by pyro. usualy "asset/[graphmodel.name]"
	 * @param staticResourceFolder 		- defined by the "@pyroGeneratorResource"-annotation (That annotation can contain an array of possible folders).
	 * @param relativeResourcePath		- the path of the resource, that will be preserved for the target path
	 * @param relativeTargetFolderPath 	- the relative-path inside the folder defined by "@generatable"-annotation (inside the workspace-folder).
	 * 									Inside that folder, the "resourceFilePath" will be placed.
	 * @throws IOException
	 */
	public static void copyInternalResource(String staticResourceBase, String staticResourceFolder, String relativeResourcePath, String relativeTargetFolderPath) throws IOException {	
		String staticResourceFilePath = Paths.get(staticResourceFolder, relativeResourcePath).toString();
		java.io.InputStream resourceStream = loadInternalResource(staticResourceBase, staticResourceFilePath);
		String relativeTargetPath = Paths.get(relativeTargetFolderPath, relativeResourcePath).toString();
		copyResource(resourceStream, relativeTargetPath);
	}
	
	/**
	 * Provides an InputStream of the resource located inside the jar at: /META-INF/[staticResourceBase, e.g. GraphModelName]/" + staticResourceFilePath,
	 * where staticResourceFilePath, could be a folder, where all the GeneratorResources are located.
	 * @param staticResourceBase		- the folder predefined by pyro. usualy "asset/[graphmodel.name]"
	 * @param staticResourceFilePath	- the relative path inside the resource e.g. "[referencedResourceFolder]/[contained file]",
	 * 										where referencedResourceFolder is a parameter of an annotation 
	 * @return
	 */
	public static java.io.InputStream loadInternalResource(String staticResourceBase, String staticResourceFilePath) {
		String resource = Paths.get("/META-INF", staticResourceBase, staticResourceFilePath).toString();
		resource = sanitizePath(resource);
		return IGenerator.class.getResourceAsStream(resource);
	}
	
	public static java.io.InputStream loadStream(String relativeWorkspaceFilePath) throws IOException {
    	String workspaceRoot = getWorkspaceRoot();
    	String absoluteWorkspaceFilePath = Paths.get(workspaceRoot, relativeWorkspaceFilePath).toString();
    	File workspaceFile = new File(absoluteWorkspaceFilePath);
    	return new FileInputStream(workspaceFile);
	}
	
	public static String writeFile(String relativeTargetFilePath, String content) throws IOException {
		return writeFile(relativeTargetFilePath, content, true);
	}
	
	public static String writeFile(String relativeTargetFilePath, InputStream content) throws IOException {
		return writeFile(relativeTargetFilePath, content, true);
	}
	
	public static String writeFile(String relativeTargetFilePath, InputStream content, boolean overwrite) throws IOException {
		File file = createFile(relativeTargetFilePath, overwrite);
		if(file == null) {
			return null;
		}
    	writeByteContent(file, content);
    	return file.getAbsolutePath();
	}
	
	public static String writeFile(String relativeTargetFilePath, String content, boolean overwrite) throws IOException {
		File file = createFile(relativeTargetFilePath, overwrite);
		if(file == null) {
			return null;
		}
		Path path = Paths.get(file.getAbsolutePath());
		return java.nio.file.Files.writeString(path, content).toString();
	}
	
	private static void writeByteContent(File file, InputStream content) throws IOException {
		System.out.println("writeByteContent : "+file.getAbsolutePath());
		file.createNewFile();
		FileOutputStream out = new FileOutputStream(file);
		int read = 0;
        final byte[] bytes = new byte[1024];
        while ((read = content.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
		out.close();
	}
	
	public static File createFile(String relativeTargetFilePath, boolean overwrite) {
		String fileName = getFileName(relativeTargetFilePath);
		int index = relativeTargetFilePath.lastIndexOf(fileName);
		String relativeFolderPath = relativeTargetFilePath.substring(0, index <= 0? relativeTargetFilePath.length() : index);
		String absolutefolderPath = createFolder(relativeFolderPath);
		Path path = Paths.get(absolutefolderPath, fileName);
		File file = new File(path.toString());
		if(file.exists()) {
			if(file.isDirectory()) {
				throw new RuntimeException("File could not be written. A directory with the same name exists.");
			} else {
				if(overwrite) {
					file.delete();
				} else {
					return null;
				}
			}
		}
		return file;
	}
    
    public static String sanitizePath(String path) {
    	return path.replace(File.separator, "/");
    }
    
    public static String getFileName(String path) {
    	String sanitizedPath = sanitizePath(path);
    	int index = sanitizedPath.lastIndexOf("/") + 1;
    	return sanitizedPath.substring(index);
    }
    
    public static String normalize(String relativePath) {
    	String workspaceRoot = getWorkspaceRoot();
    	return Paths.get(workspaceRoot, relativePath).toString();
    }
}