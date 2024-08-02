package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.*;
import java.io.IOException;

public class DeviceWriteParams implements XdrAble {

    private DeviceLink link;
    private int ioTimeout;
    private int lockTimeout;
    private DeviceFlags flags;
    private byte [] data;

    public DeviceWriteParams(DeviceLink link, int ioTimeout, int lockTimeout, DeviceFlags flags, byte[] data) {
        this.link = link;
        this.ioTimeout = ioTimeout;
        this.lockTimeout = lockTimeout;
        this.flags = flags;
        this.data = data;
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        link.xdrEncode(xdr);
        xdr.xdrEncodeInt(ioTimeout);
        xdr.xdrEncodeInt(lockTimeout);
        flags.xdrEncode(xdr);
        xdr.xdrEncodeDynamicOpaque(data);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        link = new DeviceLink(xdr);
        ioTimeout = xdr.xdrDecodeInt();
        lockTimeout = xdr.xdrDecodeInt();
        flags = new DeviceFlags(xdr);
        data = xdr.xdrDecodeDynamicOpaque();
    }
}