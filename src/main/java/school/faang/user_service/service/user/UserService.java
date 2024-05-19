package school.faang.user_service.service.user;

import school.faang.user_service.dto.UserDto;

public interface UserService {
    boolean existsById(long userId);
    UserDto createUser(UserDto userDto);
    void deactivateUser(long userId);
    UserDto findById(long userId);
    UserDto update(UserDto userDto);
}