package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public void deleteById(long eventId) {
        findEventById(eventId);
        eventRepository.deleteById(eventId);
    }

    @Override
    public EventDto findById(long eventId) {
        return eventMapper.toDto(findEventById(eventId));
    }

    private Event findEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("event with id=%d not found", eventId)));
    }
}