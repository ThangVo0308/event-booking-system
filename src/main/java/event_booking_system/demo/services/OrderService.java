package event_booking_system.demo.services;

import event_booking_system.demo.entities.Order;
import event_booking_system.demo.entities.Ticket;
import event_booking_system.demo.entities.User;
import event_booking_system.demo.enums.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    Order createOrder(Order order);

    Order updateOrder(Order order);

    void deleteOrder(String id);

    Order findOrderById(String id);

    List<Order> findAllOrders();

    List<Order> findOrdersByUser(User user);

    List<Order> findOrdersByTicket(Ticket ticket);

    List<Order> findOrdersByStatus(OrderStatus status);

    List<Order> findOrdersByUserAndStatus(User user, OrderStatus status);
}