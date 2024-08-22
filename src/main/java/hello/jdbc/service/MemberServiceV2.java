package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 -파라미터 연동, 풀을 고려한 종료
 * 트랜잭션은 비즈니스 로직이 있는 서비스 계층에서 시작
 * 결국 서비스 계층에서 커넥션을 만들고, 트랜잭션 커밋 이후에 커넥션을 종료
 * 즉, 트랜잭션을 사용하는 동안 같은 커넥션을 유지(다른 커넥션을 맺으면 다른 세션이 맺어지므로)
 * 이러한 문제를 해결하기위해, 단순한 방법으로 커넥션을 파라미터로 전달해서 같은 커넥션이 사용되도록 유지
 *
 * 문제점
 * 1. JDBC 구현 기술이 서비스 계층에 누수되는 문제
 * 2. 트랜잭션 동기화 문제
 * 3. 트랜잭션 적용 반복 문제
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;
    //의존성 주입

    public void accountTransfer(String fromId, String toId, int money) throws SQLException { //SQLExceipin같은 JDBC 기술에 의존
        Connection con = dataSource.getConnection();
        //1.커넥션 유지가 필요한 각 메서드는 파라미터로 넘어온 커넥션을 사용해야한다.
        //2. 커넥션 유지가 필요한 두 메서드는 레포지토리에서 커넥션을 닫으면 안된다.
        try{
            con.setAutoCommit(false); //트랜잭션 시작, 자동commit을 off

            //비즈니스 로직 수행
            bizLogic(fromId, toId, money, con);

            con.commit(); // 성공시 커밋

        }catch (Exception e){
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        }finally{
            release(con);
        }


    }

    private void bizLogic(String fromId, String toId, int money, Connection con) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney()+ money);
    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private static void release(Connection con) {
        if(con !=null){
            try {
                con.setAutoCommit(true); //커넥션 풀 고려
                con.close();
            }catch(Exception e){
                log.info("error",e);
            }
        }
    }
}
