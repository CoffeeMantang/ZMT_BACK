package com.coffeemantang.ZMT_BACK.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// MemberController를 위한 DTO
public class MemberDTO {
    private int memberId;
    private String token;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String tel;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate birthDay;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDate joinDay;
    private int gender;
    private int type;
    private String question;
    private String answer;
}
