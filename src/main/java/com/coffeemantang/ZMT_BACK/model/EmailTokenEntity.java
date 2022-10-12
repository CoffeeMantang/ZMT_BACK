package com.coffeemantang.ZMT_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emailtoken")
public class EmailTokenEntity {
    private static final long EMAIL_TOKEN_EXPIRATION_TIME_VALUE = 5L; // 이메일 토큰 만료 시간

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "emailtoken_id")
    private String emailtokenId;
    @Column(name = "expirationdate")
    private LocalDateTime expirationdate; // 만료시간
    @Column(name = "expired")
    private boolean expired; // 만료여부
    @Column(name = "member_id")
    @JoinColumn(name = "member_id")
    private int memberId;

    // 이메일 인증 토큰 생성
    public static EmailTokenEntity createEmailToken(int memberId) {
        EmailTokenEntity emailTokenEntity = new EmailTokenEntity();
        emailTokenEntity.expirationdate = LocalDateTime.now().plusMinutes(EMAIL_TOKEN_EXPIRATION_TIME_VALUE); // 5분 후 만료
        emailTokenEntity.expired = false;
        emailTokenEntity.memberId = memberId;

        return emailTokenEntity;
    }

    // 토큰 만료
    public void setTokenToUsed() {
        this.expired = true;
    }

}
