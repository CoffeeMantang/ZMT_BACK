package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.dto.StatsDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreInfoDTO;
import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import com.coffeemantang.ZMT_BACK.service.StoreInfoService;
import com.coffeemantang.ZMT_BACK.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/partners/store")
public class StoreController {

    private final StoreService storeService;

    private final StoreInfoService storeInfoService;

    // 가게 생성
    @PostMapping("/create")
    public ResponseEntity<?> createStore(@AuthenticationPrincipal String memberId, StoreDTO storeDTO){
        try {
            // StoreDTO를 StoreEntity로 변환
            StoreEntity tempStoreEntity = StoreDTO.toEntity(storeDTO);
            // 생성 당시에는 id가 없어야 하기 때문에 null로 초기화
            tempStoreEntity.setStoreId(null);
            // 생성일 현재시간을 초기화
            tempStoreEntity.setJoinDay(LocalDateTime.now());
            // AuthenticationPrincipal에서 넘어온 memberId set
            tempStoreEntity.setMemberId(Integer.parseInt(memberId));
            // 가게의 상태 초기화
            tempStoreEntity.setState(0);
            // StoreService를 이용해 StoreEntity 생성
            String storeId = storeService.create(Integer.parseInt(memberId), storeDTO);
            // 리턴할 Store DTO 초기화
            ResponseDTO responseDTO = ResponseDTO.builder().error(storeId).build();
            // 응답
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            // 예외 발생 시 error에 e.getMessage() 넣어 리턴
            ResponseDTO response = ResponseDTO.builder().error(e.getMessage()).build();
            e.printStackTrace();
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 가게 수정
    @PostMapping("/update")
    public ResponseEntity<?> updateStore(@AuthenticationPrincipal String memberId, StoreDTO storeDTO) {

        try {
            StoreEntity storeEntity = storeService.updateStore(Integer.parseInt(memberId), storeDTO);
            if (storeEntity != null) {
                StoreDTO responseStoreDTO = StoreDTO.builder()
                        .memberId(storeEntity.getMemberId())
                        .storeId(storeEntity.getStoreId())
                        .name(storeEntity.getName())
                        .category(storeEntity.getCategory())
                        .address1(storeEntity.getAddress1())
                        .address2(storeEntity.getAddress2())
                        .state(storeEntity.getState())
                        .addressX(storeEntity.getAddressX())
                        .addressY(storeEntity.getAddressY())
                        .build();
                return ResponseEntity.ok().body(responseStoreDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //가게 삭제
    @DeleteMapping("/delete")
    public String deleteStore(@AuthenticationPrincipal String memberId, String storeId) {
        storeService.deleteStore(Integer.parseInt(memberId), storeId);

        return "redirect:/";
    }

    // 가게 보기 (클릭했을 때) (로그인한 유저 입장)
    @PostMapping("/view")
    public ResponseEntity<?> viewStore(@AuthenticationPrincipal String memberId, @RequestBody StoreDTO storeDTO) {

        try {
            StoreDTO responseStoreDTO = storeService.viewStore(Integer.parseInt(memberId), storeDTO);
            return ResponseEntity.ok().body(responseStoreDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("가게 정보를 가져오는 도중 오류 발생");
        }

    }

    // 가게 정보 입력
    @PostMapping("/addinfo")
    public ResponseEntity<?> addStoreInfo(@AuthenticationPrincipal String memberId, @RequestBody StoreInfoDTO storeInfoDTO) throws Exception {

        try {
            storeInfoService.addStoreInfo(Integer.parseInt(memberId), storeInfoDTO);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 가게 정보 수정
    @PostMapping("/updateinfo")
    public ResponseEntity<?> updateStoreInfo(@AuthenticationPrincipal String memberId, @RequestBody StoreInfoDTO storeInfoDTO) {

        try {
            StoreInfoDTO responseStoreInfoDTO = storeInfoService.updateStoreInfo(Integer.parseInt(memberId), storeInfoDTO);
            return ResponseEntity.ok().body(responseStoreInfoDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 가게이름으로 검색하기 - 로그인해야함
    @GetMapping(value = "/searchResult/{keyword}")
    public ResponseEntity<?> getSearchResult(@PathVariable("keyword") String keyword, @RequestParam(value = "page") int page, @RequestParam(value = "sort", required = false) String sort,
                                             @AuthenticationPrincipal String memberId) throws Exception {
        try{
            List<StoreDTO> result = storeService.getSearchResultforMember(keyword, page, sort, Integer.parseInt(memberId));
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

    // 가게이름으로 검색하기 - 로그인해야함
    @GetMapping(value = "/searchMenuResult/{keyword}")
    public ResponseEntity<?> getMenuSearch(@PathVariable("keyword") String keyword, @RequestParam(value = "page") int page, @RequestParam(value = "sort", required = false) String sort,
                                             @AuthenticationPrincipal String memberId) throws Exception {
        try{
            List<StoreDTO> result = storeService.getSearchByMenuNameForMember(keyword, page, sort, Integer.parseInt(memberId));
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

    // 로그인한 멤버 정보로 해당 가게의 배달비 가져오기
    @GetMapping("/getStoreCharge")
    public ResponseEntity<?> getCharge(@AuthenticationPrincipal String memberId, @RequestParam(value="storeId") String storeId) throws Exception{
        try{
            int charge =  storeService.findCharge(Integer.parseInt(memberId), storeId);
            // ResponseDTO responseDTO = ResponseDTO.builder().error("ok").data(Arrays.asList(charge)).build();
            return ResponseEntity.ok().body(charge);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    // 카테고리로 검색하기(로그인 없이) - 여기페이지는 1부터 시작합니다.
    @GetMapping(value = "/categorySearch/{category}")
    public ResponseEntity<?> cagetorySearch(@AuthenticationPrincipal String memberId, @PathVariable("category") int category,@RequestParam(value = "page") int page, @RequestParam(value = "sort", required = false) String sort) throws Exception{
        try{
            List<StoreDTO> result = storeService.getCategorySearchWithLogin(category, page, sort, Integer.parseInt(memberId));
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

    // 기간별 수익
    // map 요소 : storeId, type, date, from, to, menuId (storeId, type, date는 항상 입력)
    //
    // date =
    // 0 : 직접 캘린더에서 기간 찍음. 0일 경우 from,to도 입력
    // (선택 기간 시작일이 from, 끝나는 일이 to),
    // -1 : 당일,
    // 1 : 1달 전,
    // 3 : 3달 전,
    // 6 : 6달 전
    //
    // type =
    // 0 : 해당 기간의 메뉴 리스트
    // 1 : 해당 메뉴,기간의 옵션 리스트. 1인 경우 menuId에 해당 menuId도 입력.
    @PostMapping("/stats")
    public ResponseEntity<?> viewStats(@AuthenticationPrincipal String memberId, @RequestParam HashMap<String, String> map) {

        try {
            StatsDTO statsDTO = storeService.viewStats(Integer.parseInt(memberId), map);
            return ResponseEntity.ok().body(statsDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 내 가게 목록 가져오기(사진, 가게명, 가게아이디)
    @GetMapping("/getMyStore")
    public ResponseEntity<?> getMyStore(@AuthenticationPrincipal String memberId) throws Exception{
        try{
            List<StoreDTO> list = storeService.getMyStore(Integer.parseInt(memberId));
            ResponseDTO responseDTO = ResponseDTO.builder().data(Arrays.asList(list.toArray())).build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 가게 상태 변경하기
    @GetMapping("/setStoreState")
    public ResponseEntity<?> setState(@AuthenticationPrincipal String memberId, @RequestParam("storeId") String storeId,
                                      @RequestParam("state") int state) throws Exception{
        try {
            storeService.setState(Integer.parseInt(memberId), storeId, state);
            return ResponseEntity.ok().body("ok");
        }catch (Exception e){
            e.printStackTrace();
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


}
