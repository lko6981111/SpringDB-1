package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach(){
//        //기본 DriverManager - 항상 새로운 커넥션을 획득
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD);


        //커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        //HikariDataSource로 받아야지 관련 메서드를 쓸 수 있다, Datasource는 인터페이스로 메서드 못 씀
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPoolName(PASSWORD);

        //의존성 주입을 통해 repositoyr 생성
        repository = new MemberRepositoryV1(dataSource);

    }

    @Test
    void crud() throws SQLException {
        //save
        Member member = new Member("memberV100",10000);
        repository.save(member);
        //이 테스트는 2번 실행하면 PK 중복 오류가 발생

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}",findMember);
        assertThat(findMember).isEqualTo(member);
        //findMember과 member는 다른 객체이지만, 롬복의 equals를 통해 비교하기 때문에 값이 같게 나온다

        //update : money : 10000-> 20000
        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(()->repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
        //회원을 삭제한 다음 findById()를 통해서 조회, 그래서 회원이 없기 때문에 NoSuchElementException이 발생,
        // assertThatThrownBy는 해당 예외가 발생해야 검증에 성공

        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}