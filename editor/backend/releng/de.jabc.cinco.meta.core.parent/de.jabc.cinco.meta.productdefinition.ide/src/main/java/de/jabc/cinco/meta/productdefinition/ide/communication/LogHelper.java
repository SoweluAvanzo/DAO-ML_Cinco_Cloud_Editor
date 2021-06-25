package de.jabc.cinco.meta.productdefinition.ide.communication;

import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;

public class LogHelper {
	private static String prefixIdentifier = "[cinco-language-server] - ";
	
	/**
	 * Sending a message into the client's log. (for debugging purpose)
	 */
	
	public static void log(ILanguageServerAccess access, String message) {
		log(access.getLanguageClient(), message, MessageType.Info);
	}
	
	public static void log(ILanguageServerAccess access, String message, MessageType type) {
		log(access.getLanguageClient(), message, type);
	}
	
	public static void logError(ILanguageServerAccess access, String message) {
		log(access.getLanguageClient(), message, MessageType.Error);
	}
	
	public static void log(LanguageClient client, String message, MessageType type) {
		var messageParams = new MessageParams();
		messageParams.setMessage(prefixIdentifier + message);
		messageParams.setType(type);
		client.logMessage(messageParams);
	}
}
