package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 트랜잭션 - DataSource, transactionManager 자동 등록
 */
@Slf4j
@SpringBootTest //테스트를 돌릴 때, 스프링부트테스트라는 것을 인식 -> 필요한 빈들을 등록
class MemberServiceV3_4Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepositoryV3 memberRepository;
    @Autowired
    private MemberServiceV3_3 memberService;

    @TestConfiguration
    static class TestConfig {


        //스프링 부트가 제공하는 자동 등록을 이용해서 데이터소스와 트랜잭션 매니저를 편리하게 적용해보자.먼저 application.properties 추가하자


        //application.properties에 지정된 속성을 참고해서 데이터소스와 트랜잭션 매니저를 자동으로 생성

        private final DataSource dataSource;

        public TestConfig(DataSource dataSource){
            this.dataSource = dataSource;
        }
        //DataSource를 생성자 주입으로 받아옵니다. 이 DataSource는 스프링 부트의 자동 설정에 의해 이미 애플리케이션 컨텍스트에 등록되어 있는 빈입니다.
        // 즉, 수동으로 DataSource를 등록할 필요 없이, 스프링 부트가 자동으로 설정해주는 DataSource를 활용합니다.
        //TestConfig은 스프링 빈으로 자동 등록됩니다. 스프링 빈의 경우 생성자가 하나만 있으면 @Autowired가 생략됩니다. 따라서 의존관계가 자동으로 주입

        // 트랜잭션 매니저를 명시적으로 정의하지 않습니다. 이는 스프링 부트가 자동으로 DataSourceTransactionManager를 생성하고 빈으로 등록하는 것을 이용
        //스프링 부트는 애플리케이션 컨텍스트에 DataSource 빈이 존재하는 경우, 기본적으로 DataSourceTransactionManager를 자동으로 등록
        @Bean
        MemberRepositoryV3 memberRepositoryV3() {
            return new MemberRepositoryV3(dataSource);
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3() {
            return new MemberServiceV3_3(memberRepositoryV3());
        }

    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    void AopCheck(){
        log.info("memberService class={}",memberService.getClass());
        log.info("memberRepository class={}",memberRepository.getClass());
        Assertions.assertThat(AopUtils.isAopProxy(memberService)).isTrue();
        Assertions.assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외 발생")
    void accountTransferEx() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        //when
        assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

//        assertThatThrownBy(new ThrowingCallable() {
//            @Override
//            public void call() throws Throwable {
//                memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000);
//            }
//        }).isInstanceOf(IllegalStateException.class);


        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberEx.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(10000); //롤백에 의해 만원이 됨
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }
}