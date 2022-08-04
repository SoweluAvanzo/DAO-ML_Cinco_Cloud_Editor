package info.scce.pyro.auth;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import entity.core.PyroUserDB;

public class PyroUserRegistry {

	private static Map<String, PyroUserDB> USER_REGISTRY = new ConcurrentHashMap<>();

	public static void addUser(AuthPojo user) {
		if(user == null) {
			return;
		}
		String userString = user.getUserString();
        PyroUserDB pyroUser = new PyroUserDB(userString);
        addUser(pyroUser);
	}
	
	public static void addUser(PyroUserDB user) {
		if(user == null) {
			return;
		}	
		USER_REGISTRY.putIfAbsent(""+user.id, user);
	}

	public static PyroUserDB getUser(String id) {
		return USER_REGISTRY.get(id);
	}

	public static PyroUserDB removeUser(String id) {
		return USER_REGISTRY.remove(id);
	}
}
