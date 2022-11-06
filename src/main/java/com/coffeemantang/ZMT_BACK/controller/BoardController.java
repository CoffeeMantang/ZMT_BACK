package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.BoardDTO;
import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // 글 작성
    @PostMapping("/write")
    public ResponseEntity<?> writeBoard(@AuthenticationPrincipal String memberId, @RequestBody BoardDTO boardDTO, @RequestParam int type) {

        try {
            boardService.writeBoard(Integer.parseInt(memberId), boardDTO, type);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 글 수정
    @PostMapping("/update")
    public ResponseEntity<?> updateBoard(@AuthenticationPrincipal String memberId, @RequestBody BoardDTO boardDTO) {

        try {
            BoardDTO responseBoardDTO = boardService.updateBoard(Integer.parseInt(memberId), boardDTO);
            return ResponseEntity.ok().body(responseBoardDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 글 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBoard(@AuthenticationPrincipal String memberId, @RequestBody BoardDTO boardDTO) {

        try {
            boardService.deleteBoard(Integer.parseInt(memberId), boardDTO);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 글 보기
    @PostMapping("/view")
    public ResponseEntity<?> viewBoard(@RequestBody BoardDTO boardDTO) {

        try {
            BoardDTO responseBoardDTO =  boardService.viewBoard(boardDTO);
            return ResponseEntity.ok().body(responseBoardDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 글 목록
    @PostMapping("/list")
    public ResponseEntity<?> viewBoardList(@RequestParam int type, @PageableDefault(size = 15) Pageable pageable) {

        try {
            List<BoardDTO> boardDTOList = boardService.viewBoardList(type, pageable);
            if (boardDTOList.isEmpty()) {
                ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
                return ResponseEntity.ok().body(responseDTO);
            } else {
                return ResponseEntity.ok().body(boardDTOList);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 내가 쓴 글 목록
    @PostMapping("/mylist")
    public ResponseEntity<?> viewBoardMyList(@AuthenticationPrincipal String memberId, @PageableDefault(size = 15) Pageable pageable) {

        try {
            List<BoardDTO> boardDTOList = boardService.viewBoardMyList(Integer.parseInt(memberId), pageable);
            if (boardDTOList.isEmpty()) {
                ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
                return ResponseEntity.ok().body(responseDTO);
            } else {
                return ResponseEntity.ok().body(boardDTOList);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

}
