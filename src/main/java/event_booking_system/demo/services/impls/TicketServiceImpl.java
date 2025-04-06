package event_booking_system.demo.services.impls;

import event_booking_system.demo.entities.Event;
import event_booking_system.demo.entities.Ticket;
import event_booking_system.demo.enums.TicketType;
import event_booking_system.demo.exceptions.AppException;
import event_booking_system.demo.exceptions.CommonErrorCode;
import event_booking_system.demo.repositories.TicketRepository;
import event_booking_system.demo.services.TicketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TicketServiceImpl implements TicketService {

    TicketRepository ticketRepository;

    @Override
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket updateTicket(Ticket ticket) {
        Ticket existingTicket = ticketRepository.findById(ticket.getId())
                .orElseThrow(() -> new AppException(CommonErrorCode.TICKET_NOT_FOUND, NOT_FOUND));
        existingTicket.setEvent(ticket.getEvent());
        existingTicket.setType(ticket.getType());
        existingTicket.setPrice(ticket.getPrice());
        existingTicket.setTotal_quantity(ticket.getTotal_quantity());
        existingTicket.setAvailable_quantity(ticket.getAvailable_quantity());
        return ticketRepository.save(existingTicket);
    }

    @Override
    public void deleteTicket(String id) {
        if (!ticketRepository.existsById(id)) {
            throw new AppException(CommonErrorCode.TICKET_NOT_FOUND, NOT_FOUND);
        }
        ticketRepository.deleteById(id);
    }

    @Override
    public Optional<Ticket> findTicketById(String id) {
        return Optional.ofNullable(ticketRepository.findById(id)
                .orElseThrow(() -> new AppException(CommonErrorCode.TICKET_NOT_FOUND, NOT_FOUND)));
    }

    @Override
    public List<Ticket> findAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> findTicketsByEvent(Event event) {
        return ticketRepository.findByEventId(event.getId());
    }

    @Override
    public List<Ticket> findTicketsByType(TicketType type) {
        return ticketRepository.findByType(type);
    }

    @Override
    public List<Ticket> findAvailableTicketsByEvent(Event event) {
        return ticketRepository.findAvailableTicketByEventId(event.getId());
    }

    @Override
    public List<Ticket> findTicketsByPrice(Double price) {
        return ticketRepository.findByPrice(price);
    }
}
