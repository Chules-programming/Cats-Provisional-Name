package com.cats.cats.repository;

import com.cats.cats.entities.Conversation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ConversationRepository extends MongoRepository<Conversation, ObjectId> {

    /**
     * Busca una conversación entre dos usuarios específicos y un gato determinado,
     * sin importar el orden de los usuarios.
     */
    @Query("{ $or: [ " +
            "{ userId1: ?0, userId2: ?1, catId: ?2 }, " +
            "{ userId1: ?1, userId2: ?0, catId: ?2 } " +
            "] }")
    Conversation findByUserIds(ObjectId user1Id, ObjectId user2Id, ObjectId catId);

    /**
     * Busca todas las conversaciones donde el usuario participa como userId1 o userId2.
     */
    List<Conversation> findByUserId1OrUserId2(ObjectId userId1, ObjectId userId2);
}

