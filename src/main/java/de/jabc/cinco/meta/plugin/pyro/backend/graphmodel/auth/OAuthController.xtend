package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.auth

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class OAuthController extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename()'''OAuthController.java'''
	
	def content()
	'''
	package info.scce.pyro.auth;
	
	import de.ls5.dywa.generated.controller.info.scce.pyro.core.PyroUserController;
	import de.ls5.dywa.generated.entity.info.scce.pyro.core.PyroUser;
	import org.apache.shiro.authc.UsernamePasswordToken;
	
	import javax.json.Json;
	import javax.json.JsonObject;
	import javax.json.JsonReader;
	import javax.ws.rs.QueryParam;
	import javax.ws.rs.client.ClientBuilder;
	import javax.ws.rs.client.Entity;
	import javax.ws.rs.client.InvocationCallback;
	import javax.ws.rs.client.WebTarget;
	import javax.ws.rs.core.NewCookie;
	import javax.ws.rs.core.Response;
	import java.io.StringReader;
	import java.io.UnsupportedEncodingException;
	import java.net.URI;
	import java.net.URLDecoder;
	import java.nio.charset.Charset;
	import java.util.Arrays;
	import java.util.LinkedHashMap;
	import java.util.Map;
	import java.util.Random;
	import java.util.concurrent.CompletableFuture;
	import java.util.concurrent.ExecutionException;
	
	@javax.transaction.Transactional
	@javax.ws.rs.Path("/oauth")
	public class OAuthController {
	
		@javax.inject.Inject
		private PyroUserController subjectController;
	
	
		private final WebTarget authTarget = ClientBuilder.newClient()
				.target("«gc.authCompound.authURL»");
	
		private final WebTarget apiTarget = ClientBuilder.newClient()
				.target("«gc.authCompound.userURL»");
	
		private final static String CLIENT_ID = "«gc.authCompound.clientID»";
		private final static String CLIENT_SECRET = "«gc.authCompound.clientSecret»";
		private final static String REDIRECT_URI = "«gc.authCompound.callbackURL»";
		private final static String STATE = "«gc.authCompound.state»";
	
		private final static String[] ADMINS = {«gc.authCompound.admins.split(",").map['''"«it»"'''].join(",")»};
	
	
	    @javax.ws.rs.GET
	    @javax.ws.rs.Path("/cb/public")
	    @javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	    @org.jboss.resteasy.annotations.GZIP
	    public Response callback(@QueryParam("code") String code,@QueryParam("state") String state) throws ExecutionException, InterruptedException {
	
	    	if(!state.equals(STATE)) {
	    		return Response.status(500).build();
			}
	
			String token = accessTokenAsync(code).get();
			String resp = userAsync(token).get();
	
			JsonReader reader = Json.createReader(new StringReader(resp));
			JsonObject jsonObject = reader.readObject();
			String username = jsonObject.getString("«gc.authCompound.userAccountName»");
			String user_id = jsonObject.getInt("«gc.authCompound.userAccountIdentifier»")+"";
			PyroUser user = createUserAndLogin(user_id,username);
			String cookie = authenticateUser(user);
	
			return Response
					.temporaryRedirect(URI.create(System.getenv("CLIENT_ORIGIN")+"/#/home/login"))
					.cookie(new NewCookie("JSESSIONID",cookie))
					.build();
	
	    }
	
		private CompletableFuture<String> accessTokenAsync(String code) {
			CompletableFuture<String> completableFuture = new CompletableFuture<>();
			OAuthAccessToken payload = new OAuthAccessToken();
			payload.client_id = CLIENT_ID;
			payload.client_secret = CLIENT_SECRET;
			payload.code = code;
			payload.redircet_uri = REDIRECT_URI;
			payload.state = STATE;
			authTarget
					.request()
					.async()
					.post(Entity.json(payload) ,new InvocationCallback<String>() {
						@Override
						public void completed(String resp) {
							completableFuture.complete(splitQuery(resp).get("access_token"));
						}
	
						@Override
						public void failed(Throwable throwable) {
							completableFuture.completeExceptionally(throwable);
						}
					});
			return completableFuture;
		}
	
		private PyroUser createUserAndLogin(String userId, String userName) {
			final de.ls5.dywa.generated.entity.info.scce.pyro.core.PyroUser searchObject = subjectController.createSearchObject(null);
			searchObject.setusername(userName);
			searchObject.setemail(userId);
			final java.util.List<PyroUser> users = subjectController.findByProperties(searchObject);
			if(users.isEmpty()){
				PyroUser user = subjectController.create(userName);
				if (Arrays.stream(ADMINS).anyMatch(userName::equals)) {
					user.getsystemRoles_PyroSystemRole().add(de.ls5.dywa.generated.entity.info.scce.pyro.core.PyroSystemRole.ADMIN);
					user.getsystemRoles_PyroSystemRole().add(de.ls5.dywa.generated.entity.info.scce.pyro.core.PyroSystemRole.ORGANIZATION_MANAGER);
				}
				user.setemail(userId);
				user.setemailHash(null);
				user.setpassword(randomPW());
				user.setusername(userName);
				return user;
			}
			return users.get(0);
	
	
		}
	
		private String randomPW() {
			byte[] array = new byte[7]; // length is bounded by 7
			new Random().nextBytes(array);
			return new String(array, Charset.forName("UTF-8"));
		}
	
		private String authenticateUser(PyroUser user) {
			System.out.println("authenticate");
			UsernamePasswordToken token = new UsernamePasswordToken(user.getusername(), user.getpassword());
	
			org.apache.shiro.SecurityUtils.getSubject().login(token);
			return org.apache.shiro.SecurityUtils.getSubject().getSession().getId().toString();
		}
	
		private CompletableFuture<String> userAsync(String token) {
			CompletableFuture<String> completableFuture = new CompletableFuture<>();
			apiTarget
					.request("application/json")
					.header("Authorization","token "+token)
					.async()
					.get(new InvocationCallback<String>() {
						@Override
						public void completed(String resp) {
							completableFuture.complete(resp);
						}
	
						@Override
						public void failed(Throwable throwable) {
							// on fail
							completableFuture.completeExceptionally(throwable);
						}
					});
			return completableFuture;
		}
	
		private Map<String, String> splitQuery(String query) {
			Map<String, String> query_pairs = new LinkedHashMap<>();
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf("=");
				try {
					query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			return query_pairs;
		}
	
	}
	


	'''
	
}