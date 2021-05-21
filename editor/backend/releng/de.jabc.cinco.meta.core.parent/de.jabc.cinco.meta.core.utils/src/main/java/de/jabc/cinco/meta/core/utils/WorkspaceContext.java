package de.jabc.cinco.meta.core.utils;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.workspace.IProjectConfigProvider;

public class WorkspaceContext implements IWorkspaceContext {
	IProjectConfigProvider projectConfigProvider;
	ResourceSet resourceSet;

	public WorkspaceContext(IProjectConfigProvider projectConfigProvider, Resource resource) {
		this.resourceSet = resource.getResourceSet();
		if(projectConfigProvider != null) {
			this.projectConfigProvider = projectConfigProvider;
		} else {
			this.projectConfigProvider = null;
		}
	}

	@Override
	public URI getFileURI(String relativePath) {
        URI relativeURI = URI.createURI(relativePath);
		return getFileURI(relativeURI);
	}

	@Override
	public URI getFileURI(URI relativePath) {
		return relativePath.resolve(this.getRootURI());
	}

	@Override
	public File getFile(String relativePath) {
		URI absoluteURI = getFileURI(relativePath);
		String absolutePath = absoluteURI.toFileString();
		File file = new File(absolutePath).getAbsoluteFile();
		return file;
	}

	@Override
	public File getFile(URI relativePath) {
		URI absoluteURI = getFileURI(relativePath);
		String absolutePath = absoluteURI.toFileString();
		File file = new File(absolutePath).getAbsoluteFile();
		return file;
	}

	@Override
	public boolean fileExists(String relativePath) {
		File file = getFile(relativePath);
		return file.exists();
	}

	@Override
	public boolean fileExists(URI relativePath) {
		File file = getFile(relativePath);
		return file.exists();
	}

	@Override
	public URI getRootURI() {
		return this.projectConfigProvider.getProjectConfig(this.resourceSet).getPath();
	}

	@Override
	/**
	 * TODO: return the EObject referenced by the given file,
	 * that is Type(casted) to the given class.
	 */
	public <T> T getContent(File file, Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * TODO: return the File-Folder that is containing the given file(uri/string)
	 */
	public File getFolder(String absolutePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * TODO: return the File-Folder that is containing the given file(uri/string)
	 */
	public File getFolder(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * TODO: return the File-Folder that is containing the given file(uri/string)
	 */
	public File getFolder(File file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * TODO: check if the given absolutePath/uri/file is contained in the rootPath of
	 * the workspace.
	 */
	public boolean isContainedInRoot(String absolutePath) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	/**
	 * TODO: check if the given absolutePath/uri/file is contained in the rootPath of
	 * the workspace.
	 */
	public boolean isContainedInRoot(URI uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	/**
	 * TODO: check if the given absolutePath/uri/file is contained in the rootPath of
	 * the workspace.
	 */
	public boolean isContainedInRoot(File file) {
		// TODO Auto-generated method stub
		return false;
	}
}
