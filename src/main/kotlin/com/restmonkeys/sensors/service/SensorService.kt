package com.restmonkeys.sensors.service

import com.restmonkeys.sensors.model.Measurement
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

/**
 * Created by Andrei Chernyshev on 9/19/17.
 */
@Service
class SensorService(val kafka: KafkaTemplate<String, Measurement>) {

    fun emitMeasurement(measurement: Measurement) {
        kafka.sendDefault(measurement)
    }
}