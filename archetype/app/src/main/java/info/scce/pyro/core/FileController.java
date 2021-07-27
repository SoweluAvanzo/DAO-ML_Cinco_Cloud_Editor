package info.scce.pyro.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileController {

	public entity.core.BaseFileDB getFileReference(final long id) {
		final entity.core.BaseFileDB file = entity.core.BaseFileDB.findById(id);

		if (file == null) {
			return null;
		}

		return file;
	}
	
	public entity.core.BaseFileDB getBaseFile(String baseFilePath) {
		try {
			java.net.URI uri = new java.net.URI(baseFilePath);
			String[] segments = uri.getPath().split("/");
			String idStr = segments[segments.length-2];
			long id = Long.parseLong(idStr);
			return entity.core.BaseFileDB.findById(id);
		} catch (java.net.URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public InputStream loadFile(final entity.core.BaseFileDB identifier) {
		if (identifier == null) {
			return null;
		}
		try {
			final String path = "uploads" + File.separator + identifier.id + File.separator;
			final String fileName = identifier.fileExtension == null ? 
					identifier.filename
					: identifier.filename + "." + identifier.fileExtension;
			return new FileInputStream(new File(path + fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public entity.core.BaseFileDB storeFile(final String fileName, final InputStream data) throws IOException {
		final entity.core.BaseFileDB result = new entity.core.BaseFileDB();
		result.filename = org.apache.commons.io.FilenameUtils.removeExtension(fileName);
		
		if(org.apache.commons.io.FilenameUtils.indexOfExtension(fileName) > -1)
			result.fileExtension = org.apache.commons.io.FilenameUtils.getExtension(fileName);
		else 
			result.fileExtension = null;
		result.persist();
		
		OutputStream out = null;
	    InputStream filecontent = data;
		
		try {
			File newFile = new File("uploads" + File.separator + result.id + File.separator + fileName);
			System.out.println(newFile.getAbsolutePath());
			if (!newFile.getParentFile().exists())
    			newFile.getParentFile().mkdirs();
			newFile.createNewFile();
	        out = new FileOutputStream(newFile);

	        int read = 0;
	        final byte[] bytes = new byte[1024];

	        while ((read = filecontent.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	        System.out.println("New file \"" + fileName + "\" created");
	    } catch (FileNotFoundException fne) { 
	    	
	    	result.delete();
	        System.out.println("You either did not specify a file to upload or are "
	                + "trying to upload a file to a protected or nonexistent "
	                + "location.");
			System.out.println("<br/> ERROR: " + fne.getMessage());

	    } finally {
	        if (out != null) {
	            out.close();
	        }
	        if (filecontent != null) {
		            filecontent.close();
	        }
	    }
		
		return result;
	}
	
	public void deleteFile(String baseFilePath) {
		deleteFile(getBaseFile(baseFilePath));
	}

	public void deleteFile(entity.core.BaseFileDB identifier) {
		File f = new File("uploads" + File.separator + identifier.id + File.separator
		+ identifier.filename+"."+identifier.fileExtension);
		f.delete();
		identifier.delete();
	}
}