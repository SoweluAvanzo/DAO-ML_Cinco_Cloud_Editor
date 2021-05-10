package info.scce.cincocloud.sync.ticket;

/**
 * Author mitwalli
 */

@com.fasterxml.jackson.annotation.JsonFilter("PYRO_Selective_Filter")
public class TicketMessage {

    private String ticket;

    @com.fasterxml.jackson.annotation.JsonProperty("ticket")
    public String getTicket() {
        return this.ticket;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("ticket")
    public void setTicket(final String ticket) {
        this.ticket = ticket;
    }
}
