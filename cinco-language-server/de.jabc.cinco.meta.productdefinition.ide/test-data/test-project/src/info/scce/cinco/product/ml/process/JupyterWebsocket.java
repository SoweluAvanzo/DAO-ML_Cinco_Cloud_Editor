package info.scce.cinco.product.ml.process;

import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ClientEndpointConfig.Builder;
import javax.websocket.ClientEndpointConfig.Configurator;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class JupyterWebsocket extends Endpoint {
    Session userSession = null;
    BiConsumer<String, JupyterWebsocket> onMessageCallback = null;

    public JupyterWebsocket(URI endpointURI, String token, BiConsumer<String, JupyterWebsocket> onMessageCallback) {
        super();
        System.out.println(endpointURI.toString());
        Builder configBuilder = ClientEndpointConfig.Builder.create();
        configBuilder.configurator(new Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {
                headers.put("Authorization", Collections.singletonList("token "+token));
            }
        });
        ClientEndpointConfig clientConfig = configBuilder.build();
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.setAsyncSendTimeout(10000);
            container.setDefaultMaxSessionIdleTimeout(10000);
            container.connectToServer(this, clientConfig, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.onMessageCallback = onMessageCallback;
        JupyterWebsocket instance = this;
        this.userSession.addMessageHandler(
            new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    onMessageCallback.accept(message, instance);
                }
            }
        );
    }

	public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        System.out.println(session.getId());
        thr.printStackTrace();
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("opening websocket");
        this.userSession = session;
    }
}