package com.restmonkeys.sensors

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
class SensorsApplication

fun main(args: Array<String>) {
    SpringApplication.run(SensorsApplication::class.java, *args)
}
