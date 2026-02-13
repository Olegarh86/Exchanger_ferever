package exceptions;

public class NotFoundException extends RuntimeException {
    private static final String notFound = "Not Found: ";
    public NotFoundException(String message) {
        super(notFound + message);
    }
}
