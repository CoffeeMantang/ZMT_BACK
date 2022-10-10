package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
// Member의 Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {
    // 이메일로 찾기
    MemberEntity findByEmail(String email);
    // 해당하는 이메일이 있는지 확인
    Boolean existsByEmail (String email);
    // 이메일과 비밀번호로 찾기
    MemberEntity findByEmailAndPassword(String email, String password);
    // 아이디로 찾기
    MemberEntity findByMemberId(int memberId);
    // 아이디로 비밀번호 질문 가져오기
    @Query(value = "SELECT question FROM member WHERE member_id = :memberId", nativeQuery = true)
    String findQuestionByMemberId(@Param("memberId") int memberId);
    // 아이디와 답변으로 매칭되는 컬럼 찾기
    @Query(value = "SELECT COUNT(member_id) FROM member WHERE member_id = :memberId AND answer = :answer", nativeQuery = true)
    int findByAnswer(@Param("memberId") int memberId, @Param("answer") String answer);
    // 아이디로 비밀번호 가져오기
    @Query(value = "SELECT password FROM member WHERE member_id = :memberId", nativeQuery = true)
    String findPasswordByMemberId(@Param("memberId") int memberId);
    // 아이디로 닉네임 가져오기
    @Query(value = "SELECT nickname FROM member WHERE member_id = :memberId", nativeQuery = true)
    String findNicknameByMemberId(@Param("memberId") int memberId);
    // 같은 전화번호가 있는지 확인
    @Query(value = "SELECT COUNT(member_id) FROM member WHERE tel = :tel", nativeQuery = true)
    int findByTel(@Param("tel") String tel);
    // 아이디로 전화번호 가져오기
    @Query(value = "SELECT tel FROM member WHERE member_id = :memberId", nativeQuery = true)
    String findTelByMemberId(@Param("memberId") int memberId);
    // 같은 닉네임이 있는지 확인
    @Query(value = "SELECT COUNT(member_id) FROM member WHERE nickname = :nickname", nativeQuery = true)
    int findByNickname(@Param("nickname") String nickname);
    // 아이디로 이름 가져오기
    @Query(value = "SELECT name FROM member WHERE member_id = :memberId", nativeQuery = true)
    String findNameByMemberId(@Param("memberId") int memberId);
    // 비밀번호 답변 가져오기
    @Query(value = "SELECT answer FROM member WHERE member_id = :memberId", nativeQuery = true)
    String findAnswerByMemberId(@Param("memberId") int memberId);
    // 비밀번호 질문답변 가져오기
    @Query(value = "SELECT question, answer FROM member WHERE member_id = :memberId", nativeQuery = true)
    MemberEntity findQuestionAnswerByMemberId(@Param("memberId") int memberId);
}
