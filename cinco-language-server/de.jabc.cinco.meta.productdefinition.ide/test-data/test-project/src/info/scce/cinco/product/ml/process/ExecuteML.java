package info.scce.cinco.product.ml.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import entity.core.PyroProjectService_Connect_Jupyter_AccountDB;
import entity.core.PyroUserDB;
import graphmodel.IdentifiableElement;
import info.scce.cinco.product.base.process.JupyterPythonGenerator;
import info.scce.cinco.product.base.process.baseprocess.BaseProcess;
import info.scce.cinco.product.ml.process.mlprocess.MLProcess;
import info.scce.cinco.product.ml.process.transformer.BaseProcessTransformer;
import info.scce.pyro.sync.DisplayMessage;
import info.scce.pyro.sync.DisplayMessages;
import info.scce.pyro.sync.GraphModelWebSocket;
import info.scce.pyro.sync.WebSocketMessage;
import javax.json.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ExecuteML extends CincoCustomAction<MLProcess> {
	private JupyterUtil util = new JupyterUtil();

	boolean compilingReady = false;
    boolean convertStarted = false;
    boolean finished = false;
    List<DisplayMessage> messages = new LinkedList<>();

	@Override
	public void execute(MLProcess element) {
		
		// TODO: SAMI - find new definition
		String projectFileBaseURL = ""; // getUriInfo().getBaseUri().toString()+"pyrofile/read/projectresource/"+getProject().getDywaId()+"/";

		PyroUserDB user = this.commandExecuter().getBatch().getUser();

		// transform MLProcess to BaseProcess
		BaseProcessTransformer transformer = new BaseProcessTransformer(projectFileBaseURL);
		BaseProcess bp = transformer.transform(element);

		// generate Code from BaseProcess
		CharSequence code = new JupyterPythonGenerator().generate(bp);
		
		System.out.println(code);

		PyroProjectService_Connect_Jupyter_AccountDB service = util.getService();

		if(service == null) {
			return;
		}

		String token = service.getToken();
		String username = service.getUsername();
		String url = service.getURL();
		
        /*
         * USER
         */
		JsonObject userObj = util.checkUser(username, token, url);

        /*
        Check Server status
         */
		if(userObj.isNull("server")) {
			util.startServer(username, token, url);
		} else {
			System.out.println("Server is running");
		}
		
		// start kernel
		JsonObject kernelInfo = null;
		try {
			kernelInfo = util.post(url+JupyterUtil.NOTEBOOK_URL+"/"+username+"/api/kernels",token,null).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		if(kernelInfo == null) {
			System.out.println("Kernel failure");
			return;
		}
		System.out.println("Kernel started. ID="+kernelInfo.getString("id"));

		GraphModelWebSocket webSocket = this.commandExecuter().getGraphModelWebSocket();
		// run Code on Jupyter Hub
		try {
			// open websocket
			String uriString = "wss://"+url+"/user/"+username+"/api/kernels/"+kernelInfo.getString("id")+"/channels";
			URI uri = new URI(uriString);
			System.out.println(uriString);
			final JupyterWebsocket clientEndPoint = new JupyterWebsocket(
				uri,
				token,
				(String message, JupyterWebsocket clientEndpoint) -> onMessage(message, element, user, webSocket, clientEndpoint)
			);

			//send message
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
			String json = mapper.writeValueAsString(getExecutionMsg(code.toString()));
			clientEndPoint.sendMessage(json);

			// wait 5 seconds for messages from websocket
			//Thread.sleep(5000);

		} catch (URISyntaxException ex) {
			System.err.println("URISyntaxException exception: " + ex.getMessage());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}


		// write results to file
		
	}
	
	public boolean canExecute(MLProcess p) {
		return p.getInputss().isEmpty();
	}
	
	public String getName() {
		return "Execute on Jupyter";
	}

	private ExecuteMsg getExecutionMsg(String code) {
		ExecuteMsg msg = new ExecuteMsg();
		msg.header.msg_id = getRandomHexString(32);
		msg.content.code = code;
		return msg;
	}

	private static String getRandomHexString(int numchars){
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		while(sb.length() < numchars){
			sb.append(Integer.toHexString(r.nextInt()));
		}
		return sb.toString().substring(0, numchars);
	}

	public void onMessage(String message, IdentifiableElement element, PyroUserDB user, GraphModelWebSocket webSocket, JupyterWebsocket clientEndPoint) {
		if(finished) {
			return;
		}
		System.out.println(message);
		JsonObject obj = Json.createReader(new StringReader(message)).readObject();
		if(obj.getJsonObject("header").getString("msg_type").equals("stream") && !compilingReady && !convertStarted) {
			//text case
			DisplayMessage m = new DisplayMessage();
			m.setMessageType("text");
			m.setContent(obj.getJsonObject("content").getString("text"));
			messages.add(m);
			System.out.println(obj.getJsonObject("content").getString("text"));
		}
		if(obj.getJsonObject("header").getString("msg_type").equals("display_data") && !compilingReady && !convertStarted) {
			//image case
			DisplayMessage m = new DisplayMessage();
			if(obj.getJsonObject("content").getJsonObject("data").containsKey("text/html")) {
				m.setMessageType("html");
				m.setContent(obj.getJsonObject("content").getJsonObject("data").getString("text/html"));
				messages.add(m);
			}
			else if(obj.getJsonObject("content").getJsonObject("data").containsKey("image/png")) {
				m.setMessageType("image");
				m.setContent(obj.getJsonObject("content").getJsonObject("data").getString("image/png"));
				messages.add(m);
			}
			//System.out.println(obj.getJsonObject("content").getJsonObject("data").getString("content"));
		}
		if(obj.getJsonObject("header").getString("msg_type").equals("error") && !compilingReady && !convertStarted) {
			//error case
			DisplayMessage m1 = new DisplayMessage();
			m1.setMessageType("ename");
			m1.setContent(obj.getJsonObject("content").getString("ename"));
			messages.add(m1);
			DisplayMessage m2 = new DisplayMessage();
			m2.setMessageType("evalue");
			m2.setContent(obj.getJsonObject("content").getString("evalue"));
			for(JsonString trace:obj.getJsonObject("content").getJsonArray("traceback").stream().filter(n->n instanceof JsonString).map(n->(JsonString)n).collect(Collectors.toList())) {
				DisplayMessage mt = new DisplayMessage();
				mt.setMessageType("trace");
				mt.setContent(trace.getString());
				messages.add(mt);
			}
			messages.add(m2);
			System.out.println(obj.getJsonObject("content").getString("ename"));
		}
		if(obj.getJsonObject("header").getString("msg_type").equals("status") && obj.getJsonObject("content").getString("execution_state").equals("idle")) {
			System.out.println("Execution Finished");
			finished = true;
			DisplayMessages dms = new DisplayMessages();
			dms.setMessages(messages);
			System.out.println("----> send message "+messages.size()+ " to "+user.id+ " in ");
			webSocket.send(element.getDelegateId(), WebSocketMessage.fromEntity(user.id,"display",dms));
			try {
				clientEndPoint.userSession.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class ExecuteMsg {
	public ExecuteHeader header = new ExecuteHeader();
	public List<String> buffers = new LinkedList<String>();
	public Empty parent_header = new Empty();
	public Empty metadata = new Empty();
	public ExecuteContent content = new ExecuteContent();
	public String channel = "shell";
}

class ExecuteContent {
	public boolean allow_stdin = true;
	public boolean stop_on_error = true;
	public boolean store_history = true;
	public Empty user_expressions = new Empty();
	public String execution_state;
	public String code;
	public boolean silent = false;
	public String name;
	public String text;
}

class Empty{}

class ExecuteHeader {
	public String msg_id;
	public String username = "username";
	public String session;
	public String msg_type = "execute_request";
	public String version = "5.2";
}
