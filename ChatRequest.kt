package com.mycompany.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Request DTO for the REST chat endpoint.
 * Contains the user message and an optional session ID.
 */
data class ChatRequest @JsonCreator constructor(
    @JsonProperty("message") val message: String,
    @JsonProperty("sessionId") val sessionId: String? = null
)
