package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.*;
import java.io.IOException;

public class DeviceLink implements XdrAble {

    private int linkId;

    public DeviceLink(int linkId) {
        this.linkId = linkId;
    }

    public DeviceLink(XdrDecodingStream xdr) throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        xdr.xdrEncodeInt(linkId);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        linkId = xdr.xdrDecodeInt();
    }

    public int getLinkId() {
        return linkId;
    }
}