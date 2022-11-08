package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.Recommend.ContentElement;
import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.dto.RecommendDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.model.*;
import com.coffeemantang.ZMT_BACK.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.text.similarity.CosineSimilarity;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
// 메뉴추천 기능을 위한 Service
public class RecommendService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MemberRocationRepository memberRocationRepository;
    @Autowired
    private OrderMenuRepository orderMenuRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private MenuImgRepository menuImgRepository;

    private CosineSimilarity cosineSimilarity = new CosineSimilarity();

    // 사용자 기반 추천 : 메뉴아이디의 리스트를 넘기면 해당 메뉴들은 빼고 추천해줌 더 추천할게 없으면 null 리턴
    public List<RecommendDTO> userRecommend(final int memberId, final List<Integer> menuIdList) throws Exception {
        try{
            // 1. 현재 회원의 Entity 가져오기
            MemberEntity memberEntity = memberRepository.findByMemberId(memberId);
            // 2. 현재 회원이 설정한 주소 가져오기
            Optional<MemberRocationEntity> oMemberRocation = memberRocationRepository.findAllByMemberIdAndState(memberId, 1);
            // 3. 현재 회원이 설정한 주소에서 '시군구'와 '읍면동'을 가져옴
            String[] address1 = oMemberRocation.get().getAddress1().split(" ");
            String dong = null;
            String si = null;
            for(int i = 0; i < address1.length - 1; i++) {
                if (address1[i].endsWith("읍")) {
                    dong = address1[i];
                    break;
                }
                if (address1[i].endsWith("면")) {
                    dong = address1[i];
                    break;
                }
                if (address1[i].endsWith("동")) {
                    dong = address1[i];
                    break;
                }
            }
            for(int i = 0; i < address1.length - 1; i++) {
                if (address1[i].endsWith("시")) {
                    si = address1[i];
                    break;
                }
                if (address1[i].endsWith("군")) {
                    si = address1[i];
                    break;
                }
                if (address1[i].endsWith("구")) {
                    si = address1[i];
                    break;
                }
            }
            // 4. 현재 회원과 같은 동에 사는 사람들의 memberId를 가져옴
            List<Integer> memberList = memberRepository.findMemberIdByAddress(si + " " + dong);
            // 5. 현재 회원이 주문한 메뉴 리스트 가져오기(단, 현재 주소지에서 주문 가능한 메뉴)
            List<Integer> menuList = orderMenuRepository.findMenuIdByMemberIdAndAddress(si + " " + dong, memberId);
            // 6. 현재 회원의 Map 생성
            Map<CharSequence, Integer> curMemberMap = new HashMap<>(); // key: menuId, value: 주문횟수
            for(Integer menuId : menuList){
                int count = orderMenuRepository.countByMemberIdAndMenuId(memberId, menuId);
                curMemberMap.put(String.valueOf(menuId), count);
            }
            // 7. 같은 지역의 사는 유저들의 Map 가져와서 cos 유사도 비교
            Map<Integer, Double> cosMap = new HashMap<>(); // K : memberId, V : cosSimilarity
            for (Integer id : memberList) {
                Map<CharSequence, Integer> memberMap = new HashMap<>();
                for (Integer menuId : menuList){
                    int count = orderMenuRepository.countByMemberIdAndMenuId(memberId, menuId);
                    memberMap.put(String.valueOf(menuId), count);
                }
                double sim = cosineSimilarity.cosineSimilarity(curMemberMap, memberMap); // 코사인유사도 계산값
                cosMap.put(id, sim); // cosMap에 추가
            }
            // 8. 유사도 내림차순으로 정렬
            List<Integer> sortedMemberIdList = new ArrayList<>(cosMap.keySet());
            sortedMemberIdList.sort((s1, s2) -> cosMap.get(s2).compareTo(cosMap.get(s1)));
            // 8-1. 유사도가 가장 높은 회원은 자기자신이므로 제외
            sortedMemberIdList.remove(0);

            // 9. 가장 유사도가 높은 유저부터 차례대로 비교해 10개 추출
            int count = 0;
            Comparator<Integer> comparator = (s1, s2) -> s2.compareTo(s1); // 내림차순 정렬을 위한 Comparator
            List<Integer> similarList = new ArrayList<>();
            for(Integer otherMember : sortedMemberIdList){
                // 9-1. 유사도가 높은 유저가 주문한 메뉴들 가져오기(단, 주문가능한 메뉴에 한정)
                List<Integer> otherMemberMenuList = orderMenuRepository.findMenuIdByMemberIdAndAddress(si + " " + dong, otherMember);
                Map<Integer, Integer> otherMemberMap = new TreeMap<>(comparator); // K: menuId, V: orderCount
                // 9-2. 유사도가 높은 유저가 주문한 메뉴들의 주문횟수 가져오기
                for(Integer menuId : otherMemberMenuList){
                    otherMemberMap.put(menuId, orderMenuRepository.countByMemberIdAndMenuId(otherMember, menuId));
                }
                // 9-3. 현재 유저가 주문하지 않은 메뉴만 추출
                curMemberMap.forEach(
                        (k, v) -> otherMemberMap.remove(k)
                );
                // 9-4. 위에서 순서대로 메뉴 추출 (단, 주문횟수 3회 이상인 메뉴만)
                Iterator<Integer> otherMemberKeys = otherMemberMap.keySet().iterator();
                while(otherMemberKeys.hasNext()){
                    int value = otherMemberMap.get(otherMemberKeys.next());
                    if(value < 3){
                        break;
                    }
                    // 매개변수로 들어온 리스트에 이미 해당 메뉴가 있는지 체크
                    if(!menuIdList.contains(value)){
                        // 없는 경우에만 similarList에 삽입
                        similarList.add(value);
                        count++;
                    }
                }
                // 9-5. 메뉴 10개를 채우면 종료
                if(count >= 10){
                    break;
                }
            }
            if(count == 0){ // 끝까지 돌았는데 찾은게 0개면 null 리턴
                return null;
            }
            // 10. 추출한 menuId리스트로 recommendDTO 생성
            List<RecommendDTO> resultList = new ArrayList<>();
            for (Integer menuId : similarList){
               MenuEntity menuEntity = menuRepository.findByMenuId(menuId);
               StoreEntity storeEntity = storeRepository.findByMenuId(menuId);
               MenuImgEntity menuImgEntity = menuImgRepository.findByMenuId(menuId);
               RecommendDTO recommendDTO = RecommendDTO.builder()
                       .menuName(menuEntity.getMenuName())
                       .menuId(menuId)
                       .menuPic(menuImgEntity.getPath())
                       .storeId(storeEntity.getStoreId())
                       .state(0)
                       .storeName(storeEntity.getName()).build();
               if(menuEntity.getState() != 0 || storeEntity.getState() != 1){ // 현재 주문 불가능한 메뉴 체크
                   recommendDTO.setState(1);
               }
               resultList.add(recommendDTO);
            }
            return resultList;
        }
        catch(Exception e){
            log.warn("RecommendService.userRecommend() : Exception");
            throw new RuntimeException("RecommendService.userRecommend() : Exception");
        }
    }

    // 컨텐츠 기반 추천
    public List<RecommendDTO> contentRecommend(final int memberId, final List<Integer> menuIdList) throws Exception{
        try{
            // 1. 현재 회원의 Entity 가져오기
            MemberEntity memberEntity = memberRepository.findByMemberId(memberId);
            // 2. 현재 회원이 설정한 주소 가져오기
            Optional<MemberRocationEntity> oMemberRocation = memberRocationRepository.findAllByMemberIdAndState(memberId, 1);
            // 3. 현재 회원이 설정한 주소에서 '시군구'와 '읍면동'을 가져옴
            String[] address1 = oMemberRocation.get().getAddress1().split(" ");
            String dong = null;
            String si = null;
            for(int i = 0; i < address1.length - 1; i++) {
                if (address1[i].endsWith("읍")) {
                    dong = address1[i];
                    break;
                }
                if (address1[i].endsWith("면")) {
                    dong = address1[i];
                    break;
                }
                if (address1[i].endsWith("동")) {
                    dong = address1[i];
                    break;
                }
            }
            for(int i = 0; i < address1.length - 1; i++) {
                if (address1[i].endsWith("시")) {
                    si = address1[i];
                    break;
                }
                if (address1[i].endsWith("군")) {
                    si = address1[i];
                    break;
                }
                if (address1[i].endsWith("구")) {
                    si = address1[i];
                    break;
                }
            }
            // 4. 현재 회원이 주문한 메뉴 리스트 가져오기(주문횟수 순으로 정렬, 주문횟수는 3회 이상)
            List<Integer> menuList = orderMenuRepository.findMenuIdByMemberIdAndAddressSortDESC(si + " " + dong, memberId);
            Map<Integer, Map<CharSequence, Integer>> menuMap = new LinkedHashMap<>();
            String[] elements = ContentElement.getElements(); // 컨텐츠 기반 추천에 필요한 컬럼들 가져오기
            // 5. 현재 회원이 주문한 메뉴들의 태그 가져와서 맵 만들기
            for(Integer menuId : menuList){
                Optional<String> oTag = menuRepository.findTagByMenuId(memberId);
                if(oTag.isPresent()){
                    // 5-1. 태그를 요소별로 나누어 맵 만들기
                    Map<CharSequence, Integer> tempMap = new HashMap<>();
                    for(int i = 0; i < elements.length-1; i++){
                        if(elements[i].contains(oTag.get())){ // 해당하는 요소가 있는 메뉴인 경우 value에 1
                            tempMap.put(elements[i], 1);
                        }else{
                            tempMap.put(elements[i], 0); // 아닌경우에는 0
                        }
                    }
                    // 5-2. 만든 맵을 menuMap에 추가
                    menuMap.put(menuId, tempMap);
                }
            }
            // 6. 현재 위치에서 주문 가능한 메뉴 모두 가져오기
            List<MenuEntity> canMenuIdList = menuRepository.findMenuByAddress(si + " " + dong);
            // 7. 현재 위치에서 주문 가능한 메뉴들의 Map 만들기
            Map<Integer, Map<CharSequence, Integer>> canMenuMap = new HashMap<>();
            for(MenuEntity menuEntity : canMenuIdList){
                // 7-1. 태그를 요소별로 나누어 맵 만들기
                Map<CharSequence, Integer> tempMap = new HashMap<>();
                for(int i = 0; i < elements.length-1; i++){
                    if(elements[i].contains(menuEntity.getTag())){ // 해당하는 요소가 있는 메뉴인 경우 value에 1
                        tempMap.put(elements[i], 1);
                    }else{
                        tempMap.put(elements[i], 0); // 아닌경우에는 0
                    }
                }
                // 7-2. 만든 맵을 canMenuMap에 추가
                canMenuMap.put(menuEntity.getMenuId(), tempMap);
            }
            // 8. 가장 많이 주문한 메뉴부터 cos유사도 계산해 상위 3개 메뉴씩 가져와 10개 모으기(단, menuIdList에 있는것 제외)
            List<Integer> similarityMenuList = new ArrayList<>(); // 결과물로 나오는 메뉴들을 담을 List
            int count = 0; // 갯수를 카운트하기 위한 변수
            for(Integer menuId : menuList){
                Map<Integer, Double> similarityMap = new HashMap<>(); // K: menuId, V: 유사도
                // 8-1. 만든 맵을 이용해 주문 가능한 모든 메뉴들과 유사도 비교해 저장
                Iterator<Integer> tempKeys = canMenuMap.keySet().iterator();
                while(tempKeys.hasNext()){
                    double similarity = cosineSimilarity.cosineSimilarity(menuMap.get(menuId), canMenuMap.get(tempKeys.next()));
                    similarityMap.put(tempKeys.next(), similarity);
                }
                // 8-2. 내가 주문한 메뉴와 주문가능한 메뉴들의 유사도 맵을 value값 내림차순으로 정렬
                List<Integer> sortedMenuIdList = new ArrayList<>(similarityMap.keySet());
                sortedMenuIdList.sort((s1, s2) -> similarityMap.get(s2).compareTo(similarityMap.get(s1)));
                // 8-3. 첫번째는 자기자신이므로 제거
                sortedMenuIdList.remove(0);
                // 8-4. 매개변수로 들어온 menuIdList와 비교해서 없는 메뉴만 추가(3개만)
                int tempCount = 0; // 갯수 카운트를 위한 변수. 3개만 꺼내야 함
                for(Integer sortedMenuId : sortedMenuIdList){
                    for(Integer paramMenuId : menuIdList ){
                        if(paramMenuId != sortedMenuId){
                            similarityMenuList.add(sortedMenuId);
                            tempCount++;
                            if(tempCount >= 3 || count >= 10) break; // 해당 메뉴와 비슷한 메뉴 3개를 추가했거나 전체 리스트가 10이넘으면 break
                        }
                    }
                    if(tempCount >= 3 || count >= 10) break;
                }
                if(tempCount >= 3 || count >= 10) break;
            }
            // 9. 추출한 menuId리스트로 recommendDTO 생성
            List<RecommendDTO> resultList = new ArrayList<>();
            for (Integer menuId : similarityMenuList){
                MenuEntity menuEntity = menuRepository.findByMenuId(menuId);
                StoreEntity storeEntity = storeRepository.findByMenuId(menuId);
                MenuImgEntity menuImgEntity = menuImgRepository.findByMenuId(menuId);
                RecommendDTO recommendDTO = RecommendDTO.builder()
                        .menuName(menuEntity.getMenuName())
                        .menuId(menuId)
                        .menuPic(menuImgEntity.getPath())
                        .storeId(storeEntity.getStoreId())
                        .state(0)
                        .storeName(storeEntity.getName()).build();
                if(menuEntity.getState() != 0 || storeEntity.getState() != 1){ // 현재 주문 불가능한 메뉴 체크
                    recommendDTO.setState(1);
                }
                resultList.add(recommendDTO);
            }
            return resultList;
        }catch (Exception e){
            log.warn("RecommendService.contentRecommend() : Exception");
            throw new RuntimeException("RecommendService.contentRecommend() : Exception");
        }
    }

}
