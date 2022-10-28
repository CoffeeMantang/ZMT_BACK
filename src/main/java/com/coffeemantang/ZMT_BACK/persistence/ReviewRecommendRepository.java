package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.ReviewImgEntity;
import com.coffeemantang.ZMT_BACK.model.ReviewRecommendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRecommendRepository extends JpaRepository<ReviewRecommendEntity, Integer> {
    // 아이디와 리뷰아이디로 갯수 가져오기
    long countByMemberIdAndReviewId(int memberId, int reviewId);

    // 아이디와 리뷰아이디로 엔티티 가져오기
    Optional<ReviewRecommendEntity> findByMemberIdAndReviewId(int memberId, int reviewId);
}
