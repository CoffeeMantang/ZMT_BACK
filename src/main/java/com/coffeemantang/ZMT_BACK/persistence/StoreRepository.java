package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// Store의 Repository
public interface StoreRepository extends JpaRepository<StoreEntity, String> {
    // 가게 아이디로 찾기
    StoreEntity findByStoreId(String storeId);
    // 회원 아이디로 찾기
//    List<StoreEntity> findByMemberId(int memberId);

    //회원 아이디로 StoreEntity 가져오기
    StoreEntity findByMemberId(int memberId);

    // 가게 아이디와 회원 아이디로 StoreEntity 가져오기
    StoreEntity findByStoreIdAndMemberId(String storeId, int memberId);

    // 메뉴아이디로 가게정보 가져오기
    @Query(value="SELECT * FROM store AS s INNER JOIN menu AS m ON s.store_id = m.store_id AND m.menu_id = :menuId limit 1", nativeQuery = true)
    StoreEntity findByMenuId(int menuId);

    // 가게 아이디로 회원 아이디 찾기
    @Query(value = "SELECT member_id FROM store WHERE store_id = :storeId", nativeQuery = true)
    int selectMemberIdByStoreId(@Param("storeId") String storeId);

    // 가게이름으로 가게찾기 - 주문순 정렬
    @Query(value = "SELECT s.store_id, s.name, s.thumb, s.address1, s.address2, s.address_x, s.address_y, s.category, s.hits, s.joinday, s.state, s.min, s.member_id, COUNT(orderlist_id) AS cnt FROM ( " +
            "SELECT store.store_id, store.name, store.thumb,store.address2, store.address1, store.address_x, store.address_y, store.category, store.hits, store.joinday, store.state, store.min, store.member_id FROM store INNER JOIN charge ON " +
            "store.store_id = charge.store_id AND charge.dong LIKE CONCAT('%',:address,'%') " +
            ") AS s LEFT JOIN orderlist AS r ON s.store_id = r.store_id WHERE s.name LIKE CONCAT('%', :keyword, '%') GROUP BY s.store_id ORDER BY cnt desc LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<StoreEntity> findByNameOrderByOrderCount(@Param("limit") int limit, @Param("offset") int offset, @Param("address") String address, @Param("keyword") String keyword);

    // 가게이름으로 가게찾기 - 리뷰평점 순 정렬
    @Query(value = "SELECT s.store_id, s.name, s.thumb,s.address2, s.address1, s.address_x, s.address_y, s.category, s.hits, s.joinday, s.state, s.min, s.member_id, AVG(score) AS cnt FROM ( " +
            "SELECT store.store_id, store.name, store.thumb,store.address2, store.address1, store.address_x, store.address_y, store.category, store.hits, store.joinday, store.state, store.min, store.member_id FROM store INNER JOIN charge ON " +
            "store.store_id = charge.store_id AND charge.dong LIKE CONCAT('%',:address,'%') " +
            ") AS s LEFT JOIN review AS r ON s.store_id = r.store_id GROUP BY s.store_id WHERE s.name LIKE CONCAT('%', :keyword, '%') ORDER BY cnt desc LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<StoreEntity> findByNameOrderByReviewScore(@Param("limit") int limit, @Param("offset") int offset, @Param("address") String address, @Param("keyword") String keyword);

    // 메뉴명으로 주문가능한 가게찾기 - 주문순 정렬
    @Query(value = "SELECT s.store_id, s.name, s.thumb,s.address2, s.address1, s.address_x, s.address_y, s.category, s.hits, s.joinday, s.state, s.min, s.member_id, COUNT(orderlist_id) AS cnt FROM ( " +
            "SELECT store.store_id, store.name, store.thumb,store.address2, store.address1, store.address_x, store.address_y, store.category, store.hits, store.joinday, store.state, store.min, store.member_id FROM store INNER JOIN charge ON " +
            "store.store_id = charge.store_id AND charge.dong LIKE CONCAT('%',:address,'%') " +
            ") AS s LEFT JOIN orderlist AS r ON s.store_id = r.store_id " +
            "INNER JOIN menu AS m ON m.store_id = s.store_id AND m.menu_name LIKE CONCAT('%',:keyword,'%') GROUP BY s.store_id ORDER BY cnt desc LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<StoreEntity> findByMenuNameOrderByOrderCount(@Param("limit") int limit, @Param("offset") int offset, @Param("address") String address, @Param("keyword") String keyword);

    // 메뉴명으로 주문가능한 가게찾기 - 리뷰점수순 정렬
    @Query(value = "SELECT s.store_id, s.name, s.thumb,s.address2, s.address1, s.address_x, s.address_y, s.category, s.hits, s.joinday, s.state, s.min, s.member_id, COUNT(score) AS cnt FROM ( " +
            "SELECT store.store_id, store.name, store.thumb,store.address2, store.address1, store.address_x, store.address_y, store.category, store.hits, store.joinday, store.state, store.min, store.member_id FROM store INNER JOIN charge ON " +
            "store.store_id = charge.store_id AND charge.dong LIKE CONCAT('%',:address,'%') " +
            ") AS s LEFT JOIN review AS r ON s.store_id = r.store_id " +
            "INNER JOIN menu AS m ON m.store_id = s.store_id AND m.menu_name LIKE CONCAT('%',:keyword,'%') GROUP BY s.store_id ORDER BY cnt desc LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<StoreEntity> findByMenuNameOrderByReviewScore(@Param("limit") int limit, @Param("offset") int offset, @Param("address") String address, @Param("keyword") String keyword);

    // 카테고리로 주문가능한 가게 찾기 - 주문순 정렬
    @Query(value = "SELECT s.store_id, s.name, s.thumb,s.address2, s.address1, s.address_x, s.address_y, s.category, s.hits, s.joinday, s.state, s.min, s.member_id, COUNT(orderlist_id) AS cnt FROM ( " +
            "SELECT store.store_id, store.name, store.thumb,store.address2, store.address1, store.address_x, store.address_y, store.category, store.hits, store.joinday, store.state, store.min, store.member_id FROM store INNER JOIN charge ON " +
            "store.store_id = charge.store_id AND charge.dong LIKE CONCAT('%',:address,'%') " +
            ") AS s LEFT JOIN orderlist AS r ON s.store_id = r.store_id " +
            "where s.category = :category GROUP BY s.store_id ORDER BY cnt desc LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<StoreEntity> findByCategoryOrderByOrderCount(@Param("limit") int limit, @Param("offset") int offset, @Param("address") String address, @Param("category") int category);
}

