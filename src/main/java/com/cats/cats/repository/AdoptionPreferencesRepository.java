package com.cats.cats.repository;

import com.cats.cats.entities.AdoptionPreferences;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface AdoptionPreferencesRepository extends MongoRepository<AdoptionPreferences, ObjectId> {
    List<AdoptionPreferences> findByUserId(ObjectId userId);
}
