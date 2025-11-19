# Kafka Log4j2 Appender

Кастомный Log4j2 appender для отправки логов в Apache Kafka с поддержкой роутинга по уровням логирования и гибкой конфигурацией.

## ✨ Особенности

- 🚀 **Отправка логов в Kafka** через Log4j2
- 🎯 **Роутинг по уровням** - возможность задавать разные топики для ERROR, WARN, INFO, DEBUG
- 🔧 **Гибкая конфигурация** - любые настройки Kafka Producer, например SASL/SSL аутентификации
- ⚡ **Производительность** - асинхронная отправка
- 🛡️ **Без зависимостей** - не привязан к Spring Boot

## 📦 Локальная установка
1. Скачать KafkaAppender и инсталировать в локальный мейвен репозиторий
2. Добавить зависимость
### Maven
```xml
<dependency>
    <groupId>ru.em.kafkaappender</groupId>
    <artifactId>KafkaAppender</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle
```gradle
implementation 'ru.em.kafkaappender:KafkaAppender:0.1.0'
```

## 📦 Установка из репо
1. Добавить в pom.xml
### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

2. Добавьте зависимость 
   Прим.: нужно выбрать актуальную версию билда: [JitPack](https://jitpack.io/#Olga-Tysevich/KafkaAppender) 

### Maven
```xml
<dependency>
    <groupId>com.github.Olga-Tysevich</groupId>
    <artifactId>KafkaAppender</artifactId>
    <version>0.1.0</version> 
</dependency>
```

## ⚙️ Конфигурация

### Базовый пример (`log4j2.xml`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <KafkaAppender name="Kafka">
            <KafkaConfig
                    bootstrapServers="localhost:29092"
                    topic="logs">

                <TopicMappings>
                    <Mapping level="ERROR" topic="prod-error-logs"/>
                    <Mapping level="WARN" topic="prod-warn-logs"/>
                    <Mapping level="INFO" topic="prod-info-logs"/>
                </TopicMappings>

                <ProducerProperties>
                    <Property name="acks" value="1"/>
                    <Property name="retries" value="3"/>
                    <Property name="batch.size" value="16384"/>
                    <Property name="linger.ms" value="1"/>
                </ProducerProperties>
            </KafkaConfig>

            <PatternLayout pattern="%d{ISO8601} [%t] %-5level %c{1} - %m%n"/>
        </KafkaAppender>

        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{ISO8601} [%t] %-5level %c{1} - %m%n"/>
        </Console>

        <Async name="AsyncKafka">
            <AppenderRef ref="Kafka"/>
        </Async>
    </Appenders>

    <Loggers>
        <!-- ТОЛЬКО логи приложения идут в Kafka -->
        <Logger name="ru.em.demo" level="debug" additivity="false">
            <AppenderRef ref="AsyncKafka"/>
        </Logger>

        <!-- ТОЛЬКО логи KafkaAppender идут в консоль -->
        <Logger name="KafkaAppenderInternal" level="info" additivity="false">
            <AppenderRef ref="Console"/>
        </Logger>

        <!-- ВСЕ остальные логи идут только в консоль -->
        <Root level="info">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

## 🔧 Параметры конфигурации

### KafkaConfig

| Параметр           | Обязательный | Описание                                                             | По умолчанию |
|--------------------|:------------:|----------------------------------------------------------------------|--------------|
| `bootstrapServers` |      ✅       | Kafka brokers (host:port)                                            | -            |
| `topic`  |      ✅       | Топик, используемый по умолчанию, <br/>если не указан специализированный  | -            |

### TopicMappings

Определяет соответствие уровней логирования Kafka топикам (optional):

```xml
<TopicMappings>
    <Mapping level="ERROR" topic="error-logs"/>
    <Mapping level="WARN" topic="warn-logs"/>
    <Mapping level="INFO" topic="info-logs"/>
    <Mapping level="DEBUG" topic="debug-logs"/>
</TopicMappings>
```

**Поддерживаемые уровни:** `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`

### ProducerProperties

Любые настройки [Kafka Producer](https://kafka.apache.org/documentation/#producerconfigs):

```xml
<ProducerProperties>
    <Property name="acks" value="1"/>
    <Property name="retries" value="3"/>
    <Property name="batch.size" value="16384"/>
    <Property name="linger.ms" value="100"/>
    <Property name="compression.type" value="snappy"/>
</ProducerProperties>
```

## 🚀 Производительность

### Асинхронная отправка
Всегда используйте Async appender для максимальной производительности:

```xml
<Async name="AsyncKafka" bufferSize="1024">
    <AppenderRef ref="Kafka"/>
</Async>
```

## ❌ Обработка ошибок

Appender обрабатывает ошибки следующими способами:
- **Ошибки подключения к Kafka** - логируются, не прерывают работу приложения
- **Ошибки сериализации** - логируются, сообщение пропускается
- **Ошибки сети** - автоматические retry согласно настройкам Producer

## 📋 Системные требования

- Java 17 или выше
- Log4j2 2.17.0 или выше
- Kafka Clients 2.5.0 или выше


## 🌿 Чтобы Spring Boot использовал Log4j2 вместо Logback, нужно сделать несколько вещей:

---

### 1️⃣ Удалить зависимость Logback

Spring Boot по умолчанию подтягивает `spring-boot-starter-logging` (Logback). Нужно её исключить:

**Maven:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

**Gradle:**

```gradle
implementation('org.springframework.boot:spring-boot-starter') {
    exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging'
}
```

---

### 2️⃣ Добавить зависимости Log4j2

Для Spring Boot 3.x / Java 17+:

**Maven:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```

**Gradle:**

```gradle
implementation 'org.springframework.boot:spring-boot-starter-log4j2'
```

> Эта зависимость подтягивает все необходимое для работы Log4j2 и его интеграции со Spring Boot.

---

### 3️⃣ Положить конфигурацию Log4j2

* Файл `log4j2.xml` положить в `src/main/resources`.
* Spring Boot автоматически его подхватит, если присутствует `spring-boot-starter-log4j2` и нет Logback.

---

### 4️⃣ Опционально — указать Spring Boot использовать Log4j2 через свойство

В `application.properties` или `application.yml` можно явно указать:

```properties
# application.properties
logging.config=classpath:log4j2.xml
```

или

```yaml
# application.yml
logging:
  config: classpath:log4j2.xml
```
