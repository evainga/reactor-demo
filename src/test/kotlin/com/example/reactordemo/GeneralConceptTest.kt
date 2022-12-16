package com.example.reactordemo

import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class GeneralConceptTest() {

    /* General Introduction to Reactor
    TODO
        https://medium.com/intuit-engineering/reactive-programming-project-reactor-webflux-oh-my-4bfa470feee7
    */

    /*   our use case
    TODO
     */

    /*
    Flux vs. Mono  // creation and subscription
    */

    @Test
    fun `without subscription content is not printed to console`() {
        println("Executing test")

        Mono.create<String> { println("Executing code") }
    }

    @Test
    fun `after subscription content is printed to console`() {
        println("Executing test")

        val mono = Mono.create<String> { println("Executing code") }

        mono.subscribe()
    }

    @Test
    fun `use stepVerifier for tests`() {
        println("Executing test")

        val mono = Mono.just(FREEZE)

        StepVerifier.create(mono)
            .expectNext(FREEZE)
            .verifyComplete()
    }
}
