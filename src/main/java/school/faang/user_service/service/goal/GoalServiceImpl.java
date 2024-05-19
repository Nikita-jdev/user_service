package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    @Override
    public void deleteById(long goalId) {
        findGoalById(goalId);
        goalRepository.deleteById(goalId);
    }

    @Override
    public GoalDto findById(long goalId) {
        return goalMapper.toDto(findGoalById(goalId));
    }

    @Override
    public List<GoalDto> findGoalsByUserId(long userId) {
        return goalMapper.toDtoList(goalRepository.findGoalsByUserId(userId)
                .toList());
    }


    private Goal findGoalById(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("goal with id=%d not found", goalId)));
    }
}