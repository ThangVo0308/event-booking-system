package event_booking_system.demo.services;

import event_booking_system.demo.entities.Event;
import event_booking_system.demo.entities.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Event createEvent(Event event);

    Event updateEvent(Event event);

    void deleteEvent(String id);

    Optional<Event> findEventById(String id);

    List<Event> findAllEvents();

    List<Event> findEventsByName(String eventName);

    List<Event> findEventsByLocation(String location);

    List<Event> findUpcomingEvents(Date startTime);

    List<Event> findPastEvents(Date endTime);

    List<Event> findEventsByUser(User user);

    List<Event> findEventsByLocationAndTimeRange(String location, Date startTime, Date endTime);
}
