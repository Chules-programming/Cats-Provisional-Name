package com.cats.cats.services;

import com.cats.cats.entities.Review;
import com.cats.cats.repository.ReviewRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> findAll() {
        return reviewRepository.findAllByOrderByDateDesc();
    }
    public double getAverageRatingByUserId(ObjectId userId) {
        List<Review> userReviews = reviewRepository.findByUserId(userId);
        if (userReviews.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (Review review : userReviews) {
            sum += review.getRating();
        }
        return sum / userReviews.size();
    }
}
