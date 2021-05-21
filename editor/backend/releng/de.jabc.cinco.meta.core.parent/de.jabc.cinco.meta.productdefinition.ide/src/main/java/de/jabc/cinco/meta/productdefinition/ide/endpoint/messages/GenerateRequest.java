package de.jabc.cinco.meta.productdefinition.ide.endpoint.messages;

import org.eclipse.xtend.lib.annotations.Data;

@Data
public class GenerateRequest {
	String sourceUri; // uri of cpd, that's used for generation
	String targetUri; // uri of the target-path

	public String getSourceURI() {
		return sourceUri;
	}
	
	public String getTargetURI() {
		return targetUri;
	}
}
