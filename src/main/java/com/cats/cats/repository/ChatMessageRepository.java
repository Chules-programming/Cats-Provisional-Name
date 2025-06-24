package com.cats.cats.repository;

import com.cats.cats.entities.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId> {

    // Add these methods to fix the errors
    @Query(value = "{ 'conversationId' : ?0 }", sort = "{ 'timestamp' : 1 }")
    List<ChatMessage> findByConversationIdOrderByTimestampAsc(ObjectId conversationId);

    @Query(value = "{ 'conversationId' : ?0 }")
    List<ChatMessage> findByConversationId(ObjectId conversationId);

    @Query(value = "{ 'conversationId' : ?0, 'timestamp' : { $gt: ?1 } }", sort = "{ 'timestamp' : 1 }")
    List<ChatMessage> findByConversationIdAndTimestampGreaterThanOrderByTimestampAsc(
            ObjectId conversationId,
            Date timestamp
    );
}

