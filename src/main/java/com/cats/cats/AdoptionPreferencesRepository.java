package com.cats.cats;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface AdoptionPreferencesRepository extends MongoRepository<AdoptionPreferences, ObjectId> {
    List<AdoptionPreferences> findByUserId(ObjectId userId);
}
