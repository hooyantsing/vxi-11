package xyz.hooy.vxi11.rpc;

import xyz.hooy.vxi11.rpc.idl.Device_ReadResp;
import xyz.hooy.vxi11.util.BitUtils;

public class DeviceReadResponse {

    public final static int END_OFFSET = 2;

    public final static int TERMINATION_CHARACTER_OFFSET = 1;

    public final static int REQUESTED_COUNT_OFFSET = 0;

    private final DeviceError error;
    private final int reason;
    private final byte[] data;

    public DeviceReadResponse(Device_ReadResp resp) {
        this.error = new DeviceError(resp.error);
        this.reason = resp.reason;
        this.data = resp.data;
    }

    public boolean isEnd() {
        return BitUtils.isBit(reason, END_OFFSET);
    }

    public boolean isTerminationCharacter() {
        return BitUtils.isBit(reason, TERMINATION_CHARACTER_OFFSET);
    }

    public boolean isRequestCount() {
        return BitUtils.isBit(reason, REQUESTED_COUNT_OFFSET);
    }

    public boolean noReason() {
        return reason == 0;
    }

    public DeviceError getError() {
        return error;
    }

    public byte[] getData() {
        return data;
    }
}