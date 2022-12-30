package com.example.reactordemo

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers.boundedElastic
import reactor.test.StepVerifier
import reactor.test.publisher.TestPublisher
import java.lang.Thread.currentThread

class PerformanceBoostsTest() {

    @Test
    fun `avoid re-executing the same mono with cache operator`() {

        val mono = Mono.fromCallable {
            println("executing the mono")
            BREEZE
        }

        // Apply the cache operator
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


    @Test
    fun `show caching with TestPublisher`() {
        val executeHeavyOperation: () -> Mono<String> = mockk()

        val testPublisher = TestPublisher.createCold<String>().also { it.emit(FREEZE) }
        every { executeHeavyOperation() } answers { testPublisher.mono() }

        // Apply the cache operator
        val cachedMono = executeHeavyOperation().cache()

        StepVerifier.create(Mono.zip(cachedMono, cachedMono, cachedMono, cachedMono))
            .expectNextCount(1)
            .then {
                assertThat(testPublisher.subscribeCount()).isEqualTo(1)
            }
            .verifyComplete()

        verify(exactly = 1) { executeHeavyOperation() }
    }

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

    @Test
    fun `use parallel flux`() {

        val parallelFlux =

            Flux.range(1, 10)
                .parallel(5)     // alternative: ParallelFlux.from(Flux.range(1, 10), 5)
                .runOn(boundedElastic())
                .map {
                    println(currentThread().name)
                    it
                }

        StepVerifier.create(parallelFlux)
            .recordWith { mutableListOf<Int>() }
            .thenConsumeWhile { true }
            .consumeRecordedWith {
                assertThat(it).containsAll((1..10))
            }
            .verifyComplete()
    }

}

