package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

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
    }
}