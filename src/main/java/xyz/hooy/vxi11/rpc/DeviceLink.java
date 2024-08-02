package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.*;
import java.io.IOException;

public class DeviceLink implements XdrAble {

    private int value;

    public DeviceLink(int value) {
        this.value = value;
    }

    public DeviceLink(XdrDecodingStream xdr) throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        xdr.xdrEncodeInt(value);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        value = xdr.xdrDecodeInt();
    }

    public int getValue() {
        return value;
    }
}