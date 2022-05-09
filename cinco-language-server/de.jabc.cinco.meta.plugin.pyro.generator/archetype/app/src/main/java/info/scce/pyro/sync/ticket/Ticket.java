package info.scce.pyro.sync.ticket;

import java.util.Calendar;
import java.util.Date;

public class Ticket {
	private entity.core.PyroUserDB user;
	private Date expirationDate;
	
	public Ticket(entity.core.PyroUserDB user) {
		this.user = user;
		
		// setting expiration date
		Date date = new Date();
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.MINUTE, 60);
	    expirationDate = cal.getTime();
	}
	
	public boolean isValid() {
		Date currentDate = new Date();
		return currentDate.before(expirationDate);
	}
	
	public entity.core.PyroUserDB getUser() {
		return user;
	}
}
