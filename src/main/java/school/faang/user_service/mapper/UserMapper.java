package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {GoalMapper.class})
public interface UserMapper {
    @Mapping(source = "mentors", target = "mentorIds", qualifiedByName = "mapUsersToUserIds")
    @Mapping(source = "mentees", target = "menteeIds", qualifiedByName = "mapUsersToUserIds")
    UserDto toDto(User user);

    @Mapping(source = "mentorIds", target = "mentors", qualifiedByName = "mapUserIdsToUserDtos")
    @Mapping(source = "menteeIds", target = "mentees", qualifiedByName = "mapUserIdsToUserDtos")
    User toEntity(UserDto userDto);

    List<UserDto> toDTOList(List<User> userList);

    @Named("mapUsersToUserIds")
    default List<Long> mapUsersToUserIds(List<UserDto> userDtos) {
        return userDtos.stream()
                .map(UserDto::getId)
                .toList();
    }

    @Named("mapUserIdsToUserDtos")
    default List<User> mapUserIdsToUserDtos(List<Long> userIds) {
        return userIds.stream()
                .map(userId -> {
                    User user = new User();
                    user.setId(userId);
                    return user;
                })
                .toList();
    }
}