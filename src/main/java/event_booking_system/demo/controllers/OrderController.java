package event_booking_system.demo.controllers;

import event_booking_system.demo.components.Translator;
import event_booking_system.demo.dtos.requests.orders.OrderRequest;
import event_booking_system.demo.dtos.responses.order.OrderResponse;
import event_booking_system.demo.dtos.responses.paging.Pagination;
import event_booking_system.demo.dtos.responses.paging.PaginationResponse;
import event_booking_system.demo.entities.Order;
import event_booking_system.demo.entities.Ticket;
import event_booking_system.demo.entities.User;
import event_booking_system.demo.enums.OrderStatus;
import event_booking_system.demo.exceptions.AppException;
import event_booking_system.demo.exceptions.CommonErrorCode;
import event_booking_system.demo.mappers.OrderMapper;
import event_booking_system.demo.services.OrderService;
import event_booking_system.demo.services.TicketService;
import event_booking_system.demo.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Order APIs")

public class OrderController {

    OrderService orderService;
    UserService userService;
    TicketService ticketService;
    Translator translator;
    OrderMapper orderMapper = OrderMapper.INSTANCE;
    @Operation(summary = "Get all orders", description = "Retrieve all orders with pagination")
    @GetMapping
    public ResponseEntity<PaginationResponse<OrderResponse>> findAll(
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Order> orders = orderService.findAllOrders();
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(orderMapper::toOrderResponse)
                .toList();

        PaginationResponse<OrderResponse> response = PaginationResponse.<OrderResponse>builder()
                .items(orderResponses)
                .pagination(new Pagination(page, size, orderPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Create a new order", description = "Create a new order for a user and ticket")
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid OrderRequest request) {
        User user = userService.findUserById(request.userId());
        Ticket ticket = ticketService.findTicketById(request.ticketId())
                .orElseThrow(() -> new AppException(CommonErrorCode.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND));

        Order order = orderMapper.toOrder(request);
        order.setUser(user);
        order.setTicket(ticket);

        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderMapper.toOrderResponse(savedOrder));
    }

    @Operation(summary = "Update an existing order", description = "Update the details of an existing order")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> update(@PathVariable String id, @RequestBody @Valid OrderRequest request) {
        User user = userService.findUserById(request.userId());
        Ticket ticket = ticketService.findTicketById(request.ticketId())
                .orElseThrow(() -> new AppException("TICKET_NOT_FOUND", HttpStatus.NOT_FOUND));

        Order order = orderMapper.toOrder(request);
        order.setId(id);
        order.setUser(user);
        order.setTicket(ticket);

        Order updatedOrder = orderService.updateOrder(order);
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderMapper.toOrderResponse(updatedOrder));
    }

    @Operation(summary = "Delete an order", description = "Delete an existing order by its ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @Operation(summary = "Get orders by user", description = "Retrieve orders made by a specific user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<PaginationResponse<OrderResponse>> findOrdersByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        User user = userService.findUserById(userId);

        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Order> orders = orderService.findOrdersByUser(user);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(orderMapper::toOrderResponse)
                .toList();

        PaginationResponse<OrderResponse> response = PaginationResponse.<OrderResponse>builder()
                .items(orderResponses)
                .pagination(new Pagination(page, size, orderPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Search orders by status", description = "Search for orders by their status (e.g., pending, completed)")
    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<OrderResponse>> search(
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        OrderStatus orderStatus;
        try {
            orderStatus = status.isEmpty() ? null : OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException("INVALID_ORDER_STATUS", HttpStatus.BAD_REQUEST);
        }

        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Order> orders = orderStatus != null ? orderService.findOrdersByStatus(orderStatus) : orderService.findAllOrders();
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(orderMapper::toOrderResponse)
                .toList();

        PaginationResponse<OrderResponse> response = PaginationResponse.<OrderResponse>builder()
                .items(orderResponses)
                .pagination(new Pagination(page, size, orderPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
