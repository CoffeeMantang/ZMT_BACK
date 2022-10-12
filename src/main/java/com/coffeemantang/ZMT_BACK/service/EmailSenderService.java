package com.coffeemantang.ZMT_BACK.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// 이메일을 전송하는 서비스
public class EmailSenderService {
    private final JavaMailSender javaMailSender;

    @Async //비동기
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }
}
