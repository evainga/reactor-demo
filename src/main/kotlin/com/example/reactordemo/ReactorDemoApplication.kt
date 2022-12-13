package com.example.reactordemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReactorDemoApplication

fun main(args: Array<String>) {
    runApplication<ReactorDemoApplication>(*args)
}
