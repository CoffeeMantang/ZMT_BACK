package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.*;
import com.coffeemantang.ZMT_BACK.model.*;
import com.coffeemantang.ZMT_BACK.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
// Store에 저장된 내용을 가져올 때 사용
// StoreRepository를 이용해 가게를 CRUD
public class StoreService {

    private final StoreRepository storeRepository;

    private final MemberRepository memberRepository;

    private final BookmarkRepository bookmarkRepository;

    private final MenuRepository menuRepository;

    private final MenuImgRepository menuImgRepository;

    private final OptionRepository optionRepository;

    private final ReviewRepository reviewRepository;

    private final MemberRocationRepository memberRocationRepository;

    private final ChargeRepository chargeRepository;

    private final OrderListRepository orderListRepository;

    // 가게 생성
    public void create(int memberId, StoreDTO storeDTO) throws IOException {
        if(storeDTO == null || storeDTO.getMemberId() == 0){
            log.warn("StoreService.create() : storeEntity에 내용이 부족해요");
            throw new RuntimeException("StoreService.create() : storeEntity에 내용이 부족해요");
        } else if (memberRepository.findByMemberId(storeDTO.getMemberId()).getType() != 1) {
            // 사업자 회원이 아니면 오류 리턴
            log.warn("사업자 회원이 아닌 회원이 가게생성 시도");
            throw new RuntimeException("사업자 회원이 아닌 회원이 가게생성 시도");
        }

        StoreEntity storeEntity = new StoreEntity();
        storeEntity.setStoreId(null);
        storeEntity.setMemberId(memberId);
        storeEntity.setJoinDay(LocalDateTime.now());
        storeEntity.setState(0);
        storeEntity.setName(storeDTO.getName());
        storeEntity.setCategory(storeDTO.getCategory());
        storeEntity.setAddress1(storeDTO.getAddress1());
        storeEntity.setAddress2(storeDTO.getAddress2());
        storeEntity.setAddressX(storeDTO.getAddressX());
        storeEntity.setAddressY(storeDTO.getAddressY());

        MultipartFile multipartFile = storeDTO.getFile();

        String current_date = null;
        if(!multipartFile.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            current_date = now.format(dateTimeFormatter);

            String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;

            String path = "images" + File.separator + current_date;
            File file = new File(path);

            if (!file.exists()) {
                boolean wasSuccessful = file.mkdirs();

                if (!wasSuccessful) {
                    log.warn("file : was not successful");
                }
            }
            while (true) {
                String originalFileExtension;
                String contentType = multipartFile.getContentType();

                if (ObjectUtils.isEmpty(contentType)) {
                    break;
                } else {
                    if (contentType.contains("image/jpeg")) {
                        originalFileExtension = ".jpg";
                    } else if (contentType.contains("images/png")) {
                        originalFileExtension = ".png";
                    } else {
                        break;
                    }
                }

                String new_file_name = System.nanoTime() + originalFileExtension;

                storeEntity.setThumb(path + file.separator + new_file_name);

                file = new File(absolutePath + path + File.separator + new_file_name);
                multipartFile.transferTo(file);

                file.setWritable(true);
                file.setReadable(true);
                break;
            }
        }
        storeRepository.save(storeEntity);
    }

