package com.mcgill.ecse321.GameShop.dto.ReplyDto;

import java.sql.Date;

import com.mcgill.ecse321.GameShop.model.Reply.ReviewRating;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReplyRequestDto {

    @NotNull(message = "Reply date cannot be null")
    private Date replyDate;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Review rating cannot be null")
    private ReviewRating reviewRating;

    @NotNull(message = "Review ID cannot be null")
    private Integer reviewId;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Manager email cannot be empty")
    private String managerEmail;

    public ReplyRequestDto() {
    }

    public ReplyRequestDto(Date replyDate, String description, ReviewRating reviewRating, Integer reviewId, String managerEmail) {
        this.replyDate = replyDate;
        this.description = description;
        this.reviewRating = reviewRating;
        this.reviewId = reviewId;
        this.managerEmail = managerEmail;
    }

    // Getters and Setters

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReviewRating getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(ReviewRating reviewRating) {
        this.reviewRating = reviewRating;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }
}
