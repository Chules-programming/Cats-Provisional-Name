package com.cats.cats.repository;

import com.cats.cats.entities.Conversation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConversationRepository extends MongoRepository<Conversation, ObjectId> {
    List<Conversation> findByUserId1OrUserId2(ObjectId userId1, ObjectId userId2);
}
