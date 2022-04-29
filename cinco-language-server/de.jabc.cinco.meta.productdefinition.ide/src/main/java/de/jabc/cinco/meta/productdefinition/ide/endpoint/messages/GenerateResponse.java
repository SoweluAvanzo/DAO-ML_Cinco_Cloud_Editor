package de.jabc.cinco.meta.productdefinition.ide.endpoint.messages;

import org.eclipse.xtend.lib.annotations.Data;

@Data
public class GenerateResponse {
	String targetUri; // uri of the target-path
	
	public void setTargetURI(String path) {
		this.targetUri = path;
	}
}
