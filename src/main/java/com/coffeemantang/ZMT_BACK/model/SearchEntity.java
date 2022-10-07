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
@Table(name = "search")
public class SearchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "search_id")
    private int searchId;
    @Column(name = "search")
    private int search; // 검색한 키워드
    @Column(name = "member_id")
    @JoinColumn(name = "member_id")
    private int memberId; // 검색한 사람
    @Column(name = "time")
    private LocalDateTime time; // 검색일시
}