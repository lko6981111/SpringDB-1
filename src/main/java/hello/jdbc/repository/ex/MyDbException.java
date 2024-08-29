package hello.jdbc.repository.ex;

public class MyDbException extends RuntimeException{
    //RuntimeException을 상속 받았다. 따라서 MyDbException은 런타임(언체크) 예외가 된다.
    public MyDbException() {
    }

    public MyDbException(String message) {
        super(message);
    }

    public MyDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDbException(Throwable cause) {
        super(cause);
    }
}
