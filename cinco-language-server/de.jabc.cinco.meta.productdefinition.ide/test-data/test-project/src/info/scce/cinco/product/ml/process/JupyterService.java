package info.scce.cinco.product.ml.process;

import javax.json.JsonObject;
import entity.core.PyroProjectService_Connect_Jupyter_AccountDB;
import java.util.List;
import java.util.Map;

public class JupyterService extends info.scce.pyro.api.PyroProjectService<PyroProjectService_Connect_Jupyter_AccountDB>{

	@Override
	public boolean canExecute(java.util.List<PyroProjectService_Connect_Jupyter_AccountDB> services) {
		// service should be a singleton
		return services.isEmpty();
	}

	@Override
	public boolean isValid(Map<String,String> inputs, List<PyroProjectService_Connect_Jupyter_AccountDB> services) {
		/* 
		 * service should be a singleton and running.
		 * Just check if user exists on running service.
		 * If a user is returned, the service is valid.
		 */
		if(
				!services.isEmpty()
				|| !inputs.containsKey("Username")
				|| !inputs.containsKey("Token")
				|| !inputs.containsKey("URL")
		) {
			return false;
		}
		String username = inputs.get("Username");
		String token = inputs.get("Token");
		String url = inputs.get("URL");
		JupyterUtil util = new JupyterUtil();
		JsonObject userObj = util.checkUser(username, token, url);
		if(userObj == null) {
			return false;
		}
        return true;
	}
	
	@Override
	public void execute(PyroProjectService_Connect_Jupyter_AccountDB service) {
		// server is executed externally. No need for execution.
	}
}