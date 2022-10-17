package com.coffeemantang.ZMT_BACK.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 리뷰 답글 기능을 위한 DTO
public class ReviewCommentDTO {
    private int reviewcommentId;
    private int reviewId;
    private int memberId;
    private String content;
}
