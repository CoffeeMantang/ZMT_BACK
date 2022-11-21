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
    public ResponseEntity<?> createStore(@AuthenticationPrincipal String memberId, @RequestBody StoreDTO storeDTO){
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
            storeService.create(Integer.parseInt(memberId), storeDTO);
            // 리턴할 Store DTO 초기화
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            // 응답
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            // 예외 발생 시 error에 e.getMessage() 넣어 리턴
            ResponseDTO response = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 가게 수정
    @PostMapping("/update")
    public ResponseEntity<?> updateStore(@AuthenticationPrincipal String memberId, @Valid @RequestBody StoreDTO storeDTO) {

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
    public StoreDTO viewStore(@AuthenticationPrincipal String memberId, @RequestBody StoreDTO storeDTO) {

        try {
            StoreDTO responseStoreDTO = storeService.viewStore(Integer.parseInt(memberId), storeDTO);
            return responseStoreDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("가게 정보를 가져오는 도중 오류 발생");
        }

    }

    // 가게 정보 입력
    @PostMapping("/addinfo")
    public ResponseEntity<?> addStoreInfo(@AuthenticationPrincipal String memberId, @RequestBody StoreInfoDTO storeInfoDTO) {

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
    @PostMapping("/stats")
    public ResponseEntity<?> viewStats(@AuthenticationPrincipal String memberId, @RequestParam HashMap<String, String> map) {

        try {
            int type = Integer.parseInt(map.get("type"));
            StatsDTO responseStatsDTO = new StatsDTO();
            if (0 == type) { //수익만
                responseStatsDTO = storeService.viewStats(Integer.parseInt(memberId), map);
            }
            return ResponseEntity.ok().body(responseStatsDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }


}
