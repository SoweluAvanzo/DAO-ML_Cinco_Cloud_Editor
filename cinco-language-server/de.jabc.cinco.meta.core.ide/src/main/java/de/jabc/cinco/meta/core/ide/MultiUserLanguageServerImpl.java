package de.jabc.cinco.meta.core.ide;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.xtext.ide.server.ILanguageServerShutdownAndExitHandler;
import org.eclipse.xtext.ide.server.LanguageServerImpl;

import com.google.inject.Inject;

public class MultiUserLanguageServerImpl extends LanguageServerImpl {

	private static final Logger LOG = Logger.getLogger(MultiUserLanguageServerImpl.class);
	private static int MINIMUM_CONNECTIONS = -1;
	@Inject
	private ILanguageServerShutdownAndExitHandler shutdownAndExitHandler;
	
	// connection-handling
	private static List<LanguageServer> CONNECTIONS = new LinkedList<>(); 
	private static Object CONNECTION_LOCK = new Object();

	public static void setMinConnections(int minimumConnections) {
		synchronized(CONNECTION_LOCK) {
			MINIMUM_CONNECTIONS = minimumConnections;
		}
	}
	
	/**
	 * Adds a connection for shutdown handling
	 * @param connectedInstance
	 * @throws Exception 
	 */
	public static void addConnection(Callable<LanguageServer> procedure) throws Exception {
		synchronized(CONNECTION_LOCK) {
			LanguageServer connectedInstance = procedure.call();
			CONNECTIONS.add(connectedInstance);
		}
	}
	
	/**
	 * Removes the managed connection
	 * and shuts down the Server if no connections are available.
	 */
	@Override
	public CompletableFuture<Object> shutdown() {
		synchronized(CONNECTION_LOCK) {
			if(CONNECTIONS.contains(this)) {
				CONNECTIONS.remove(this);
			}
			if(CONNECTIONS.size() <= MINIMUM_CONNECTIONS) {
				LOG.info("No client-connections available - shutingdown server.");
				shutdownAndExitHandler.shutdown();
			}
			return CompletableFuture.completedFuture(new Object());
		}
	}
	
	/**
	 * Removes the managed connection
	 * and exits the Server if no connections are available.
	 */
	@Override
	public void exit() {
		synchronized(CONNECTION_LOCK) {
			if(CONNECTIONS.contains(this)) {
				CONNECTIONS.remove(this);
			}
			if(CONNECTIONS.size() <= MINIMUM_CONNECTIONS) {
				LOG.info("No client-connections available - shutingdown server.");
				shutdownAndExitHandler.shutdown();
			}
		}
	}
}
