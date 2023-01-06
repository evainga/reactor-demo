package com.example.reactordemo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class ParticipantController(
    private val repository: ParticipantRepository
) {

    @GetMapping("/participant")
    fun getParticipant(@RequestParam id: String): Mono<String> {
        return repository.findById(id)
            .map { it.name }
    }

    @GetMapping("/all")
    fun getAllParticipants(): Flux<String> {
        return repository.findAll()
            .map { it.name + " " }
    }
}
