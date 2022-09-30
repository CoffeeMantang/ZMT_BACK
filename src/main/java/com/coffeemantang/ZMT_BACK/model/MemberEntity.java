package com.coffeemantang.ZMT_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity(name="member")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    private String memberId; // 사용자에게 고유하게 부여하는 id

    private String email;
    private String password;
    private String nickname;
    private String tel;
    private LocalDate birthDay;
    private LocalDateTime joinDay;
    private int gender;
    private int type;
    private String question;
    private String answer;

}
