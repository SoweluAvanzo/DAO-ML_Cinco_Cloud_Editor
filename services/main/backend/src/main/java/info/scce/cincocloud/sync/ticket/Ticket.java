package info.scce.cincocloud.sync.ticket;

import info.scce.cincocloud.db.PyroUserDB;
import java.util.Calendar;
import java.util.Date;

public class Ticket {

  private final PyroUserDB user;
  private final Date expirationDate;

  public Ticket(PyroUserDB user) {
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

  public PyroUserDB getUser() {
    return user;
  }
}
