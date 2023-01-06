package com.example.reactordemo

import com.example.BREEZE
import com.example.FREEZE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import java.time.Duration

class OrderingTest {

    @Test
    fun `show that flux retains order`() {

        val sequentialFlux = Flux.range(1, 10)

        StepVerifier.create(sequentialFlux)
            .expectNext(1)
            .expectNext(2)
            .expectNext(3)
            .expectNext(4)
            .expectNext(5)
            .expectNext(6)
            .expectNext(7)
            .expectNext(8)
            .expectNext(9)
            .expectNext(10)
            .verifyComplete()
    }

    @Test
    fun `parallel flux does not retain order`() {

        val parallelFlux =
            Flux.range(1, 10)
                .parallel(5)
                .runOn(Schedulers.boundedElastic())

        StepVerifier.create(parallelFlux)
            .recordWith { mutableListOf<Int>() }
            .thenConsumeWhile { true }
            .consumeRecordedWith {
                println(it)
                assertThat(it).containsAll((1..10))
            }
            .verifyComplete()
    }

    @Test
    fun `sequential flatMapping`() {
        val numbers = Flux.just(1, 2, 3, 4, 5)

        val flatMapped = numbers
            .flatMap { Flux.just(it.toString() + FREEZE).delayElements(Duration.ofMillis(100)) }

        StepVerifier.create(flatMapped)
            .recordWith { mutableListOf<String>() }
            .thenConsumeWhile { true }
            .consumeRecordedWith { println("flatMap: $it") }
            .verifyComplete()

        val flatMappedSequential = numbers
            .flatMapSequential { Flux.just(it.toString() + BREEZE).delayElements(Duration.ofMillis(100)) }

        StepVerifier.create(flatMappedSequential)
            .recordWith { mutableListOf<String>() }
            .thenConsumeWhile { true }
            .consumeRecordedWith { println("flatMapSequential: $it") }
            .verifyComplete()
    }

}
