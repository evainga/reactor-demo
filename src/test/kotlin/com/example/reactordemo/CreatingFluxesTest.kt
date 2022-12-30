package com.example.reactordemo

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class CreatingFluxesTest() {
    
    @Test
    fun `create Flux`() {
        val flux = Flux.just(FREEZE, BREEZE)
        StepVerifier.create(flux)
            .expectNext(FREEZE)
            .expectNext(BREEZE)
            .verifyComplete()
    }

    @Test
    fun `create Flux from array`() {
        val flux = Flux.fromArray(arrayOf(FREEZE, BREEZE))
        StepVerifier.create(flux)
            .expectNext(FREEZE)
            .expectNext(BREEZE)
            .verifyComplete()
    }

    @Test
    fun `create Flux from list`() {
        val flux = Flux.fromIterable(listOf(FREEZE, BREEZE))
        StepVerifier.create(flux)
            .expectNext(FREEZE)
            .expectNext(BREEZE)
            .verifyComplete()
    }

    @Test
    fun `create Flux from range`() {

        val flux = Flux.range(1, 10)

        StepVerifier.create(flux)
            .expectNext(1)
            .expectNext(2)
            .expectNext(3) // etc..
            .expectNextCount(7) // shortcut
            .verifyComplete()
    }

    @Test
    fun `empty flux`() {
        val flux = Flux.empty<String>()

        StepVerifier.create(flux)
            .verifyComplete()
    }

    @Test
    fun `create Flux from error`() {
        val flux = Flux.error<String>(IllegalStateException("Oops!"))

        StepVerifier.create(flux)
            .expectError(IllegalStateException::class.java)
            .verify()
    }
}
