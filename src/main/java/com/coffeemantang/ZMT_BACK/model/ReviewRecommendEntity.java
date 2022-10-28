package com.coffeemantang.ZMT_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviewrecommend")
public class ReviewRecommendEntity {
    @Id
    @Column(name = "member_id")
    @JoinColumn(name = "member_id")
    private int memberId; // 추천한 사람
    @Column(name = "review_id")
    @JoinColumn(name = "review_id")
    private int reviewId; // 추천한 리뷰
}
