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
@Table(name = "reviewcomment")
public class ReviewCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "reviewcommentId")
    private int reviewcommentId;
    @Column(name = "review_id")
    @JoinColumn(name = "review_id")
    private int reviewId;
    @Column(name = "member_id")
    @JoinColumn(name = "member_id")
    private int memberId;
    @Column(name = "content")
    private String content;

}
