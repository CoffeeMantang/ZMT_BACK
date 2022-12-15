package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.ChargeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargeRepository extends JpaRepository<ChargeEntity, String> {
    public ChargeEntity findByStoreIdAndDongContaining(String storeId, String dong);

    public List<ChargeEntity> findByDongContainingAndStoreId(String dong, String storeId);

    public List<ChargeEntity> findByStoreId(String storeId);

    // 가게에 같은 동 정보가 있는지 체크
    public long countByStoreIdAndDongContaining(String storeId, String dong);


}
