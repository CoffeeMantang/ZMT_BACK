package com.coffeemantang.ZMT_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "memberrocation")
public class MemberRocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "memberrocation_id")
    private int memberrocationId;
    @Column(name = "member_id")
    @JoinColumn(name = "member_id")
    private int memberId;
    @Column(name = "nickname")
    private String nickname; // 주소지의 별명
    @Column(name = "address1")
    private String address1; // 기본주소
    @Column(name = "address2")
    private String address2; // 상세주소
    @Column(name = "address_x")
    private double addressX; // 위도
    @Column(name = "address_y")
    private double addressY; // 경도
    @Column(name = "state")
    private int state; // 1: 현재주소(기본주소)
}