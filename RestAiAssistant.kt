package com.mycompany.ai

import com.mycompany.data.repository.CustomerRepository
import com.mycompany.data.repository.ServicePlanRepository
import com.mycompany.data.repository.SubscriptionRepository
import com.mycompany.data.service.SubscriptionService
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage
import io.quarkiverse.langchain4j.RegisterAiService
import io.quarkiverse.langchain4j.ToolBox
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.enterprise.context.ApplicationScoped

/**
 * REST-based AI Assistant interface using ApplicationScoped with @MemoryId.
 * This enables server-side memory persistence for stateless REST requests.
 */
@RegisterAiService
@RegisterForReflection
@ApplicationScoped
interface RestAiAssistant {

    /**
     * The @SystemMessage sets the persona and instructions for the model.
     */
    @SystemMessage(
        """
        You are a customer support agent of a streaming service company 'Anyflix'.
        Do not reveal your chain-of-thought, reasoning steps, or internal analysis.
        Provide only the final answer.
        You are friendly, polite and concise.
        You need to verify the credentials of the customer that you are servicing before you perform any actions.
        And a customer can be enrolled into a TRAIL plan only once.
        If the question is unrelated to service subscription, you should politely redirect the customer to the right department.

        Today is {current_date}.
        """
    )

    /**
     * Chat method with memory ID support.
     * LangChain4j uses the memoryId to retrieve the conversation context from its store.
     */
    @ToolBox(
        CustomerRepository::class,
        ServicePlanRepository::class,
        SubscriptionRepository::class,
        SubscriptionService::class
    )
    fun chat(
        @MemoryId memoryId: String,
        @UserMessage userMessage: String
    ): String
}
