package xyz.hooy.vxi11.rpc;
import org.acplt.oncrpc.*;

import java.io.IOException;


public class DeviceServiceRequestParams implements XdrAble {
    
    private String handle;

    public DeviceServiceRequestParams() {
    }

    public DeviceServiceRequestParams(String handle) {
        this.handle = handle;
    }

    protected DeviceServiceRequestParams(XdrDecodingStream xdr) throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        xdr.xdrEncodeString(handle);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        handle = xdr.xdrDecodeString();
    }

    public String getHandle() {
        return handle;
    }
}