package de.jabc.cinco.meta.productdefinition.ide.endpoint;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

import de.jabc.cinco.meta.productdefinition.ide.endpoint.messages.GenerateRequest;
import de.jabc.cinco.meta.productdefinition.ide.endpoint.messages.GenerateResponse;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import java.util.concurrent.CompletableFuture;

/**
 * AstEndPoint of the Server, implemented by the LanguageServerExtension.
 */
@JsonSegment("cinco")
public interface GenerationEndpoint {
	
	/**
	 * This method gets called, if the language-client sends a request to
	 * "cinco/generate".
	 */
	@JsonRequest("generate")
	public CompletableFuture<GenerateResponse> requestGeneration(GenerateRequest request);
}
