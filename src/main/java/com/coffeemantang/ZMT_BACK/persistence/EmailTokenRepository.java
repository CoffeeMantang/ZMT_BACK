package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.EmailTokenEntity;
import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailTokenRepository extends JpaRepository<EmailTokenEntity, String> {
    // 만료되지 않았으며 현재보다 이후에 만료되는 토큰정보를 가져옴
    Optional<EmailTokenEntity> findByEmailtokenIdAndExpirationdateAfterAndExpired(String emailTokenId, LocalDateTime now, boolean expired);
}
