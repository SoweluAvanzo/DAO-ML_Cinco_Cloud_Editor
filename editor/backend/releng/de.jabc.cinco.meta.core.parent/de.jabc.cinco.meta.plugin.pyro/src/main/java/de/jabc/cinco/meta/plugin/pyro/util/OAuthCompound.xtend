package de.jabc.cinco.meta.plugin.pyro.util

class OAuthCompound {
	//Button name
	public final String name;
	//Callback URL of Pyro Server
	public final String callbackURL;
	//Client ID, provided by provider
	public final String clientID;
	//Client Secret, provided by provider
	public final String clientSecret;
	//Scope for application
	public final String scope;
	//URL of provider login
	public final String signinURL;
	//URL for provider token reception
	public final String authURL;
	//URL for provider user data reception
	public final String userURL;
	//attribute name of user account id
	public final String userAccountIdentifier;
	//attribute name of user account name
	public final String userAccountName;
	//Comma separated list of provider user names to become administrators
	public final String admins;
	
	public final String state;
	
	new(
		String name,
		String callbackURL,
		String clientID,
		String clientSecret,
		String scope,
		String signinURL,
		String authURL,
		String userURL,
		String userAccountIdentifier,
		String userAccountName,
		String admins,
		String state
	) {
		this.name = name;
		this.callbackURL = callbackURL;
		this.clientID = clientID;
		this.clientSecret = clientSecret;
		this.scope = scope;
		this.signinURL = signinURL;
		this.authURL = authURL;
		this.userURL = userURL;
		this.userAccountIdentifier = userAccountIdentifier;
		this.userAccountName = userAccountName;
		this.admins = admins;
		this.state = state;
	}
	
	def random() {
		
	}
}