package xyz.hooy.vxi11.exception;

import xyz.hooy.vxi11.rpc.ErrorCode;

public class Vxi11ServerException extends Vxi11Exception {

    private int errorCode;

    public Vxi11ServerException(int code) {
        super("VXI-11 Error: " + ErrorCode.getErrorString(code));
    }

    public int getErrorCode() {
        return errorCode;
    }
}
