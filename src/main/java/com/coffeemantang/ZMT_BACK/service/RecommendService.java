package com.coffeemantang.ZMT_BACK.service;

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
            for(int i = 0; i < address1.length; i++) {
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
            for(int i = 0; i < address1.length; i++) {
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

}
