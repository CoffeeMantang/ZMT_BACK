package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.ChargeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargeRepository extends JpaRepository<ChargeEntity, String> {
    public ChargeEntity findByStoreIdAndDongContaining(String storeId, String dong);

    public List<ChargeEntity> findByDongContainingAndStoreId(String dong, String storeId);

}
