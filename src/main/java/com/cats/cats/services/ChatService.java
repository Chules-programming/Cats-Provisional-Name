package com.cats.cats.services;
import com.cats.cats.entities.ChatMessage;
import com.cats.cats.entities.Conversation;
import com.cats.cats.repository.ChatMessageRepository;
import com.cats.cats.repository.ConversationRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public List<Conversation> findByUserId(ObjectId userId) {
        return conversationRepository.findByUserId1OrUserId2(userId, userId);
    }

    public Conversation saveConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }
}
