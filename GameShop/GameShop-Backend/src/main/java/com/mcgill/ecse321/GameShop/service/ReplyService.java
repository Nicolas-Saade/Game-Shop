package com.mcgill.ecse321.GameShop.service;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.model.Reply;
import com.mcgill.ecse321.GameShop.model.Reply.ReviewRating;
import com.mcgill.ecse321.GameShop.model.Review;
import com.mcgill.ecse321.GameShop.repository.ManagerRepository;
import com.mcgill.ecse321.GameShop.repository.ReplyRepository;
import com.mcgill.ecse321.GameShop.repository.ReviewRepository;

import jakarta.transaction.Transactional;

@Service
public class ReplyService {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    // Helper method to check if a string is empty or null
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Create a new Reply.
     */
    @Transactional
    public Reply createReply(Date replyDate, String description, ReviewRating reviewRating, int reviewId,
            String managerEmail) {

        // Validate inputs
        if (replyDate == null) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Reply date cannot be null");
        }
        if (isEmpty(description)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Description cannot be empty or null");
        }
        if (reviewRating == null) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Review rating cannot be null");
        }

        // Retrieve the manager by email
        Manager manager = managerRepository.findByEmail(managerEmail);
        if (manager == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("Manager with email %s not found", managerEmail));
        }

        // Retrieve the review by ID
        Review review = reviewRepository.findById(reviewId);
        if (review == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, String.format("Review with ID %d not found", reviewId));
        }

        // Create the reply
        Reply reply = new Reply(replyDate, description, review, manager);
        reply.setReviewRating(reviewRating);

        // Save and return the reply
        return replyRepository.save(reply);
    }

    /**
     * Retrieve a Reply by its ID.
     */
    @Transactional
    public Reply getReplyById(int replyId) {
        // Retrieve the reply by ID
        Reply reply = replyRepository.findById(replyId);
        if (reply == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Reply not found");
        }
        return reply;
    }

    /**
     * Update an existing Reply.
     */
    @Transactional
    public Reply updateReply(int replyId, String description, ReviewRating reviewRating) {
        // Retrieve the reply by ID
        Reply reply = getReplyById(replyId);

        // Update description if provided
        if (!isEmpty(description)) {
            reply.setDescription(description);
        }

        // Update review rating if provided
        if (reviewRating != null) {
            reply.setReviewRating(reviewRating);
        }

        // Save and return the updated reply
        return replyRepository.save(reply);
    }

    /**
     * Retrieve the Review associated with a Reply by Reply ID.
     */
    @Transactional
    public Review getReviewByReplyId(int replyId) {
        // Retrieve the reply by ID
        Reply reply = getReplyById(replyId);
        // Return the associated review
        return reply.getReview();
    }
}