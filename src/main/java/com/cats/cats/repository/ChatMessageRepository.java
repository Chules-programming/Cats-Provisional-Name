package com.cats.cats.repository;
import com.cats.cats.entities.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId> {
    List<ChatMessage> findByConversationId(ObjectId conversationId);
}
