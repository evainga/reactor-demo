package com.example.reactordemo

import org.springframework.data.mongodb.repository.ReactiveMongoRepository


interface ParticipantRepository : ReactiveMongoRepository<Participant, String>
