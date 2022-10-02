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
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "member_id")
    private String memberId; // 사용자에게 고유하게 부여하는 id
    @Column(name = "exp")
    private int exp; // 리뷰에서 쌓이는 경험치
    @Column(name = "email")
    private String email; // 로그인할때 입력하는 아이디
    @Column(name = "password")
    private String password; // 비밀번호
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "tel") // 전화번호
    private String tel;
    @Column(name = "birthday")
    private LocalDate birthDay; // 생일
    @Column(name = "joinday")
    private LocalDateTime joinDay; // 가입일
    @Column(name = "gender")
    private int gender; // 0:남, 1:여
    @Column(name = "type")
    private int type; // 0:관리자, 1:사업자, 2:일반회원, 3:광고주
    @Column(name = "question")
    private String question; // 본인확인 질문
    @Column(name = "answer")
    private String answer; // 본인확인 답변

}
