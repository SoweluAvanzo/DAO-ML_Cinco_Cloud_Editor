package de.jabc.cinco.meta.productdefinition.ide.communication;

import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;

public class StaticMessages {

	public static String generateMessage = "starting generation...";
	
	public static MessageParams getGenerateMessage() {
		MessageParams message = new MessageParams();
		message.setType(MessageType.Info);
		message.setMessage(generateMessage);
		return message;
	}
}
