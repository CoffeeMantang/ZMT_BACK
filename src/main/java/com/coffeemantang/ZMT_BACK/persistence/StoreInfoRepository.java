package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.StoreInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreInfoRepository extends JpaRepository<StoreInfoEntity, Integer> {

    // storeinfoId로 info 컬럼값 가져오기
    @Query(value = "select * from storeinfo where storeinfo_id = :storeinfoId", nativeQuery = true)
    StoreInfoEntity findNoticeAndTelByStoreinfoId(@Param("storeinfoId") int storeinfoId);


    StoreInfoEntity findByStoreinfoId(int storeinfoId);

    StoreInfoEntity findByStoreId(String storeId);
}
