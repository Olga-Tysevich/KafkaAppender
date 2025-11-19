package ru.em.kafkaappender;

import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import ru.em.kafkaappender.config.KafkaAppenderConfig;
import ru.em.kafkaappender.logger.KafkaAppenderLogger;
import ru.em.kafkaappender.producer.TopicRoutingManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import ru.em.kafkaappender.producer.KafkaProducerManager;

import java.io.Serializable;

@Plugin(name = "KafkaAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class KafkaAppender extends AbstractAppender {

    private final TopicRoutingManager topicRoutingManager;
    private final KafkaProducerManager producerManager;

    protected KafkaAppender(String name,
                            Filter filter,
                            Layout<? extends Serializable> layout,
                            boolean ignoreExceptions,
                            Property[] properties,
                            KafkaAppenderConfig kafkaConfig) {
        super(name, filter, layout, ignoreExceptions, properties);
        this.topicRoutingManager = new TopicRoutingManager(kafkaConfig);
        this.producerManager = new KafkaProducerManager(kafkaConfig);
    }

    @PluginFactory
    public static KafkaAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginAttribute(value = "ignoreExceptions", defaultBoolean = true) boolean ignoreExceptions,
            @PluginElement("Properties") Property[] properties,
            @PluginElement("KafkaConfig") KafkaAppenderConfig kafkaConfig) {

        if (name == null) {
            KafkaAppenderLogger.error("No name provided for KafkaAppender");
            throw new IllegalArgumentException("No name provided for KafkaAppender");
        }
        if (kafkaConfig == null) {
            KafkaAppenderLogger.error("KafkaConfig must be provided");
            throw new IllegalArgumentException("KafkaConfig must be provided");
        }

        if (properties == null) {
            KafkaAppenderLogger.info("Initialize with empty Property");
            properties = Property.EMPTY_ARRAY;
        }

        return new KafkaAppender(name, filter, layout, ignoreExceptions, properties, kafkaConfig);
    }

    @Override
    public void start() {
        super.start();
        producerManager.start();
    }

    @Override
    public void stop() {
        super.stop();
        producerManager.stop();
    }

    @Override
    public void append(LogEvent event) {
        KafkaAppenderLogger.info("KafkaAppender.append() called: " + event.getMessage().getFormattedMessage());

        try {
            String message = getLayout().toSerializable(event).toString();
            String topic = topicRoutingManager.resolveTopic(event.getLevel());

            producerManager.send(topic, message);
            KafkaAppenderLogger.info("Sent message: {} to topic: {}", message, topic);

        } catch (Exception e) {
            if (!ignoreExceptions()) {
                throw new AppenderLoggingException("Failed to append log event", e);
            }
            KafkaAppenderLogger.error("Failed to process log event: " + e.getMessage());
        }
    }
}