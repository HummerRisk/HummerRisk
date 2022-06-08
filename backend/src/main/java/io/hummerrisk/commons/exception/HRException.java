package io.hummerrisk.commons.exception;

public class HRException extends RuntimeException {

    public HRException(String message) {
        super(message);
    }

    private HRException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new HRException(message);
    }

    public static HRException getException(String message) {
        throw new HRException(message);
    }

    public static void throwException(Throwable t) {
        throw new HRException(t);
    }
}
