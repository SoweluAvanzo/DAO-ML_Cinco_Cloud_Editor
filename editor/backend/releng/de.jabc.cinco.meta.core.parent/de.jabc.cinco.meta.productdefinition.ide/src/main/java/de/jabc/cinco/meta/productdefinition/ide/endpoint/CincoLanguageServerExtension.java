package de.jabc.cinco.meta.productdefinition.ide.endpoint;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;
import org.eclipse.xtext.ide.server.ILanguageServerExtension;
import org.eclipse.xtext.workspace.IProjectConfigProvider;

import com.google.inject.Inject;

import de.jabc.cinco.meta.productdefinition.ide.endpoint.messages.GenerateRequest;
import de.jabc.cinco.meta.productdefinition.ide.endpoint.messages.GenerateResponse;
import de.jabc.cinco.meta.productdefinition.ide.endpoint.parser.ParserHelper;
import mgl.MGLModel;
import productDefinition.CincoProduct;
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
	
	@Inject(optional = true)
	IProjectConfigProvider projectConfigProvider;
	
	@Override
	public void initialize(ILanguageServerAccess access) {
		this.access = access;
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
		
		// parse
		LogHelper.log(access, "parsing resources...");
		String cpdPath = request.getSourceURI(); // TODO: use this
		
		Map<String, EObject> parsedResources = ParserHelper.getAllResources(this.access);
		
		List<WorkspaceFolder> workspaceFolders = this.getWorkspaceFolders(this.access);
		WorkspaceFolder targetFolder = workspaceFolders.get(0); // TODO: set targetFolder

		// prepare
		List<CincoProduct> cpds = parsedResources.values().stream()
				.filter((EObject e) -> e instanceof CincoProduct)
				.map((m) -> (CincoProduct) m)
				.collect(Collectors.toList()); 
		CincoProduct cpd = cpds.get(0); // TODO: only the one from the request
		Set<MGLModel> mgls = parsedResources.values().stream()
				.filter((EObject e) -> e instanceof MGLModel)
				.map((m) -> (MGLModel) m) // TODO: online cpd referenced
				.collect(Collectors.toSet());
		org.eclipse.emf.common.util.URI projectURI = org.eclipse.emf.common.util.URI.createURI(
				targetFolder.getUri() + "/"
			);
		String projectLocation = projectURI.toFileString();
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
		
		// TODO: deploy
		// LogHelper.log(access, "deploying cinco-product...");
		
		// respond information after generation
		GenerateResponse generateResponse = new GenerateResponse();
		generateResponse.setTargetURI(targetFolder.getUri());
		return CompletableFuture.completedFuture(generateResponse);
	}
	
	public List<WorkspaceFolder> getWorkspaceFolders(ILanguageServerAccess access) {
		return access.getInitializeParams().getWorkspaceFolders();
	}
}
