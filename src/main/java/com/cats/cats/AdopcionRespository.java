package com.cats.cats;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdopcionRespository extends MongoRepository<Adopcion, ObjectId> {
}
