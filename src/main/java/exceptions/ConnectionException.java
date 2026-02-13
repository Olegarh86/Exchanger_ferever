package exceptions;

public class ConnectionException extends RuntimeException{
    private static final String exception = "Connection is not available";
    public ConnectionException(String message) {
        super(exception + message);
    }
}
