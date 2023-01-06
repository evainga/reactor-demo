package com.example.reactordemo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("participants")
data class Participant(
    @Id val id: String,
    val name: String
)
