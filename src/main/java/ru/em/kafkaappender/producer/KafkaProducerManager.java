package ru.em.kafkaappender.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.em.kafkaappender.config.KafkaAppenderConfig;
import ru.em.kafkaappender.logger.KafkaAppenderLogger;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class KafkaProducerManager {
    private final KafkaAppenderConfig config;
    private final AtomicReference<Producer<String, String>> producerRef = new AtomicReference<>();
    private volatile boolean started = false;

    public KafkaProducerManager(KafkaAppenderConfig config) {
        this.config = config;
    }

    public void start() {
        if (!started) {
            synchronized (this) {
                if (!started) {
                    Properties props = createProducerProperties();
                    Producer<String, String> producer = new KafkaProducer<>(props);
                    producerRef.set(producer);
                    started = true;
                    KafkaAppenderLogger.info("KafkaProducerManager started for: " + config.getBootstrapServers());
                }
            }
        }
    }

    public void send(String topic, String message) {
        if (!started) {
            start();
        }

        Producer<String, String> producer = producerRef.get();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                KafkaAppenderLogger.error("Failed to send message to Kafka topic '" + topic + "': " + exception.getMessage());
            }
        });
    }

    public void stop() {
        if (started && producerRef.get() != null) {
            producerRef.get().close();
            started = false;
            KafkaAppenderLogger.info("KafkaProducerManager stopped");
        }
    }

    private Properties createProducerProperties() {
        Properties props = new Properties();

        props.put("bootstrap.servers", config.getBootstrapServers());
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        props.putAll(config.getProducerProperties());

        return props;
    }
}