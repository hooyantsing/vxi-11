package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.*;
import xyz.hooy.vxi11.util.BitUtils;

import java.io.IOException;

public class DeviceReadResponse implements XdrAble {

    public final static int END_OFFSET = 2;

    public final static int TERMINATION_CHARACTER_OFFSET = 1;

    public final static int REQUESTED_COUNT_OFFSET = 0;

    private DeviceErrorCode error;
    private int reason;
    private byte[] data;

    public DeviceReadResponse() {
    }

    protected DeviceReadResponse(XdrDecodingStream xdr) throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        error.xdrEncode(xdr);
        xdr.xdrEncodeInt(reason);
        xdr.xdrEncodeDynamicOpaque(data);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        error = new DeviceErrorCode(xdr);
        reason = xdr.xdrDecodeInt();
        data = xdr.xdrDecodeDynamicOpaque();
    }

    public boolean isEnd() {
        return BitUtils.isBit(reason,END_OFFSET);
    }

    public boolean isTerminationCharacter() {
        return BitUtils.isBit(reason,TERMINATION_CHARACTER_OFFSET);
    }

    public boolean isRequestCount() {
        return BitUtils.isBit(reason,REQUESTED_COUNT_OFFSET);
    }

    public boolean noReason() {
        return reason == 0;
    }

    public DeviceErrorCode getError() {
        return error;
    }

    public byte[] getData() {
        return data;
    }
}