package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.StoreInfoEntity;
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

    public StoreInfoDTO(final StoreInfoEntity storeInfoEntity) {

        this.storeinfoId = storeInfoEntity.getStoreinfoId();
        this.storeId = storeInfoEntity.getStoreId();
        this.notice = storeInfoEntity.getNotice();
        this.tel = storeInfoEntity.getTel();
        this.openTime1 = storeInfoEntity.getOpenTime1();
        this.openTime2 = storeInfoEntity.getOpenTime2();
        this.openTime3 = storeInfoEntity.getOpenTime3();
        this.openTime4 = storeInfoEntity.getOpenTime4();
        this.openTime5 = storeInfoEntity.getOpenTime5();
        this.openTime6 = storeInfoEntity.getOpenTime6();
        this.openTime7 = storeInfoEntity.getOpenTime7();
        this.closeTime1 = storeInfoEntity.getCloseTime1();
        this.closeTime2 = storeInfoEntity.getCloseTime2();
        this.closeTime3 = storeInfoEntity.getCloseTime3();
        this.closeTime4 = storeInfoEntity.getCloseTime4();
        this.closeTime5 = storeInfoEntity.getCloseTime5();
        this.closeTime6 = storeInfoEntity.getCloseTime6();
        this.closeTime7 = storeInfoEntity.getCloseTime7();
        this.breakTimeStart1 = storeInfoEntity.getBreakTimeStart1();
        this.breakTimeStart2 = storeInfoEntity.getBreakTimeStart2();
        this.breakTimeStart3 = storeInfoEntity.getBreakTimeStart3();
        this.breakTimeStart4 = storeInfoEntity.getBreakTimeStart4();
        this.breakTimeStart5 = storeInfoEntity.getBreakTimeStart5();
        this.breakTimeStart6 = storeInfoEntity.getBreakTimeStart6();
        this.breakTimeStart7 = storeInfoEntity.getBreakTimeStart7();
        this.breakTimeEnd1 = storeInfoEntity.getBreakTimeEnd1();
        this.breakTimeEnd2 = storeInfoEntity.getBreakTimeEnd2();
        this.breakTimeEnd3 = storeInfoEntity.getBreakTimeEnd3();
        this.breakTimeEnd4 = storeInfoEntity.getBreakTimeEnd4();
        this.breakTimeEnd5 = storeInfoEntity.getBreakTimeEnd5();
        this.breakTimeEnd6 = storeInfoEntity.getBreakTimeEnd6();
        this.breakTimeEnd7 = storeInfoEntity.getBreakTimeEnd7();

    }

    public static StoreInfoEntity toEntity(final StoreInfoDTO storeInfoDTO) {

        return StoreInfoEntity.builder()
                .storeinfoId(storeInfoDTO.getStoreinfoId())
                .storeId(storeInfoDTO.getStoreId())
                .notice(storeInfoDTO.getNotice())
                .tel(storeInfoDTO.getTel())
                .openTime1(storeInfoDTO.getOpenTime1())
                .openTime2(storeInfoDTO.getOpenTime2())
                .openTime3(storeInfoDTO.getOpenTime3())
                .openTime4(storeInfoDTO.getOpenTime4())
                .openTime5(storeInfoDTO.getOpenTime5())
                .openTime6(storeInfoDTO.getOpenTime6())
                .openTime7(storeInfoDTO.getOpenTime7())
                .closeTime1(storeInfoDTO.getCloseTime1())
                .closeTime2(storeInfoDTO.getCloseTime2())
                .closeTime3(storeInfoDTO.getCloseTime3())
                .closeTime4(storeInfoDTO.getCloseTime4())
                .closeTime5(storeInfoDTO.getCloseTime5())
                .closeTime6(storeInfoDTO.getCloseTime6())
                .closeTime7(storeInfoDTO.getCloseTime7())
                .breakTimeStart1(storeInfoDTO.getBreakTimeStart1())
                .breakTimeStart2(storeInfoDTO.getBreakTimeStart2())
                .breakTimeStart3(storeInfoDTO.getBreakTimeStart3())
                .breakTimeStart4(storeInfoDTO.getBreakTimeStart4())
                .breakTimeStart5(storeInfoDTO.getBreakTimeStart5())
                .breakTimeStart6(storeInfoDTO.getBreakTimeStart6())
                .breakTimeStart7(storeInfoDTO.getBreakTimeStart7())
                .breakTimeEnd1(storeInfoDTO.getBreakTimeEnd1())
                .breakTimeEnd2(storeInfoDTO.getBreakTimeEnd2())
                .breakTimeEnd3(storeInfoDTO.getBreakTimeEnd3())

                .breakTimeEnd4(storeInfoDTO.getBreakTimeEnd4())
                .breakTimeEnd5(storeInfoDTO.getBreakTimeEnd5())
                .breakTimeEnd6(storeInfoDTO.getBreakTimeEnd6())
                .breakTimeEnd7(storeInfoDTO.getBreakTimeEnd7())
                .build();

    }

}
