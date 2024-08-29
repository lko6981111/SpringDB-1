package hello.jdbc.repository;

import hello.jdbc.domain.Member;

import java.sql.SQLException;

public interface MemberRepositoryEx {
    //SQLException은 체크 예외이기 때문에 인터페이스에도 해당 체크 예외가 선언 되어야 한다.
    //즉, 인터페이스가 jdbc에 종속적으로 된다. 그래서 향후 다른 기술로 변경한다면 인터페이스 자체를 변경해야 한다.
    //인터페이스의 구현체가 체크 예외를 던지려면, 인터페이스 메서드에 먼저 체크 예외를 던지는 부분이 선언 되어있어야 한다.
    //그래야 구현 클래스의 메서드도 체크 예외를 던질 수 있다. 참고로 구현 클래스의 메서드에 선언할 수 있는 예외는 부모 타입에서 던진 예외와 같거나 하위 타입이어야 한다.
    Member save(Member member) throws SQLException;
    Member findById(String memberId) throws SQLException;
    void update(String memberId, int money) throws SQLException;
    void delete(String memberId) throws SQLException;
}