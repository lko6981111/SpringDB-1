package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class DBConnectionUtilTest {

    @Test
    void connection(){
        Connection connection = DBConnectionUtil.getConnection();
        //구현된 메서드를 통해 커넥션 구현체를 받음
        assertThat(connection).isNotNull();
        //null이 아닌지 파악
    }
}
