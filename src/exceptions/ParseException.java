package exceptions;

public class ParseException extends Exception {
    public ParseException(String errorMessage) {
        super(errorMessage);
    }

    public ParseException() {
        super();
    }
}
