package app.project.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicBuilder {
    @Bean
    public NewTopic buildMainTopic() {
        return TopicBuilder.name("main-topic").build();
    }

    @Bean
    public NewTopic buildDeadTopic() {
        return TopicBuilder.name("dead-letter-topic").build();
    }

    @Bean
    public NewTopic buildRetryTopic() {
        return TopicBuilder.name("main-topic-retry").build();
    }
}
