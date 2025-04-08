package event_booking_system.demo.services.impls;

import event_booking_system.demo.entities.Order;
import event_booking_system.demo.entities.Ticket;
import event_booking_system.demo.entities.User;
import event_booking_system.demo.enums.OrderStatus;
import event_booking_system.demo.exceptions.AppException;
import event_booking_system.demo.repositories.OrderRepository;
import event_booking_system.demo.repositories.PaymentRepository;
import event_booking_system.demo.repositories.TicketRepository;
import event_booking_system.demo.services.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static event_booking_system.demo.exceptions.CommonErrorCode.INSUFFICIENT_TICKETS;
import static event_booking_system.demo.exceptions.CommonErrorCode.ORDER_NOT_FOUND;
import static event_booking_system.demo.exceptions.CommonErrorCode.TICKET_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;
    TicketRepository ticketRepository;
    PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Order createOrder(Order order) {
        Order newOrder = Order.builder()
                .user(order.getUser())
                .status(OrderStatus.PENDING)
                .updatedAt(new Date())
                .build();

        return orderRepository.save(newOrder);
    }

    @Override
    public Order updateOrder(Order order) {
        Order existingOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND, NOT_FOUND));
        existingOrder.setUser(order.getUser());
        existingOrder.setStatus(order.getStatus());
        return orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new AppException(ORDER_NOT_FOUND, NOT_FOUND);
        }
        orderRepository.deleteById(id);
    }

    @Override
    public Order findOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND, NOT_FOUND));
    }

    @Override
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findOrdersByUser(User user) {
        return orderRepository.findByUserId(user.getId());
    }

    @Override
    public List<Order> findOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Order> findOrdersByUserAndStatus(User user, OrderStatus status) {
        return orderRepository.findByUserIdAndStatus(user.getId(), status);
    }
}