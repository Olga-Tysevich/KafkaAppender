package ru.em.kafkaappender.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import ru.em.kafkaappender.mapping.ProducerProperties;
import ru.em.kafkaappender.mapping.TopicMappings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Plugin(name = "KafkaConfig", category = Node.CATEGORY, printObject = true)
public class KafkaAppenderConfig {

    private final String bootstrapServers;
    private final String defaultTopic;
    private final Map<String, String> producerProperties;
    private final Map<String, String> topicMappings;

    private KafkaAppenderConfig(Builder builder) {
        Objects.requireNonNull(builder.bootstrapServers, "bootstrapServers is required!");
        Objects.requireNonNull(builder.defaultTopic, "defaultTopic is required!");
        this.bootstrapServers = builder.bootstrapServers;
        this.defaultTopic = builder.defaultTopic;
        this.producerProperties = Objects.requireNonNullElse(builder.producerProperties.getProperties(), new HashMap<>());
        this.topicMappings = Objects.requireNonNullElse(builder.topicMappings.getMappings(), new HashMap<>());
    }


    @PluginBuilderFactory
    public static Builder newBuilder() {
        return new Builder();
    }

    @Getter
    @Setter
    public static class Builder implements org.apache.logging.log4j.core.util.Builder<KafkaAppenderConfig> {

        @PluginAttribute("bootstrapServers")
        private String bootstrapServers;

        @PluginAttribute("topic")
        private String defaultTopic;

        @PluginElement("TopicMappings")
        private TopicMappings topicMappings;

        @PluginElement("ProducerProperties")
        private ProducerProperties producerProperties;


        @Override
        public KafkaAppenderConfig build() {
            return new KafkaAppenderConfig(this);
        }
    }
}