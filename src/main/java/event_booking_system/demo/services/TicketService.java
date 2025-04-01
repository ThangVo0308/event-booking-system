package event_booking_system.demo.services;

import event_booking_system.demo.entities.Event;
import event_booking_system.demo.entities.Ticket;
import event_booking_system.demo.enums.TicketType;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    Ticket createTicket(Ticket ticket);

    Ticket updateTicket(Ticket ticket);

    void deleteTicket(String id);

    Optional<Ticket> findTicketById(String id);

    List<Ticket> findAllTickets();

    List<Ticket> findTicketsByEvent(Event event);

    List<Ticket> findTicketsByType(TicketType type);

    List<Ticket> findAvailableTicketsByEvent(Event event);

    List<Ticket> findTicketsByPrice(Double price);
}
