package xyz.hooy.vxi11.exception;

public class Vxi11Exception extends RuntimeException{

    public Vxi11Exception(String message) {
        super(message);
    }

    public Vxi11Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public Vxi11Exception(Throwable cause) {
        super(cause);
    }
}
