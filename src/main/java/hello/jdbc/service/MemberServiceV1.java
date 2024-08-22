package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
/**
 * 순수한 비즈니스 로직만 존재
 * SQLException이라는 JDBC 기술에 의존
 */
@RequiredArgsConstructor // final이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;
    //의존성 주입(DI)

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
       //fromId의 회원을 조회해서 toId의 회원에게 money만큼의 돈을 계좌이체하는 로직
        Member fromMember = memberRepository.findById(fromId); //fromId값을 통해 멤버 찾음
        Member toMember = memberRepository.findById(toId);//toId값을 통해 멤버 찾음

        memberRepository.update(fromId, fromMember.getMoney() - money);
        //fromId 멤버의 돈을 업데이트
        validation(toMember); //예외사항 검사
        memberRepository.update(toId, toMember.getMoney()+money);
        //toId 멤버의 돈을 업데이트
    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){ //멤버의 이름이 ex면 문제발생
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
