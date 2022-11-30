package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.dto.ReviewCommentDTO;
import com.coffeemantang.ZMT_BACK.dto.ReviewDTO;
import com.coffeemantang.ZMT_BACK.persistence.ReviewCommentRepository;
import com.coffeemantang.ZMT_BACK.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    // 리뷰 생성 (이미지 추가)
    @PostMapping("/create")
    public ResponseEntity<?> createReview(@AuthenticationPrincipal String memberId, @RequestBody ReviewDTO reviewDTO) throws Exception{
        try{
            // 리뷰 쓰기가 가능할 경우에만 create
            if (reviewService.checkOrder(Integer.parseInt(memberId), reviewDTO.getStoreId())){
                reviewService.create(Integer.parseInt(memberId),reviewDTO);

                ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
                return ResponseEntity.ok().body(responseDTO);
            }
            ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 리뷰 삭제
    @PostMapping("/delete")
    public ResponseEntity<?> deleteReview(@AuthenticationPrincipal String memberId, @RequestBody ReviewDTO reviewDTO) throws Exception{
        try{
            reviewService.delete(Integer.parseInt(memberId), reviewDTO.getReviewId());

            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 리뷰 답글(해당 가게의 주인만 가능)
    @PostMapping("/comment")
    public ResponseEntity<?> comment(@AuthenticationPrincipal String memberId, @RequestBody ReviewCommentDTO reviewCommentDTO) throws Exception{
        try{
            reviewService.createComment(Integer.parseInt(memberId), reviewCommentDTO);

            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    
    // 리뷰 추천(계정 당 하나)...
    @PostMapping("/recommend")
    public ResponseEntity<?> recommend(@AuthenticationPrincipal String memberId, @RequestParam(value = "reviewId") int reviewId) throws Exception{
        try{
            if(reviewService.recommend(Integer.parseInt(memberId), reviewId)){
                ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
                return ResponseEntity.ok().body(responseDTO);
            }
            ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 리뷰 작성 가능 여부 가져오기
    @PostMapping("/checkreview")
    public ResponseEntity<?> recommend(@AuthenticationPrincipal String memberId, @RequestParam(value = "orderlistId") String orderlistId) throws Exception{
        try{
            if(reviewService.checkReview(Integer.parseInt(memberId), orderlistId)){
                ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
                return ResponseEntity.ok().body(responseDTO);
            }else{
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 내 리뷰 목록 가져오기(페이징)
    @PostMapping("myreview")
    public ResponseEntity<?> myReview(@AuthenticationPrincipal String memberId, @PageableDefault(size = 10) Pageable pageable) throws Exception{
        try{
            List<ReviewDTO> listReview = reviewService.getMyReview(Integer.parseInt(memberId), pageable);
            if(listReview.isEmpty()){ // 리뷰가 없을때
                ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
                return ResponseEntity.ok().body(responseDTO);
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
