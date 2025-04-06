package event_booking_system.demo.services;

import event_booking_system.demo.entities.Event;
import event_booking_system.demo.entities.Media;
import event_booking_system.demo.enums.MediaType;

import java.util.List;
import java.util.Optional;

public interface MediaService {

    Media createMedia(Media Media);

    Media updateMedia(Media Media);

    void deleteMedia(String id);

    Media findMediaById(String id);

    List<Media> findAllMedia();

    List<Media> findMediaByEvent(Event event);

    List<Media> findMediaByType(MediaType type);

    List<Media> findMediaByEventAndType(Event event, MediaType type);
}