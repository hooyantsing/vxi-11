package xyz.hooy.vxi11.rpc;
import org.acplt.oncrpc.*;

import java.io.IOException;

public class DeviceEnableServiceRequestParams implements XdrAble {

    private DeviceLink link;

    private boolean enable;

    private String handle;

    public DeviceEnableServiceRequestParams() {
    }

    public DeviceEnableServiceRequestParams(DeviceLink link, boolean enable, String handle) {
        this.link = link;
        this.enable = enable;
        this.handle = handle;
    }

    protected DeviceEnableServiceRequestParams(XdrDecodingStream xdr) throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        link.xdrEncode(xdr);
        xdr.xdrEncodeBoolean(enable);
        xdr.xdrEncodeString(handle);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        link = new DeviceLink(xdr);
        enable = xdr.xdrDecodeBoolean();
        handle = xdr.xdrDecodeString();
    }
}
