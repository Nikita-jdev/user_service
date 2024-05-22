package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class ExperienceMinPatternFilter implements UserFilterStrategy {
    @Override
    public boolean check(User user, UserFilterDto filter) {
        return (filter.getExperienceMin() != null && user.getExperience() < filter.getExperienceMin());
    }
}