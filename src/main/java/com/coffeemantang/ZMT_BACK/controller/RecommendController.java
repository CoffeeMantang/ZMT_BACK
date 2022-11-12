package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.RecommendDTO;
import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/recommend")
public class RecommendController {
    @Autowired
    private RecommendService recommendService;

    //로그인 시에 사용 가능한 추천시스템
    @PostMapping("/recommend")
    public ResponseEntity<?> recommend(@AuthenticationPrincipal String memberId, @RequestParam(value = "curList", required = false) List<Integer> curList) throws Exception{
        try{
            if(curList == null){
                curList = new ArrayList<>();
            }
            // 1. 사용자 기반 추천 시스템을 사용해 List 뽑아오기
            List<RecommendDTO> resultList = recommendService.userRecommend(Integer.parseInt(memberId), curList);

            // 2. 컨텐츠 기반 추천 시스템을 사용해 List 뽑아오기
            curList.clear();
            for(RecommendDTO dto : resultList){
                curList.add(dto.getMenuId());
            }
            List<RecommendDTO> resultList2 = recommendService.contentRecommend(Integer.parseInt(memberId), curList);

            // 3. 두가지 합치기
            resultList.addAll(resultList2);

            // 4. 리턴
            return ResponseEntity.ok().body(resultList);

        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
