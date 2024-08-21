package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

/**
 * 기본 동작, 트랜잭션이 없어서 문제 발생
 */
class MemberServiceV1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV1 memberRepository;
    private MemberServiceV1 memberService;

    @BeforeEach //각각의 테스트가 수행되기 전에 실행
    void before(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD);
        memberRepository = new MemberRepositoryV1(dataSource);
        memberService = new MemberServiceV1(memberRepository);
        //생성자 주입을 통해 의존관계 주입
    }

    @AfterEach //각각의 테스트가 실행되고 난 이후에 실행
    void after() throws SQLException{
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
        //테스트가 끝나면 delete하여 db정리, 삭제하지않으면 다음 테스트에서 데이터 중복으로 오류 발생
        //더 나은 방법으로는 트랜잭션을 활용, 테스트 전에 트랜잭션을 시작하고, 테스트 이후에 트랜잭션을 롤백해버리면 데이터가 처음 상태로 돌아옴
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);//Member_A,10000
        Member memberB = new Member(MEMBER_B, 10000);//Member_B,10000
        memberRepository.save(memberA);//A 생성
        memberRepository.save(memberB);//B 생성

        //when
        memberService.accountTransfer(memberA.getMemberId(),memberB.getMemberId(),2000);
        //A -2000, B+2000

        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId()); //A조회
        Member findMemberB = memberRepository.findById(memberB.getMemberId()); //B조회
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외 발생")
    void accountTransferEx() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);//Member_A,10000
        Member memberEx = new Member(MEMBER_EX, 10000);//ex,10000
        memberRepository.save(memberA);//A 생성
        memberRepository.save(memberEx);//ex 생성

        //when
        assertThatThrownBy(()->memberService.accountTransfer(memberA.getMemberId(),memberEx.getMemberId(),2000))
                .isInstanceOf(IllegalStateException.class);
        //예외가 발생시 테스트(IllegalStateException)

        //이것은 람다식을 사용하지 않았을 때 코드, 이렇게 예외 발생에 대한 테스트를 할때는 람다식을 활용하자!
//        assertThatThrownBy(new ThrowingCallable() {
//            @Override
//            public void call() throws Throwable {
//                memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000);
//            }
//        }).isInstanceOf(IllegalStateException.class);


        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberEx.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);//A는 2000원 감소 -> 문제 발생
        assertThat(findMemberB.getMoney()).isEqualTo(10000);//B는 예외 발생에 의해 아무 변화 없음
    }
}