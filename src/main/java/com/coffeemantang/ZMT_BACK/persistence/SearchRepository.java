package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.ReviewEntity;
import com.coffeemantang.ZMT_BACK.model.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchRepository extends JpaRepository<SearchEntity, Integer> {
    List<SearchEntity> findTop10ByMemberIdOrderByTimeDesc(int memberId);

    Optional<SearchEntity> findTop1ByMemberIdOrderByTimeDesc(int memberId);
}
