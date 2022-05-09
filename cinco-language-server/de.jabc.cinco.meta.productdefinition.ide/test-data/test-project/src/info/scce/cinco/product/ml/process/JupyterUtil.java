package info.scce.cinco.product.ml.process;

import entity.core.PyroProjectService_Connect_Jupyter_AccountDB;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import java.io.StringReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Author zweihoff
 */
public class JupyterUtil {
    // static final String BASE_URL = null; // "https://ls5vs026.cs.tu-dortmund.de";
    // static final String WS_URL = null; // "wss://ls5vs026.cs.tu-dortmund.de";
    static final String HUB_URL = "/hub/api";
    static final String NOTEBOOK_URL = "/user";
    
    PyroProjectService_Connect_Jupyter_AccountDB getService() {
        PyroProjectService_Connect_Jupyter_AccountDB first = PyroProjectService_Connect_Jupyter_AccountDB.findAll().firstResult();
        return first;
    }

    JsonObject checkUser(String username, String token, String url) {
        try {
            return this.get(url + HUB_URL + "/users/" + username, token).get();
        } catch(Exception e) {
            //user not found create one
            try {
                return this.post(url + HUB_URL + "/users/" + username, token, null).get();
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }

    void startServer(String username,String token, String url) {
        System.out.println("Server is not running");
        try {
            this.post(url + HUB_URL + "/users/" + username + "/server", token, null).get();
            System.out.println("Server started");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    CompletableFuture<JsonObject> post(String url, String token, Object payload) {
        System.out.println("POST: "+url);
        CompletableFuture<JsonObject> completableFuture = new CompletableFuture<>();
        ResteasyClientBuilder.newClient().target("https://" + url)
                .request()
                .header("Authorization","token "+token)
                .header("content-type", "application/json")
                .async()
                .post(payload==null? Entity.text(""):Entity.json(payload),new InvocationCallback<String>() {
                    @Override
                    public void completed(String resp) {
                        if(resp == null || resp.isEmpty()) {
                            System.out.println("No Response");
                            completableFuture.complete(null);
                            return;
                        }
                        //System.out.println("Response: "+resp);
                        JsonReader reader = Json.createReader(new StringReader(resp));
                        completableFuture.complete(reader.readObject());
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        System.out.println("POST FAILED : "+url);
                        completableFuture.completeExceptionally(throwable);
                    }
                });
        return completableFuture;
    }

    CompletableFuture<JsonObject> get(String url, String token) {
        System.out.println("GET: "+url);
        CompletableFuture<JsonObject> completableFuture = new CompletableFuture<>();
        ResteasyClientBuilder.newClient().target("https://" + url)
                .request()
                .header("Authorization","token "+token)
                .header("content-type", "application/json")
                .async()
                .get(new InvocationCallback<String>() {
                    @Override
                    public void completed(String resp) {
                        //System.out.println("Response: "+resp);
                        JsonReader reader = Json.createReader(new StringReader(resp));
                        completableFuture.complete(reader.readObject());
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        completableFuture.completeExceptionally(throwable);
                    }
                });
        return completableFuture;
    }
}