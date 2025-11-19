package ru.em.kafkaappender.mapping;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Getter
@Setter
@Plugin(name = "Mapping", category = Node.CATEGORY, printObject = true)
public class LevelTopicMapping {

    private String level;
    private String topic;

    @PluginFactory
    public static LevelTopicMapping createMapping(
            @PluginAttribute("level") String level,
            @PluginAttribute("topic") String topic) {
        LevelTopicMapping mapping = new LevelTopicMapping();
        mapping.setLevel(level);
        mapping.setTopic(topic);
        return mapping;
    }
}