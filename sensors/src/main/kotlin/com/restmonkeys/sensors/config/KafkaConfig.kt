package com.restmonkeys.sensors.config

import com.restmonkeys.sensors.model.Measurement
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.processor.WallclockTimestampExtractor
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KStreamBuilderFactoryBean
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import java.util.*

/**
 * Created by Andrei Chernyshev on 9/19/17.
 */
@Configuration
class KafkaConfig {


    companion object {
        const val HOUR: Long = 3600000
    }


    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var bootstrapServers: String

    @Value("\${spring.kafka.template.default-topic}")
    lateinit var topic: String

    @Bean
    fun producerConfigs(): Map<String, Any> {
        return mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, Measurement> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Measurement> {
        return KafkaTemplate(producerFactory()).apply {
            defaultTopic = topic
        }
    }

    @Bean
    fun kafkaStreams(kStreamBuilder: KStreamBuilder, streamsConfig: StreamsConfig): KafkaStreams {
        return KafkaStreams(kStreamBuilder, streamsConfig).apply { start() }
    }

    @Bean(name = arrayOf(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME))
    fun kStreamsConfigs(@Value("\${spring.kafka.consumer.group-id}") applicationId: String,
                        @Value("\${spring.kafka.bootstrap-servers}") bootstrapServers: String): StreamsConfig {
        val props = HashMap<String, Any>()
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId)
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name)
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name)
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor::class.java.name)
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, "localhost:8080")

        return StreamsConfig(props)
    }

    @Bean
    fun myKStreamBuilder(streamsConfig: StreamsConfig): FactoryBean<KStreamBuilder> =
            KStreamBuilderFactoryBean(streamsConfig)

}