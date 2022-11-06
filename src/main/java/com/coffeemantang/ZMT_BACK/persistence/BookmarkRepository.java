package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.BookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Integer> {

    // memberId, storeId에 맞는 row가 있으면 count함
    int countByMemberIdAndStoreId(int memberId, String storeId);

    // memberId로 즐겨찾기 목록 가져오기
    List<BookmarkEntity> findAllByMemberId(int memberId);
}
