package com.mycompany.model

import java.time.Instant

/**
 * Response DTO for the REST chat endpoint.
 * Returns the AI response and the active session ID.
 */
data class ChatResponse(
    val message: String,
    val sessionId: String,
    val timestamp: Instant = Instant.now()
)
