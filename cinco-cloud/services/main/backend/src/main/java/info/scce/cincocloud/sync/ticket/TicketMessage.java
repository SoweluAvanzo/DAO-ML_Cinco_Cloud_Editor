package info.scce.cincocloud.sync.ticket;

import com.fasterxml.jackson.annotation.JsonFilter;

/**
 * Author mitwalli
 */
@JsonFilter("CincoCloud_Selective_Filter")
public class TicketMessage {

  private String ticket;

  public String getTicket() {
    return this.ticket;
  }

  public void setTicket(final String ticket) {
    this.ticket = ticket;
  }
}
