package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.model.EventType;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.MentorshipStartEvent;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MentorshipStartPublisher extends AbstractEventPublisher {
    @Setter
    @Value("${spring.data.redis.channels.mentorship_channel.name}")
    private String mentorshipChannel;

    @Autowired
    public MentorshipStartPublisher(EventMapper eventMapper, ObjectMapper objectMapper,
                                    RedisMessagePublisher redisMessagePublisher) {
        super(eventMapper, objectMapper, redisMessagePublisher);
    }

    public void publishMentorshipEvent(Long mentorId, Long menteeId) {
        redisMessagePublisher.publish(mentorshipChannel, initEvent(mentorId, menteeId));
        log.info("The mentorship start event has been sent. Mentor = {}, Mentee = {}", mentorId, menteeId);
    }

    private MentorshipStartEvent initEvent(Long mentorId, Long menteeId) {
        return MentorshipStartEvent.builder()
                .eventType(EventType.MENTORSHIP_START)
                .mentorId(mentorId)
                .menteeId(menteeId)
                .receivedAt(LocalDateTime.now())
                .build();
    }
}
