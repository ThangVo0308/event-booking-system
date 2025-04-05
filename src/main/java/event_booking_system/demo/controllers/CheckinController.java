package event_booking_system.demo.controllers;

import event_booking_system.demo.components.Translator;
import event_booking_system.demo.dtos.requests.checkins.CheckinRequest;
import event_booking_system.demo.dtos.responses.checkin.CheckinResponse;
import event_booking_system.demo.dtos.responses.paging.Pagination;
import event_booking_system.demo.dtos.responses.paging.PaginationResponse;
import event_booking_system.demo.entities.Checkin;
import event_booking_system.demo.entities.Order;
import event_booking_system.demo.mappers.CheckinMapper;
import event_booking_system.demo.services.CheckinService;
import event_booking_system.demo.services.OrderService;

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
import java.util.Objects;

@RestController
@RequestMapping("/checkins")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CheckinController {

    CheckinService checkinService;
    OrderService orderService;
    Translator translator;
    CheckinMapper checkinMapper = CheckinMapper.INSTANCE;

    @GetMapping
    public ResponseEntity<PaginationResponse<CheckinResponse>> findAll(
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Checkin> checkins = checkinService.findAllCheckins();
        Page<Checkin> checkinPage = new PageImpl<>(checkins, pageable, checkins.size());

        List<CheckinResponse> checkinResponses = checkinPage.getContent().stream()
                .map(checkinMapper::toCheckinResponse)
                .toList();

        PaginationResponse<CheckinResponse> response = PaginationResponse.<CheckinResponse>builder()
                .items(checkinResponses)
                .pagination(new Pagination(page, size, checkinPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CheckinResponse> create(@RequestBody @Valid CheckinRequest request) {
        Order order = orderService.findOrderById(request.orderId());

        Checkin checkin = checkinMapper.toCheckin(request);
        checkin.setOrder(order);

        Checkin savedCheckin = checkinService.createCheckin(checkin);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(checkinMapper.toCheckinResponse(savedCheckin));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CheckinResponse> update(@PathVariable String id, @RequestBody @Valid CheckinRequest request) {
        Order order = orderService.findOrderById(request.orderId());

        Checkin checkin = checkinMapper.toCheckin(request);
        checkin.setId(id);
        checkin.setOrder(order);

        Checkin updatedCheckin = checkinService.updateCheckin(checkin);
        return ResponseEntity.status(HttpStatus.OK)
                .body(checkinMapper.toCheckinResponse(updatedCheckin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        checkinService.deleteCheckin(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<CheckinResponse>> search(
            @RequestParam(defaultValue = "") String qrCode,
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Checkin> checkins = qrCode.isEmpty() ? checkinService.findAllCheckins() : List.of(Objects.requireNonNull(checkinService.findCheckinByQrCode(qrCode).orElse(null)));
        Page<Checkin> checkinPage = new PageImpl<>(checkins, pageable, checkins.size());

        List<CheckinResponse> checkinResponses = checkinPage.getContent().stream()
                .map(checkinMapper::toCheckinResponse)
                .toList();

        PaginationResponse<CheckinResponse> response = PaginationResponse.<CheckinResponse>builder()
                .items(checkinResponses)
                .pagination(new Pagination(page, size, checkinPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}