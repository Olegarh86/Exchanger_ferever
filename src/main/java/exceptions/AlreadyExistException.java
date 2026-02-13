package exceptions;

public class AlreadyExistException extends RuntimeException {
    private static final String alreadyExist = "Already exist: ";
    public AlreadyExistException(String message) {
        super(alreadyExist + message);
    }
}
