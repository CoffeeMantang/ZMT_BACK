package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.ReviewCommentDTO;
import com.coffeemantang.ZMT_BACK.dto.ReviewDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.model.*;
import com.coffeemantang.ZMT_BACK.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
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
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StoreRepository storeRepository;

    public void create(final int memberId, List<MultipartFile> multipartFiles, String title, String content, String orderlistId, int score) throws Exception {

        try{
            // 리뷰작성 가능한지 체크
            OrderListEntity orderListEntity = orderListRepository.findByOrderlistId(orderlistId);
            LocalDateTime start = LocalDateTime.now().minusMonths(1); // 한달전

            if(orderListEntity == null || orderListEntity.getOrderDate().isBefore(start) || orderListEntity.getState() != 4){ // 리뷰작성가능한지 체크
                log.warn("리뷰작성불가능1");
                return;
            }else{
                long count = reviewRepository.countByOrderlistIdAndDateBetween(orderlistId, start, LocalDateTime.now() );
                if(count > 0){
                    log.warn("리뷰작성불가능2");
                    return;
                }
            }
            // 리뷰 엔티티 생성
            final ReviewEntity reviewEntity = ReviewEntity.builder().memberId(memberId)
                    .title(title)
                    .content(content)
                    .date(LocalDateTime.now())
                    .recommend(0)
                    .storeId(orderListEntity.getStoreId())
                    .orderlistId(orderListEntity.getOrderlistId())
                    .score(score)
                    .build();

            int reviewId = reviewRepository.save(reviewEntity).getReviewId(); // 저장한 리뷰 아이디

            // 사진이 있으면
            if (checkFileNull(multipartFiles)) {

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
                    // String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;

                    String absolutePath = "C:" + File.separator + "zmtImgs" + File.separator + "reviewImg";

                    // 파일을 저장할 세부 경로 지정
                    String path = absolutePath;
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
                    int cnt = 1;
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
                        //String new_file_name = System.nanoTime() + originalFileExtension; // 나노초 + 확장자

                        // 파일명은 아이디 + _숫자로
                        String new_file_name = String.valueOf(reviewId) + "_" + String.valueOf(cnt);

                        // 엔티티 생성
                        ReviewImgEntity reviewImgEntity = ReviewImgEntity.builder()
                                .reviewId(reviewId) // 리뷰아이디
                                .path(new_file_name + originalFileExtension)
                                .build();

                        // 생성후 리스트에 추가
                        fileList.add(reviewImgEntity);

                        // 업로드 한 파일 데이터를 지정한 파일에 저장
                        file = new File(path + File.separator + new_file_name + originalFileExtension);
                        multipartFile.transferTo(file);

                        // 파일 권한 설정
                        file.setWritable(true);
                        file.setReadable(true);

                        // 엔티티 저장
                        reviewImgRepository.save(reviewImgEntity);
                        cnt++;

                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
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
    public StoreDTO getStoreReviewList(final String storeId, final Pageable pageable) throws Exception{
        try{
            // 아이디 유효성 검사
            if(storeId == null || storeId.equals("")){
                log.warn("ReviewService getStoreReviewList() : storeId error");
                throw new RuntimeException("ReviewService getStoreReviewList() : storeId error");
            }

            log.warn("들어온 가게아이디: " + storeId);

            // 해당 페이지의 가게 리뷰 리스트 가져오기
            Page<ReviewEntity> reviewPage = reviewRepository.findByStoreIdOrderByDateDesc(storeId, pageable);
            if(reviewPage == null){
                log.warn("reviewPage는 널이예요");
            }
            List<ReviewEntity> reviewList = reviewPage.getContent();
            log.warn("reviewList의 갯수는" + reviewList.size());


            List<ReviewDTO> listReviewDTO = new ArrayList<>();
            // 해당 리뷰의 이미지 가져와서 DTO만들기
            for (ReviewEntity list:reviewList) {
                log.warn("리뷰아이디" + list.getReviewId() + "인 리뷰의 이미지 찾기");
                List<ReviewImgEntity> listReviewImgEntity = reviewImgRepository.findByReviewId(list.getReviewId());
                // 1. 해당 리뷰의 ReviewDTO 만들기
                ReviewDTO reviewDTO = new ReviewDTO(list);
                // 멤버의 닉네임 찾아서 넣기
                MemberEntity nickname = memberRepository.findByMemberId(list.getMemberId());
                reviewDTO.setNickname(nickname.getNickname());
                // 2. 만든 ReviewDTO에 ReviewImgDTO 넣기
                List<String> imageList = new ArrayList<>();
                log.warn("listReviewImgEntity의 사이즈 " + listReviewImgEntity.size());
                if(listReviewImgEntity.size() > 0){
                    for(ReviewImgEntity list2: listReviewImgEntity){
                        imageList.add("http://localhost:8080/images/review/" + list2.getPath()); // path 넣기
                    }
                }
                reviewDTO.setImages(imageList);
                // 3. 유저의 닉네임 추가
                MemberEntity memberEntity = memberRepository.findByMemberId(list.getMemberId());
                reviewDTO.setNickname(memberEntity.getNickname());
                // 리턴할 list에 reviewDTO 추가
                listReviewDTO.add(reviewDTO);
            }

            StoreEntity storeEntity = storeRepository.findByStoreId(storeId);

            StoreDTO storeDTO = StoreDTO.builder().storeId(storeId).name(storeEntity.getName()).build();
            // 가게 리뷰 갯수 가져오기
            long count = reviewRepository.countByStoreId(storeId);
            storeDTO.setReviewCount(count);
            // 가게 리뷰 평점 가져오기
            Optional<Double> avg = reviewRepository.findReviewScoreByStoreId(storeId);
            if(avg.isPresent()){
                storeDTO.setScore(avg.get());
            }
            storeDTO.setReviewList(listReviewDTO);
            return storeDTO;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
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

    // 리뷰작성 가능여부 체크
    public boolean checkReview(final int memberId, final String storeId) throws Exception{
        try{
            // 한달 내에 해당 가게 + 해당 회원의 주문 내역 갯수와 리뷰 갯수 가져오기
            // 한달 내의 리뷰 갯수
            final int reviewCount = reviewRepository.countByStoreIdAndMemberId(storeId, memberId, LocalDateTime.now());
            // 한달 내의 주문내역 갯수
            final int orderCount = orderListRepository.countByStoreIdAndMemberId(storeId, memberId, LocalDateTime.now());

            // 주문내역 갯수가 더 많으면 true 반환
            if(orderCount > reviewCount){
                return true;
            }
            return false;
        }catch (Exception e){
            log.warn("ReviewService checkReview() : Exception");
            throw new RuntimeException("ReviewService checkReview() : Exception");
        }
    }

    // 내 리뷰 가져오기
    public List<ReviewDTO> getMyReview(final int memberId, final Pageable pageable) throws Exception{
        try{
            // 아이디 유효성검사
            if(memberId < 1){
                log.warn("ReviewService getMyReview() : memberId error");
                throw new RuntimeException("ReviewService getMyReview() : memberId error");
            }

            // 해당 페이지의 내 리뷰 리스트 가져오기
            Page<ReviewEntity> reviewPage = reviewRepository.findByMemberIdOrderByDateDesc(memberId, pageable);
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
            log.warn("ReviewService checkReview() : Exception");
            throw new RuntimeException("ReviewService checkReview() : Exception");
        }
    }

    // 리뷰 삭제하기
    @Transactional
    public boolean deleteReview(final int memberId, final int reviewId) throws Exception{
        try{
            // 아이디 유효성검사
            if(memberId < 1){
                log.warn("ReviewService getMyReview() : memberId error");
                throw new RuntimeException("ReviewService getMyReview() : memberId error");
            }
            // 아이디와 리뷰아이디 매칭 체크
            if(reviewRepository.countByMemberIdAndReviewId(memberId, reviewId) < 1){
                return false;
            }
            // 삭제
            Optional<ReviewEntity> oReview = reviewRepository.findByReviewId(reviewId);
            if(oReview.isPresent()){
                reviewRepository.delete(oReview.get());
            }
            return true;
        }catch (Exception e){
            log.warn("ReviewService deleteReview() : Exception");
            throw new RuntimeException("ReviewService deleteReview() : Exception");
        }
    }

    // 파일 null 체크
    public boolean checkFileNull(List<MultipartFile> files) {
        return files != null;
    }
}