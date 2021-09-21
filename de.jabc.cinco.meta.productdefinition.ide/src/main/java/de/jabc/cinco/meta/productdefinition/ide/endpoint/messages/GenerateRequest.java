package de.jabc.cinco.meta.productdefinition.ide.endpoint.messages;

import org.eclipse.xtend.lib.annotations.Data;

@Data
public class GenerateRequest {
	String sourceUri; // uri of cpd, that's used for generation
	String targetUri; // uri of the target-path

	public GenerateRequest() {}
	
	public GenerateRequest(String sourceURI, String targetURI) {
		this.sourceUri = sourceURI;
		this.targetUri = targetURI;
	}
	
	public String getSourceURI() {
		return sourceUri;
	}
	
	public String getTargetURI() {
		return targetUri;
	}
}
