package exceptions;

public class BadRequestException extends RuntimeException {
    private static final String badRequest = "Bad request: ";
    public BadRequestException(String message) {
        super(badRequest + message);
    }
}
