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
@Table(name = "bookmark")
public class BookmarkEntitiy {
    @Id
    @JoinColumn(name = "member_id")
    @Column(name = "member_id")
    private int memberId;
    @JoinColumn(name = "store_id")
    @Column(name = "store_id")
    private int storeId;
}