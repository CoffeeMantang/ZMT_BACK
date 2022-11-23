package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.*;
import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import com.coffeemantang.ZMT_BACK.security.TokenProvider;
import com.coffeemantang.ZMT_BACK.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/nonmember")
public class NonmemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailTokenService emailTokenService;

    private final StoreService storeService;

    private final MenuService menuService;

    private final OptionService optionService;

    private final BoardService boardService;

    private final StoreInfoService storeInfoService;
    @Autowired
    ReviewService reviewService;

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody MemberDTO memberDTO) {
        // 로그인 성공 시에만 MemberEntity 가져옴
        MemberEntity member = memberService.getByCredentials(
                memberDTO.getEmail(),
                memberDTO.getPassword(),
                passwordEncoder
        );
        // MemberEntity 가져오기 성공 시
        if (member != null) {
            // TokenProvider 클래스를 이용해 토큰을 생성한 후 MemberDTO에 넣어서 반환
            final String token = tokenProvider.create(member);
            final MemberDTO responseMemberDTO = MemberDTO.builder()
                    .email(member.getEmail())
                    .memberId(member.getMemberId())
                    .type(member.getType()) // 멤버의 타입
                    .nickname(member.getNickname())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseMemberDTO);
        } else {
            // MemberEntity 가져오기 실패 시 -> 로그인 실패
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("login failed").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

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
    @GetMapping("/store/view")
    public ResponseEntity<?> viewStore(@RequestBody StoreDTO storeDTO) {

        try {
            StoreDTO responseStoreDTO = storeService.viewStore(0, storeDTO);
            return ResponseEntity.ok().body(responseStoreDTO);
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

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> registerMember(@RequestBody MemberDTO memberDTO) {
        try {
            // 요청을 이용해 저장할 사용자 만들기
            MemberEntity member = MemberEntity.builder()
                    .email(memberDTO.getEmail())
                    .password(passwordEncoder.encode(memberDTO.getPassword()))
                    .name(memberDTO.getName())
                    .nickname(memberDTO.getNickname())
                    .tel(memberDTO.getTel())
                    .birthDay(memberDTO.getBirthDay())
                    .joinDay(LocalDateTime.now()) // 현재 시간
                    .gender(memberDTO.getGender())
                    .type(2) // 2: 일반회원
                    .question(memberDTO.getQuestion())
                    .answer(memberDTO.getAnswer()).build();
            // 서비스를 이용해 Repository에 사용자 저장
            MemberEntity registeredMember = memberService.create(member);
            MemberDTO responseMemberDTO = MemberDTO.builder()
                    .email(registeredMember.getEmail())
                    .nickname(registeredMember.getNickname())
                    .build();
            emailTokenService.createEmailToken(registeredMember.getMemberId(), registeredMember.getEmail()); // 이메일 전송
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    // 인증 이메일 재전송
    @PostMapping("/reconfirm")
    public ResponseEntity<?> viewConfirmEmail(@RequestBody MemberDTO memberDTO){
        try{
            emailTokenService.createEmailToken(memberDTO.getMemberId(), memberDTO.getEmail()); // 이메일 전송
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 이메일 중복 체크
    @PostMapping("/checkemail")
    public ResponseEntity<?> checkEmail(@RequestBody MemberDTO memberDTO){
        try{
            if(memberService.checkEmail(memberDTO.getEmail())){
                ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
                return ResponseEntity.ok().body(responseDTO);
            }else{
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 로그인 테스트2

    @GetMapping("/test2")
    public ResponseEntity<?> test2(@AuthenticationPrincipal String memberId){

        ResponseDTO responseDTO = ResponseDTO.builder().build();
        return ResponseEntity.badRequest().body(responseDTO);
    }

    // 닉네임 중복체크
    @PostMapping("/checknickname")
    public ResponseEntity<?> checkNickname(@RequestBody MemberDTO memberDTO){
        try{
            boolean check = memberService.checkNickname(memberDTO.getNickname());
            if(check){
                MemberDTO responseMemberDTO = MemberDTO.builder().nickname(memberDTO.getNickname()).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            }else{
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
