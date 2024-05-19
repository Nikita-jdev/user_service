package school.faang.user_service.service.mentorhip;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {
    private final UserService userService;
    private final GoalService goalService;

    @Override
    public List<UserDto> getMentees(long mentorId) {
        UserDto mentor = userService.findById(mentorId);
        return mentor.getMenteeIds().stream()
                .map(userService::findById)
                .toList();
    }

    @Override
    public void stopMentorship(long mentorId) {
       List<UserDto> mentees = getMentees(mentorId);
        mentees.forEach(mentee -> {
           List<Long> mentorIds = mentee.getMentorIds().stream()
                   .filter(idOfMentor -> idOfMentor != mentorId)
                   .toList();
           mentee.setMentorIds(mentorIds);
           List<Long> goalIds = goalService.findGoalsByUserId(mentee.getId()).stream()
                   .map(goalDto -> {
                       if (goalDto.getMentorId() == mentorId) {
                           goalDto.setMentorId(mentee.getId());
                       }
                       return goalDto.getId();
                   })
                   .toList();
           mentee.setGoalIds(goalIds);
           userService.update(mentee);
       });
    }
}
