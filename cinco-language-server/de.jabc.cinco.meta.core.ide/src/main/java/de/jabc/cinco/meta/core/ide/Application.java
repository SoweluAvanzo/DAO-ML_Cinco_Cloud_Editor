package de.jabc.cinco.meta.core.ide;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.xtext.ide.server.LanguageServerImpl;
import org.eclipse.xtext.ide.server.SocketServerLauncher;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Application extends SocketServerLauncher {
	
	private static final Logger LOG = Logger.getLogger(Application.class);

	private static String[] ARGS;
	public static final String MIN_CONNECTIONS = "-min";
	
	public static final int DEFAULT_PORT = 5008;
	public static final String DEFAULT_HOST = "0.0.0.0";
	public static final int DEFAULT_MIN_CONNECTIONS = -1;
	
	public static void main(String[] args) {
		ARGS = args;
		new Application().launch(args);
	}
	
	@Override
	public void launch(String[] args) {
		new Thread(new RunServer()).run();	
	}
	
	@Override
	protected String getHost(String... args) {
		String host = getValue(args, HOST);
		if (host != null) {
			return host;
		} else {
			return DEFAULT_HOST;
		}
	}

	@Override
	protected int getPort(String... args) {
		try {
			String value = getValue(args, PORT);
			if(value != null && !value.isEmpty()) {
				Integer port = Integer.parseInt(value);
				if(port != null)
					return port;
			}
		} catch(NumberFormatException e) {
			System.err.println("Port is not a number!");
			e.printStackTrace();
		}
		return DEFAULT_PORT;
	}
	
	private int getMinimumConnections(String... args) {
		try {
			String value = getValue(args, MIN_CONNECTIONS);
			if(value != null && !value.isEmpty()) {
				Integer min = Integer.parseInt(value);
				if(min != null)
					return min;
			}
		} catch(NumberFormatException e) {
			System.err.println("MinimumConnections is not a number!");
		}
		return DEFAULT_MIN_CONNECTIONS;
	}
	
	private class RunServer implements Runnable {

		@Override
		public void run() {
			Injector injector = Guice.createInjector(getServerModule());
			ExecutorService threadPool = Executors.newCachedThreadPool();
			try (AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open()
					.bind(getSocketAddress(ARGS))) {
				
				// setup minimum of connections until the server shutsdown
				int min_con = getMinimumConnections(ARGS);
				MultiUserLanguageServerImpl.setMinConnections(min_con);
				LOG.info("Started server socket at " + getSocketAddress(ARGS));
				
				// user accepting loop
				while (true) {
					AsynchronousSocketChannel socketChannel = serverSocket.accept().get();
					// adding connection
					MultiUserLanguageServerImpl.addConnection(()-> {
						InputStream in = Channels.newInputStream(socketChannel);
						OutputStream out = Channels.newOutputStream(socketChannel);
						MultiUserLanguageServerImpl languageServer = injector.getInstance(MultiUserLanguageServerImpl.class);
						LOG.info("Connecting to client: " + socketChannel.getRemoteAddress());
						Launcher<LanguageClient> launcher = Launcher.createLauncher(languageServer, LanguageClient.class, in,
								out, threadPool, (m)->m);
						languageServer.connect(launcher.getRemoteProxy());
						LOG.info("Connected to client: " + socketChannel.getRemoteAddress());
						launcher.startListening();
						return languageServer;
					});
				}
			} catch (IOException e) {
				LOG.error("[CINCO-Language-Server] Encountered an error establishing a connection!");
				e.printStackTrace();
			}  catch (InterruptedException e) {
				LOG.error("[CINCO-Language-Server] Encountered an concurrency error!");
				e.printStackTrace();
			}  catch (ExecutionException e) {
				LOG.error("[CINCO-Language-Server] Encountered an execution-error!");
				e.printStackTrace();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
