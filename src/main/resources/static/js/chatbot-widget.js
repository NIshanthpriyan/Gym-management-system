/**
 * AI Gym Assistant - Global Floating Widget Script
 * Injects modern floating chat button and popup window on member pages
 */

(function () {
    // Avoid double initialization
    if (document.getElementById('gym-chatbot-container')) return;

    // Helper functions for authentication token and username
    function getAuthToken() {
        return localStorage.getItem('gym_token') || localStorage.getItem('token') || (typeof getToken === 'function' ? getToken() : null);
    }

    function getAuthUsername() {
        return localStorage.getItem('gym_username') || localStorage.getItem('username') || (typeof getUsername === 'function' ? getUsername() : 'Member');
    }

    // Create widget HTML markup
    const widgetContainer = document.createElement('div');
    widgetContainer.id = 'gym-chatbot-container';
    widgetContainer.innerHTML = `
        <!-- Floating Chatbot Action Button -->
        <div id="gym-chatbot-btn" class="chatbot-floating-btn" title="Chat with AI Gym Assistant">
            <i class="fas fa-robot"></i>
            <span class="badge-ping"></span>
        </div>

        <!-- Floating Chatbot Popup Window -->
        <div id="gym-chatbot-window" class="chatbot-popup-window">
            <div class="chat-box-header">
                <div class="d-flex align-items-center gap-3">
                    <div class="chat-avatar-mascot">
                        <i class="fas fa-dumbbell"></i>
                    </div>
                    <div>
                        <h6 class="m-0 fw-bold text-white d-flex align-items-center gap-2">
                            AI Gym Assistant <span class="badge bg-success" style="font-size: 9px; padding: 2px 6px;">ONLINE</span>
                        </h6>
                        <small class="text-secondary" style="font-size: 11px;">Fitness, Nutrition & Account AI</small>
                    </div>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <button id="widget-btn-clear" class="btn btn-sm btn-link text-secondary p-1" title="Clear Chat History">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                    <a href="/member/chatbot.html" class="btn btn-sm btn-link text-secondary p-1" title="Open Fullscreen Chat">
                        <i class="fas fa-expand-alt"></i>
                    </a>
                    <button id="widget-btn-close" class="btn btn-sm btn-link text-secondary p-1" title="Close Chat">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            </div>

            <!-- Messages Area -->
            <div id="widget-chat-messages" class="chat-messages-container">
                <!-- Welcome Bot Message -->
                <div class="chat-msg-row bot">
                    <div class="chat-avatar-mascot" style="width: 32px; height: 32px; font-size: 14px;">
                        <i class="fas fa-robot"></i>
                    </div>
                    <div>
                        <div class="chat-bubble">
                            <p>👋 <strong>Hi there! I am your AI Gym Assistant.</strong></p>
                            <p>Ask me anything about your <strong>membership</strong>, <strong>attendance</strong>, <strong>workout plans</strong>, <strong>exercises</strong>, or <strong>healthy diets</strong>!</p>
                        </div>
                        <span class="chat-msg-time">Just now</span>
                    </div>
                </div>
            </div>

            <!-- Suggestion Chips -->
            <div id="widget-chat-suggestions" class="chat-suggestions-wrapper">
                <button class="chat-chip-btn" data-query="What is my membership status?"><i class="fas fa-id-badge"></i> Membership</button>
                <button class="chat-chip-btn" data-query="How is my attendance this month?"><i class="fas fa-calendar-check"></i> Attendance</button>
                <button class="chat-chip-btn" data-query="Suggest a 3-day workout split"><i class="fas fa-dumbbell"></i> Workout Split</button>
                <button class="chat-chip-btn" data-query="High protein vegetarian meal ideas"><i class="fas fa-utensils"></i> High Protein Diet</button>
                <button class="chat-chip-btn" data-query="Who is my assigned trainer?"><i class="fas fa-user-tie"></i> My Trainer</button>
                <button class="chat-chip-btn" data-query="What are the gym timings?"><i class="fas fa-clock"></i> Gym Timings</button>
            </div>

            <!-- Input Bar -->
            <div class="chat-input-wrapper">
                <button id="widget-btn-voice" class="chat-action-btn chat-voice-btn" title="Voice Typing">
                    <i class="fas fa-microphone"></i>
                </button>
                <input type="text" id="widget-chat-input" class="chat-input-field" placeholder="Ask anything about gym, diet, workout..." autocomplete="off">
                <button id="widget-btn-send" class="chat-action-btn chat-send-btn" title="Send Message">
                    <i class="fas fa-paper-plane"></i>
                </button>
            </div>
        </div>
    `;

    document.body.appendChild(widgetContainer);

    // Elements
    const chatBtn = document.getElementById('gym-chatbot-btn');
    const chatWindow = document.getElementById('gym-chatbot-window');
    const btnClose = document.getElementById('widget-btn-close');
    const btnClear = document.getElementById('widget-btn-clear');
    const btnSend = document.getElementById('widget-btn-send');
    const btnVoice = document.getElementById('widget-btn-voice');
    const chatInput = document.getElementById('widget-chat-input');
    const messagesContainer = document.getElementById('widget-chat-messages');
    const suggestionsContainer = document.getElementById('widget-chat-suggestions');

    let isListening = false;
    let recognition = null;

    // Toggle Chat Window
    chatBtn.addEventListener('click', () => {
        const isHidden = chatWindow.style.display === 'none' || !chatWindow.style.display;
        chatWindow.style.display = isHidden ? 'flex' : 'none';
        if (isHidden) {
            chatInput.focus();
            scrollToBottom();
            loadChatHistory();
        }
    });

    btnClose.addEventListener('click', () => {
        chatWindow.style.display = 'none';
    });

    // Send on button click or Enter key
    btnSend.addEventListener('click', sendMessage);
    chatInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

    // Suggestion chips click
    suggestionsContainer.addEventListener('click', (e) => {
        const chip = e.target.closest('.chat-chip-btn');
        if (chip) {
            const query = chip.getAttribute('data-query');
            if (query) {
                chatInput.value = query;
                sendMessage();
            }
        }
    });

    // Clear Chat History
    btnClear.addEventListener('click', async () => {
        if (!confirm('Are you sure you want to clear your AI chat history?')) return;
        const token = getAuthToken();
        if (token) {
            try {
                await fetch('/api/chat/clear', {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });
            } catch (err) {
                console.error('Error clearing chat history:', err);
            }
        }
        messagesContainer.innerHTML = `
            <div class="chat-msg-row bot">
                <div class="chat-avatar-mascot" style="width: 32px; height: 32px; font-size: 14px;">
                    <i class="fas fa-robot"></i>
                </div>
                <div>
                    <div class="chat-bubble">
                        <p>🧹 <em>Chat history cleared.</em></p>
                        <p>How can I assist you with your fitness journey today?</p>
                    </div>
                    <span class="chat-msg-time">Just now</span>
                </div>
            </div>
        `;
    });

    // Speech-to-Text (Voice Typing)
    if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
        const SpeechRec = window.SpeechRecognition || window.webkitSpeechRecognition;
        recognition = new SpeechRec();
        recognition.continuous = false;
        recognition.interimResults = false;
        recognition.lang = 'en-US';

        recognition.onstart = () => {
            isListening = true;
            btnVoice.classList.add('listening');
            chatInput.placeholder = 'Listening... Speak now';
        };

        recognition.onresult = (event) => {
            const transcript = event.results[0][0].transcript;
            chatInput.value = transcript;
            setTimeout(() => sendMessage(), 400);
        };

        recognition.onerror = () => {
            stopVoice();
        };

        recognition.onend = () => {
            stopVoice();
        };

        btnVoice.addEventListener('click', () => {
            if (isListening) {
                recognition.stop();
            } else {
                try {
                    recognition.start();
                } catch (e) {
                    console.warn(e);
                }
            }
        });
    } else {
        btnVoice.style.display = 'none';
    }

    function stopVoice() {
        isListening = false;
        btnVoice.classList.remove('listening');
        chatInput.placeholder = 'Ask anything about gym, diet, workout...';
    }

    // Send message to Spring Boot AI Chatbot API
    async function sendMessage() {
        const text = chatInput.value.trim();
        if (!text) return;

        // Append User Message to UI
        appendMessage('user', text, new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }));
        chatInput.value = '';
        btnSend.disabled = true;

        // Show Typing Indicator
        const typingIndicator = showTypingIndicator();
        scrollToBottom();

        const token = getAuthToken();
        const headers = { 'Content-Type': 'application/json' };
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        try {
            const res = await fetch('/api/chat/send', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify({ message: text, sessionId: 'session-' + getAuthUsername() })
            });

            removeTypingIndicator(typingIndicator);
            btnSend.disabled = false;

            if (res.ok) {
                const data = await res.json();
                appendMessage('bot', data.reply, new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }), data.isMedicalSafetyNotice);
                
                // Update Suggestion Chips if provided
                if (data.suggestions && data.suggestions.length > 0) {
                    updateSuggestions(data.suggestions);
                }
            } else {
                appendMessage('bot', '⚠️ Sorry, I encountered an issue communicating with the fitness AI service. Please make sure you are logged in.', 'Now');
            }
        } catch (err) {
            removeTypingIndicator(typingIndicator);
            btnSend.disabled = false;
            console.error('Chat error:', err);
            appendMessage('bot', '⚠️ Network error. Unable to reach the Gym Assistant service.', 'Now');
        }

        scrollToBottom();
    }

    // Load past chat history from backend
    async function loadChatHistory() {
        const token = getAuthToken();
        if (!token) return;

        try {
            const res = await fetch('/api/chat/history', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (res.ok) {
                const history = await res.json();
                if (history && history.length > 0) {
                    messagesContainer.innerHTML = '';
                    history.forEach(item => {
                        const timeStr = item.timestamp ? new Date(item.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '';
                        appendMessage('user', item.userMessage, timeStr);
                        appendMessage('bot', item.botResponse, timeStr);
                    });
                    scrollToBottom();
                }
            }
        } catch (err) {
            console.warn('Could not load chat history:', err);
        }
    }

    function appendMessage(sender, text, timeStr, isSafety = false) {
        const row = document.createElement('div');
        row.className = `chat-msg-row ${sender}`;

        const formattedText = formatMarkdown(text);

        if (sender === 'bot') {
            row.innerHTML = `
                <div class="chat-avatar-mascot" style="width: 32px; height: 32px; font-size: 14px; ${isSafety ? 'background: linear-gradient(135deg, #ef4444, #f59e0b);' : ''}">
                    <i class="fas ${isSafety ? 'fa-shield-alt' : 'fa-robot'}"></i>
                </div>
                <div>
                    <div class="chat-bubble" ${isSafety ? 'style="border-color: rgba(239, 68, 68, 0.4); background: rgba(239, 68, 68, 0.1);"' : ''}>
                        ${formattedText}
                    </div>
                    <span class="chat-msg-time">${timeStr || 'Just now'}</span>
                </div>
            `;
        } else {
            row.innerHTML = `
                <div class="chat-user-avatar">
                    <i class="fas fa-user"></i>
                </div>
                <div>
                    <div class="chat-bubble">
                        ${escapeHtml(text)}
                    </div>
                    <span class="chat-msg-time">${timeStr || 'Just now'}</span>
                </div>
            `;
        }

        messagesContainer.appendChild(row);
    }

    function showTypingIndicator() {
        const indicator = document.createElement('div');
        indicator.id = 'chat-typing-indicator';
        indicator.className = 'chat-msg-row bot';
        indicator.innerHTML = `
            <div class="chat-avatar-mascot" style="width: 32px; height: 32px; font-size: 14px;">
                <i class="fas fa-robot"></i>
            </div>
            <div>
                <div class="chat-bubble">
                    <div class="typing-dots">
                        <div class="typing-dot"></div>
                        <div class="typing-dot"></div>
                        <div class="typing-dot"></div>
                    </div>
                </div>
            </div>
        `;
        messagesContainer.appendChild(indicator);
        return indicator;
    }

    function removeTypingIndicator(indicator) {
        if (indicator && indicator.parentNode) {
            indicator.parentNode.removeChild(indicator);
        }
    }

    function updateSuggestions(suggestions) {
        suggestionsContainer.innerHTML = '';
        suggestions.forEach(s => {
            const btn = document.createElement('button');
            btn.className = 'chat-chip-btn';
            btn.setAttribute('data-query', s);
            btn.innerHTML = `<i class="fas fa-bolt"></i> ${escapeHtml(s)}`;
            suggestionsContainer.appendChild(btn);
        });
    }

    function formatMarkdown(text) {
        if (!text) return '';
        let html = escapeHtml(text);
        
        // Bold: **text**
        html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
        // Italics: *text*
        html = html.replace(/\*(.*?)\*/g, '<em>$1</em>');
        // Inline code: `text`
        html = html.replace(/`(.*?)`/g, '<code>$1</code>');
        // Line breaks
        html = html.replace(/\n/g, '<br>');
        
        return html;
    }

    function escapeHtml(str) {
        return (str || '')
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    function scrollToBottom() {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
})();
