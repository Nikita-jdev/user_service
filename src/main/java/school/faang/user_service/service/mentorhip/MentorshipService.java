package school.faang.user_service.service.mentorhip;

import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface MentorshipService {
    List<UserDto> getMentees(long mentorId);
    void stopMentorship(long mentorId);
}