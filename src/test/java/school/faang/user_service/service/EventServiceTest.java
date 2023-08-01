package school.faang.user_service.service;


import lombok.Builder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;


import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private EventMapperImpl eventMapper;
    @InjectMocks
    private EventService eventService;

    private User user1 = User.builder()
            .id(1L)
            .skills(
                    List.of(
                            Skill.builder().id(2L).title("2").build(),
                            Skill.builder().id(3L).title("3").build()
                    )
            )
            .build();
    private User user2 = User.builder()
            .id(1L)
            .skills(
                    List.of(
                            Skill.builder().id(1L).title("1").build(),
                            Skill.builder().id(2L).title("2").build()
                    )
            )
            .build();

    private EventDto eventDto = EventDto.builder()
            .relatedSkills(
                    List.of(
                            SkillDto.builder().id(1L).title("1").build(),
                            SkillDto.builder().id(2L).title("2").build()
                    )
            )
            .ownerId(1L)
            .id(1L)
            .build();
    private Event event = Event.builder()
            .relatedSkills(
                    List.of(
                            Skill.builder().id(1L).title("1").build(),
                            Skill.builder().id(2L).title("2").build()
                    )
            )
            .id(1L)
            .owner(user2)
            .build();


    @Test
    public void testOwnerHasNoSkillsForEvent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Assertions.assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void testOwnerHasSkillsForEvent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        eventService.create(eventDto);
        verify(eventRepository, Mockito.times(1)).save(eventMapper.toEvent(eventDto));
    }
}
