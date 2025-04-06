package event_booking_system.demo.service.impls;

import event_booking_system.demo.entities.Event;
import event_booking_system.demo.entities.User;
import event_booking_system.demo.exceptions.AppException;
import event_booking_system.demo.repositories.EventRepository;
import event_booking_system.demo.services.EventService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static event_booking_system.demo.exceptions.CommonErrorCode.EVENT_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EventServiceImpl implements EventService {

    EventRepository eventRepository;

    @Override
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Event event) {
        Event existingEvent = eventRepository.findById(event.getId())
                .orElseThrow(() -> new AppException(EVENT_NOT_FOUND, NOT_FOUND));
        existingEvent.setEvent_name(event.getEvent_name());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setLocation(event.getLocation());
        existingEvent.setStartTime(event.getStartTime());
        existingEvent.setEndTime(event.getEndTime());
        existingEvent.setUser(event.getUser());
        return eventRepository.save(existingEvent);
    }

    @Override
    public void deleteEvent(String id) {
        if (!eventRepository.existsById(id)) {
            throw new AppException(EVENT_NOT_FOUND, NOT_FOUND);
        }
        eventRepository.deleteById(id);
    }

    @Override
    public Optional<Event> findEventById(String id) {
        return Optional.ofNullable(eventRepository.findById(id)
                .orElseThrow(() -> new AppException(EVENT_NOT_FOUND, NOT_FOUND)));
    }

    @Override
    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> findEventsByName(String eventName) {
        return eventRepository.findByEventNameContainingIgnoreCase(eventName);
    }

    @Override
    public List<Event> findEventsByLocation(String location) {
        return eventRepository.findByLocation(location);
    }

    @Override
    public List<Event> findUpcomingEvents(Date startTime) {
        return eventRepository.findByStartTimeAfter(startTime);
    }

    @Override
    public List<Event> findPastEvents(Date endTime) {
        return eventRepository.findByEndTimeBefore(endTime);
    }

    @Override
    public List<Event> findEventsByUser(User user) {
        return eventRepository.findByUserId(user.getId());
    }

    @Override
    public List<Event> findEventsByLocationAndTimeRange(String location, Date startTime, Date endTime) {
        return eventRepository.findByLocationAndTimeRange(location, startTime, endTime);
    }
}