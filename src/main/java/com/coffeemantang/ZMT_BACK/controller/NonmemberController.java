package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.*;
import com.coffeemantang.ZMT_BACK.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/nonmember")
public class NonmemberController {

    private final StoreService storeService;

    private final MenuService menuService;

    private final OptionService optionService;

    private final BoardService boardService;

    private final StoreInfoService storeInfoService;

    // 가게 목록
    @PostMapping("/store/list")
    public List<StoreDTO> viewStoreList() {

        try {
            List<StoreDTO> storeDTOList = storeService.viewStoreList();
            return storeDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("가게 리스트를 가져오는 도중 오류 발생");
        }

    }

    // 가게 보기(클릭했을 때)
    @PostMapping("/store/view")
    public StoreDTO viewStore(@RequestBody StoreDTO storeDTO) {

        try {
            StoreDTO responseStoreDTO = storeService.viewStore(0, storeDTO);
            return responseStoreDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("가게 정보를 가져오는 도중 오류 발생");
        }

    }

    // 메뉴 목록
    @PostMapping("/store/menu")
    public List<MenuDTO> viewMenuList(String storeId) {

        try {
            List<MenuDTO> menuDTOList = menuService.viewMenuList(storeId);
            return menuDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("메뉴 리스트를 가져오는 도중 오류 발생");
        }

    }

    // 가게 정보
    @PostMapping("/store/info")
    public StoreInfoDTO viewStoreInfo(@RequestBody StoreDTO storeDTO) {

        try {
            StoreInfoDTO storeInfoDTO = storeInfoService.viewStoreInfo(storeDTO);
            return storeInfoDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("가게 정보를 가져오는 도중 오류 발생");
        }
    }

    // 옵션 목록
    @PostMapping("/store/option")
    public List<OptionDTO> viewOptionList(int menuId) {

        try {
            List<OptionDTO> optionDTOList = optionService.viewOptionList(menuId);
            return optionDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("옵션 리스트를 가져오는 도중 오류 발생");
        }

    }

    // 게시판 글 보기
    @PostMapping("/board/view")
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
    @PostMapping("/board/list")
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

}
