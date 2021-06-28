package de.jabc.cinco.meta.core.utils;

import java.io.File;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.xtext.workspace.IProjectConfig;
import org.eclipse.xtext.workspace.IProjectConfigProvider;

public class WorkspaceContext implements IWorkspaceContext {
    private ResourceSet resourceSet;
	final URI rootURI;
	
	public WorkspaceContext(URI rootURI, ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
		if(rootURI != null) {
			this.rootURI = rootURI;
		} else {
			// fallback to the folder where the resource is located
			URIConverter conv = resourceSet.getURIConverter();
			this.rootURI = conv.normalize(URI.createURI(""));
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
		return rootURI;
	}

	@Override
	public File getRootFile() {
		URI rootUri = this.getRootURI();
		return this.getFile(rootUri);
	}

	@Override
	/**
	 * return the EObject referenced by the given file,
	 * that is Type(casted) to the given class.
	 */
	public <T> T getContent(URI uri, Class<T> clazz) {
		Resource resource = this.resourceSet.getResource(uri, true);
		if(resource == null)
			return null;
		EList<EObject> list = resource.getContents();
		if(list.isEmpty())
			return null;
		EObject eObject = list.get(0);
		return clazz.cast(eObject);
	}

	@Override
	/**
	 * return the File-Folder that is containing the given file(uri/string)
	 */
	public File getFolder(String absolutePath) {
		File file = this.getFile(absolutePath);
		return getFolder(file);
	}

	@Override
	/**
	 * return the File-Folder that is containing the given file(uri/string)
	 */
	public File getFolder(URI uri) {
		File file = this.getFile(uri);
		return getFolder(file);
	}

	@Override
	/**
	 * return the File-Folder that is containing the given file(uri/string)
	 */
	public File getFolder(File file) {
		return file.getParentFile();
	}

	@Override
	/**
	 * check if the given absolutePath/uri/file is contained in the rootPath of
	 * the workspace, or is the same as the root-path.
	 */
	public boolean isContainedInRoot(String absolutePath) {
		File file = this.getFile(absolutePath);
		return isContainedInRoot(file);
	}

	@Override
	/**
	 * check if the given absolutePath/uri/file is contained in the rootPath of
	 * the workspace, or is the same as the root-path.
	 */
	public boolean isContainedInRoot(URI uri) {
		File file = this.getFile(uri);
		return isContainedInRoot(file);
	}

	@Override
	/**
	 * check if the given absolutePath/uri/file is contained in the rootPath of
	 * the workspace, or is the same as the root-path.
	 */
	public boolean isContainedInRoot(File file) {
		File root = this.getRootFile();
		if(!root.isDirectory()) {
			return false;
		}
		return containsOrIsSame(root, file);
	}
	
	public boolean containsOrIsSame(File directory, File containment) {
		if(directory == null || containment == null || !directory.exists() || !containment.exists() )
			return false;
		
		String absoluteDirPath = directory.getAbsolutePath();
		String containmentPath = containment.getAbsolutePath();
		if(absoluteDirPath.contentEquals(containmentPath)) {
			return true;
		}
		File parent = containment.getParentFile();
		if(parent != null) {
			return containsOrIsSame(directory, parent);
		}
		return false;
	}

	@Override
	public String getRootFolderName() {
		File root = this.getRootFile();
		if(root == null)
			throw new RuntimeException("No workspace-root defined.");
		return root.getName();
	}
	
	public static IWorkspaceContext createInstance(IProjectConfigProvider projectConfigProvider, EObject eObject) {
		if(eObject == null || projectConfigProvider == null)
			throw new IllegalArgumentException("IProjectConfigProvider and/or EObject must not be null!");
		Resource res = eObject.eResource();
		return WorkspaceContext.createInstance(projectConfigProvider, res);
	}
	
	public static IWorkspaceContext createInstance(IProjectConfigProvider projectConfigProvider, Resource res) {
		ResourceSet set = null;
		if(res != null)
			set = res.getResourceSet();
		if(set == null) {
			throw new IllegalArgumentException("given Resource is not associated with any ResourceSet!");
		}
		
		IProjectConfig projectConfig = projectConfigProvider.getProjectConfig(set);
		URI root = projectConfig.getPath();
		if(projectConfigProvider != null) {
			
		}
		
		IWorkspaceContext workspaceContext = new WorkspaceContext(root, set);
		return workspaceContext;
	}
}
