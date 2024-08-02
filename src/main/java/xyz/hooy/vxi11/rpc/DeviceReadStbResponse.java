package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.*;
import java.io.IOException;

public class DeviceReadStbResponse implements XdrAble {

    private DeviceErrorCode error;
    private byte stb;

    public DeviceReadStbResponse() {
    }

    protected DeviceReadStbResponse(XdrDecodingStream xdr) throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        error.xdrEncode(xdr);
        xdr.xdrEncodeByte(stb);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        error = new DeviceErrorCode(xdr);
        stb = xdr.xdrDecodeByte();
    }

    public DeviceErrorCode getError() {
        return error;
    }

    public byte getStb() {
        return stb;
    }
}