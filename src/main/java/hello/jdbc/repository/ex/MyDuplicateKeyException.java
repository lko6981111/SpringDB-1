package hello.jdbc.repository.ex;

public class MyDuplicateKeyException extends MyDbException{//RuntimeException을 받아도 되는데, MyDbException을 받으면 db에서 발생한 에러로 카테고리를 묶을 수 있다
//기존에 사용했던 MyDbException을 상속받아서 의미있는 계층을 형성, 이렇게하면 데이ㅓ베이스 관련 예외라는 계층을 만들수 있다
//우리가 직접 만든 것이기 때문에, JDBC나 JPA 같은 특정 기술에 종속적이지 않다.
    public MyDuplicateKeyException() {
    }

    public MyDuplicateKeyException(String message) {
        super(message);
    }

    public MyDuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDuplicateKeyException(Throwable cause) {
        super(cause);
    }
}
