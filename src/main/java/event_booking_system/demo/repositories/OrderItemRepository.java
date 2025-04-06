package event_booking_system.demo.repositories;

import event_booking_system.demo.entities.OrderItem;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Observed
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
}
