// Chat application state
let activeSessionId = null;

// DOM elements
const chatMessages = document.getElementById('chatMessages');
const userInput = document.getElementById('userInput');
const sendButton = document.getElementById('sendButton');
const typingIndicator = document.getElementById('typingIndicator');

// Initialize the chat
document.addEventListener('DOMContentLoaded', () => {
    // Focus on input field
    userInput.focus();

    // Add event listeners
    sendButton.addEventListener('click', sendMessage);
    userInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

    // Check for existing session in localStorage
    const savedSession = localStorage.getItem('chatSessionId');
    if (savedSession) {
        console.log('Restoring session:', savedSession);
        activeSessionId = savedSession;
    }
});

/**
 * Sends a message to the chat API
 */
async function sendMessage() {
    const message = userInput.value.trim();

    if (!message) {
        return;
    }

    // Disable input while processing
    setInputEnabled(false);

    // Add user message to UI
    addMessageToUI('user', message);

    // Clear input field
    userInput.value = '';

    // Show typing indicator
    showTypingIndicator(true);

    try {
        // Send request to API
        const payload = {
            message: message
        };

        // Add session ID if we have one
        if (activeSessionId) {
            payload.sessionId = activeSessionId;
        }

        const response = await fetch('/api/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        // Store the session ID returned by server
        if (data.sessionId) {
            if (activeSessionId !== data.sessionId) {
                console.log('New session started:', data.sessionId);
                activeSessionId = data.sessionId;
                localStorage.setItem('chatSessionId', activeSessionId);
            }
        }

        // Add assistant response to UI
        addMessageToUI('assistant', data.message);

    } catch (error) {
        console.error('Error sending message:', error);
        addMessageToUI('assistant',
            '⚠️ Sorry, I encountered an error. Please try again. Error: ' + error.message);
    } finally {
        // Hide typing indicator
        showTypingIndicator(false);

        // Re-enable input
        setInputEnabled(true);
        userInput.focus();
    }
}

/**
 * Adds a message to the UI
 */
function addMessageToUI(role, content) {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message-content ${role}`;

    const bubbleDiv = document.createElement('div');
    bubbleDiv.className = 'message-bubble';

    const contentP = document.createElement('p');
    contentP.textContent = content;

    bubbleDiv.appendChild(contentP);
    messageDiv.appendChild(bubbleDiv);
    chatMessages.appendChild(messageDiv);

    // Scroll to bottom
    scrollToBottom();
}

/**
 * Scrolls the chat to the bottom
 */
function scrollToBottom() {
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

/**
 * Shows or hides the typing indicator
 */
function showTypingIndicator(show) {
    if (show) {
        typingIndicator.classList.add('active');
        scrollToBottom();
    } else {
        typingIndicator.classList.remove('active');
    }
}

/**
 * Enables or disables the input controls
 */
function setInputEnabled(enabled) {
    userInput.disabled = !enabled;
    sendButton.disabled = !enabled;
}

/**
 * Starts a new chat session by clearing local storage and UI
 */
function startNewChat() {
    activeSessionId = null;
    localStorage.removeItem('chatSessionId');

    // Clear messages except welcome
    chatMessages.innerHTML = `
        <div class="welcome-message">
            <div class="message-content assistant">
                <div class="message-bubble">
                    <p>Welcome to Anyflix! 👋 I'm your AI support assistant. How can I help you today?</p>
                </div>
            </div>
        </div>
    `;

    userInput.focus();
}
