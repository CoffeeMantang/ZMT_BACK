package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import com.coffeemantang.ZMT_BACK.model.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {
    Optional<ReviewEntity> findByReviewId(int reviewId);
    // 회원아이디와 가게아이디로 기간 내의 리뷰갯수 가져오기
    @Query(value = "SELECT COUNT(review.review_id) FROM review WHERE " +
            "store_id = :storeId AND member_id = :memberId AND review.date > :date", nativeQuery = true)
    int countByStoreIdAndMemberId(@Param("storeId") String storeId, @Param("memberId") int memberId, @Param("date") LocalDateTime date);

    // 가게아이디로 리뷰리스트 가져오기
    Page<ReviewEntity> findByStoreIdOrderByDateDesc(String storeId, Pageable pageable);

    // 멤버아이디로 리뷰리스트 가져오기
    Page<ReviewEntity> findByMemberIdOrderByDateDesc(int memberId, Pageable pageable);

    // 멤버아이디와 리뷰아이디로 검색
    Long countByMemberIdAndReviewId(int memberId, int reviewId);

    // 평균리뷰점수 가져오기
    @Query(value = "SELECT AVG(score) FROM review WHERE store_id = :storeId", nativeQuery = true)
    Optional<Double> findReviewScoreByStoreId(@Param("storeId") String storeId);

    // 가게의 리뷰 갯수 가져오기
    Long countByStoreId(String storeId);

    // orderlist_id로 한달 내의 리뷰가 작성되었는지 확인하기
    long countByOrderlistIdAndDateBetween(String orderlistId, LocalDateTime start, LocalDateTime end);

}
