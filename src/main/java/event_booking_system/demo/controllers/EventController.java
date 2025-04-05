package event_booking_system.demo.controllers;

import event_booking_system.demo.components.Translator;
import event_booking_system.demo.dtos.requests.events.EventRequest;
import event_booking_system.demo.dtos.responses.event.EventResponse;
import event_booking_system.demo.dtos.responses.paging.Pagination;
import event_booking_system.demo.dtos.responses.paging.PaginationResponse;
import event_booking_system.demo.entities.Event;
import event_booking_system.demo.entities.User;
import event_booking_system.demo.mappers.EventMapper;
import event_booking_system.demo.services.EventService;
import event_booking_system.demo.services.UserService;
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

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EventController {

    EventService eventService;
    UserService userService;
    Translator translator;
    EventMapper eventMapper = EventMapper.INSTANCE;

    @GetMapping
    public ResponseEntity<PaginationResponse<EventResponse>> findAll(
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Event> events = eventService.findAllEvents();
        Page<Event> eventPage = new PageImpl<>(events, pageable, events.size());

        List<EventResponse> eventResponses = eventPage.getContent().stream()
                .map(eventMapper::toEventResponse)
                .toList();

        PaginationResponse<EventResponse> response = PaginationResponse.<EventResponse>builder()
                .items(eventResponses)
                .pagination(new Pagination(page, size, eventPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> create(@RequestBody @Valid EventRequest request) {
        User user = userService.findUserById(request.userId());

        Event event = eventMapper.toEvent(request);
        event.setUser(user);

        Event savedEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventMapper.toEventResponse(savedEvent));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> update(@PathVariable String id, @RequestBody @Valid EventRequest request) {
        User user = userService.findUserById(request.userId());

        Event event = eventMapper.toEvent(request);
        event.setId(id);
        event.setUser(user);

        Event updatedEvent = eventService.updateEvent(event);
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventMapper.toEventResponse(updatedEvent));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<EventResponse>> search(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Event> events = name.isEmpty() ? eventService.findAllEvents() : eventService.findEventsByName(name);
        Page<Event> eventPage = new PageImpl<>(events, pageable, events.size());

        List<EventResponse> eventResponses = eventPage.getContent().stream()
                .map(eventMapper::toEventResponse)
                .toList();

        PaginationResponse<EventResponse> response = PaginationResponse.<EventResponse>builder()
                .items(eventResponses)
                .pagination(new Pagination(page, size, eventPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}