package event_booking_system.demo.repositories;

import event_booking_system.demo.entities.Ticket;
import event_booking_system.demo.enums.TicketType;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Observed
public interface TicketRepository extends JpaRepository<Ticket, String> {

    List<Ticket> findByEventId(String eventId);

    List<Ticket> findByTicketType(TicketType ticketType);

    @Query("SELECT t from ticket WHERE t.available_quantity > 0 AND t.event.id = :eventId")
    List<Ticket> findAvailableTicketByEventId(String eventId);

    List<Ticket> findByPrice(Double price);
}
