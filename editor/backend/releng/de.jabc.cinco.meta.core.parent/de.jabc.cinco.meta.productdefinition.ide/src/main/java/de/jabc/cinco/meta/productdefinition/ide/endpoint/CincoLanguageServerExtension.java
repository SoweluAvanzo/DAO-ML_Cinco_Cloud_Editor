package de.jabc.cinco.meta.productdefinition.ide.endpoint;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;
import org.eclipse.xtext.ide.server.ILanguageServerExtension;

import de.jabc.cinco.meta.productdefinition.ide.endpoint.messages.GenerateRequest;
import de.jabc.cinco.meta.productdefinition.ide.endpoint.messages.GenerateResponse;
import de.jabc.cinco.meta.productdefinition.ide.endpoint.parser.ParserHelper;
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
		String cpdPath = request.getSourceURI();
		Map<String, EObject> parsedResources = ParserHelper.getAllResources(this.access);
		
		List<WorkspaceFolder> workspaceFolders = this.getWorkspaceFolders(this.access);
		WorkspaceFolder targetFolder = workspaceFolders.get(0); // TODO: set targetFolder
		
		// TODO prepare resources + validation check
		
		// IF VALID
		// TODO: execute generation
		// LogHelper.log(access, "generating cinco-product...");
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
