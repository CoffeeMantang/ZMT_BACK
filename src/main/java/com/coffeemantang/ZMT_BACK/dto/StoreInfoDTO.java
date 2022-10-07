package com.coffeemantang.ZMT_BACK.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// StoreController를 위한 DTO. StoreInfo를 붙여서 보내야 할 경우 두가지를 합친 DTO를 별도로 만듬
public class StoreInfoDTO {
    private int storeinfoId;
    private String storeId;
    private String notice; // 가게공지
    private String tel; // 가게 전화번호
    
    // 오픈시간 
    private LocalTime openTime1;
    private LocalTime openTime2;
    private LocalTime openTime3;
    private LocalTime openTime4;
    private LocalTime openTime5;
    private LocalTime openTime6;
    private LocalTime openTime7;
    
    // 폐점시간
    private LocalTime closeTime1;
    private LocalTime closeTime2;
    private LocalTime closeTime3;
    private LocalTime closeTime4;
    private LocalTime closeTime5;
    private LocalTime closeTime6;
    private LocalTime closeTime7;
    
    // 휴식시간 시작
    private LocalTime breakTimeStart1;
    private LocalTime breakTimeStart2;
    private LocalTime breakTimeStart3;
    private LocalTime breakTimeStart4;
    private LocalTime breakTimeStart5;
    private LocalTime breakTimeStart6;
    private LocalTime breakTimeStart7;

    // 휴식시간 끝
    private LocalTime breakTimeEnd1;
    private LocalTime breakTimeEnd2;
    private LocalTime breakTimeEnd3;
    private LocalTime breakTimeEnd4;
    private LocalTime breakTimeEnd5;
    private LocalTime breakTimeEnd6;
    private LocalTime breakTimeEnd7;
}
