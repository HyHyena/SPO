package exceptions;

public class CompileException extends Exception {
    public CompileException(String errorMessage) {
        super(errorMessage);
    }

    public CompileException() {
        super();
    }
}
