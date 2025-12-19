package com.pos.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EventAPI

fun main(args: Array<String>) {
    runApplication<EventAPI>(*args)
}