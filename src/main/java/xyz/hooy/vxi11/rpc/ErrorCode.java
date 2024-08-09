package xyz.hooy.vxi11.rpc;

import java.lang.reflect.Field;

public final class ErrorCode {

    private ErrorCode(){}

    public static final int NO_ERROR = 0;
    public static final int SYNTAX_ERROR = 2;
    public static final int DEVICE_NOT_ACCESSIBLE = 3;
    public static final int INVALID_LINK_IDENTIFIER = 4;
    public static final int PARAMETER_ERROR = 5;
    public static final int CHANNEL_NOT_ESTABLISHED = 6;
    public static final int OPERATION_NOT_SUPPORTED = 8;
    public static final int OUT_OF_RESOURCES = 9;
    public static final int DEVICE_LOCKED_BY_ANOTHER_LINK = 11;
    public static final int NO_LOCK_HELD_BY_THIS_LINK = 12;
    public static final int IO_TIMEOUT = 15;
    public static final int IO_ERROR = 17;
    public static final int INVALID_ADDRESS = 21;
    public static final int ABORT = 23;
    public static final int CHANNEL_ALREADY_ESTABLISHED = 29;

    public static String getErrorName(int code) {
        try {
            for (Field field : ErrorCode.class.getFields()) {
                if (field.getType() == int.class && field.getInt(null) == code) {
                    return field.getName();
                }
            }
            return "UNKNOWN";
        } catch (IllegalAccessException e) {
            return "UNKNOWN";
        }
    }

    public static String getErrorString(int code) {
        return getErrorName(code) + " (" + code + ")";
    }
}
