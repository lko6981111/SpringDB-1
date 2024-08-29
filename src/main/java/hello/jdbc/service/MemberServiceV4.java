package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;



/**
 * 예외 누수 문제 해결
 * SQLException 제거
 *
 * MemberRepository 인터페이스 의존
 */
@Slf4j
public class MemberServiceV4 {

    private final MemberRepository memberRepository;
    //의존성 주입

    public MemberServiceV4(MemberRepository memberRepository) {

        this.memberRepository = memberRepository;

    }

    @Transactional
    //스프링부트를 사용하면 트랜잭션 AOP를 처리하기 위해 필요한 스프링 빈들도 자동으로 등록
    //개발자는 트랜잭션 처리가 필요한곳에 애노테이션만 붙여주면된다
    //애노테이션은 메서드에 붙여도 되고, 클래스에 붙여도 된다. 클래스에 붙이면 외부에서 호출 가능한 public 메서드가 AOP 적용 대상이 된다
    public void accountTransfer(String fromId, String toId, int money)  {

        bizLogic(fromId, toId, money); // 트랜잭션 관련 코드 제거, 순수 비즈니스 로직만 남음
    }

    private void bizLogic(String fromId, String toId, int money) {
        //메서드에서 throws SQLException 부분이 제거된 것을 확인할 수 있다.
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }


}
