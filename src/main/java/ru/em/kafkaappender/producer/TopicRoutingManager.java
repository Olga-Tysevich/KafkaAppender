package ru.em.kafkaappender.producer;

import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import ru.em.kafkaappender.config.KafkaAppenderConfig;
import org.apache.logging.log4j.Level;
import ru.em.kafkaappender.logger.KafkaAppenderLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TopicRoutingManager {
    private final KafkaAppenderConfig kafkaConfig;
    private final Map<Level, String> levelToTopicMap = new HashMap<>();

    public TopicRoutingManager(KafkaAppenderConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
        initializeTopicMappings();
    }

    private void initializeTopicMappings() {
        for (Map.Entry<String, String> topic : kafkaConfig.getTopicMappings().entrySet()) {
            Level level = Level.toLevel(topic.getKey().toUpperCase());
            String topicName = topic.getValue();
            levelToTopicMap.put(level, topicName);
        }
        KafkaAppenderLogger.info("Initialize with topic mappings: {}", levelToTopicMap);
    }

    public String resolveTopic(Level level) {
        if (!levelToTopicMap.containsKey(level)
                && (kafkaConfig.getDefaultTopic() == null || kafkaConfig.getDefaultTopic().isEmpty())) {
            throw new AppenderLoggingException("The default topic is not set. Topic not found for level: " + level);
        }

        return Objects.requireNonNullElse(levelToTopicMap.get(level), kafkaConfig.getDefaultTopic());
    }
}