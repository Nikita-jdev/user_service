package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exeptions.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private GoalMapper goalMapper;
    @Mock
    private GoalValidator validator;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private GoalService service;

    long userId = 1;
    GoalDto goalDto = new GoalDto();

    @Test
    void deleteGoalValidationTest() {
        when(goalRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.deleteGoal(anyLong()));

        assertEquals("Goal does not exist", exception.getMessage());

        verify(goalRepository, times(0)).deleteById(anyLong());
    }

    @Test
    public void findGoalsByUserIdLessThanOneTest() {
        assertThrows(DataValidationException.class, () -> {
            service.findGoalsByUserId(0L);
        });
    void updateGoalAndCompleteTest() {
        Goal goal = Goal.builder().status(GoalStatus.COMPLETED).build();
        List<Long> skillIds = List.of(1L, 2L);

        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(goalDto.getSkillIds()).thenReturn(skillIds);
        when(goalRepository.findUsersByGoalId(id)).thenReturn(List.of(User.builder().build()));

        goalService.updateGoal(id, goalDto);

        verify(skillRepository, times(2)).assignSkillToUser(anyLong(), anyLong());
    }

    @Test
    void deleteGoalTest(){
        when(goalRepository.existsById(1L)).thenReturn(true);

        service.deleteGoal(1L);

        verify(goalRepository).deleteById(1L);
    }

    void updateGoalTest() {
        Goal goal = Goal.builder().status(GoalStatus.ACTIVE).build();
        List<Long> skillIds = List.of(1L, 2L);

    @Test
    public void getGoalsByUserLessThanOneTest() {
        assertThrows(DataValidationException.class, () -> {
            service.getGoalsByUser(0L, null);
        });
    }
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);

    @Test
    void createGoalTest() {
        service.createGoal(goalDto);
        verify(goalRepository).save(goalMapper.toEntity(goalDto));
        goalService.updateGoal(id, goalDto);

        verify(goalRepository).save(goal);
    }
}