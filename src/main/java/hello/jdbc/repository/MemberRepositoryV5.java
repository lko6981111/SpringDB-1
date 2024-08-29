package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

/**
 * JdbcTemplate 사용
 * 템플릿 콜백 패던을 활용하여 이런 반복을 효과적으로 처리
 * 트랜잭션을 위한 커넥션 동기화는 물론, 예외 발생시 스프링 예외 변환기도 자동으로 실행
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository {

    private final JdbcTemplate template;

    public MemberRepositoryV5(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }
    //외부에서 DataSource를 주입 받아서 사용한다.

    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values(?,?)";
        template.update(sql, member.getMemberId(), member.getMoney());
        return member;
    }

    @Override
    public Member findById(String memberId) {
        //findById는 회원 하나를 조회하는 것이 목적
        String sql = "select * from member where member_id=?";
        return template.queryForObject(sql, memberRowMapper(), memberId);
    }


    //수정과 삭제는 등록과 비슷, 데이터를 변경하는 쿼리는 executeUpdate()를 사용
    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";
        template.update(sql, money, memberId);
    }

    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id=?";
        template.update(sql, memberId);
    }

    private RowMapper<Member> memberRowMapper() {
        //특정 memberId에 따라 찾아옴
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }

}
