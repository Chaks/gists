package com.mycompany.resource

import com.mycompany.ai.RestAiAssistant
import com.mycompany.model.ChatRequest
import com.mycompany.model.ChatResponse
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.infrastructure.Infrastructure
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.Logger
import java.util.UUID

/**
 * REST resource for handling chat requests.
 * Manages session IDs to maintain conversation context.
 */
@Path("/api/chat")
@ApplicationScoped
class ChatResource {

    @Inject
    lateinit var restAiAssistant: RestAiAssistant

    private val logger: Logger = Logger.getLogger(ChatResource::class.java)

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun chat(request: ChatRequest): Uni<ChatResponse> {
        // Use provided session ID or generate a new one
        val sessionId = request.sessionId ?: UUID.randomUUID().toString()

        logger.info("Processing chat request for session: $sessionId")

        return Uni.createFrom().item {
            try {
                // Call AI assistant with the session ID
                val response = restAiAssistant.chat(sessionId, request.message)

                logger.info("AI response for session $sessionId: ${response.take(50)}...")
                ChatResponse(message = response, sessionId = sessionId)
            } catch (e: Exception) {
                logger.error("Error in AI chat", e)
                throw e
            }
        }
            .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
            .onFailure().recoverWithItem { e ->
                ChatResponse(
                    message = "Sorry, I encountered an error: ${e.message}",
                    sessionId = sessionId
                )
            }
    }
}
