package com.restmonkeys.sensors.controller

import com.restmonkeys.sensors.model.Sensor
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by Andrei Chernyshev on 9/18/17.
 */
@RestController
@RequestMapping("/sensors")
class SensorController {

    @RequestMapping
    fun list(): List<Sensor> {
        return emptyList()
    }
}