package com.restmonkeys.sensors

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import javax.ws.rs.ApplicationPath

@SpringBootApplication
@ApplicationPath("/api/v1")
class SensorsApplication

fun main(args: Array<String>) {
    SpringApplication.run(SensorsApplication::class.java, *args)
}
