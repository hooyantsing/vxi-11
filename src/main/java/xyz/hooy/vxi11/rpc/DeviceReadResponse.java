package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.*;

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
        return (reason & (1 << END_OFFSET)) != 0;
    }

    public boolean isTerminationCharacter() {
        return (reason & (1 << TERMINATION_CHARACTER_OFFSET)) != 0;
    }

    public boolean isRequestCount() {
        return (reason & (1 << REQUESTED_COUNT_OFFSET)) != 0;
    }

    public DeviceErrorCode getError() {
        return error;
    }

    public byte[] getData() {
        return data;
    }
}