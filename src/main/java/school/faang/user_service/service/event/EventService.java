package school.faang.user_service.service.event;

import school.faang.user_service.dto.EventDto;

public interface EventService {
    void deleteById(long eventId);
    EventDto findById(long eventId);
}