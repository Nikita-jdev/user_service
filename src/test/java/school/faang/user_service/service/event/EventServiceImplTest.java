package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    private static final long EVENT_ID = 1L;

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @InjectMocks
    private EventServiceImpl eventService;
    private Event event;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        event = new Event();
        eventDto = new EventDto();
        event.setId(EVENT_ID);
        eventDto.setId(EVENT_ID);
    }

    @Test
    void whenDeleteByIdAndIdNotExistThenThrowException() {
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());
        Assert.assertThrows(EntityNotFoundException.class,
                () -> eventService.deleteById(EVENT_ID));
    }

    @Test
    void whenDeleteByIdSuccessfully() {
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        EventDto actual = eventService.findById(EVENT_ID);
        assertThat(actual).isEqualTo(eventDto);
    }

    @Test
    void whenFindByIdAndIdNotExistThenThrowException() {
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());
        Assert.assertThrows(EntityNotFoundException.class,
                () -> eventService.findById(EVENT_ID));
    }

    @Test
    public void whenFindByIdThenReturnEventDto() {
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        EventDto actual = eventService.findById(EVENT_ID);
        assertThat(actual).isEqualTo(eventDto);
    }
}