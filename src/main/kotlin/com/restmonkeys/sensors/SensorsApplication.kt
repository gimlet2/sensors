package com.restmonkeys.sensors

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SensorsApplication

fun main(args: Array<String>) {
    SpringApplication.run(SensorsApplication::class.java, *args)
}
