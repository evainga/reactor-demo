package com.example.reactordemo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class GeneralConceptTest() {

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
    fun `showcase for blocking mono`() {
        val mono = Mono.just(FREEZE)

        val block: String? = mono.block()

        assertThat(block).isEqualTo(FREEZE)
    }

    @Test
    fun `showcase for blocking empty mono`() {

        val mono = Mono.empty<String>()

        val block: String? = mono.block()

        assertThat(block).isEqualTo(null)
    }

    @Test
    fun `use stepVerifier for tests`() {

        val mono = Mono.just(FREEZE)

        StepVerifier.create(mono)
            .expectNext(FREEZE)
            .verifyComplete()
    }

    @Test
    fun `use stepVerifier for tests with empty mono`() {

        val mono = Mono.empty<String>()

        StepVerifier.create(mono)
            .verifyComplete()
    }
}
