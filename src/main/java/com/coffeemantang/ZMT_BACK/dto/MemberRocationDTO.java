package com.coffeemantang.ZMT_BACK.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRocationDTO {
    private int memberrocationId;
    private int memberId;
    private String nickname;
    private String address1;
    private String address2;
    private double addressX;
    private double addressY;
    private int state;
}
