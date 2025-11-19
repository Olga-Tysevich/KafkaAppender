package ru.em.kafkaappender.mapping;

import lombok.Getter;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;

import java.util.HashMap;
import java.util.Map;

@Getter
@Plugin(name = "ProducerProperties", category = Node.CATEGORY, printObject = true)
public class ProducerProperties {

    private final Map<String, String> properties = new HashMap<>();

    @PluginBuilderFactory
    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder implements org.apache.logging.log4j.core.util.Builder<ProducerProperties> {

        @PluginElement("Property")
        private Property[] propertyElements;

        @Override
        public ProducerProperties build() {
            ProducerProperties instance = new ProducerProperties();

            if (propertyElements != null) {
                for (Property p : propertyElements) {
                    if (p.getName() != null) {
                        instance.properties.put(p.getName(), p.getValue());
                    }
                }
            }

            return instance;
        }
    }
}