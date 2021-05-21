package de.jabc.cinco.meta.core.mgl.scoping;

import java.util.LinkedHashSet;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;
import org.eclipse.xtext.workspace.IProjectConfigProvider;

import com.google.inject.Inject;

import de.jabc.cinco.meta.core.utils.IWorkspaceContext;
import de.jabc.cinco.meta.core.utils.WorkspaceContext;
import mgl.Import;
import mgl.MGLModel;
//import de.jabc.cinco.meta.core.utils.PathValidator;

public class MGLImportUriGlobalScopeProvider extends ImportUriGlobalScopeProvider {

	@Inject(optional = true)
	IProjectConfigProvider projectConfigProvider;
	
	@Override
	protected LinkedHashSet<URI> getImportedUris(Resource resource) {
		IWorkspaceContext workspaceContext = new WorkspaceContext(projectConfigProvider, resource);
		LinkedHashSet<URI> uris = super.getImportedUris(resource);
		for (EObject o : resource.getContents()) {
			if (o instanceof MGLModel) {
				MGLModel gm = (MGLModel) o;
				for (Import i : gm.getImports()) {
					try {
						URI uri = workspaceContext.getFileURI(i.getImportURI());
						if (uri == null)
							continue;
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
