package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * SQLExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository {

    private final DataSource dataSource; //DataSource 의존관계 주입
    private final SQLExceptionTranslator exTranslator;//변환기 생성
    public MemberRepositoryV4_2(DataSource dataSource){
        this.dataSource = dataSource;
        this.exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }
    //외부에서 DataSource를 주입 받아서 사용한다.

    @Override
    public Member save(Member member){
        String sql = "insert into member(member_id, money) values(?,?)";
        //member에 value값을 넣어라, 데이터 등록

        Connection con = null; // 연결을 위해 사용
        PreparedStatement pstmt = null; //sql을 담기 위해 사용
        //PreparedStatement는 Statement의 자식 타입인데, ?를 통한 파라미터 바인딩을 가능하게 해준다

        try {
            con = getConnection(); // 커넥션 획득
            pstmt = con.prepareStatement(sql); //데이터베이스에 전달할 sql과 파라미터로 전다할 데이터들을 준비
            pstmt.setString(1, member.getMemberId());//sql의 첫번째 ?에 값을 지정, 문자이므로 setString을 사용
            pstmt.setInt(2, member.getMoney());//sql의 두번째 ?에 값을 지정, Int 형이므로 setInt 사용
            pstmt.executeUpdate();//Statememnt를 통해 준비된 sql을 커넥션을 통해 실제 데이터베이스에 전달
            //executeUpdate()은 int를 반환하는데 영향받는 DB row수를 반환
            return member;
        } catch (SQLException e) {
            throw exTranslator.translate("save",sql,e);//변환기를 통해 손쉽게 해결
        } finally {
            //리소스 정리는 꼭 해줘야야한다. 그래서 예외가 바랭하든, 하지 않든 항상 수행되어야 하는 finally굼ㄴ에서 작성
            close(con, pstmt, null);//쿼리를 실행하고나서 리소스 정리
            //만약 이 부분을 놓치게 되면 커넥션이 끊어지지 않고 계속 유지되는 문제가 발생할 수 있어 리로스 누수가 발생

        }

    }

    @Override
    public Member findById(String memberId){
        //findById는 회원 하나를 조회하는 것이 목적
        String sql = "select * from member where member_id=?";
        //데이터 조회를 위한 select SQL을 준비

        Connection con = null; //연결을 위해
        PreparedStatement pstmt = null;//sql 담음
        ResultSet rs = null; //sql 요청 응답
        //내부에 있는 커서를 이동해서 다음 데이터를 조회

        try{
            con = getConnection(); // 커넥션 획득
            pstmt= con.prepareStatement(sql);//데이터베이스에 전달할 sql과 파라미터로 전다할 데이터들을 준비
            pstmt.setString(1,memberId);//sql의 첫번째 ?에 값을 지정, 문자이므로 setString을 사용

            rs = pstmt.executeQuery();//데이터를 조회 할 때는 executeQuery()를 사용, 결과를 ResultSet에 담아서 반환
            if(rs.next()){
                //rs.next()을 호출하면 커서가 다음으로 이동, 참고로 최초의 커서는 데이터를 가리키고 있지 않기 때문에
                //rs.next()를 최초 한번은 호출해야 데이터를 조회할 수 있다.
                //true면 데이터 O, false면 데이터 X

                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                //rs로 가져온 현재 가리키는 객체를 통해 값 저장

                return member;
            }   else{
                throw new NoSuchElementException("member not found memberId="+memberId);
                //예외 처리 , 뒤에 설명문을 구체적으로 적는것이 중요
            }

        } catch (SQLException e) {
            throw exTranslator.translate("findById",sql,e);
        }finally{
            close(con,pstmt,rs);//리소스 정리
        }
    }


    //수정과 삭제는 등록과 비슷, 데이터를 변경하는 쿼리는 executeUpdate()를 사용
    public void update(String memberId, int money)  {
        String sql = "update member set money=? where member_id=?";
        //수정 sql

        Connection con = null; //연결을 위해
        PreparedStatement pstmt = null; //담기 위해

        try {
            con = getConnection();// 커넥션 획득
            pstmt = con.prepareStatement(sql);//데이터베이스에 전달할 sql과 파라미터로 전다할 데이터들을 준비
            pstmt.setInt(1, money);//sql의 첫번째 ?에 값을 지정,
            pstmt.setString(2, memberId);//sql의 두번째 ?에 값을 지정,

            int resultSize = pstmt.executeUpdate();//executeUpdate()는 실행하고 영향받은 row수를 반환
            log.info("resultSize={}",resultSize);
        } catch (SQLException e) {
            throw exTranslator.translate("update",sql,e);
        } finally {
            close(con, pstmt, null);
        }
    }

    public void delete(String memberId)  {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw exTranslator.translate("delete",sql,e);
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //JdbcUtils 편의 메소드, 이것을 사용하면 커넥션을 좀 더 편리하게 닫을 수 있다.

        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        //만약에 커넥션을 종료하면 문제 발생
        DataSourceUtils.releaseConnection(con,dataSource);
        //트랜잭션을 사용하기 위해 동기화된 ㅓㅋ넥션은 커넥션을 닫지 않고 그대로 유지
        //트랜잭션 동기화 매니저가 관리하는 커넥션이 없는 경후 해당 커넥션을 닫음

    }

    private Connection getConnection()  {
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 하낟.
        Connection con = DataSourceUtils.getConnection(dataSource);
        //트랜잭션 동기화 매니저가 관리하는 커넥션이 있으면 해당 커넥션을 반환
        //혹은 트랜잭션 동기화 매니저가 관리하는 커넥션ㅇ ㅣ엇는 경우 새로운 커넥션을 생성하여 반환

        //DataSource는 표준 인터페이스 이기 때문에 DriveManagerDataSource에서 HikariDataSource로 변경되어도 해당 코드를 변경하지 않아도 된다.
        log.info("get connection={}, class={}",con,con.getClass());
        return con;
    }
}
