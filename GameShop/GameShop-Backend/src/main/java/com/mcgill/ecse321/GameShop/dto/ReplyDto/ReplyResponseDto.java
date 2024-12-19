package com.mcgill.ecse321.GameShop.dto.ReplyDto;

import java.sql.Date;

import com.mcgill.ecse321.GameShop.model.Reply;
import com.mcgill.ecse321.GameShop.model.Reply.ReviewRating;

public class ReplyResponseDto {

    private int replyId;
    private Date replyDate;
    private String description;
    private ReviewRating reviewRating;
    private int reviewId;
    private String managerEmail;

    public ReplyResponseDto() {
    }

    public ReplyResponseDto(Reply reply) {
        this.replyId = reply.getReply_id();
        this.replyDate = reply.getReplyDate();
        this.description = reply.getDescription();
        this.reviewRating = reply.getReviewRating();
        this.reviewId = reply.getReview().getReview_id();
        this.managerEmail = reply.getManager().getEmail();
    }

    // Getters

    public int getReplyId() {
        return replyId;
    }

    public Date getReplyDate() {
        return replyDate;
    }

    public String getDescription() {
        return description;
    }

    public ReviewRating getReviewRating() {
        return reviewRating;
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getManagerEmail() {
        return managerEmail;
    }
}
