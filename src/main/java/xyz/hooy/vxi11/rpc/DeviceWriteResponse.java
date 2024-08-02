package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.*;
import java.io.IOException;

public class DeviceWriteResponse implements XdrAble {

    private DeviceErrorCode error;
    private int size;

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        error.xdrEncode(xdr);
        xdr.xdrEncodeInt(size);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        error = new DeviceErrorCode(xdr);
        size = xdr.xdrDecodeInt();
    }

    public DeviceErrorCode getError() {
        return error;
    }

    public int getSize() {
        return size;
    }
}