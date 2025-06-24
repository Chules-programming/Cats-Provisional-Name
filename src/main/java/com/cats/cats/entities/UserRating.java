package com.cats.cats.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_ratings")
public class UserRating {
    @Id
    private ObjectId id;
    private ObjectId raterId;
    private ObjectId ratedId;
    private int rating;
    private ObjectId adoptionId;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getRaterId() {
        return raterId;
    }

    public void setRaterId(ObjectId raterId) {
        this.raterId = raterId;
    }

    public ObjectId getRatedId() {
        return ratedId;
    }

    public void setRatedId(ObjectId ratedId) {
        this.ratedId = ratedId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public ObjectId getAdoptionId() {
        return adoptionId;
    }

    public void setAdoptionId(ObjectId adoptionId) {
        this.adoptionId = adoptionId;
    }
}
