package com.pos.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class EventAPI

fun main(args:Array<String>) {
    runApplication<EventAPI>(*args)
}