    // 가게 수정
    public StoreEntity updateStore(int memberId, @Valid StoreDTO storeDTO) {

        if(memberId != storeDTO.getMemberId()) {
            log.warn("StoreService.updateOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("StoreService.updateOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        StoreEntity storeEntity = storeRepository.findByStoreId(storeDTO.getStoreId());
        storeEntity.setName(storeDTO.getName());
        storeEntity.setCategory(storeDTO.getCategory());
        storeEntity.setAddress1(storeDTO.getAddress1());
        storeEntity.setAddress2(storeDTO.getAddress2());
        storeEntity.setState(storeDTO.getState());
        storeEntity.setAddressX(storeDTO.getAddressX());
        storeEntity.setAddressY(storeDTO.getAddressY());
        storeRepository.save(storeEntity);

        return storeEntity;

    }

    // 가게 삭제
    public void deleteStore(int memberId, String storeId) {

        StoreEntity storeEntity = storeRepository.findByStoreId(storeId);

        if (memberId != storeEntity.getMemberId()) {
            log.warn("StoreService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("StoreService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        storeRepository.deleteById(storeId);
    }

    // 가게 목록
    public List<StoreDTO> viewStoreList() {

        List<StoreEntity> storeEntityList = storeRepository.findAll();
        List<StoreDTO> storeDTOList = new ArrayList<>();
        for (StoreEntity storeEntity : storeEntityList) {
            StoreDTO storeDTO = new StoreDTO(storeEntity);
            storeDTOList.add(storeDTO);
        }

        return storeDTOList;
    }

    // 가게 보기(클릭했을 때).
    public StoreDTO viewStore(int memberId, StoreDTO storeDTO) {

        StoreEntity storeEntity = storeRepository.findByStoreId(storeDTO.getStoreId());
        StoreDTO newStoreDTO = new StoreDTO(storeEntity);
        if (memberId != 0){ // 비회원이면 0
            newStoreDTO.setBookmark(bookmarkRepository.countByMemberIdAndStoreId(memberId, storeDTO.getStoreId()));
        }

        return newStoreDTO;

    }

    // 가게페이지 정보 가져오기(로그인 없이)
    public StoreDTO nonLoginStore(final String storeId) throws Exception{
        try{
            StoreEntity storeEntity = storeRepository.findByStoreId(storeId);
            List<MenuEntity> menuList = menuRepository.selectMenuOrderByMenuNumber(storeId, 2);
            List<MenuDTO> menuDTOList = new ArrayList<>();
            List<ImageDTO> imageList = new ArrayList<>();
            ImageDTO imageDTO = ImageDTO.builder().build();
            if(storeEntity.getThumb() != null){
                imageDTO.setImage("http://localhost:8080/images/store/" + storeEntity.getThumb());
            }
            imageList.add(imageDTO);

            for(MenuEntity menuEntity : menuList) {
                // 1. 메뉴의 첫번째 이미지 가져오기
                MenuImgEntity menuImgEntity = null;
                menuImgEntity = menuImgRepository.findTop1ByMenuId(menuEntity.getMenuId());
                // 2. StoreDTO에 MenuDTO List 넣기
                MenuDTO menuDTO = MenuDTO.builder().menuId(menuEntity.getMenuId())
                        .menuName(menuEntity.getMenuName())
                        .notice(menuEntity.getNotice())
                        .category(menuEntity.getCategory())
                        .price(menuEntity.getPrice()).build();
                if (menuImgEntity != null) {
                    menuDTO.setMenuPic("http://localhost:8080/images/menu/" + menuImgEntity.getPath());
                }
                menuDTOList.add(menuDTO);
                if (menuImgEntity != null) {
                    ImageDTO tempImageDTO = ImageDTO.builder().image("http://localhost:8080/images/menu/" + menuImgEntity.getPath()).build();
                    imageList.add(tempImageDTO);
                }
            }
            StoreDTO storeDTO = StoreDTO.builder()
                    .storeId(storeId)
                    .name(storeEntity.getName())
                    .images(imageList)
                    .menuList(menuDTOList)
                    .min(storeEntity.getMin())
                    .state(storeEntity.getState()).build();

            // 3. 평균리뷰점수 가져와서 추가
            Optional<Double> oReviewScore = reviewRepository.findReviewScoreByStoreId(storeId);
            if(oReviewScore.isPresent()){
                storeDTO.setScore(Math.round((oReviewScore.get()*10)/10.0)); //소숫점 두번째에서 반올림
            }else{
                storeDTO.setScore(0.0); //리뷰가 없으면 0으로
            }
            // 4. 리뷰갯수 가져와서 추가
            Long reviewCount = reviewRepository.countByStoreId(storeId);
            storeDTO.setReviewCount(reviewCount);

            return storeDTO;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 가게이름으로 검색하기 - paging
    public List<StoreDTO> getSearchResult(final String keyword, final int page, final String sort, String address, double x, double y) throws Exception{
        try{
            List<StoreEntity> entities = new ArrayList<>();
            // 1. 가게이름으로 검색한 entity 가져오기(10개씩!)
            switch (sort){
                case("orderCount"):// 주문수 정렬
                    entities = storeRepository.findByNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
                    break;
                case("reviewScore"):
                    // 리뷰평점 정렬
                    entities = storeRepository.findByNameOrderByReviewScore(10, ((page-1)*10), address, keyword);
                    break;
                case("charge"): // 배달팁 낮은순
                    entities = storeRepository.findByMenuNameOrderByCharge(10, ((page-1)*10), address, keyword);
                    break;
                case("distance"): // 거리순
                    entities = storeRepository.findByMenuNameOrderByDistance(10, ((page-1)*10), address, keyword, x, y);
                    break;
                default: // 기본값은 주문순으로
                    entities = storeRepository.findByNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
            }
            List<StoreDTO> dtos = new ArrayList<>();
            // 2. DTO list로 변환
            for(StoreEntity entity : entities){
                // 2-1. 리뷰 평점 가져오기
                Optional<Double> score = reviewRepository.findReviewScoreByStoreId(entity.getStoreId());
                StoreDTO dto = StoreDTO.builder().storeId(entity.getStoreId())
                        .state(entity.getState()).name(entity.getName())
                        .thumb(entity.getThumb()).build();
                if(score.isPresent()){
                    dto.setScore(score.get());
                }
                dtos.add(dto);
            }
            return dtos;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 가게이름으로 검색하기 - paging
    public List<StoreDTO> getSearchResultforMember(final String keyword, final int page, final String sort, final int memberId) throws Exception{
        try{
            // 멤버아이디로 주소가져오기
            MemberRocationEntity memberRocationEntity = memberRocationRepository.findAddress1ByMemberIdAndState(memberId, 1); //대표주소 가져옴
            String address = memberRocationEntity.getAddress1();
            double x = memberRocationEntity.getAddressX();
            double y = memberRocationEntity.getAddressY();
            List<StoreEntity> entities = new ArrayList<>();
            // 1. 가게이름으로 검색한 entity 가져오기(10개씩!)
            switch (sort){
                case("orderCount"):// 주문수 정렬
                    entities = storeRepository.findByNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
                    break;
                case("reviewScore"):
                    // 리뷰평점 정렬
                    entities = storeRepository.findByNameOrderByReviewScore(10, ((page-1)*10), address, keyword);
                    break;
                case("charge"): // 배달팁 낮은순
                    entities = storeRepository.findByNameOrderByCharge(10, ((page-1)*10), address, keyword);
                    break;
                case("distance"): // 거리순
                    entities = storeRepository.findByNameOrderByDistance(10, ((page-1)*10), address, keyword, x, y);
                    break;
                default: // 기본값은 주문순으로
                    entities = storeRepository.findByNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
            }
            List<StoreDTO> dtos = new ArrayList<>();
            // 2. DTO list로 변환
            for(StoreEntity entity : entities){
                // 2-1. 리뷰 평점 가져오기
                Optional<Double> score = reviewRepository.findReviewScoreByStoreId(entity.getStoreId());
                StoreDTO dto = StoreDTO.builder().storeId(entity.getStoreId())
                        .state(entity.getState()).name(entity.getName())
                        .thumb(entity.getThumb()).build();
                if(score.isPresent()){
                    dto.setScore(score.get());
                }
                dtos.add(dto);
            }
            return dtos;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 메뉴명으로 검색하기 = nonLogin
    public List<StoreDTO> getSearchByMenuName(final String keyword, final int page, final String sort, final String address, double x, double y) throws Exception{
        try{
            List<StoreEntity> entities = new ArrayList<>();
            // 1. 가게이름으로 검색한 entity 가져오기(10개씩!)
            switch (sort){
                case("orderCount"):// 주문수 정렬
                    entities = storeRepository.findByMenuNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
                    break;
                case("reviewScore"):
                    // 리뷰평점 정렬
                    entities = storeRepository.findByMenuNameOrderByReviewScore(10, ((page-1)*10), address, keyword);
                    break;
                case("charge"): // 배달팁 낮은순
                    entities = storeRepository.findByNameOrderByCharge(10, ((page-1)*10), address, keyword);
                    break;
                case("distance"): // 거리순
                    entities = storeRepository.findByNameOrderByDistance(10, ((page-1)*10), address, keyword, x, y);
                    break;
                default: // 기본값은 주문순으로
                    entities = storeRepository.findByMenuNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
            }
            List<StoreDTO> dtos = new ArrayList<>();
            // 2. DTO list로 변환
            for(StoreEntity entity : entities){
                // 2-1. 리뷰 평점 가져오기
                Optional<Double> score = reviewRepository.findReviewScoreByStoreId(entity.getStoreId());
                StoreDTO dto = StoreDTO.builder().storeId(entity.getStoreId())
                        .state(entity.getState()).name(entity.getName())
                        .thumb("http://localhost:8080/images/store/" + entity.getThumb()).build();
                if(score.isPresent()){
                    dto.setScore(score.get());
                }
                dtos.add(dto);
            }
            return dtos;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 메뉴명으로 검색하기 - require login
    public List<StoreDTO> getSearchByMenuNameForMember(final String keyword, final int page, final String sort, final int memberId) throws Exception{
        try{
            // 멤버아이디로 주소가져오기
            MemberRocationEntity memberRocationEntity = memberRocationRepository.findAddress1ByMemberIdAndState(memberId, 1); //대표주소 가져옴
            String address = memberRocationEntity.getAddress1();
            List<StoreEntity> entities = new ArrayList<>();
            double x = memberRocationEntity.getAddressX();
            double y = memberRocationEntity.getAddressY();
            // 1. 가게이름으로 검색한 entity 가져오기(10개씩!)
            switch (sort){
                case("orderCount"):// 주문수 정렬
                    entities = storeRepository.findByMenuNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
                    break;
                case("reviewScore"):
                    // 리뷰평점 정렬
                    entities = storeRepository.findByMenuNameOrderByReviewScore(0, ((page-1)*10), address, keyword);
                    break;
                case("charge"): // 배달팁 낮은순
                    entities = storeRepository.findByMenuNameOrderByCharge(10, ((page-1)*10), address, keyword);
                    break;
                case("distance"): // 거리순
                    entities = storeRepository.findByMenuNameOrderByDistance(10, ((page-1)*10), address, keyword, x, y);
                    break;
                default: // 기본값은 주문순으로
                    entities = storeRepository.findByMenuNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
            }
            List<StoreDTO> dtos = new ArrayList<>();
            // 2. DTO list로 변환
            for(StoreEntity entity : entities){
                // 2-1. 리뷰 평점 가져오기
                Optional<Double> score = reviewRepository.findReviewScoreByStoreId(entity.getStoreId());
                StoreDTO dto = StoreDTO.builder().storeId(entity.getStoreId())
                        .state(entity.getState()).name(entity.getName())
                        .thumb("http://localhost:8080/images/store/" + entity.getThumb()).build();
                if(score.isPresent()){
                    dto.setScore(score.get());
                }
                dtos.add(dto);
            }
            return dtos;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 회원아이디와 가게아이디로 배달비 찾기
    public int findCharge(final int memberId, final String storeId) throws Exception{
        try{
            MemberRocationEntity mrEntity = memberRocationRepository.findAddress1ByMemberIdAndState(memberId, 1);
            String[] address1 = mrEntity.getAddress1().split(" "); // 멤버의 address1 가져오기
            String dong = null;
            // 읍 면 동 가져오기
            for(int i = 0 ; i < address1.length ; i++){
                if(address1[i].endsWith("읍") || address1[i].endsWith("면") || address1[i].endsWith("동")){
                    dong = address1[i];
                    break;
                }
            }
            // 가져온 읍면동으로 배달비 검색
            List<ChargeEntity> charge = chargeRepository.findByDongContainingAndStoreId(dong, storeId);
            if(charge.size() > 0){
                return charge.get(0).getCharge();
            }else{
                return 99999; // 99999면 검색실패한경우 -> 배달불가능으로 표시해주면 됩니다.
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 카테고리로 검색하기(로그인 없이)
    public List<StoreDTO> getCategorySearch(final int category, final int page, final String sort, String address) throws Exception{
        try{
            List<StoreEntity> entities = new ArrayList<>();
            // 1. 가게이름으로 검색한 entity 가져오기(10개씩!)
            switch (sort){
                case("orderCount"):// 주문수 정렬
                    entities = storeRepository.findByCategoryOrderByOrderCount(10, ((page-1)*10), address, category);
                    break;
                case("reviewScore"):
                    // 리뷰평점 정렬
                    break;
                case("charge"): // 배달팁 낮은순
                    break;
                case("distance"): // 거리순
                    break;
                default: // 기본값은 주문순으로
                    entities = storeRepository.findByCategoryOrderByOrderCount(10, ((page-1)*10), address, category);
            }
            List<StoreDTO> dtos = new ArrayList<>();
            // 2. DTO list로 변환
            for(StoreEntity entity : entities){
                // 2-1. 리뷰 평점 가져오기
                Optional<Double> score = reviewRepository.findReviewScoreByStoreId(entity.getStoreId());
                StoreDTO dto = StoreDTO.builder().storeId(entity.getStoreId())
                        .state(entity.getState()).name(entity.getName())
                        .thumb("http://localhost:8080/images/store/" + entity.getThumb() ).build();
                if(score.isPresent()){
                    dto.setScore(score.get());
                }
                dtos.add(dto);
            }
            return dtos;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 카테고리로 검색하기(로그인시)
    public List<StoreDTO> getCategorySearchWithLogin(final int category, final int page, final String sort, int memberId) throws Exception{
        try{
            // 멤버아이디로 주소가져오기
            MemberRocationEntity memberRocationEntity = memberRocationRepository.findAddress1ByMemberIdAndState(memberId, 1); //대표주소 가져옴
            String address = memberRocationEntity.getAddress1();
            List<StoreEntity> entities = new ArrayList<>();
            // 1. 가게이름으로 검색한 entity 가져오기(10개씩!)
            switch (sort){
                case("orderCount"):// 주문수 정렬
                    entities = storeRepository.findByCategoryOrderByOrderCount(10, ((page-1)*10), address, category);
                    break;
                case("reviewScore"):
                    // 리뷰평점 정렬
                    break;
                case("charge"): // 배달팁 낮은순
                    break;
                case("distance"): // 거리순
                    break;
                default: // 기본값은 주문순으로
                    entities = storeRepository.findByCategoryOrderByOrderCount(10, ((page-1)*10), address, category);
            }
            List<StoreDTO> dtos = new ArrayList<>();
            // 2. DTO list로 변환
            for(StoreEntity entity : entities){
                // 2-1. 리뷰 평점 가져오기
                Optional<Double> score = reviewRepository.findReviewScoreByStoreId(entity.getStoreId());
                StoreDTO dto = StoreDTO.builder().storeId(entity.getStoreId())
                        .state(entity.getState()).name(entity.getName())
                        .thumb("http://localhost:8080/images/store/" + entity.getThumb()).build();
                if(score.isPresent()){
                    dto.setScore(score.get());
                }
                dtos.add(dto);
            }
            return dtos;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 기간별 수익
    public StatsDTO viewStats(int memberId, HashMap<String, String> map) {

        if (memberId != storeRepository.selectMemberIdByStoreId(map.get("storeId"))) {
            log.warn("StoreService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("StoreService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        try {

            String type = map.get("type");
            String date = map.get("date");
            String from = map.get("from");
            String to = map.get("to");
            String date1 = null;
            String date2 = null;

            // 사용자가 선택한 날짜 형식에 맞게 변수에 쿼리 문장 설정
            switch (date) {
                // 당일
                case "0" :
                    date1 = LocalDate.now() + " 00:00:00";
                    date2 = LocalDate.now() + " 23:59:59";
                    break;
                // 1달 전
                case "1" :
                    date1 = "DATE_SUB(now(), interval 1 month)";
                    date2 = "now()";
                    break;
                // 3달 전
                case "3" :
                    date1 = " DATE_SUB("+LocalDate.now()+", interval 3 month) ";
                    date2 = LocalDate.now()+"";
                    break;
                // 6달 전
                case "6" :
                    date1 = "DATE_SUB(now(), interval 6 month)";
                    date2 = "now()";
                    break;
                // 직접 입력
                case "-1" :
                    date1 = from + " 00:00:00";
                    date2 = to + " 23:59:59";
                    break;
            }

            StatsDTO statsDTO = new StatsDTO();
            String storeId = map.get("storeId");

            // 사용자가 선택한 보여줄 정보에 맞는 함수로 이동
            switch (type) {
                // 메뉴
                case "0" :
                    statsDTO = viewMenuStats(storeId, date1, date2);
                    break;
                // 옵션
                case "1" :
                    statsDTO = viewOptionStats(map, date1, date2);
                    break;

            }

            return statsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("StoreService.viewStats : 에러 발생");
        }
    }

    // 기간별 메뉴 통계
    public StatsDTO viewMenuStats(String storeId, String date1, String date2) {

        try {

            // 전체 수익
            int profit;
            Integer tempProfit = orderListRepository.selectPriceByDate(2, storeId, date1, date2);
            if(tempProfit == null) tempProfit = 0;
            profit = tempProfit;

            StatsDTO statsDTO = new StatsDTO();
            List<MenuEntity> menuEntityList = menuRepository.findByStoreId(storeId);
            List<MenuDTO> statsMenuDTOList = new ArrayList<>();
            int totalAll = 0;

            // 가게에 있는 모든 메뉴 대입
            for (MenuEntity menuEntity : menuEntityList) { // 나중에 QLRM 사용해서 쿼리 합치기
                // 주문내역에 있는 해당 메뉴의 주문 수량 가져오기
                int count = orderListRepository.selectMenuCountByDate(2, menuEntity.getStoreId(), menuEntity.getMenuId(), date1, date2);
                // 메뉴 가격과 수량 합
                Integer tempTotal = orderListRepository.selectMenuSumByDate(2, menuEntity.getStoreId(), menuEntity.getMenuId(), date1, date2);
                // Integer to int 변환
                if(tempTotal == null) tempTotal = 0;
                int total = tempTotal;
                // MenuDTO에 넣기
                MenuDTO statsMenuDTO = new MenuDTO(menuEntity.getMenuName(), menuEntity.getPrice(), count, total);
                // MenuDTO를 MenuDTOList에 넣기
                statsMenuDTOList.add(statsMenuDTO);
                // 모든 메뉴 판매 수익
                totalAll = totalAll + total;
            }

            // 수량으로 정렬 후 StatsDTO에 넣기
            Comparator<MenuDTO> comparingMenuDTO = Comparator.comparing(MenuDTO::getCount, Comparator.reverseOrder());
            List<MenuDTO> newStatsMenuDTOList = statsMenuDTOList.stream().sorted(comparingMenuDTO).collect(Collectors.toList());
            statsDTO.setMenuDTOList(newStatsMenuDTOList);
            statsDTO.setProfit(profit);
            statsDTO.setTotalAll(totalAll);

            return statsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("StoreService.viewMenuStats : 기간별 메뉴 통계를 가져오는 중 에러 발생");
        }
    }

    // 기간별 옵션 통계
    public StatsDTO viewOptionStats(HashMap<String, String> map, String date1, String date2) {

        try {
            String storeId = map.get("storeId");
            int menuId = Integer.parseInt(map.get("menuId"));

            StatsDTO statsDTO = new StatsDTO();
            List<OptionEntity> optionEntityList = optionRepository.findByMenuId(menuId);
            List<OptionDTO> statsOptionDTOList = new ArrayList<>();
            int totalAll = 0;

            // 가게에 있는 모든 메뉴 대입
            for (OptionEntity optionEntity : optionEntityList) {
                // 주문내역에 있는 해당 옵션의 주문 수량 가져오기
                int count = orderListRepository.selectOptionCountByDate(2, storeId,
                        optionEntity.getMenuId(), optionEntity.getOptionId(), date1, date2);
                // 옵션 가격과 수량 합
                Integer tempTotal = orderListRepository.selectOptionSumByDate(2, storeId,
                        optionEntity.getMenuId(), optionEntity.getOptionId(), date1, date2);
                // Integer to int 변환
                if(tempTotal == null) tempTotal = 0;
                int total = tempTotal;
                // OptionDTO에 넣기
                OptionDTO statsOptionDTO = new OptionDTO(optionEntity.getOptionName(), optionEntity.getPrice(), count, total);
                // MenuDTO를 MenuDTOList에 넣기
                statsOptionDTOList.add(statsOptionDTO);
                // 모든 메뉴 판매 수익
                totalAll = totalAll + total;
            }

            // 수량으로 정렬 후 StatsDTO에 넣기
            Comparator<OptionDTO> comparatorOptionDTO = Comparator.comparing(OptionDTO::getCount, Comparator.reverseOrder());
            List<OptionDTO> newStatsOptionDTOList = statsOptionDTOList.stream().sorted(comparatorOptionDTO).collect(Collectors.toList());
            statsDTO.setOptionDTOList(newStatsOptionDTOList);
            statsDTO.setTotalAll(totalAll);

            return statsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("StoreService.viewMenuStats : 기간별 옵션 통계를 가져오는 중 에러 발생");
        }
    }

//    // 기간별 주문 취소 내역
//    public StatsDTO viewCancelStats(String storeId, String date1, String date2) {
//
//        try {
//
//            // 전체 수익
//            int profit = orderListRepository.selectPriceByDate(2, storeId, date1, date2);
//
//            StatsDTO statsDTO = new StatsDTO();
//            List<MenuDTO> menuDTOList = .findByStoreId(storeId);
//            List<MenuDTO> statsMenuDTOList = new ArrayList<>();
//            int totalAll = 0;
//
//            // 가게에 있는 모든 메뉴 대입
//            for (MenuDTO menuDTO : menuDTOList) {
//                // 주문내역에 있는 해당 메뉴의 주문 수량 가져오기
//                int count = orderListRepository.selectQuantityCountByDate(2, menuDTO.getStoreId(), menuDTO.getMenuId(), date1, date2);
//                // 메뉴 가격과 수량 합치기
//                int total = menuDTO.getPrice() * count;
//                // MenuDTO에 넣기
//                MenuDTO statsMenuDTO = new MenuDTO(menuDTO.getMenuName(), menuDTO.getPrice(), count, total);
//                // MenuDTO를 MenuDTOList에 넣기
//                statsMenuDTOList.add(statsMenuDTO);
//                // 모든 메뉴 판매 수익
//                totalAll = totalAll + total;
//            }
//
//            // 수량으로 정렬 후 StatsDTO에 넣기
//            Comparator<MenuDTO> comparingMenuDTO = Comparator.comparing(MenuDTO::getCount, Comparator.reverseOrder());
//            List<MenuDTO> newStatsMenuDTOList = statsMenuDTOList.stream().sorted(comparingMenuDTO).collect(Collectors.toList());
//            statsDTO.setMenuDTOList(newStatsMenuDTOList);
//            statsDTO.setProfit(profit);
//            statsDTO.setTotalAll(totalAll);
//
//            return statsDTO;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("StoreService.viewMenuStats : 기간별 메뉴 통계를 가져오는 중 에러 발생");
//        }
//    }
}
