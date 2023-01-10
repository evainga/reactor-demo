package com.example.reactordemo

import com.example.FREEZE
import org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@RestController
class ParticipantController(
    private val repository: ParticipantRepository
) {

    @GetMapping("/participant")
    fun getParticipant(@RequestParam id: String): Mono<String> {
        return repository.findById(id)
            .map { it.name }
    }

    @GetMapping("/participants")
    fun getAllAsJson(): Flux<String> {
        return repository.findAll()
            .map { it.name }
    }

    @GetMapping("/sse", produces = [TEXT_EVENT_STREAM_VALUE])
    fun getAllAsSse(): Flux<String> {
        return Flux.range(1, 100).map { "$FREEZE $it" }.delayElements(Duration.ofMillis(100))
    }
}
