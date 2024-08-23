package xyz.hooy.vxi11.rpc;

import xyz.hooy.vxi11.exception.Vxi11ServerException;
import xyz.hooy.vxi11.rpc.idl.Device_Error;
import xyz.hooy.vxi11.rpc.idl.Device_ErrorCode;

public class DeviceError {

    private final int errorId;

    public DeviceError(int errorCode) {
        this.errorId = errorCode;
    }

    public DeviceError(Device_ErrorCode deviceErrorCode) {
        this(deviceErrorCode.value);
    }

    public DeviceError(Device_Error deviceError) {
        this(deviceError.error);
    }

    public int getErrorId() {
        return errorId;
    }

    public void checkErrorThrowException() {
        if (errorId != DeviceErrorCode.NO_ERROR) {
            throw new Vxi11ServerException(errorId);
        }
    }
}