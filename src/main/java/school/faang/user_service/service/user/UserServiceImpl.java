package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorhip.MentorshipService;
import school.faang.user_service.service.profile_picture.ProfilePictureService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfilePictureService profilePictureService;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;

    @Override
    public boolean existsById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(String.format("user with id: %d is not exists", userId));
        }
        return true;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        profilePictureService.assignPictureToUser(user);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public void deactivateUser(long userId) {
        User user = findUserById(userId);
        user.setActive(false);
        List<Goal> goals = user.getGoals();
        goals.forEach(goal -> {
            if (goal.getUsers().size() == 1) {
                goalService.deleteById(goal.getId());
            }
        });
        List<Event> events = user.getOwnedEvents();
        for (Event event : events) {
            if (event.getStatus() == EventStatus.PLANNED) {
                eventService.deleteById(event.getId());
            }
        }
        mentorshipService.stopMentorship(userId);
    }

    @Override
    public UserDto findById(long userId) {
        return userMapper.toDto(findUserById(userId));
    }

    @Override
    public UserDto update(UserDto userDto) {
        User updatedUser = findUserById(userDto.getId());
        updatedUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(updatedUser);
        return userDto;
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("user with id=%d not found", userId)));
    }
}