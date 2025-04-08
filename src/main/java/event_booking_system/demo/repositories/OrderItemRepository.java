package event_booking_system.demo.repositories;

import event_booking_system.demo.entities.OrderItem;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Observed
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    List<OrderItem> findByTicketId(String ticketId);

    List<OrderItem> findByOrderId(String orderId);
}
