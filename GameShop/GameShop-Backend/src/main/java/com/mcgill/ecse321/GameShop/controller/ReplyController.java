package com.mcgill.ecse321.GameShop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mcgill.ecse321.GameShop.dto.ReplyDto.ReplyRequestDto;
import com.mcgill.ecse321.GameShop.dto.ReplyDto.ReplyResponseDto;
import com.mcgill.ecse321.GameShop.dto.ReviewDto.ReviewResponseDto;
import com.mcgill.ecse321.GameShop.model.Reply;
import com.mcgill.ecse321.GameShop.model.Review;
import com.mcgill.ecse321.GameShop.service.ReplyService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    /**
     * Create a new reply.
     * 
     * @param request the reply request data transfer object containing the details
     *                of the reply to be created
     * @return the response data transfer object containing the details of the
     *         created reply
     */
    @PostMapping("/replies")
    public ReplyResponseDto createReply(@Valid @RequestBody ReplyRequestDto request) {
        // Create the reply
        Reply reply = replyService.createReply(
                request.getReplyDate(),
                request.getDescription(),
                request.getReviewRating(),
                request.getReviewId(),
                request.getManagerEmail());
        return new ReplyResponseDto(reply);
    }

    /**
     * Get a reply by ID.
     * 
     * @param rid the ID of the reply to retrieve
     * @return the response data transfer object containing the details of the
     *         retrieved reply
     */
    @GetMapping("/replies/{rid}")
    public ReplyResponseDto getReplyById(@PathVariable int rid) {
        Reply reply = replyService.getReplyById(rid);
        return new ReplyResponseDto(reply);
    }

    /**
     * Update an existing reply.
     * 
     * @param id      the ID of the reply to update
     * @param request the reply request data transfer object containing the updated
     *                details of the reply
     * @return the response data transfer object containing the details of the
     *         updated reply
     */
    @PutMapping("/replies/{id}")
    public ReplyResponseDto updateReply(@PathVariable int id, @RequestBody ReplyRequestDto request) {
        // Update the reply
        Reply reply = replyService.updateReply(
                id,
                request.getDescription(),
                request.getReviewRating());
        return new ReplyResponseDto(reply);
    }

    /**
     * Get the review associated with a reply.
     * 
     * @param replyId the ID of the reply to retrieve the associated review from
     * @return the review response data transfer object containing the details of
     *         the associated review
     */
    @GetMapping("/replies/{replyId}/review")
    public ReviewResponseDto getReviewByReplyId(@PathVariable int replyId) {
        // Get the review associated with the reply
        Review review = replyService.getReviewByReplyId(replyId);
        return new ReviewResponseDto(review);
    }
}
