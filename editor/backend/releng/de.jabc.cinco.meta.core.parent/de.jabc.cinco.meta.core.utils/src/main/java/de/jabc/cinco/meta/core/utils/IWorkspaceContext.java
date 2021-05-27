package de.jabc.cinco.meta.core.utils;

import java.io.File;
import org.eclipse.emf.common.util.URI;

public interface IWorkspaceContext {

	public URI getFileURI(String relativePath);
	public URI getFileURI(URI relativePath);
	public File getFile(String relativePath);
	public File getFile(URI relativePath);
	public boolean fileExists(String relativePath);
	public boolean fileExists(URI relativePath);
	public URI getRootURI();
	public File getRootFile();
	public <T> T getContent(URI uri, Class<T> clazz);
	public File getFolder(String absolutePath);
	public File getFolder(URI uri);
	public File getFolder(File file);
	public String getRootFolderName();
	public boolean isContainedInRoot(String absolutePath);
	public boolean isContainedInRoot(URI uri);
	public boolean isContainedInRoot(File file);
}
