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
}
