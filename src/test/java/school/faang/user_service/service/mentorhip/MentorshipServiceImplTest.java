package school.faang.user_service.service.mentorhip;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {
    private static final long MENTOR_ID = 1L;
    private static final long MENTEE_ID = 2L;

    @Mock
    private UserService userService;
    @Mock
    private GoalService goalService;
    @InjectMocks
    private MentorshipServiceImpl mentorshipService;
    private UserDto mentor;
    private UserDto mentee;
    private GoalDto goalDto;
    private List<UserDto> mentees;
    private List<GoalDto> goalDtos;

    @BeforeEach
    void setUp() {
        mentor = new UserDto();
        mentee = new UserDto();
        mentor.setId(MENTOR_ID);
        mentee.setId(MENTEE_ID);
        mentor.setMenteeIds(List.of(MENTEE_ID));
        mentee.setMentorIds(List.of(MENTOR_ID));
        mentees = new ArrayList<>();
        mentees.add(mentee);
        goalDto = new GoalDto();
        goalDto.setMentorId(MENTOR_ID);
        goalDtos = List.of(goalDto);

    }

    @Test
    public void whenGetMenteesAndUserNotFoundThenThrowsException() {
        when(userService.findById(MENTOR_ID)).thenThrow(EntityNotFoundException.class);
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.getMentees(MENTOR_ID));
    }

    @Test
    public void whenGetMenteesThenGetListOfMentees() {
        when(userService.findById(MENTOR_ID )).thenReturn(mentor);
        when(userService.findById(MENTEE_ID)).thenReturn(mentee);
        List<UserDto> actual = mentorshipService.getMentees(MENTOR_ID);
        assertThat(actual).isEqualTo(mentees);
    }

    @Test
    public void whenStopMentorshipSuccessfully() {
        when(userService.findById(MENTOR_ID)).thenReturn(mentor);
        when(userService.findById(MENTEE_ID)).thenReturn(mentee);
        when(goalService.findGoalsByUserId(MENTEE_ID)).thenReturn(goalDtos);
        mentorshipService.stopMentorship(MENTOR_ID);
        verify(userService).update(mentee);
    }
}