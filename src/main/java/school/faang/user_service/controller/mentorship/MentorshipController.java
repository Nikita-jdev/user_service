package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.exception.mentorship.InvalidRequestMentorId;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.validation.mentorship.RequestValidation;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("mentees/{id}")
    public List<UserDTO> getMentees(@PathVariable("id") long mentorId) {
        RequestValidation.checkValidityId(mentorId);
        return mentorshipService.getMentees(mentorId);
    }
}
