package com.example.reactordemo

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Bean
import reactor.core.observability.micrometer.Micrometer
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.core.scheduler.Schedulers.*
import reactor.test.StepVerifier
import java.lang.Thread.currentThread
import kotlin.test.DefaultAsserter.assertEquals

private const val FREEZE = "freeze"
private const val BREEZE = "breeze"

class TestingWebfluxTest() {

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

    /*
            How to create a Mono

            other possibilies:
            Mono.create<String> {it.success(FREEZE)}
            Mono.fromCallable { FREEZE }
            Mono.from(Mono.just(FREEZE))
            Mono.fromSupplier
            Mono.fromFuture

            ...
    */
    @Test
    fun `create Mono`() {

        val mono = Mono.just(FREEZE)

        StepVerifier.create(mono)
            .expectNext(FREEZE)
            .verifyComplete()
    }

    @Test
    fun `empty Mono`() {
        val publisher = Mono.empty<String>()

        StepVerifier.create(publisher)
            .verifyComplete()
    }

    /*

        How to create a Flux

        other possibilies:
        Flux.fromArray(arrayOf(FREEZE, BREEZE))
        Flux.fromIterable(listOf(FREEZE, BREEZE))
        ...
    */

    @Test
    fun `create Flux`() {
        val flux = Flux.just(FREEZE, BREEZE)
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
    fun `create Mono from error`() {
        val mono = Mono.error<String>(IllegalStateException("Oops!"))

        StepVerifier.create(mono)
            .expectError(IllegalStateException::class.java)
            .verify()
    }

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


    /*
    some additional performance boosts
    */

    @Test
    fun `avoid re-executing the same mono with cache operator`() {

        val mono = Mono.fromCallable {
            println("executing the mono")
            BREEZE
        }

        // Apply the cache operator to the Mono
        val cachedMono = mono.cache()

        // Subscribe to the Mono
        StepVerifier.create(cachedMono)
            .expectNext(BREEZE)
            .verifyComplete()

        // Subscribe to the Mono again
        StepVerifier.create(cachedMono)
            .expectNext(BREEZE)
            .verifyComplete()
    }

    /*
    Additionally, using the subscribeOn operator with a reactive Scheduler such as Schedulers.boundedElastic()
    can further improve the performance of the Mono by executing the code on a separate thread or thread pool.
     This can reduce the impact of long-running or blocking code on the main thread and improve the responsiveness
     of the application.
    */
    @Test
    fun `subscribe on with reactive scheduler`() {

        val mono1 = Mono.just(FREEZE)
            .doOnSubscribe { println("Mono 1 subscribed on thread ${currentThread().name}") }

        val mono2 = Mono.just(BREEZE)
            .doOnSubscribe { println("Mono 2 subscribed on thread ${currentThread().name}") }

        val zippedMono =
            Mono.zip(
                mono1.subscribeOn(boundedElastic()),
                mono2.subscribeOn(boundedElastic())
            ) { s1, s2 -> "$s1 $s2" }

        StepVerifier.create(zippedMono)
            .expectNext("freeze breeze")
            .verifyComplete()
    }

    /*

    ParallelFlux

    Working with MongoDB
    .collectList -> @Meta(batchsize)

    */
    @Test
    fun `use metrics of mono`() {

        val mono = Mono.just(BREEZE)
            .tap(Micrometer.metrics(mockk()))

    }


}
