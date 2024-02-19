package info.scce.cincocloud.sync.ticket;

import info.scce.cincocloud.db.UserDB;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.inject.Singleton;

/**
 * By: https://stackoverflow.com/questions/39692065/websocket-angular-2-and-json-web-token-authentication
 * <p>
 * Ticket created for one time purpose, like WebSocket-connection. The removeTicket method has to be called if that
 * purpose is fullfilled, like in the onConnect-function of a WebSocket-Server.
 *
 * @author mitwalli
 */
@Singleton
public class TicketRegistrationHandler {

  static HashMap<String, Ticket> registeredTickets = new HashMap<>();

  public static TicketMessage createTicket(UserDB user) {
    cleanUp();
    synchronized (registeredTickets) {
      String newTicket = java.util.UUID.randomUUID().toString();
      while (registeredTickets.containsKey(newTicket)) {
        newTicket = java.util.UUID.randomUUID().toString();
      }
      Ticket ticket = new Ticket(user);
      registeredTickets.put(newTicket, ticket);
      return createTicketMessage(newTicket);
    }
  }

  public static void removeTicket(String ticket) {
    synchronized (registeredTickets) {
      registeredTickets.remove(ticket);
    }
  }

  public static void removeTicketsOf(UserDB user) {
    if (user == null) {
      return;
    }
    synchronized (registeredTickets) {
      // collect all tickets associated with the user
      List<HashMap.Entry<String, Ticket>> toBeRemoved = registeredTickets.entrySet().stream()
          .filter((e) -> e.getValue() != null
              && e.getValue().getUser() != null
              && user.id.equals(e.getValue().getUser().id)).collect(Collectors.toList());
      // remove those tickets
      for (HashMap.Entry<String, Ticket> e : toBeRemoved) {
        registeredTickets.remove(e.getKey());
      }
    }
  }

  public static boolean redeemTicket(String ticket) {
    if (isRegisteredTicket(ticket) != null) {
      removeTicket(ticket);
      return true;
    }
    return false;
  }

  public static UserDB checkGetRelated(String ticketValue) {
    Ticket ticket = isRegisteredTicket(ticketValue);
    if (ticket != null) {
      UserDB user = ticket.getUser();
      removeTicket(ticketValue);
      return user;
    }
    return null;
  }

  public static Ticket isRegisteredTicket(String ticketValue) {
    Ticket ticket = registeredTickets.get(ticketValue);
    if (ticket != null && ticket.isValid()) {
      return ticket;
    }
    if (ticket != null) {
      removeTicket(ticketValue);
    }
    return null;
  }

  private static TicketMessage createTicketMessage(String ticketValue) {
    TicketMessage ticketMessage = new TicketMessage();
    ticketMessage.setTicket(ticketValue);
    return ticketMessage;
  }

  private static void cleanUp() {
    synchronized (registeredTickets) {
      List<String> toRemove = registeredTickets.entrySet().stream()
          .filter(e -> !e.getValue().isValid())
          .map(Map.Entry::getKey).collect(Collectors.toList());
      for (String k : toRemove) {
        registeredTickets.remove(k);
      }
    }
  }
}
