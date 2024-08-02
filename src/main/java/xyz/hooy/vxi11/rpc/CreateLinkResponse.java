package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrAble;
import org.acplt.oncrpc.XdrDecodingStream;
import org.acplt.oncrpc.XdrEncodingStream;

import java.io.IOException;

public class CreateLinkResponse implements XdrAble {

    private DeviceErrorCode error;
    private DeviceLink lid;
    private short abortPort;
    private int maxRecvSize;

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        error.xdrEncode(xdr);
        lid.xdrEncode(xdr);
        xdr.xdrEncodeShort(abortPort);
        xdr.xdrEncodeInt(maxRecvSize);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        error = new DeviceErrorCode(xdr);
        lid = new DeviceLink(xdr);
        abortPort = xdr.xdrDecodeShort();
        maxRecvSize = xdr.xdrDecodeInt();
    }

    public int getError() {
        return error.getValue();
    }

    public int getLinkId() {
        return lid.getValue();
    }

    public short getAbortPort() {
        return abortPort;
    }

    public int getMaxReceiveSize() {
        return maxRecvSize;
    }
}