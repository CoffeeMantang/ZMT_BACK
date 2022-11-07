package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.BookmarkDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.model.BookmarkEntity;
import com.coffeemantang.ZMT_BACK.persistence.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    // 즐겨찾기 추가
    public BookmarkEntity addBookmark(int memberId, StoreDTO storeDTO) {

        BookmarkEntity bookmarkEntity = new BookmarkEntity();
        bookmarkEntity.setMemberId(memberId);
        bookmarkEntity.setStoreId(storeDTO.getStoreId());

        return bookmarkRepository.save(bookmarkEntity);

    }

    // 즐겨찾기 해제
    public void deleteBookmark(int memberId, BookmarkDTO bookmarkDTO) {

        if(memberId != bookmarkDTO.getMemberId()) {
            log.warn("StoreService.updateOption() : 로그인된 유저와 북마크 소유자가 다릅니다.");
            throw new RuntimeException("StoreService.updateOption() : 로그인된 유저와 북마크 소유자가 다릅니다.");
        }

        bookmarkRepository.deleteByMemberIdAndStoreId(bookmarkDTO.getMemberId(), bookmarkDTO.getStoreId());

    }

    // 즐겨찾기 목록
    public List<BookmarkDTO> viewBookmarkList(int memberId) {

        List<BookmarkEntity> bookmarkEntityList = bookmarkRepository.findAllByMemberId(memberId);
        List<BookmarkDTO> bookmarkDTOList = new ArrayList<>();
        for (BookmarkEntity bookmarkEntity : bookmarkEntityList) {
            BookmarkDTO bookmarkDTO = new BookmarkDTO(bookmarkEntity);
            bookmarkDTOList.add(bookmarkDTO);
        }

        return bookmarkDTOList;

    }

}
