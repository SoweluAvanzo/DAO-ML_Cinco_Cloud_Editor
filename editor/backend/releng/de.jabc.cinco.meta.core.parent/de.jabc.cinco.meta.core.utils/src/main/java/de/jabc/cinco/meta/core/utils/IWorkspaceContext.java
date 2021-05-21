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
}
