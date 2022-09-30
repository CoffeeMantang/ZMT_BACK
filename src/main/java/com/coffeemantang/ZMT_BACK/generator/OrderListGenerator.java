package com.coffeemantang.ZMT_BACK.generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.sql.*;

// 주문번호 생성을 위한 클래스 - 8자리 랜덤 생성
@Slf4j
public class OrderListGenerator implements IdentifierGenerator{

    @Override
    public String generate(SharedSessionContractImplementor session, Object object) throws HibernateException {

        String id = generatePrimaryKey();
        Connection conn = null;

        String QUERY = "select orderlist_id from orderlist where orderlist_id = ? ";

        try {
            conn = session.getJdbcConnectionAccess().obtainConnection();
            PreparedStatement pstmt = conn.prepareStatement(QUERY);

            pstmt.setObject(1, id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){ // db 체크 후 겹치는 아이디 있으면 다시돌리기
                return generate(session, object);
            }
        } catch (SQLException e) {
            log.error("Orderlist id 생성 오류");
            throw new RuntimeException(e);
        }

        return id;
    }

    private String generatePrimaryKey() {
        String shortId = RandomStringUtils.random(8, "0123456789abcdefghijklmnABCDEFGHIJKLMN");  //8자리 아이디 생성
        return shortId;
    }
}
