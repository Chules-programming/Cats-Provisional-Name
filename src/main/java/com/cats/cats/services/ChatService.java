package com.cats.cats.services;

import com.cats.cats.entities.ChatMessage;
import com.cats.cats.entities.Conversation;
import com.cats.cats.repository.ChatMessageRepository;
import com.cats.cats.repository.ConversationRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    /**
     * Busca una conversación existente entre dos usuarios sobre un gato específico,
     * o crea una nueva si no existe.
     */
    public Conversation findOrCreateConversation(ObjectId user1Id, ObjectId user2Id, ObjectId catId) {
        Conversation conversation = conversationRepository.findByUserIds(user1Id, user2Id, catId);

        if (conversation == null) {
            conversation = new Conversation();
            conversation.setUserId1(user1Id);
            conversation.setUserId2(user2Id);
            conversation.setCatId(catId);
            conversation.setCreatedAt(new Date());
            conversationRepository.save(conversation);
        }

        return conversation;
    }


    /**
     * Guarda una nueva conversación.
     */
    public Conversation saveConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    /**
     * Guarda un nuevo mensaje de chat.
     */
    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    /**
     * Devuelve la lista de conversaciones en las que el usuario participa.
     */
    public List<Conversation> findByUserId(ObjectId userId) {
        return conversationRepository.findByUserId1OrUserId2(userId, userId);
    }

    /**
     * Devuelve todos los mensajes de una conversación ordenados cronológicamente (ascendente).
     */
    public List<ChatMessage> getMessagesByConversationOrdered(ObjectId conversationId) {
        return chatMessageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
    }
    // Add this method to get conversation by ID
    public Conversation getConversationById(ObjectId conversationId) {
        Optional<Conversation> conversation = conversationRepository.findById(conversationId);
        return conversation.orElse(null);
    }
}