package event_booking_system.demo.services;

import event_booking_system.demo.entities.OrderItem;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderItemService {
    OrderItem findById(String id);

    OrderItem create(OrderItem item);

    List<OrderItem> findByEventId(String eventId);

    Page<OrderItem> findAll(int offset, int limit);

}
