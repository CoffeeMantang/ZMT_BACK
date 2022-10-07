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
@Table(name = "store")
public class StoreEntity {
    @Id
    @GenericGenerator(name = "store_id", strategy = "com.coffeemantang.ZMT_BACK.generator.StoreGenerator") // Store용 Generator 사용
    @GeneratedValue(generator = "store_id")
    @Column(name = "store_id")
    private String storeId;
    @Column(name = "member_id")
    @JoinColumn(name = "member_id")
    private int memberId;
    @Column(name = "name")
    private String name;
    @Column(name = "joinday")
    private LocalDateTime joinDay;
    @Column(name = "category")
    private int category;
    @Column(name = "thumb")
    private String thumb; // 썸네일 이미지 주소
    @Column(name = "address1")
    private String address1; // 주소1
    @Column(name = "address2")
    private String address2; // 세부주소
    @Column(name = "state")
    private int state; // 영업준비중, 영업중, 영업중단
    @Column(name = "address_x")
    private double addressX;
    @Column(name = "address_y")
    private double addressY;
    @Column(name = "hits")
    private int hits; // 가게조회수
}
