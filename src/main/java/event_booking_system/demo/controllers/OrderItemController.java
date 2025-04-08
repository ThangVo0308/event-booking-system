package event_booking_system.demo.controllers;

import event_booking_system.demo.dtos.requests.orders.OrderItemRequest;
import event_booking_system.demo.dtos.responses.order.OrderItemResponse;
import event_booking_system.demo.entities.Event;
import event_booking_system.demo.entities.Order;
import event_booking_system.demo.entities.OrderItem;
import event_booking_system.demo.entities.Ticket;
import event_booking_system.demo.mappers.OrderItemMapper;
import event_booking_system.demo.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/order-items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Order Item APIs")
public class OrderItemController {
    OrderItemService orderItemService;
    OrderService orderService;
    TicketService ticketService;
    EventService eventService;
    UserService userService;

    OrderItemMapper orderItemMapper = OrderItemMapper.INSTANCE;

    @Operation(summary = "Add Order item", description = "Create order item with order ID and event ID", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<OrderItemResponse> createOrderItem(@RequestBody OrderItemRequest orderItemRequest)
            throws ParseException {
        Order order = orderService.findOrderById(orderItemRequest.orderId());
        Ticket ticket = ticketService.findTicketById(orderItemRequest.ticketId());

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .ticket(ticket)
                .quantity(orderItemRequest.quantity())
                .price(ticket.getPrice())
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(orderItemMapper.toOrderItemResponse(orderItemService.create(orderItem)));
    }

    @Operation(summary = "Get Order Items by Ticket ID", description = "Retrieve all order items associated with a specific Ticket ID", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<OrderItemResponse>> getOrderItemsByTicketId(
            @PathVariable String ticketId) {
        List<OrderItem> orderItems = orderItemService.findByTicketId(ticketId);

        List<OrderItemResponse> responses = orderItems.stream()
                .map(orderItemMapper::toOrderItemResponse)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
