package com.restmonkeys.sensors.controller

import org.junit.Before
import org.junit.Test

/**
 * Created by Andrei Chernyshev on 9/18/17.
 */
class SensorControllerTest {

    lateinit var controller: SensorController

    @Before
    fun setup() {
        controller = SensorController()
    }

    @Test
    fun list() {
        // setup

        // act
        controller.list()

        // verify
    }
}