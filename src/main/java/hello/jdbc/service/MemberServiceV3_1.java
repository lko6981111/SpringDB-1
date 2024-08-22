package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * 트랜잭션 -트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    private final PlatformTransactionManager transactionManager; //트랜잭션 매니저
    //트랜잭션 매니저를 주입 받는다. 지급은 JDBC기술을 사용하므로 DataSourceTransactionManager 구현제를 주입받음
    //만약 JPA같은 기술로 변경되면 JpaTransactionManager를 주입 받으면 됨
    private final MemberRepositoryV3 memberRepository;
    //의존성 주입

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        //트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());//커넥션이 획득
        //status를 반환 - 현재 트랜잭션의 상태 정보가 포함
        //new DefaultTransactionDefinition() - 트랜잭션과 관련된 옵션을 지정할 수 있음
        //트랜잭션 매니저는 내부에서 데이터 소스를 사용

        try {
            //비즈니스 로직 수행
            bizLogic(fromId, toId, money);
            transactionManager.commit(status);

        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }

    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
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
