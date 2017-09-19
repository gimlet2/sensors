package com.restmonkeys.sensors.controller

import com.restmonkeys.sensors.model.Measurement
import com.restmonkeys.sensors.model.Sensor
import com.restmonkeys.sensors.service.SensorService
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Created by Andrei Chernyshev on 9/18/17.
 */
@RestController
@RequestMapping("/sensors")
class SensorController(val sensorService: SensorService) {

    @RequestMapping
    fun list(): List<Sensor> {
        return emptyList()
    }

    @PostMapping
    fun create(@RequestBody sensor: Sensor): Sensor {
        return sensor
    }

    @PostMapping("/{uuid}/measurements")
    fun create(@PathVariable("uuid") uuid: UUID, @RequestBody measurement: Measurement): Measurement {
        sensorService.emitMeasurement(measurement)
        return measurement
    }
}