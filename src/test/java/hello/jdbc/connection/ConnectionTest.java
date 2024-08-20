package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    @Test
    void driverManager() throws SQLException{
        Connection con1 = DriverManager.getConnection(URL,USERNAME,PASSWORD);
        //driveManager를 통해서 getConnection을 한다, 커넥션을 획득할 때 마다 파라미터를 계속 전달
        Connection con2 = DriverManager.getConnection(URL,USERNAME,PASSWORD);
        //driveManager를 통해서 getConnection을 한다.
        log.info("connection={}, class = {}",con1, con1.getClass());
        log.info("connection={}, class = {}",con2,con2.getClass());
    }

    @Test
    void dataSourceDriverManager() throws SQLException {
        //DriverManagerDataSource - 항상 새로운 커넥션을 획득
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        //기존 코드와 비슷하지만 DriverManagerDataSource는 DataSource를 통해서 커넥션을 획득할 수 있다.
        //설정 : DataSource를 만들고 필요한 속성들을 사용해서 입력
        //설정과 관련된 속성들은 한 곳에 있는 것이 향후 변경에 더 유연하게 대처
        useDataSource(dataSource);
        //DataSource 인터페이스를 통해서 가져온다
    }

    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        //커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        //HikariCP 커넥션 풀을 사용, DataSource 인터페이스로 구현
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10); //10개가 넘어가면 초과 발생 시, 잠시 동안 기다리다가 종료됨
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        Thread.sleep(1000); //커넥션 풀에서 커넥션 생성 시간 대기
        //별도의 쓰레드를 사용해서 커넥션 풀에 커넥션을 채움, 왜냐하면 커넥션 풀을 채울 때 까지 마냥 대기하면 애플리케이션 실행 시간이 늦어짐
    }

    private void useDataSource(DataSource dataSource) throws SQLException{
        Connection con1 = dataSource.getConnection(); // 사용할때 정보를 몰라도 된다.
        //처음 객체를 생성할 때만 필요한 파라미터를 넘겨주고 커넥션을 획득할 때는 단순히 dataSource.getConnection()만 호출
        //사용 : 설정은 신경쓰지 않고, 메서드만 호출해서 사용
        Connection con2 = dataSource.getConnection();
        log.info("connection={}, class = {}",con1, con1.getClass());
        log.info("connection={}, class = {}",con2,con2.getClass());
    }
}
