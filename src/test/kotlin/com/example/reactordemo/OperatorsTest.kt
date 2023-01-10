package com.example.reactordemo

import com.example.BREEZE
import com.example.FREEZE
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier


class OperatorsTest() {

    @Test
    fun `example for map operator to transform value of mono`() {

        val mono = Mono.just("123")
            .map { it.toInt() }

        StepVerifier.create(mono)
            .expectNext(123)
            .verifyComplete()
    }

    @Test
    fun `example for map to transform values of flux`() {

        val flux = Flux.just("1", "2", "3")
            .map { it.toInt() }

        // sequential mapping
        StepVerifier.create(flux)
            .expectNext(1)
            .expectNext(2)
            .expectNext(3)
            .verifyComplete()
    }

    @Test
    fun `example for filtering even numbers`() {

        val flux = Flux.just("1", "2", "3", "4")
            .map { it.toInt() }
            .filter { it % 2 == 0 }

        // sequential mapping
        StepVerifier.create(flux)
            .expectNext(2)
            .expectNext(4)
            .verifyComplete()
    }

    @Test
    fun `example for flatMap to flatten a mono`() {
        val mono: Mono<String> = Mono.just(BREEZE)
            .flatMap { Mono.just(it) } // .flatMap returns Mono<String> instead of Mono<Mono<String>> with .map

        StepVerifier.create(mono)
            .expectNext(BREEZE)
            .verifyComplete()
    }

    @Test
    fun `example for flatMap to flatten a flux`() {
        val flux: Flux<String> = Flux.just(FREEZE, BREEZE)
            .flatMap { element -> Mono.just(element) } // .flatMap returns Flux<String> instead of Flux<Mono<String>> with .map

        StepVerifier.create(flux)
            .expectNext(FREEZE)
            .expectNext(BREEZE)
            .verifyComplete()
    }


    @Test
    fun `combine a mono value with flux with flatMapMany`() {
        val flux: Flux<String> =
            Mono.just(BREEZE)
                .flatMapMany { Flux.just(1, 2, 3).map { number -> "$number$it" } }

        StepVerifier.create(flux)
            .expectNext("1breeze")
            .expectNext("2breeze")
            .expectNext("3breeze")
            .verifyComplete()
    }

    @Test
    fun `example for combining values emitted by multiple monos using the zip operator`() {

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
