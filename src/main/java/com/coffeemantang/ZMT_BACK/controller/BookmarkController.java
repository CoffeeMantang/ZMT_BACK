package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.BookmarkDTO;
import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.model.BookmarkEntity;
import com.coffeemantang.ZMT_BACK.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // 즐겨찾기 추가
    @PostMapping("/add")
    public ResponseEntity<?> addBookmark(@AuthenticationPrincipal String memberId, @RequestBody StoreDTO storeDTO) {
        try {
            BookmarkEntity bookmarkEntity = bookmarkService.addBookmark(Integer.parseInt(memberId), storeDTO);
            BookmarkDTO responseBookmarkDTO = BookmarkDTO.builder()
                    .memberId(bookmarkEntity.getMemberId())
                    .storeId(bookmarkEntity.getStoreId())
                    .build();
            return ResponseEntity.ok().body(responseBookmarkDTO);
        } catch (Exception e) {
            ResponseDTO response = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 즐겨찾기 해제
    @DeleteMapping("/delete") // 매핑 정하기
    public ResponseEntity<?> deleteBookmark(@AuthenticationPrincipal String memberId, @RequestBody BookmarkDTO bookmarkDTO) {
        try {
            bookmarkService.deleteBookmark(Integer.parseInt(memberId), bookmarkDTO);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO response = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 즐겨찾기 목록
    @PostMapping("/list")
    public List<BookmarkDTO> viewBookmarkList(@AuthenticationPrincipal String memberId) {

        try {
            List<BookmarkDTO> bookmarkDTOList = bookmarkService.viewBookmarkList(Integer.parseInt(memberId));
            return bookmarkDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("즐겨찾기 목록을 가져오는 도중 에러 발생");
        }
    }


}
