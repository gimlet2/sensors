package com.restmonkeys.agregator

/**
 * Created by Andrei Chernyshev on 9/19/17.
 */
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

class JsonPOJOSerde<T>(private val cls: Class<T>) : Serde<T> {

    private val mapper = ObjectMapper().registerKotlinModule()

    override fun configure(configs: Map<String, *>, isKey: Boolean) {

    }

    override fun close() {

    }

    override fun serializer(): Serializer<T> {
        return object : Serializer<T> {

            override fun configure(configs: Map<String, *>, isKey: Boolean) {

            }

            override fun serialize(topic: String, data: T): ByteArray {
                try {
                    return mapper.writeValueAsBytes(data)
                } catch (e: Exception) {
                    throw SerializationException("Error serializing JSON message", e)
                }

            }

            override fun close() {

            }
        }

    }

    override fun deserializer(): Deserializer<T> {
        return object : Deserializer<T> {
            override fun configure(configs: Map<String, *>, isKey: Boolean) {

            }

            override fun deserialize(topic: String, data: ByteArray): T {
                val result: T
                try {
                    result = mapper.readValue(data, cls)
                } catch (e: Exception) {
                    throw SerializationException(e)
                }

                return result
            }

            override fun close() {

            }
        }
    }
}