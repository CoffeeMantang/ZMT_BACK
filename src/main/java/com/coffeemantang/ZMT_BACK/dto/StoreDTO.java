package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// StoreController를 위한 DTO. StoreInfo를 붙여서 보내야 할 경우 두가지를 합친 DTO를 별도로 만듬
public class StoreDTO {
    private String storeId;
    private int memberId; // 가게주인
    private String name; // 가게명
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime joinDay; // 생성일
    private int category; // 큰 카테고리
    private String thumb; // 가게 썸네일 주소
    private String address1; // 큰 주소
    private String address2; // 상세주소
    private int state; // 가게상태, 0:영업준비중, 1:영업중, 2:영업중단
    private double addressX;
    private double addressY;
    private int hits; // 가게 조회수.

    public StoreDTO(final StoreEntity entity){
        this.storeId = entity.getStoreId();
        this.memberId = entity.getMemberId();
        this.name = entity.getName();
        this.address1 = entity.getAddress1();
        this.address2 = entity.getAddress2();
        this.addressX = entity.getAddressX();
        this.addressY = entity.getAddressY();
        this.category = entity.getCategory();
    }

    // DTO를 Entity로 변환하기 위한 메서드
    public static StoreEntity toEntity(final StoreDTO dto){
        return StoreEntity.builder()
                .storeId(dto.getStoreId())
                .memberId(dto.getMemberId())
                .name(dto.getName())
                .address1(dto.getAddress1())
                .address2(dto.getAddress2())
                .addressX(dto.getAddressX())
                .addressY(dto.getAddressY())
                .category(dto.getCategory()).build();
    }
}
