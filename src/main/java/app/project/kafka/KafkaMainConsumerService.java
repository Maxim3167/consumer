package app.project.kafka;

import app.project.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.FixedDelayStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class KafkaMainConsumerService {
    private final KafkaTemplate<String, User> kafkaTemplate;
    private RestTemplate restTemplate = new RestTemplate();

    public KafkaMainConsumerService(KafkaTemplate<String, User> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @RetryableTopic(
            kafkaTemplate = "kafkaTemplate",
            fixedDelayTopicStrategy = FixedDelayStrategy.SINGLE_TOPIC,
            listenerContainerFactory = "kafkaListenerContainerFactory",
            attempts = "3",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            backoff = @Backoff(delay = 60000)
    )
    @KafkaListener(topics = "main-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(User user, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            ResponseEntity<Long> response = restTemplate.postForEntity("http://localhost:8081/message/send",
                    new HttpEntity<>(user),
                    Long.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException();
            }
            if (response.getBody() != null) {
                log.info("Идентификатор от сервиса получения сообщений равен: {}", response.getBody());
            }
        }
        catch (Exception e) {
            log.warn("Ошибка при отправке данных в сервис получения сообщений");
            throw new RuntimeException();
        }
        log.info("Success consume!");
    }
    @DltHandler
    public void processMessage(User user) {
        log.error("DltHandler processMessage = {}", user);
        kafkaTemplate.send("dead-letter-topic", user);
    }
}
