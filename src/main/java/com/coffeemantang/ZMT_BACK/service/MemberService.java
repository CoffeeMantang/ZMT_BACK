package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.MemberRocationDTO;
import com.coffeemantang.ZMT_BACK.dto.SearchDTO;
import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import com.coffeemantang.ZMT_BACK.model.MemberRocationEntity;
import com.coffeemantang.ZMT_BACK.model.SearchEntity;
import com.coffeemantang.ZMT_BACK.persistence.MemberRepository;
import com.coffeemantang.ZMT_BACK.persistence.MemberRocationRepository;
import com.coffeemantang.ZMT_BACK.persistence.SearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
// Member 데이터베이스에 저장된 내용을 가져올 때 사용
// MemberRepository를 이용해 사용자를 생성하고 로그인 시 인증에 사용할 메서드 작성
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberRocationRepository memberRocationRepository;
    @Autowired
    private SearchRepository searchRepository;

    // 새 계정 생성 - 이메일 중복 검사
    public MemberEntity create(final MemberEntity memberEntity){
        if(memberEntity == null || memberEntity.getEmail() == null){
            log.warn("MemberService.create() : memberEntity에 email이 없어요");
            throw new RuntimeException("MemberService.create() : memberEntity에 email이 없어요");
        }
        final String email = memberEntity.getEmail();
        if(memberRepository.existsByEmail(email)){
            log.warn("MemberService.create() : 해당 email이 이미 존재해요");
            throw new RuntimeException("MemberService.create() : 해당 email이 이미 존재해요");
        }
        // 닉네임 중복 체크
        boolean check = checkNickname(memberEntity.getNickname());
        if(!check){
            log.warn("duplicated nickname");
            throw new RuntimeException("duplicated nickname");
        }

        return memberRepository.save(memberEntity);
    }

    // 로그인 - 자격증명
    public MemberEntity getByCredentials(final String email, final String password, final PasswordEncoder encoder){
        final MemberEntity originalMember = memberRepository.findByEmail(email); // 이메일로 MemberEntity를 찾음
        // 패스워드가 같은지 확인
        if(originalMember != null && encoder.matches(password, originalMember.getPassword())){
            return originalMember;
        }
        return null;
    }

    // 아이디로 멤버정보 가져오기
    public MemberEntity getByMemberId(final int memberId){
        if(memberId <= 0){
            log.warn("MemberService.getByMemberId() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.getByMemberId() : memberId 값이 이상해요");
        }
        final MemberEntity memberEntity = memberRepository.findByMemberId(memberId); // 아이디로 MemberEntity 찾음
        return memberEntity;
    }

    // 멤버의 비밀번호 랜덤하게 변경하고 entity 리턴
    @Transactional
    public String changeMemberPw(final int memberId, PasswordEncoder passwordEncoder){
        if(memberId <= 0){
            log.warn("MemberService.changeMemberPw() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.changeMemberPw() : memberId 값이 이상해요");
        }
        final MemberEntity memberEntity = memberRepository.findByMemberId(memberId); // 아이디로 MemberEntity 찾음
        // 12자리 랜덤 비밀번호 생성
        final String pw = RandomStringUtils.random(12, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        memberEntity.changePassword(passwordEncoder.encode(pw)); // 변경
        memberRepository.save(memberEntity);
        return pw;
    }

    // 아이디로 멤버의 비밀번호 찾기 질문 가져오기
    public String getQuestion(final int memberId){
        if(memberId <= 0){
            log.warn("MemberService.getQuestion() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.getQuestion() : memberId 값이 이상해요");
        }
        final String question = memberRepository.findQuestionByMemberId(memberId); // 아이디로 Question 찾기
        return question;
    }

    // 들어온 본인확인답변과 아이디가 일치하는지 체크 후 비밀번호 변경하고 string 리턴
    @Transactional
    public String checkAnswer(final int memberId, final String answer, PasswordEncoder passwordEncoder){
        if(memberId <= 0 || answer == null){
            log.warn("MemberService.checkAnswer() : 들어온 값이 이상해요");
            throw new RuntimeException("MemberService.checkAnswer() : 들어온 값이 이상해요");
        }
        final int findCount = memberRepository.findByAnswer(memberId, answer); //
        if(findCount < 1){
            // 답변이 일치하지 않으면
            log.warn("MemberService.checkAnswer() : 답변이 달라요");
            throw new RuntimeException("MemberService.checkAnswer() : 답변이 달라요");
        }
        // 답변이 일치하면 비밀번호 랜덤하게 변경 후 변경된 비밀번호 리턴
        final String pw = changeMemberPw(memberId, passwordEncoder);
        return pw;
    }

    // 현재 비밀번호와 변경할 비밀번호 받아서 비밀번호 변경
    @Transactional
    public boolean changePw(final int memberId, final String curPw, final String chgPw, PasswordEncoder passwordEncoder){
        if(curPw == null || curPw.equals("") || chgPw == null | chgPw.equals("")){
            log.warn("MemberService.changePw() : 들어온 값이 이상해요");
            throw new RuntimeException("MemberService.changePw() : 들어온 값이 이상해요");
        }
        if(memberId < 1){
            log.warn("MemberService.changePw() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.changePw() : memberId 값이 이상해요");
        }
        // 현재 비밀번호가 맞는지 검사
        String originPassword = memberRepository.findPasswordByMemberId(memberId); //DB에 들어가있는 PW
        if(!passwordEncoder.matches(curPw, originPassword)){
            //비밀번호가 다르면
            log.warn("MemberService.changePw() : 비밀번호가 달라요");
            throw new RuntimeException("MemberService.changePw() : 비밀번호가 달라요");
        }
        //비밀번호가 맞으면 비밀번호 변경
        final MemberEntity memberEntity = memberRepository.findByMemberId(memberId);
        memberEntity.changePassword(passwordEncoder.encode(chgPw));
        memberRepository.save(memberEntity);
        return true;
    }

    // 회원정보 가져오기
    public MemberEntity getMemberEntity(final int memberId){
        if(memberId < 1){
            log.warn("MemberService.changePw() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.changePw() : memberId 값이 이상해요");
        }
        final MemberEntity memberEntity = memberRepository.findByMemberId(memberId);
        return memberEntity;
    }

    // 닉네임 수정하기 - 닉네임 리턴
    @Transactional
    public String updateNickname(final int memberId, final String chgNickname){
        if(memberId < 1){
            log.warn("MemberService.updateNickname() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.updateNickname() : memberId 값이 이상해요");
        }
        if(chgNickname == null || chgNickname.equals("")){
            log.warn("MemberService.updateNickname() : chgNickname 값이 이상해요");
            throw new RuntimeException("MemberService.updateNickname() : chgNickname 값이 이상해요");
        }
        // 닉네임 중복 체크
        boolean check = checkNickname(chgNickname);
        if(!check){
            log.warn("duplicated nickname");
            throw new RuntimeException("duplicated nickname");
        }

        final MemberEntity memberEntity = memberRepository.findByMemberId(memberId);
        memberEntity.setNickname(chgNickname);
        memberRepository.save(memberEntity); // 값 변경
        final String nickname = memberRepository.findNicknameByMemberId(memberId); //아이디로 현재 닉네임 가져옴

        return nickname;
    }

    // 전화번호 수정하기 - 같은 전화번호 있으면 실패, 자릿수 체크(10 - 11자리)
    @Transactional
    public String updateTel(final int memberId, final String chgTel){
        if(memberId < 1){
            log.warn("MemberService.updateTel() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.updateTel() : memberId 값이 이상해요");
        }
        if(chgTel == null || chgTel.equals("")){
            log.warn("MemberService.updateTel() : chgTel 값이 이상해요");
            throw new RuntimeException("MemberService.updateTel() : chgTel 값이 이상해요");
        }
        int count = memberRepository.findByTel(chgTel); // 바꾸려는 전화번호가 이미 있는지 확인
        if(count > 0){
            // 이미 같은 전화번호가 있으면
            log.warn("MemberService.updateTel() : 이미 같은 전화번호가 있어요");
            throw new RuntimeException("MemberService.updateTel() : 이미 같은 전화번호가 있어요");
        }
        // 같은 전화번호가 없으면 전화번호 수정
        final MemberEntity memberEntity = memberRepository.findByMemberId(memberId);
        memberEntity.setTel(chgTel);
        memberRepository.save(memberEntity); // 수정
        final String tel = memberRepository.findTelByMemberId(memberId); // 현재 저장되어 있는 값 가져오기
        return tel;
    }

    // 닉네임 중복 체크
    public boolean checkNickname(final String nickname){
        if(nickname == null || nickname.equals("")){
            log.warn("MemberService.checkNickname() : nickname 값이 이상해요");
            throw new RuntimeException("MemberService.checkNickname() : nickname 값이 이상해요");
        }
        int count = memberRepository.findByNickname(nickname);
        if(count > 0){
            return false;
        }
        return true;
    }

    // 이름 변경
    @Transactional
    public String updateName(final int memberId, final String chgName){
        if(memberId < 1){
            log.warn("MemberService.updateName() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.updateName() : memberId 값이 이상해요");
        }
        if(chgName == null || chgName.equals("")){
            log.warn("MemberService.updatename() : name 값이 이상해요");
            throw new RuntimeException("MemberService.updatename() : name 값이 이상해요");
        }
        final MemberEntity memberEntity = memberRepository.findByMemberId(memberId);
        memberEntity.setName(chgName);
        memberRepository.save(memberEntity);
        String name = memberRepository.findNameByMemberId(memberId);
        return name;
    }

    // 본인확인 질문답변 변경
    @Transactional
    public MemberEntity updateQA(final int memberId, final String chgQuestion, final String chgAnswer){
        if(memberId < 1){
            log.warn("MemberService.updateQA() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.updateQA() : memberId 값이 이상해요");
        }
        if(chgQuestion == null || chgQuestion.equals("")){
            log.warn("MemberService.updateQA() : chgQuestion 값이 이상해요");
            throw new RuntimeException("MemberService.updateQA() : chgQuestion 값이 이상해요");
        }
        if(chgAnswer == null || chgAnswer.equals("")){
            log.warn("MemberService.updateQA() : chgAnswer 값이 이상해요");
            throw new RuntimeException("MemberService.updateQA() : chgAnswer 값이 이상해요");
        }
        final MemberEntity memberEntity = memberRepository.findByMemberId(memberId);
        memberEntity.setQuestion(chgQuestion);
        memberEntity.setAnswer(chgAnswer);
        memberRepository.save(memberEntity);
        final MemberEntity chgMemberEntity = memberRepository.findQuestionAnswerByMemberId(memberId);
        return chgMemberEntity;
    }

    // 같은 이메일이 있는지 확인
    public Boolean checkEmail(final String email){
        if(email == null || email.equals("")){
            log.warn("MemberService.checkEmail() : email 값이 이상해요");
            throw new RuntimeException("MemberService.checkEmail() : email 값이 이상해요");
        }
        if(memberRepository.existsByEmail(email)){ //이메일이 이미 있으면 false리턴
            return false;
        }
        return true;
    }

    // 대표주소(Address1)만 가져오기
    public String getMainAddress(final int memberId){
        if(memberId < 1){
            log.warn("MemberService.getMainAddress() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.getMainAddress() : memberId 값이 이상해요");
        }
        return memberRocationRepository.findAddress1ByMemberIdAndState(memberId, 1).getAddress1();
    }

    // 모든 주소 가져오기
    public List<MemberRocationDTO> getAllAddress(final int memberId){
        try{
            if(memberId < 1){
                log.warn("MemberService.getAllAddress() : memberId 값이 이상해요");
                throw new RuntimeException("MemberService.getAllAddress() : memberId 값이 이상해요");
            }
            List<MemberRocationDTO> listMrDTO = new ArrayList<>(); // 리턴할 리스트
            // 1. 모든 주소를 가져온다.
            List<MemberRocationEntity> mrList = memberRocationRepository.findAllByMemberId(memberId);
            // 2. 대표주소를 가져와 리턴할 리스트에 넣는다.
            for(MemberRocationEntity entity : mrList){
                if(entity.getState() == 1){
                    MemberRocationDTO dto = MemberRocationDTO.builder().address1(entity.getAddress1())
                            .address2(entity.getAddress2())
                            .nickname(entity.getNickname())
                            .memberrocationId(entity.getMemberrocationId())
                            .state(1).build();
                    listMrDTO.add(dto);
                }
            }
            // 3. 나머지를 리스트에 넣는다.
            for(MemberRocationEntity entity : mrList){
                if(entity.getState() != 1){
                    MemberRocationDTO dto = MemberRocationDTO.builder().address1(entity.getAddress1())
                            .address2(entity.getAddress2())
                            .nickname(entity.getNickname())
                            .memberrocationId(entity.getMemberrocationId())
                            .state(1).build();
                    listMrDTO.add(dto);
                }
            }
            // 4. 리턴
            return listMrDTO;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    // 주소추가하기
    public void newAddress(final int memberId, final MemberRocationDTO mrDTO) throws Exception{
        try{
            // 1. 들어온 rocation으로 Entity 만들기
            MemberRocationEntity mrEntity = MemberRocationEntity.builder()
                    .address1(mrDTO.getAddress1())
                    .address2(mrDTO.getAddress2())
                    .addressX(mrDTO.getAddressX())
                    .addressY(mrDTO.getAddressY())
                    .memberId(memberId)
                    .state(0).build();
            // 2. 닉네임이 넘어왔는지 체크하고 안넘어왔으면 address1을 닉네임으로
            if(mrDTO.getNickname() == "" || mrDTO.getNickname() == null){
                mrEntity.setNickname(mrDTO.getAddress1());
            }else{
                mrEntity.setNickname(mrDTO.getNickname());
            }
            // 3. 저장
            memberRocationRepository.save(mrEntity);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    // 최근검색어 가져오기(top10)
    public List<SearchDTO> getSearchList(int memberId) throws Exception{
        try{
            List<SearchEntity> entities = searchRepository.findTop10ByMemberIdOrderByTimeDesc(memberId);
            List<SearchDTO> resultList = new ArrayList<>();
            for(SearchEntity entity : entities){
                SearchDTO dto = SearchDTO.builder().searchId(entity.getSearchId())
                        .search(entity.getSearch())
                        .time(entity.getTime())
                        .build();
                resultList.add(dto);
            }
            return resultList;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    // 최근검색어 추가하기
    public void addSearch(int memberId, String search) throws Exception{
        try{
            Optional<SearchEntity> oEntity = searchRepository.findTop1ByMemberIdOrderByTimeDesc(memberId);


            if(oEntity.isPresent()){
                // 있으면 키워드 비교해서 다른 키워드인 경우에만 추가
                if(!oEntity.get().getSearch().equals(search)){
                    SearchEntity searchEntity = SearchEntity.builder().search(search).memberId(memberId).time(LocalDateTime.now()).build();
                    searchRepository.save(searchEntity);
                }
            }else{ // 넘어온 엔티티가 없으면 그냥 추가
                SearchEntity searchEntity = SearchEntity.builder().search(search).memberId(memberId).time(LocalDateTime.now()).build();
                searchRepository.save(searchEntity);
            }

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


}