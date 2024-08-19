package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j //로그 남기기 위해 사용되는 어노테이션
public class DBConnectionUtil {
    public static Connection getConnection(){
        try{
            Connection connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            //데이터베이스에 연결ㅎ려면 JDBC가 제공하는 메서드 사용, 인자로서 필요한 연결상수 삽입
            //각 db 드라이버에 맞게
            log.info("get connection={}, class={}",connection,connection.getClass());
            //connection의 메모리주소와 클래스 이름과 클래스 정보를 로깅함
            return connection;
            //연결된 결과를 반환해줌
        } catch (SQLException e) {
            throw new IllegalStateException(e);
            //예외상황 처리
        }
    }
}
