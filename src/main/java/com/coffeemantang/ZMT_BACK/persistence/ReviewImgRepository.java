package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.ReviewEntity;
import com.coffeemantang.ZMT_BACK.model.ReviewImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewImgRepository extends JpaRepository<ReviewImgEntity, Integer> {
    //리뷰 아이디로 찾기
    List<ReviewImgEntity> findByReviewId(int reviewId);
}
