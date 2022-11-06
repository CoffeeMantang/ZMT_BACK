package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {

    // boardId로 BoardEntity 가져오기
    BoardEntity findByBoardId(int boardId);

    Page<BoardEntity> findByTypeOrderByDateDesc(int type, Pageable pageable);

    Page<BoardEntity> findByMemberIdOrderByDateDesc(int memberId, Pageable pageable);
}
