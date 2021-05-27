package de.jabc.cinco.meta.core.mgl.scoping;

import java.util.LinkedHashSet;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;
import org.eclipse.xtext.workspace.IProjectConfig;
import org.eclipse.xtext.workspace.IProjectConfigProvider;

import com.google.inject.Inject;

import de.jabc.cinco.meta.core.utils.IWorkspaceContext;
import de.jabc.cinco.meta.core.utils.WorkspaceContext;
import mgl.Import;
import mgl.MGLModel;
import de.jabc.cinco.meta.core.utils.PathValidator;

public class MGLImportUriGlobalScopeProvider extends ImportUriGlobalScopeProvider {

	@Inject(optional = true)
	IProjectConfigProvider projectConfigProvider;
	
	@Override
	protected LinkedHashSet<URI> getImportedUris(Resource resource) {
		IWorkspaceContext workspaceContext = WorkspaceContext.createInstance(projectConfigProvider, resource);
		LinkedHashSet<URI> uris = super.getImportedUris(resource);
		for (EObject o : resource.getContents()) {
			if (o instanceof MGLModel) {
				MGLModel gm = (MGLModel) o;
				for (Import i : gm.getImports()) {
					try {
						boolean exists = PathValidator.checkPath(gm, i.getImportURI(), workspaceContext);
						if (!exists)
							continue;
						URI uri = workspaceContext.getFileURI(i.getImportURI());
						uris.add(uri);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return uris;
	}
}
