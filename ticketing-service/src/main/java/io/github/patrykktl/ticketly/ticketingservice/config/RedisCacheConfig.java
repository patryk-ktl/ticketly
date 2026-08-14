package io.github.patrykktl.ticketly.ticketingservice.config;

import io.github.patrykktl.ticketly.ticketingservice.model.CachedPage;
import io.github.patrykktl.ticketly.ticketingservice.model.dto.EventCardDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

    @Bean
    @ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "redis")
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();

        JavaType cachedPageOfEventCardDto = mapper.getTypeFactory()
                .constructParametricType(CachedPage.class, EventCardDto.class);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                );

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put(
                "eventDetails",
                defaultConfig
                        .entryTtl(Duration.ofMinutes(10))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer<>(mapper, EventCardDto.class)
                                )
                        )
        );

        cacheConfigurations.put(
                "eventSearchResults",
                defaultConfig
                        .entryTtl(Duration.ofSeconds(60))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer<CachedPage<EventCardDto>>(mapper, cachedPageOfEventCardDto)
                                )
                        )
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig.serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                GenericJacksonJsonRedisSerializer.builder().build())
                ))
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
