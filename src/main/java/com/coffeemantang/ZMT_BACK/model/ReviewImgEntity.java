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
@Table(name = "reviewimg")
public class ReviewImgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "reviewimg_id")
    private int reviewimgId;
    @Column(name = "review_id")
    @JoinColumn(name = "review_id")
    private int reviewId;
    @Column(name = "path")
    private String path;
}
