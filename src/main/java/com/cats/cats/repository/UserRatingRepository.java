package com.cats.cats.repository;

import com.cats.cats.entities.UserRating;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserRatingRepository extends MongoRepository<UserRating, ObjectId> {
    List<UserRating> findByRatedId(ObjectId ratedId);
}
