package com.example.reactordemo

import io.mockk.mockk
import org.junit.jupiter.api.Test
import reactor.core.observability.micrometer.Micrometer
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers.boundedElastic
import reactor.test.StepVerifier
import java.lang.Thread.currentThread


class PerformanceBoostsTest() {

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

