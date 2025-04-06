package event_booking_system.demo.repositories;

import event_booking_system.demo.entities.Event;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Observed
public interface EventRepository extends JpaRepository<Event, String> {
    List<Event> findByLocation(String location);

    List<Event> findByStartTimeAfter(Date startTime); // find events by start time

    List<Event> findByEndTimeBefore(Date endTime); // find events by end time

    List<Event> findByUserId(String userId);

    @Query("SELECT e FROM Event e WHERE e.location = :location and e.startTime BETWEEN :startTime AND :endTime")
    List<Event> findByLocationAndTimeRange(String location, Date startTime, Date endTime);

    @Query("SELECT e FROM Event e WHERE e.event_name LIKE %:eventName%")
    List<Event> findByEventNameContainingIgnoreCase(@Param("eventName") String eventName);
}
