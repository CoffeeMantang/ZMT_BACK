package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.BookmarkDTO;
import com.coffeemantang.ZMT_BACK.dto.MemberDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.model.BookmarkEntity;
import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import com.coffeemantang.ZMT_BACK.persistence.BookmarkRepository;
import com.coffeemantang.ZMT_BACK.persistence.ReviewRepository;
import com.coffeemantang.ZMT_BACK.persistence.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    @Autowired
    private final StoreRepository storeRepository;
    @Autowired
    private final ReviewRepository reviewRepository;

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
    public BookmarkDTO viewBookmarkList(int memberId, Pageable pageable) {

        BookmarkDTO result = new BookmarkDTO();
        Page<BookmarkEntity> pageList = bookmarkRepository.findAllByMemberId(memberId, pageable);
        List<BookmarkEntity> bookmarkEntityList = pageList.getContent();
        long count = bookmarkRepository.countByMemberId(memberId);
        List<BookmarkDTO> bookmarkDTOList = new ArrayList<>();
        for (BookmarkEntity bookmarkEntity : bookmarkEntityList) {
            BookmarkDTO bookmarkDTO = new BookmarkDTO(bookmarkEntity);
            // 가게이름 가져오기
            StoreEntity entity = storeRepository.findByStoreId(bookmarkEntity.getStoreId());
            bookmarkDTO.setStoreName(entity.getName());
            // 썸네일 넣기
            bookmarkDTO.setThumb("http://localhost:8080/images/store/" + entity.getStoreId() + ".jpg");
            // 리뷰평점 가져오기
            Optional<Double> score = reviewRepository.findReviewScoreByStoreId(entity.getStoreId());
            if(score.isPresent()){
                bookmarkDTO.setScore(score.get());
            }else{
                bookmarkDTO.setScore(0.0);
            }

            // 주소 넣기
            bookmarkDTO.setAddress1(entity.getAddress1());

            bookmarkDTOList.add(bookmarkDTO);
        }

        result.setBookmarkList(bookmarkDTOList);
        result.setCount(count);

        return result;

    }

}
