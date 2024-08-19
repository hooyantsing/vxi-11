package xyz.hooy.vxi11.exception;

public class Vxi11ClientException extends Vxi11Exception {

    public Vxi11ClientException(String message) {
        super(message);
    }

    public Vxi11ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public Vxi11ClientException(Throwable cause) {
        super(cause);
    }
}
