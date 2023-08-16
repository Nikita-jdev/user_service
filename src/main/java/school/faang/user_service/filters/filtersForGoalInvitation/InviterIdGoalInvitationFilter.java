package school.faang.user_service.filters.filtersForGoalInvitation;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public class InviterIdGoalInvitationFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filterDto) {
        return filterDto.getInviterId() > 0;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitationStream, InvitationFilterDto filterDto) {
        return goalInvitationStream.filter(goalInvt -> goalInvt.getInviter().getId() == filterDto.getInviterId().longValue());
    }

}
