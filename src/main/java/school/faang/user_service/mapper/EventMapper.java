package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface EventMapper {
    void update(@MappingTarget Event entity, EventDto updateEntity);

    Event toEvent(EventDto EventDto);

    @Mapping(target = "ownerId", source = "owner.id")
    EventDto toEventDto(Event Event);
}
