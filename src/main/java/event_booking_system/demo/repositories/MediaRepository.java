package event_booking_system.demo.repositories;

import event_booking_system.demo.enums.MediaType;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.print.attribute.standard.Media;
import java.util.List;
import java.util.Optional;

@Repository
@Observed
public interface MediaRepository extends JpaRepository<Media, String> {
    List<Media> findByEventId(String eventId);

    List<Media> findByMediaType(MediaType type);

    List<Media> findByEventIdAndMediaType(String eventId, String mediaType);

    Optional<Media> findByMediaUrl(String mediaUrl);
}
