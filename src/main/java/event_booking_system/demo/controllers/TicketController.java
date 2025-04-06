package event_booking_system.demo.controllers;

import event_booking_system.demo.components.Translator;
import event_booking_system.demo.dtos.requests.tickets.TicketRequest;
import event_booking_system.demo.dtos.responses.paging.Pagination;
import event_booking_system.demo.dtos.responses.paging.PaginationResponse;
import event_booking_system.demo.dtos.responses.ticket.TicketResponse;
import event_booking_system.demo.entities.Event;
import event_booking_system.demo.entities.Ticket;
import event_booking_system.demo.enums.TicketType;
import event_booking_system.demo.exceptions.AppException;
import event_booking_system.demo.exceptions.CommonErrorCode;
import event_booking_system.demo.exceptions.authenication.AuthenticationException;
import event_booking_system.demo.mappers.TicketMapper;
import event_booking_system.demo.services.EventService;
import event_booking_system.demo.services.TicketService;
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
@RequestMapping("/tickets")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Ticket APIs")
public class TicketController {

    TicketService ticketService;
    EventService eventService;
    Translator translator;
    TicketMapper ticketMapper = TicketMapper.INSTANCE;

    @Operation(summary = "Get all tickets", description = "Retrieve all tickets with pagination")
    @GetMapping
    public ResponseEntity<PaginationResponse<TicketResponse>> findAll(
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Ticket> tickets = ticketService.findAllTickets();
        Page<Ticket> ticketPage = new PageImpl<>(tickets, pageable, tickets.size());

        List<TicketResponse> ticketResponses = ticketPage.getContent().stream()
                .map(ticketMapper::toTicketResponse)
                .toList();

        PaginationResponse<TicketResponse> response = PaginationResponse.<TicketResponse>builder()
                .items(ticketResponses)
                .pagination(new Pagination(page, size, ticketPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Create a new ticket", description = "Create a new ticket associated with an event")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> create(@RequestBody @Valid TicketRequest request) {
        Event event = eventService.findEventById(request.eventId())
                .orElseThrow(() -> new AppException(CommonErrorCode.EVENT_NOT_FOUND, HttpStatus.NOT_FOUND));

        Ticket ticket = ticketMapper.toTicket(request);
        ticket.setEvent(event);

        Ticket savedTicket = ticketService.createTicket(ticket);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketMapper.toTicketResponse(savedTicket));
    }

    @Operation(summary = "Update an existing ticket", description = "Update the details of an existing ticket")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> update(@PathVariable String id, @RequestBody @Valid TicketRequest request) {
        Event event = eventService.findEventById(request.eventId())
                .orElseThrow(() -> new AppException(CommonErrorCode.EVENT_NOT_FOUND, HttpStatus.NOT_FOUND));

        Ticket ticket = ticketMapper.toTicket(request);
        ticket.setId(id);
        ticket.setEvent(event);

        Ticket updatedTicket = ticketService.updateTicket(ticket);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ticketMapper.toTicketResponse(updatedTicket));
    }

    @Operation(summary = "Delete a ticket", description = "Delete a ticket by its ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Get tickets by event", description = "Retrieve all tickets associated with a specific event")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<PaginationResponse<TicketResponse>> findTicketsByEvent(
            @PathVariable String eventId,
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        Event event = eventService.findEventById(eventId)
                .orElseThrow(() -> new AppException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));

        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Ticket> tickets = ticketService.findTicketsByEvent(event);
        Page<Ticket> ticketPage = new PageImpl<>(tickets, pageable, tickets.size());

        List<TicketResponse> ticketResponses = ticketPage.getContent().stream()
                .map(ticketMapper::toTicketResponse)
                .toList();

        PaginationResponse<TicketResponse> response = PaginationResponse.<TicketResponse>builder()
                .items(ticketResponses)
                .pagination(new Pagination(page, size, ticketPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Get tickets by type", description = "Retrieve tickets of a specific type")
    @GetMapping("/type/{type}")
    public ResponseEntity<PaginationResponse<TicketResponse>> findTicketsByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        TicketType ticketType;
        try {
            ticketType = TicketType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException("INVALID_TICKET_TYPE", HttpStatus.BAD_REQUEST);
        }

        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Ticket> tickets = ticketService.findTicketsByType(ticketType);
        Page<Ticket> ticketPage = new PageImpl<>(tickets, pageable, tickets.size());

        List<TicketResponse> ticketResponses = ticketPage.getContent().stream()
                .map(ticketMapper::toTicketResponse)
                .toList();

        PaginationResponse<TicketResponse> response = PaginationResponse.<TicketResponse>builder()
                .items(ticketResponses)
                .pagination(new Pagination(page, size, ticketPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Get available tickets by event", description = "Retrieve available tickets for a specific event")
    @GetMapping("/available/event/{eventId}")
    public ResponseEntity<PaginationResponse<TicketResponse>> findAvailableTicketsByEvent(
            @PathVariable String eventId,
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        Event event = eventService.findEventById(eventId)
                .orElseThrow(() -> new AppException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));

        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Ticket> tickets = ticketService.findAvailableTicketsByEvent(event);
        Page<Ticket> ticketPage = new PageImpl<>(tickets, pageable, tickets.size());

        List<TicketResponse> ticketResponses = ticketPage.getContent().stream()
                .map(ticketMapper::toTicketResponse)
                .toList();

        PaginationResponse<TicketResponse> response = PaginationResponse.<TicketResponse>builder()
                .items(ticketResponses)
                .pagination(new Pagination(page, size, ticketPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Search tickets by price", description = "Search for tickets based on their price")
    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<TicketResponse>> search(
            @RequestParam(defaultValue = "") String price,
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        Double searchPrice;
        try {
            searchPrice = price.isEmpty() ? null : Double.parseDouble(price);
        } catch (NumberFormatException e) {
            throw new AppException("INVALID_PRICE_FORMAT", HttpStatus.BAD_REQUEST);
        }

        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Ticket> tickets = searchPrice != null ? ticketService.findTicketsByPrice(searchPrice) : ticketService.findAllTickets();
        Page<Ticket> ticketPage = new PageImpl<>(tickets, pageable, tickets.size());

        List<TicketResponse> ticketResponses = ticketPage.getContent().stream()
                .map(ticketMapper::toTicketResponse)
                .toList();

        PaginationResponse<TicketResponse> response = PaginationResponse.<TicketResponse>builder()
                .items(ticketResponses)
                .pagination(new Pagination(page, size, ticketPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}