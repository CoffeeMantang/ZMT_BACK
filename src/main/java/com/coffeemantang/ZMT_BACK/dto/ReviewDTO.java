package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.ReviewEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 리뷰 관련 기능을 위한 DTO
public class ReviewDTO {
    private int reviewId;
    private int memberId;
    private String storeId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime date; // 작성 시간
    private int score; // 별점
    private String title; // 제목
    private String content; // 리뷰내용
    private int recommend; // 추천갯수
    private List<MultipartFile> files; // 넘어올 파일
    private List<String> reviewFiles; // 파일의 경로 + 파일명
    private List<String> images;
    private String nickname;

    public boolean checkNull(){
        if(this.files == null){
            return false;
        }else{
            return true;
        }
    }

    // ReviewEntity를 ReviewDTO로
    public ReviewDTO (final ReviewEntity reviewEntity){
        this.reviewId = reviewEntity.getReviewId();
        this.memberId = reviewEntity.getMemberId();
        this.storeId = reviewEntity.getStoreId();
        this.date = reviewEntity.getDate();
        this.score = reviewEntity.getScore();
        this.title = reviewEntity.getTitle();
        this.content = reviewEntity.getContent();
        this.recommend = reviewEntity.getRecommend();
    }
}
