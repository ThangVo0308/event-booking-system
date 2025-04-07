package event_booking_system.demo.services.impls;

import event_booking_system.demo.entities.OrderItem;
import event_booking_system.demo.exceptions.AppException;
import event_booking_system.demo.exceptions.CommonErrorCode;
import event_booking_system.demo.repositories.OrderItemRepository;
import event_booking_system.demo.services.OrderItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderItemImpl implements OrderItemService {
    OrderItemRepository orderItemRepository;

    @Override
    public OrderItem findById(String id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new AppException(CommonErrorCode.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND, "Order Item"));
    }

    @Override
    public OrderItem create(OrderItem item) {
        OrderItem createOrderItem = orderItemRepository.save(item);
        createOrderItem.setCreatedAt(new Date());
        return orderItemRepository.save(createOrderItem);
    }

    @Override
    public List<OrderItem> findByEventId(String eventId) {
        return orderItemRepository.findByEventId(eventId);
    }

    @Override
    public Page<OrderItem> findAll(int offset, int limit) {
        return orderItemRepository.findAll(PageRequest.of(offset, limit, Sort.by("createdAt").descending()));
    }
}
