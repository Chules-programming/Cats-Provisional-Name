package com.cats.cats.repository;

import com.cats.cats.entities.Adopcion;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdopcionRepository extends MongoRepository<Adopcion, ObjectId> {
    boolean existsByUserId(ObjectId userId);
}
