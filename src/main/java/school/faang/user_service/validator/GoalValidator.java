package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

/**
 * @author Ilia Chuvatkin
 */

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private static final int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;


    public Boolean isValidateByActiveGoals(Long userId) {
        if (goalRepository.countActiveGoalsPerUser(userId) <= MAX_ACTIVE_GOALS) {
            return true;
        }
        throw new DataValidationException("Too many active goals!");
    }

    public Boolean isValidateByExistingSkills(Long userId, Goal goal) {
        if (skillRepository.findAllByUserId(userId).containsAll(goal.getSkillsToAchieve())) {
            return true;
        }
        throw new DataValidationException("Not enough skills for the goal!");
    }

    public Boolean isValidateByEmptyTitle(GoalDto goal) {
        if (!goal.getTitle().trim().isEmpty()) {
            return true;
        }
        throw new DataValidationException("Title is empty!");
    }

    public void validateTitle(GoalDto goal) {
        String title = goal.getTitle();
        if (title == null || title.isBlank()) {
            throw new DataValidationException("Title is empty!");
        }
    }

    public void validateByExistingSkills(Goal goal) {
        if (!goal.getSkillsToAchieve().stream().allMatch(s -> skillRepository.existsByTitle(s.getTitle()))) {
            throw new DataValidationException("Some skills do not exist in database!");
        }
    }

    public void validateByCompleted(Goal goal) {
        if (goal.getStatus() != GoalStatus.ACTIVE) {
            throw new DataValidationException("Goal was completed!");
        }
    }

    public void validateUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id " + userId + " is not exists");
        }
    }
}
