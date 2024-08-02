package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.*;

import java.io.IOException;

public class DeviceGenericParams implements XdrAble {

    private DeviceLink linkId;
    private DeviceFlags flags;
    private int lockTimeout;
    private int ioTimeout;

    public DeviceGenericParams(DeviceLink linkId, int ioTimeout, int lockTimeout, DeviceFlags flags) {
        this.linkId = linkId;
        this.flags = flags;
        this.lockTimeout = lockTimeout;
        this.ioTimeout = ioTimeout;
    }

    protected DeviceGenericParams(XdrDecodingStream xdr) throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        linkId.xdrEncode(xdr);
        flags.xdrEncode(xdr);
        xdr.xdrEncodeInt(lockTimeout);
        xdr.xdrEncodeInt(ioTimeout);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        linkId = new DeviceLink(xdr);
        flags = new DeviceFlags(xdr);
        lockTimeout = xdr.xdrDecodeInt();
        ioTimeout = xdr.xdrDecodeInt();
    }
}