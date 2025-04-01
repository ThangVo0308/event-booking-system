package event_booking_system.demo.repositories;

import event_booking_system.demo.entities.Order;
import event_booking_system.demo.enums.OrderStatus;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Observed
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserId(String userId);

    List<Order> findByTicketId(String ticketId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUserIdAndStatus(String userId, OrderStatus status);
}
