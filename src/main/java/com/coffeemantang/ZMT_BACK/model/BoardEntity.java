package com.coffeemantang.ZMT_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "board")
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "board_id")
    private int boardId;

    @Column(name = "member_id")
    @JoinColumn(name = "member_id")
    private int memberId;

    @Column(name = "type")
    private int type; // 0:공지글, 1:신고글, 2:건의글, 3:카테고리추가글

    @Column(name = "title")
    private String title; // 제목

    @Column(name = "content")
    private String content; // 내용

    @Column(name = "date")
    private LocalDateTime date; // 작성일시

}