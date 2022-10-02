package com.coffeemantang.ZMT_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review")
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "review_id")
    private int reviewId;
    @Column(name = "member_id")
    private int memberId;
    @Column(name = "store_id")
    private String storeId;
    @Column(name = "date")
    private LocalDateTime date; // 작성시간
    @Column(name = "score")
    private int score; // 점수 -> 5점 만점의 별점으로 부여
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "recommend")
    private int recommend;
}
