package event_booking_system.demo.services.impls;

import event_booking_system.demo.entities.OrderItem;
import event_booking_system.demo.entities.Ticket;
import event_booking_system.demo.exceptions.AppException;
import event_booking_system.demo.exceptions.CommonErrorCode;
import event_booking_system.demo.repositories.OrderItemRepository;
import event_booking_system.demo.repositories.TicketRepository;
import event_booking_system.demo.services.OrderItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static event_booking_system.demo.exceptions.CommonErrorCode.INSUFFICIENT_TICKETS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemServiceImpl implements OrderItemService {
    OrderItemRepository orderItemRepository;
    TicketRepository ticketRepository;

    @Override
    public OrderItem findById(String id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new AppException(CommonErrorCode.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND, "Order Item"));
    }

    @Override
    public OrderItem create(OrderItem item) {
        if (item.getOrder() == null) throw new AppException(CommonErrorCode.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);

        if (item.getTicket() == null) throw new AppException(CommonErrorCode.TICKET_NOT_FOUND, HttpStatus.NOT_FOUND);

        item.setPrice(item.getTicket().getPrice() * item.getQuantity());
        ticketRepository.save(item.getTicket());
        item.setOrderTime(new Date());

        return orderItemRepository.save(item);
    }

    @Override
    public List<OrderItem> findByTicketId(String ticketId) {
        return orderItemRepository.findByTicketId(ticketId);
    }

    @Override
    public List<OrderItem> findByOrderId(String orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Override
    public Page<OrderItem> findAll(int offset, int limit) {
        return orderItemRepository.findAll(PageRequest.of(offset, limit, Sort.by("createdAt").descending()));
    }
}
