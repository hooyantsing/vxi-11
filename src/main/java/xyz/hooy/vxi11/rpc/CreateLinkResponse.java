package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrAble;
import org.acplt.oncrpc.XdrDecodingStream;
import org.acplt.oncrpc.XdrEncodingStream;

import java.io.IOException;

public class CreateLinkResponse implements XdrAble {

    private DeviceErrorCode error;
    private DeviceLink link;
    private short abortPort;
    private int maxReceiveSize;

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        error.xdrEncode(xdr);
        link.xdrEncode(xdr);
        xdr.xdrEncodeShort(abortPort);
        xdr.xdrEncodeInt(maxReceiveSize);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        error = new DeviceErrorCode(xdr);
        link = new DeviceLink(xdr);
        abortPort = xdr.xdrDecodeShort();
        maxReceiveSize = xdr.xdrDecodeInt();
    }

    public DeviceErrorCode getError() {
        return error;
    }

    public DeviceLink getLink() {
        return link;
    }

    public short getAbortPort() {
        return abortPort;
    }

    public int getMaxReceiveSize() {
        return maxReceiveSize;
    }
}