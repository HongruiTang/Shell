package uk.ac.ucl.shell.exception;

public class AppException extends RuntimeException {

    public AppException(String appName, String message) {
        super(appName + ": " + message);
    }
}
