package ru.em.kafkaappender.mapping;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;

import java.util.HashMap;
import java.util.Map;

@Getter
@Plugin(name = "TopicMappings", category = Node.CATEGORY, printObject = true)
public class TopicMappings {
    private final Map<String, String> mappings = new HashMap<>();

    @PluginBuilderFactory
    public static TopicMappings.Builder newBuilder() {
        return new TopicMappings.Builder();
    }

    @Getter
    @Setter
    public static class Builder implements org.apache.logging.log4j.core.util.Builder<TopicMappings> {

        @PluginElement("Mapping")
        private LevelTopicMapping[] mappingElements;

        @Override
        public TopicMappings build() {
            TopicMappings instance = new TopicMappings();

            if (mappingElements != null) {
                for (LevelTopicMapping m : mappingElements) {
                    instance.mappings.put(
                            m.getLevel(),
                            m.getTopic()
                    );
                }
            }

            return instance;
        }
    }
}
