package com.mcgill.ecse321.GameShop.dto.ReplyDto;

import com.mcgill.ecse321.GameShop.model.Reply;

public class ReplySummaryDto {

    private int replyId;
    private String description;
    private String managerEmail;

    public ReplySummaryDto(Reply reply) {
        this.replyId = reply.getReply_id();
        this.description = reply.getDescription();
        this.managerEmail = reply.getManager().getEmail();
    }

    // Getters

    public int getReplyId() {
        return replyId;
    }

    public String getDescription() {
        return description;
    }

    public String getManagerEmail() {
        return managerEmail;
    }
}