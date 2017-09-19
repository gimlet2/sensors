package com.restmonkeys.agregator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.kstream.TimeWindows
import org.apache.kafka.streams.processor.WallclockTimestampExtractor
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.core.KStreamBuilderFactoryBean
import java.util.*

fun main(args: Array<String>) {
    SpringApplication.run(AggregatorApplication::class.java, *args)
}

@SpringBootApplication
@EnableKafka
class AggregatorApplication {

    companion object {
        const val HOUR: Long = 3600000
    }

    @Bean(name = arrayOf(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME))
    fun kStreamsConfigs(@Value("\${spring.kafka.consumer.group-id}") applicationId: String,
                        @Value("\${spring.kafka.bootstrap-servers}") bootstrapServers: String): StreamsConfig {
        val props = HashMap<String, Any>()
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId)
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name)
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name)
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.serdeFrom(JsonSerializer<Measurement>(), JsonDeserializer<Measurement>(Measurement::class.java)).javaClass.name)
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor::class.java.name)
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 500)
        props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, "localhost:8080")

        return StreamsConfig(props)
    }

    @Bean
    fun myKStreamBuilder(streamsConfig: StreamsConfig): FactoryBean<KStreamBuilder> =
            KStreamBuilderFactoryBean(streamsConfig)

    @Bean
    fun kStream(myKStreamBuilder: KStreamBuilder,
                @Value("\${topic.income}") topicIn: String,
                @Value("\${topic.outcome}") topicOut: String,
                @Value("\${heatLimit}") limit: Double
    ): KStream<String, String> {
        val objectMapper = ObjectMapper()
        objectMapper.registerKotlinModule()
        val stream = myKStreamBuilder.stream<String, String>(topicIn)
        val mapped = stream.mapValues { objectMapper.readValue(it, Measurement::class.java) }

        mapped.groupByKey(Serdes.String(), JsonPOJOSerde(Measurement::class.java)).reduce({ a, b -> Measurement(a.temperature + b.temperature / 2) }, TimeWindows.of(HOUR), "hour").toStream()
        mapped.groupByKey(Serdes.String(), JsonPOJOSerde(Measurement::class.java)).reduce({ a, b -> Measurement(a.temperature + b.temperature / 2) }, TimeWindows.of(HOUR * 24 * 7), "week")
        mapped.groupByKey(Serdes.String(), JsonPOJOSerde(Measurement::class.java)).reduce({ a, b -> Measurement(Math.max(a.temperature, b.temperature)) }, TimeWindows.of(HOUR * 24 * 30), "max")

        mapped
                .mapValues { if (it.temperature > limit) "1" else "-1" }
                .groupByKey()
                .reduce({ a, b ->
                    val n1 = a.toInt()
                    val n2 = b.toInt()
                    when {
                        n2 == -1 -> "0"
                        n1 + n2 in 1..2 -> (n1 + n2).toString()
                        n1 + n2 <= 0 -> "0"
                        else -> "3"
                    }
                }).toStream()
                .filter({ _, value -> value == "3" })
                .mapValues { "TEMPERATURE_EXCEEDED" }
                .through(topicOut)

        return stream
    }
}


data class Measurement(val temperature: Double)
