package de.jabc.cinco.meta.productdefinition.ide.endpoint;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;
import org.eclipse.xtext.ide.server.ILanguageServerExtension;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.workspace.IProjectConfigProvider;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import de.jabc.cinco.meta.productdefinition.ide.endpoint.messages.GenerateRequest;
import de.jabc.cinco.meta.productdefinition.ide.endpoint.messages.GenerateResponse;
import de.jabc.cinco.meta.productdefinition.ide.endpoint.parser.ParserHelper;
import mgl.MGLModel;
import productDefinition.CincoProduct;
import de.jabc.cinco.meta.core.mgl.MGLRuntimeModule;
import de.jabc.cinco.meta.core.utils.IWorkspaceContext;
import de.jabc.cinco.meta.core.utils.WorkspaceContext;
import de.jabc.cinco.meta.plugin.pyro.CreatePyroPlugin;
import de.jabc.cinco.meta.productdefinition.ide.communication.LogHelper;
import de.jabc.cinco.meta.productdefinition.ide.communication.StaticMessages;

/**
 * This class represents the languageServerExtension, that contains all additional
 * Endpoints.
 * 
 * @author sami
 *
 */
public class CincoLanguageServerExtension implements ILanguageServerExtension, GenerationEndpoint {

	ILanguageServerAccess access;
	
	@Inject
	protected Provider<XtextResourceSet> resourceSetProvider;
	
	@Inject(optional = true)
	IProjectConfigProvider projectConfigProvider;
	
	@Override
	public void initialize(ILanguageServerAccess access) {
		this.access = access;
	}
	
	public Injector createInjector() {
		return Guice.createInjector(new MGLRuntimeModule());
	}
	
	/**
	 * This method gets called, if the language-client sends a request to
	 * "cinco/generate". It returns a ASTResponse, that contains the ASTs of the specified URIs
	 * or, if their are no, of all URIs related to the given FileExtension.
	 * @return 
	 */
	@Override
	public CompletableFuture<GenerateResponse> requestGeneration(GenerateRequest request) {
		// notify ide that generation-process starts
		access.getLanguageClient().showMessage(StaticMessages.getGenerateMessage());
		
		// resolve project URI
		List<WorkspaceFolder> workspaceFolders = this.getWorkspaceFolders(this.access);
		WorkspaceFolder targetFolder = workspaceFolders.get(0); // TODO: resolve targetFolder from request
		org.eclipse.emf.common.util.URI projectURI = org.eclipse.emf.common.util.URI.createURI(
				targetFolder.getUri() + "/"
			);	
		String projectLocation = projectURI.devicePath();
		
		// parse resources
		LogHelper.log(access, "parsing resources...");
		XtextResourceSet resourceSet = resourceSetProvider.get();
		Map<String, Resource> parsedResources = ParserHelper.getAllResources(this.access, resourceSet, projectURI);	
		
		// prepare generator input
		// cpd:
		String cpdPath = request.getSourceURI(); // TODO: use only cpd related to this path
		List<CincoProduct> cpds = parsedResources.values().stream()
				.map((Resource r) -> r.getContents().get(0)) 
				.filter((EObject e) -> e instanceof CincoProduct)
				.map((m) -> (CincoProduct) m)
				.collect(Collectors.toList()); 
		CincoProduct cpd = cpds.get(0); // TODO: only the one from the request
		// mgls:
		Set<MGLModel> mgls = parsedResources.values().stream()
				.map((Resource r) -> r.getContents().get(0)) 
				.filter((EObject e) -> e instanceof MGLModel)
				.map((m) -> (MGLModel) m) // TODO: online cpd referenced
				.collect(Collectors.toSet());
		IWorkspaceContext.setLocalInstance(new WorkspaceContext(projectURI, cpd.eResource().getResourceSet()));
				
		// execute generation
		LogHelper.log(access, "starting pyro-generator...");
		CreatePyroPlugin pyro = new CreatePyroPlugin();
		try {
			pyro.execute(mgls, projectLocation, cpd);
		} catch (Exception e) {
			e.printStackTrace();
			LogHelper.logError(access, "An error occured during generation!");
		}
		
		// respond information after generation
		GenerateResponse generateResponse = new GenerateResponse();
		generateResponse.setTargetURI(targetFolder.getUri());
		return CompletableFuture.completedFuture(generateResponse);
	}
	
	public List<WorkspaceFolder> getWorkspaceFolders(ILanguageServerAccess access) {
		return access.getInitializeParams().getWorkspaceFolders();
	}
}
