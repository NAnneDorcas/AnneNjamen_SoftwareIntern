package ee.taltech.inbankbackend.exceptions;

public class InvalidAgeException extends Exception {
    private final String message;
    private final Throwable cause;

    public InvalidAgeException(String message) {
        this(message, null);  // Correctly pass the message to the superclass constructor
    }

    public InvalidAgeException(String message, Throwable cause) {
        this.message = message;  // Correctly pass both message and cause to the superclass constructor
        this.cause = cause;
    }

    // Optionally, if you want to provide getters for these fields, although not necessary since super already handles it
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
