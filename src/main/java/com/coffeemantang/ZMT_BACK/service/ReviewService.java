package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.ReviewCommentDTO;
import com.coffeemantang.ZMT_BACK.dto.ReviewDTO;
import com.coffeemantang.ZMT_BACK.model.ReviewCommentEntity;
import com.coffeemantang.ZMT_BACK.model.ReviewEntity;
import com.coffeemantang.ZMT_BACK.model.ReviewImgEntity;
import com.coffeemantang.ZMT_BACK.model.ReviewRecommendEntity;
import com.coffeemantang.ZMT_BACK.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.awt.print.Pageable;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewImgRepository reviewImgRepository;
    @Autowired
    private ReviewCommentRepository reviewCommentRepository;
    @Autowired
    private OrderListRepository orderListRepository;
    @Autowired
    private ReviewRecommendRepository reviewRecommendRepository;

    public void create(final int memberId, final ReviewDTO reviewDTO) throws Exception {
        // 리뷰 엔티티 생성
        final ReviewEntity reviewEntity = ReviewEntity.builder().memberId(memberId)
                .title(reviewDTO.getTitle())
                .content(reviewDTO.getContent())
                .date(LocalDateTime.now())
                .recommend(0)
                .storeId(reviewDTO.getStoreId())
                .build();

        List<MultipartFile> multipartFiles = reviewDTO.getFiles();

        int reviewId = reviewRepository.save(reviewEntity).getReviewId(); // 저장한 리뷰 아이디

        // 반환할 파일 리스트
        List<ReviewImgEntity> fileList = new ArrayList<>();
        // 전달되어 온 파일이 존재할 경우
        String current_date = null;
        if (!CollectionUtils.isEmpty(multipartFiles)) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            current_date = now.format(dateTimeFormatter);

            // 프로젝트 디렉터리 내의 저장을 위한 절대 경로 설정
            // 경로 구분자 File.separator 사용
            String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;

            // 파일을 저장할 세부 경로 지정
            String path = "images" + File.separator + current_date;
            File file = new File(path);

            // 디렉터리가 존재하지 않을 경우
            if (!file.exists()) {
                boolean wasSuccessful = file.mkdirs(); // 디렉터리 생성

                // 디렉터리 생성에 실패했을 경우
                if (!wasSuccessful) {
                    log.warn("file: was not successful");
                }
            }

            // 다중 파일 처리
            for (MultipartFile multipartFile : multipartFiles) {
                // 파일의 확장자 추출
                String originalFileExtension;
                String contentType = multipartFile.getContentType();

                // 확장자명이 존재하지 않을 경우 처리하지 않음
                if (ObjectUtils.isEmpty(contentType)) {
                    break;
                } else { // 확장자가 jpeg, png인 파일들만 받아서 처리
                    if (contentType.contains("image/jpeg")) {
                        originalFileExtension = ".jpg";
                    } else if (contentType.contains("images/png")) {
                        originalFileExtension = ".png";
                    } else { // 다른 확장자일 경우 처리하지 않음
                        break;
                    }
                }

                // 파일명 중복 피하기 위해 나노초까지 얻어와 지정
                String new_file_name = System.nanoTime() + originalFileExtension; // 나노초 + 확장자

                // 엔티티 생성
                ReviewImgEntity reviewImgEntity = ReviewImgEntity.builder()
                        .reviewId(reviewId) // 리뷰아이디
                        .path(path + file.separator + new_file_name)
                        .build();

                // 생성후 리스트에 추가
                fileList.add(reviewImgEntity);

                // 업로드 한 파일 데이터를 지정한 파일에 저장
                file = new File(absolutePath + path + File.separator + new_file_name);
                multipartFile.transferTo(file);

                // 파일 권한 설정
                file.setWritable(true);
                file.setReadable(true);

                // 엔티티 저장
                reviewImgRepository.save(reviewImgEntity);

            }
        }
    }

    // 들어온 멤버아이디와 리뷰아이디를 비교해서 리뷰 삭제
    public void delete(final int memberId, final int reviewId) throws Exception{
        try{
            if(memberId < 1){
                log.warn("ReviewService delete() : memberId error");
                throw new RuntimeException("ReviewService delete() : memberId error");
            }
            Optional<ReviewEntity> oReviewEntity = reviewRepository.findByReviewId(reviewId);
            if(oReviewEntity.isPresent()){ // null 체크
                // 1. 멤버아이디와 해당 리뷰의 작성자가 일치하는지 확인
                ReviewEntity reviewEntity = oReviewEntity.get();
                if(reviewEntity.getMemberId() != memberId){
                    log.warn("ReviewService delete() : member review match error");
                    throw new RuntimeException("ReviewService delete() : member review match error");
                }

                // 2. 데이터가 있을 경우 삭제
                reviewRepository.delete(reviewEntity);
            }
        }catch (Exception e){
            log.warn("ReviewService delete() : unknown error");
            throw new RuntimeException("ReviewService delete() : unknown error");
        }
    }

    // 리뷰의 답글 추가
    public void createComment(final int memberId, final ReviewCommentDTO reviewCommentDTO) throws Exception{
        try{
            // 아이디 유효성 검사
            if(memberId < 0){
                log.warn("ReviewService createComment() : memberId error");
                throw new RuntimeException("ReviewService createComment() : memberId error");
            }

            // ReviewCommentEntity 생성 후 저장
            int reviewId = reviewCommentDTO.getReviewId();
            ReviewCommentEntity reviewCommentEntity = ReviewCommentEntity.builder()
                    .reviewId(reviewId)
                    .memberId(memberId)
                    .content(reviewCommentDTO.getContent()).build();
            reviewCommentRepository.save(reviewCommentEntity);
        }catch (Exception e){
            log.warn("ReviewService createComment() : Exception");
            throw new RuntimeException("ReviewService createComment() : Exception");
        }
    }

    // 주문 내역이 한달 내에 있는지 검사
    public boolean checkOrder(final int memberId, final String storeId) throws Exception{
        try{
            // 아이디 유효성 검사
            if(memberId < 0){
                log.warn("ReviewService checkOrder() : memberId error");
                throw new RuntimeException("ReviewService checkOrder() : memberId error");
            }
            if(storeId == null || storeId.equals("")){
                log.warn("ReviewService checkOrder() : storeId error");
                throw new RuntimeException("ReviewService checkOrder() : storeId error");
            }

            // 한달 내에 해당 가게 + 해당 회원의 주문 내역 갯수와 리뷰 갯수 가져오기
            // 한달 내의 리뷰 갯수
            final int reviewCount = reviewRepository.countByStoreIdAndMemberId(storeId, memberId, LocalDateTime.now());
            // 한달 내의 주문내역 갯수
            final int orderCount = orderListRepository.countByStoreIdAndMemberId(storeId, memberId, LocalDateTime.now());

            // 주문내역 갯수가 더 많으면 true 반환
            if(orderCount > reviewCount){
                return true;
            }
            // 아니면 false 반환
            return false;
        }catch (Exception e){
            log.warn("ReviewService checkOrder() : Exception");
            throw new RuntimeException("ReviewService checkOrder() : Exception");
        }
    }

    // 해당 페이지의 가게 리뷰 리스트를 가져옴(10개씩)
    public List<ReviewDTO> getStoreReviewList(final String storeId, final Pageable pageable) throws Exception{
        try{
            // 아이디 유효성 검사
            if(storeId == null || storeId.equals("")){
                log.warn("ReviewService getStoreReviewList() : storeId error");
                throw new RuntimeException("ReviewService getStoreReviewList() : storeId error");
            }

            // 해당 페이지의 가게 리뷰 리스트 가져오기
            Page<ReviewEntity> reviewPage = reviewRepository.findByStoreIdOrderByDateDesc(storeId, pageable);
            List<ReviewEntity> reviewList = reviewPage.getContent();

            List<ReviewDTO> listReviewDTO = new ArrayList<>();
            // 해당 리뷰의 이미지 가져와서 DTO만들기
            for (ReviewEntity list:reviewList) {
                List<ReviewImgEntity> listReviewImgEntity = reviewImgRepository.findByReviewId(list.getReviewId());
                // 1. 해당 리뷰의 ReviewDTO 만들기
                ReviewDTO reviewDTO = new ReviewDTO(list);
                // 2. 만든 ReviewDTO에 ReviewImgDTO 넣기
                List<String> files = reviewDTO.getReviewFiles();
                for(ReviewImgEntity list2: listReviewImgEntity){
                    files.add(list2.getPath()); // path 넣기
                }
                // 3. 리턴할 list에 reviewDTO 추가
                listReviewDTO.add(reviewDTO);
            }

            return listReviewDTO;
        }catch (Exception e){
            return null;
        }
    }

    // 리뷰 이미 추천했는지 체크
    public boolean checkRecommend(final int memberId, final int reviewId){
        // 멤버아이디와 리뷰아이디로 이미 추천했는지 확인
        if(reviewRecommendRepository.countByMemberIdAndReviewId(memberId, reviewId) < 1){
            return false;
        }
        return true;
    }

    // 리뷰 추천
    @Transactional
    public boolean recommend(final int memberId, final int reviewId) throws Exception{
        try {
            // 1. 이미 추천한 리뷰인지 확인
            if(!checkRecommend(memberId, reviewId)){
                // 이미 추천한 경우 false 리턴
                return false;
            }
            // 2. ReviewDTO에 추천 카운트 올림
            final Optional<ReviewEntity> oReviewEntity = reviewRepository.findByReviewId(reviewId);
            final ReviewEntity reviewEntity = oReviewEntity.get();
            reviewEntity.setRecommend(reviewEntity.getRecommend() + 1);
            reviewRepository.save(reviewEntity);
            // 3. ReviewRecommend 테이블에도 해당 내용 추가
            final ReviewRecommendEntity reviewRecommendEntity = ReviewRecommendEntity.builder()
                    .memberId(memberId).reviewId(reviewId).build();
            reviewRecommendRepository.save(reviewRecommendEntity);
            // 4. 리턴
            return true;
        }catch (Exception e){
            log.warn("ReviewService recommend() : Exception");
            throw new RuntimeException("ReviewService recommend() : Exception");
        }
    }


}