package com.example.reactordemo

import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class CreatingMonosTest() {
    
    @Test
    fun `create Mono`() {
        val mono = Mono.just(FREEZE)

        StepVerifier.create(mono)
            .expectNext(FREEZE)
            .verifyComplete()
    }

    @Test
    fun `create Mono from callback`() {
        val mono = Mono.create<String> { it ->
            it.success(FREEZE)
        }

        StepVerifier.create(mono)
            .expectNext(FREEZE)
            .verifyComplete()
    }

    @Test
    fun `create Mono from Callable`() {
        val mono = Mono.fromCallable { FREEZE }

        StepVerifier.create(mono)
            .expectNext(FREEZE)
            .verifyComplete()
    }

    @Test
    fun `create Mono from Mono`() {
        val mono = Mono.from(Mono.just(FREEZE))

        StepVerifier.create(mono)
            .expectNext(FREEZE)
            .verifyComplete()
    }

    /*
        other possibilies:
        Mono.fromSupplier
        Mono.fromFuture
        ...
    */

    @Test
    fun `empty Mono`() {
        val publisher = Mono.empty<String>()

        StepVerifier.create(publisher)
            .verifyComplete()
    }

    @Test
    fun `create Mono from error`() {
        val mono = Mono.error<String>(IllegalStateException("Oops!"))

        StepVerifier.create(mono)
            .expectError(IllegalStateException::class.java)
            .verify()
    }

}
