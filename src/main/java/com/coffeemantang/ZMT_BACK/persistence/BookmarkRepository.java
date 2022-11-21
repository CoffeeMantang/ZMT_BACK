package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.BookmarkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Integer> {

    // memberId, storeId에 맞는 row가 있으면 count함
    int countByMemberIdAndStoreId(int memberId, String storeId);

    // memberId로 즐겨찾기 목록 가져오기
    Page<BookmarkEntity> findAllByMemberId(int memberId, Pageable pageable);

    // memberId와 storeId로 삭제하기
    @Transactional
    void deleteByMemberIdAndStoreId(int memberId, String storeId);

    // memberID로 북마크 갯수 가져오기
    long countByMemberId(int memberId);
}
