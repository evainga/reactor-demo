package com.example.reactordemo

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier


class OperatorsTest() {

    /*
    Basic Operators
    */

    /*
    usage of map operator to transform the value emitted by a Mono or Flux by applying a function to it
     */

    @Test
    fun `example for map with mono`() {

        val mono = Mono.just("123")
            .map { it.toInt() }

        StepVerifier.create(mono)
            .expectNext(123)
            .verifyComplete()
    }

    @Test
    fun `example for map with flux`() {

        val flux = Flux.just("1", "2", "3")
            .map { it.toInt() }

        StepVerifier.create(flux)
            .expectNext(1)
            .expectNext(2)
            .expectNext(3)
            .verifyComplete()
    }

    /*
    The flatMap operator can be used to flatten a Mono or Flux that emits a value of a nested type
    into a Mono or Flux that emits the values of the nested type.
    */

    @Test
    fun `example for flatMap with mono`() {
        val mono = Mono.just(BREEZE)
            .flatMap { Mono.just(it) }

        StepVerifier.create(mono)
            .expectNext(BREEZE)
            .verifyComplete()
    }

    @Test
    fun `example for flatMap with flux`() {
        val flux = Flux.just(FREEZE, BREEZE)
            .flatMap { Mono.just(it) }

        StepVerifier.create(flux)
            .expectNext(FREEZE)
            .expectNext(BREEZE)
            .verifyComplete()
    }


    @Test
    fun `combine a mono value with flux with flatMapMany`() {
        val flux: Flux<String> =
            Mono.just(BREEZE)
                .flatMapMany { breeze ->
                    Flux.just(1, 2, 3)
                        .map { number -> "$number$breeze" }
                }

        StepVerifier.create(flux)
            .expectNext("1breeze")
            .expectNext("2breeze")
            .expectNext("3breeze")
            .verifyComplete()
    }


    /* combine the values emitted by multiple Mono instances into a single Mono that emits a tuple of the combined values.*/
    @Test
    fun `example for zip`() {

        val mono1 = Mono.just(BREEZE)
        val mono2 = Mono.just(FREEZE)
        val zippedMono = Mono.zip(mono1, mono2) { value1, value2 -> "$value1 $value2" }

        StepVerifier.create(zippedMono)
            .expectNext("breeze freeze")
            .verifyComplete()
    }

    @Test
    fun `example for zip with flux`() {
        val flux1 = Flux.just(BREEZE, FREEZE)
        val flux2 = Flux.just("!", "?")
        val zippedFlux = Flux.zip(flux1, flux2) { value1, value2 -> "$value1$value2" }


        StepVerifier.create(zippedFlux)
            .expectNext("breeze!")
            .expectNext("freeze?")
            .verifyComplete()
    }
}
