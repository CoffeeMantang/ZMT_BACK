package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.BookmarkEntitiy;
import com.coffeemantang.ZMT_BACK.model.EmailTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<BookmarkEntitiy, Integer> {
    long countByMemberIdAndStoreId(int memberId, int storeId);
}
