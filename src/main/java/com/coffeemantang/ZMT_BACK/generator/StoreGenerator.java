package com.coffeemantang.ZMT_BACK.generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class StoreGenerator implements IdentifierGenerator {
    @Override
    public String generate(SharedSessionContractImplementor session, Object object) throws HibernateException {

        String id = generatePrimaryKey();
        Connection conn = null;

        String QUERY = "select store_id from store where store_id = ? ";

        try {
            conn = session.getJdbcConnectionAccess().obtainConnection();
            PreparedStatement pstmt = conn.prepareStatement(QUERY);

            pstmt.setObject(1, id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){ // db 체크 후 겹치는 아이디 있으면 다시돌리기
                return generate(session, object);
            }
        } catch (SQLException e) {
            log.error("Store id 생성 오류");
            throw new RuntimeException(e);
        }

        return id;
    }

    private String generatePrimaryKey() {
        String shortId = RandomStringUtils.random(12, "0123456789abcdefghijklmnABCDEFGHIJKLMN");  //12자리 아이디 생성
        return shortId;
    }
}
