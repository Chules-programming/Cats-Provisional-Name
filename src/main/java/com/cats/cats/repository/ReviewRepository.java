package com.cats.cats.repository;

import com.cats.cats.entities.Review;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, ObjectId> {
    List<Review> findAllByOrderByDateDesc();
    List<Review> findByUserId(ObjectId userId);
}
