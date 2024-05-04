package school.faang.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.listener.UserBannerListener;


@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.channels.user_ban_channel.name}")
    private String userBannerTopic;
    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewTopic;
    @Value("${spring.data.redis.channels.profile_search_channel.name}")
    private String userProfileSearchTopic;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public ChannelTopic profileSearchTopic() {
        return new ChannelTopic(userProfileSearchTopic);
    }

    @Bean
    public ChannelTopic userBannerTopic(){
        return new ChannelTopic(userBannerTopic);
    }

    @Bean
    public MessageListenerAdapter userBannerListenerAdapter(UserBannerListener userBannerListener){
        return new MessageListenerAdapter(userBannerListener);
    }

    @Bean
    public RedisMessageListenerContainer getContainer(MessageListenerAdapter userBannerListenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        container.addMessageListener(userBannerListenerAdapter, userBannerTopic());
        return container;
    }
}