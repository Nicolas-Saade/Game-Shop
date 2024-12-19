package com.mcgill.ecse321.GameShop.dto.ReplyDto;

import java.util.List;

public class ReplyListDto {

    private List<ReplySummaryDto> replies;

    public ReplyListDto(List<ReplySummaryDto> replies) {
        this.replies = replies;
    }

    // Getters and Setters

    public List<ReplySummaryDto> getReplies() {
        return replies;
    }

    public void setReplies(List<ReplySummaryDto> replies) {
        this.replies = replies;
    }
}