package event_booking_system.demo.services.impls;

import event_booking_system.demo.entities.Event;
import event_booking_system.demo.entities.Media;
import event_booking_system.demo.enums.MediaType;
import event_booking_system.demo.repositories.MediaRepository;
import event_booking_system.demo.services.MediaService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MediaServiceImpl implements MediaService {

    MediaRepository mediaRepository;


    @Override
    public Media createMedia(Media media) { return mediaRepository.save(media); }

    @Override
    public Media updateMedia(Media media) {
        Media existingMedia = mediaRepository.findById(media.getId())
                .orElseThrow(() -> new IllegalArgumentException("Media not found with id: " + media.getId()));

        existingMedia.setType(media.getType());
        existingMedia.setEvent(media.getEvent());

        return mediaRepository.save(existingMedia);
    }

    @Override
    public void deleteMedia(String id) {
        if (!mediaRepository.existsById(id)) {
            throw new IllegalArgumentException("Media not found with id: " + id);
        }
        mediaRepository.deleteById(id);
    }

    @Override
    public Media findMediaById(String id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Media not found with id: " + id));
    }

    @Override
    public List<Media> findAllMedia() {
        return mediaRepository.findAll();
    }

    @Override
    public List<Media> findMediaByEvent(Event event) {
        return mediaRepository.findByEventId(event.getId());
    }

    @Override
    public List<Media> findMediaByType(MediaType type) {
        return mediaRepository.findByType(type);
    }

    @Override
    public List<Media> findMediaByEventAndType(Event event, MediaType type) {
        return mediaRepository.findByEventIdAndType(event.getId(), type);
    }

}
