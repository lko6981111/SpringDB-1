package hello.jdbc.connection;

public class ConnectionConst {
    //데이터베이스에 접속하는데 필요한 기본 정보를 편리하게 사용할 수 있도록 상수로 만듬
    public static final String URL = "jdbc:h2:tcp://localhost/~/test";
    //여기서 각각의 드라이버는 URL 정보를 체크해서 본인이 처리할 수 있는 요청인지 확인한다.
    //예를 들어서 URL이 `jdbc:h2` 로 시작하면 이것은 h2 데이터베이스에 접근하기 위한 규칙
    public static final String USERNAME = "";
    public static final String PASSWORD="";
}
