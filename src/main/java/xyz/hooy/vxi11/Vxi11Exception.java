package xyz.hooy.vxi11;

public class Vxi11Exception extends RuntimeException{

    public Vxi11Exception(int code) {
        super("VXI-11 Error: " + ErrorCode.getErrorString(code));
    }

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
