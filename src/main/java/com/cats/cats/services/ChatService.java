package com.cats.cats.services;

import com.cats.cats.ChatWebSocketHandler;
import com.cats.cats.entities.ChatMessage;
import com.cats.cats.entities.Conversation;
import com.cats.cats.repository.ChatMessageRepository;
import com.cats.cats.repository.ConversationRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatWebSocketHandler webSocketHandler;

    @Autowired
    public ChatService(
            ConversationRepository conversationRepository,
            ChatMessageRepository chatMessageRepository,
            ChatWebSocketHandler webSocketHandler
    ) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * Envía un mensaje en tiempo real a ambos usuarios involucrados en la conversación
     * y lo persiste en la base de datos.
     */
    public void sendRealTimeMessage(ChatMessage message) {
        // Envío por WebSocket
        webSocketHandler.sendMessageToUser(message.getSenderId(), message);
        webSocketHandler.sendMessageToUser(getReceiverId(message), message);
        // Persistencia
        chatMessageRepository.save(message);
    }

    /**
     * Obtiene el receptor del mensaje en función de la conversación y el remitente.
     */
    private ObjectId getReceiverId(ChatMessage message) {
        Conversation conv = conversationRepository.findById(message.getConversationId())
                .orElseThrow(() -> new IllegalArgumentException("Conversación no encontrada"));
        return conv.getUserId1().equals(message.getSenderId())
                ? conv.getUserId2()
                : conv.getUserId1();
    }

    /**
     * Busca una conversación existente o crea una nueva si no existe.
     */
    public Conversation findOrCreateConversation(ObjectId userId1, ObjectId userId2, ObjectId catId) {
        Conversation existing = conversationRepository.findByUserIds(userId1, userId2, catId);
        if (existing != null) return existing;

        Conversation newConv = new Conversation();
        newConv.setUserId1(userId1);
        newConv.setUserId2(userId2);
        newConv.setCatId(catId);
        newConv.setCreatedAt(new Date());
        newConv.setLastMessage("Conversación iniciada");

        return conversationRepository.save(newConv);
    }

    /**
     * Guarda (o actualiza) una conversación.
     */
    public void saveConversation(Conversation conversation) {
        conversationRepository.save(conversation);
    }

    /**
     * Retorna los mensajes ordenados cronológicamente para una conversación.
     */
    public List<ChatMessage> getMessagesByConversationOrdered(ObjectId conversationId) {
        return chatMessageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
    }

    /**
     * Retorna una conversación por ID o null si no existe.
     */
    public Conversation getConversationById(ObjectId conversationId) {
        return conversationRepository.findById(conversationId).orElse(null);
    }

    /**
     * Retorna todas las conversaciones en las que participa el usuario.
     */
    public List<Conversation> findByUserId(ObjectId userId) {
        return conversationRepository.findByUserId1OrUserId2(userId, userId);
    }
}

