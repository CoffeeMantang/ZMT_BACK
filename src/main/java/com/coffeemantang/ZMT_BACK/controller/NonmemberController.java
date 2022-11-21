package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.*;
import com.coffeemantang.ZMT_BACK.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
    @Autowired
    ReviewService reviewService;

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
    @GetMapping("/store/info")
    public ResponseEntity<?> viewStoreInfo(@RequestParam(value="storeId") String storeId) throws Exception{

        try {
            StoreInfoDTO storeInfoDTO = storeInfoService.viewStoreInfo(storeId);
            return ResponseEntity.ok().body(storeInfoDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("가게 정보를 가져오는 도중 오류 발생");
        }
    }

    // 메뉴 정보
    @GetMapping("/menu/info")
    public ResponseEntity<?> viewMenuInfo(@RequestParam(value="menuId") int menuId) throws Exception{
        try{
            MenuDTO menuDTO = menuService.getMenuInfo(menuId);
            return ResponseEntity.ok().body(menuDTO);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 옵션 목록
    @GetMapping("/store/option")
    public ResponseEntity<?> viewOptionList(@RequestParam(value="menuId") int menuId) {

        try {
            List<OptionDTO> optionDTOList = optionService.viewOptionList(menuId);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").data(Arrays.asList(optionDTOList)).build();
            return ResponseEntity.ok().body(optionDTOList);
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

    // 가게 페이지 불러오기
    @GetMapping(value = "/storeview/{storeId}")
    public ResponseEntity<?> viewStore(@PathVariable("storeId") String storeId) throws Exception{
        try{
            // 1. 가게정보가져오기
            StoreDTO storeDTO = storeService.nonLoginStore(storeId);
            // 2. 리턴
            return ResponseEntity.ok().body(storeDTO);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 가게 리뷰 보기(페이징) -> 추후 로그인 없이 볼 수 있는 path로 이동시킴
    @GetMapping("/review")
    public ResponseEntity<?> storeReview(@RequestParam(value = "storeId") String storeId, @PageableDefault(size = 10) Pageable pageable) throws Exception{
        try{
            StoreDTO storeDTO = reviewService.getStoreReviewList(storeId, pageable);
            return ResponseEntity.ok().body(storeDTO);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 가게이름과 지역으로 검색하기
    @GetMapping(value = "/searchResult/{keyword}")
    public ResponseEntity<?> getSearchResult(@PathVariable("keyword") String keyword, @RequestParam(value = "page") int page, @RequestParam(value = "sort", required = false) String sort,
    @RequestParam(value = "address") String address) throws Exception {
        try{
            List<StoreDTO> result = storeService.getSearchResult(keyword, page, sort, address);
            log.warn("들어온 주소: " + address);
            ResponseDTO responseDTO;
            if(result.size() == 0){ // 더이상 넘어갈게 없으면
                responseDTO = ResponseDTO.builder().data(Arrays.asList(result.toArray())).error("error").build();
            }else{ // 넘어갈게 있으면
                responseDTO = ResponseDTO.builder().data(Arrays.asList(result.toArray())).error("ok").build();
            }
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 메뉴명과 지역으로 검색하기 -
    @GetMapping(value = "/searchMenuResult/{keyword}")
    public ResponseEntity<?> getMenuSearch(@PathVariable("keyword") String keyword, @RequestParam(value = "page") int page, @RequestParam(value = "sort", required = false) String sort,
                                           @RequestParam(value = "address") String address) throws Exception{
        try{
            List<StoreDTO> result = storeService.getSearchByMenuName(keyword, page, sort, address);
            log.warn("들어온 주소: " + address);
            ResponseDTO responseDTO;
            if(result.size() == 0){ // 더이상 넘어갈게 없으면
                responseDTO = ResponseDTO.builder().data(Arrays.asList(result.toArray())).error("error").build();
            }else{ // 넘어갈게 있으면
                responseDTO = ResponseDTO.builder().data(Arrays.asList(result.toArray())).error("ok").build();
            }
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 카테고리로 검색하기(로그인 없이) - 여기페이지는 1부터 시작합니다.
    @GetMapping(value = "/categorySearch/{category}")
    public ResponseEntity<?> cagetorySearch(@PathVariable("category") int category,@RequestParam(value = "page") int page, @RequestParam(value = "sort", required = false) String sort
            ,@RequestParam(value="address") String address) throws Exception{
        try{
            List<StoreDTO> result = storeService.getCategorySearch(category, page, sort, address);
            log.warn("들어온 주소: " + address);
            ResponseDTO responseDTO;
            if(result.size() == 0){ // 더이상 넘어갈게 없으면
                responseDTO = ResponseDTO.builder().data(Arrays.asList(result.toArray())).error("error").build();
            }else{ // 넘어갈게 있으면
                responseDTO = ResponseDTO.builder().data(Arrays.asList(result.toArray())).error("ok").build();
            }
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
