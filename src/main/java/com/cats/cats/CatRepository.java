package com.cats.cats;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatRepository extends MongoRepository<Cat, ObjectId> {
    List<Cat> findByAdoptedFalse();
    List<Cat> findByBreedIgnoreCaseAndAdoptedFalse(String breed);

    @Query("{ 'name' : { $regex: ?0, $options: 'i' } }")
    Cat findByName(String name);
    Cat findByNameIgnoreCase(String name);
}
