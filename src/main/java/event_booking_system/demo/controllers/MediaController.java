package event_booking_system.demo.controllers;

import event_booking_system.demo.dtos.responses.paging.Pagination;
import event_booking_system.demo.dtos.responses.paging.PaginationResponse;
import event_booking_system.demo.dtos.responses.media.MediaResponse;
import event_booking_system.demo.entities.Event;
import event_booking_system.demo.entities.Media;
import event_booking_system.demo.enums.MediaType;
import event_booking_system.demo.exceptions.AppException;
import event_booking_system.demo.mappers.MediaMapper;
import event_booking_system.demo.services.EventService;
import event_booking_system.demo.services.MediaService;
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
@RequestMapping("/media")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Media APIs")
public class MediaController {

    MediaService mediaService;
    EventService eventService;
    MediaMapper mediaMapper = MediaMapper.INSTANCE;

    @GetMapping
    @Operation(summary = "Get all media", description = "Retrieve all media entries with pagination")
    public ResponseEntity<PaginationResponse<MediaResponse>> findAll(
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Media> mediaList = mediaService.findAllMedia();
        Page<Media> mediaPage = new PageImpl<>(mediaList, pageable, mediaList.size());

        List<MediaResponse> mediaResponses = mediaPage.getContent().stream()
                .map(mediaMapper::toMediaResponse)
                .toList();

        PaginationResponse<MediaResponse> response = PaginationResponse.<MediaResponse>builder()
                .items(mediaResponses)
                .pagination(new Pagination(page, size, mediaPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Create a new media", description = "Create a new media entry")
//    public ResponseEntity<MediaResponse> create(@RequestBody @Valid MediaRequest request) {
//        Event event = eventService.findEventById(request.getEventId())
//                .orElseThrow(() -> new AppException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));
//
//        Media media = mediaMapper.toMedia(request);
//        media.setEvent(event);
//
//        Media savedMedia = mediaService.createMedia(media);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(mediaMapper.toMediaResponse(savedMedia));
//    }

//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Update a media entry", description = "Update details of an existing media entry")
//    public ResponseEntity<MediaResponse> update(@PathVariable String id, @RequestBody @Valid MediaRequest request) {
//        Event event = eventService.findEventById(request.getEventId())
//                .orElseThrow(() -> new AppException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));
//
//        Media media = mediaMapper.toMedia(request);
//        media.setId(id);
//        media.setEvent(event);
//
//        Media updatedMedia = mediaService.updateMedia(media);
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(mediaMapper.toMediaResponse(updatedMedia));
//    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a media entry", description = "Delete an existing media entry by its ID")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get media by event", description = "Retrieve media entries associated with a specific event")
    public ResponseEntity<PaginationResponse<MediaResponse>> findMediaByEvent(
            @PathVariable String eventId,
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        Event event = eventService.findEventById(eventId)
                .orElseThrow(() -> new AppException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));

        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Media> mediaList = mediaService.findMediaByEvent(event);
        Page<Media> mediaPage = new PageImpl<>(mediaList, pageable, mediaList.size());

        List<MediaResponse> mediaResponses = mediaPage.getContent().stream()
                .map(mediaMapper::toMediaResponse)
                .toList();

        PaginationResponse<MediaResponse> response = PaginationResponse.<MediaResponse>builder()
                .items(mediaResponses)
                .pagination(new Pagination(page, size, mediaPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get media by type", description = "Retrieve media entries of a specific type")
    public ResponseEntity<PaginationResponse<MediaResponse>> findMediaByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        MediaType mediaType;
        try {
            mediaType = MediaType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException("INVALID_MEDIA_TYPE", HttpStatus.BAD_REQUEST);
        }

        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Media> mediaList = mediaService.findMediaByType(mediaType);
        Page<Media> mediaPage = new PageImpl<>(mediaList, pageable, mediaList.size());

        List<MediaResponse> mediaResponses = mediaPage.getContent().stream()
                .map(mediaMapper::toMediaResponse)
                .toList();

        PaginationResponse<MediaResponse> response = PaginationResponse.<MediaResponse>builder()
                .items(mediaResponses)
                .pagination(new Pagination(page, size, mediaPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/event/{eventId}/type/{type}")
    @Operation(summary = "Get media by event and type", description = "Retrieve media entries by event and media type")
    public ResponseEntity<PaginationResponse<MediaResponse>> findMediaByEventAndType(
            @PathVariable String eventId,
            @PathVariable String type,
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        Event event = eventService.findEventById(eventId)
                .orElseThrow(() -> new AppException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));

        MediaType mediaType;
        try {
            mediaType = MediaType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException("INVALID_MEDIA_TYPE", HttpStatus.BAD_REQUEST);
        }

        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Media> mediaList = mediaService.findMediaByEventAndType(event, mediaType);
        Page<Media> mediaPage = new PageImpl<>(mediaList, pageable, mediaList.size());

        List<MediaResponse> mediaResponses = mediaPage.getContent().stream()
                .map(mediaMapper::toMediaResponse)
                .toList();

        PaginationResponse<MediaResponse> response = PaginationResponse.<MediaResponse>builder()
                .items(mediaResponses)
                .pagination(new Pagination(page, size, mediaPage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
