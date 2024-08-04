package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrAble;
import org.acplt.oncrpc.XdrDecodingStream;
import org.acplt.oncrpc.XdrEncodingStream;

import java.io.IOException;

public class DeviceLockParams implements XdrAble {

    private DeviceLink link;
    private DeviceFlags flags;
    private int lockTimeout;

    public DeviceLockParams(DeviceLink link, int lockTimeout, DeviceFlags flags) {
        this.link = link;
        this.flags = flags;
        this.lockTimeout = lockTimeout;
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        link.xdrEncode(xdr);
        flags.xdrEncode(xdr);
        xdr.xdrEncodeInt(lockTimeout);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        link = new DeviceLink(xdr);
        flags = new DeviceFlags(xdr);
        lockTimeout = xdr.xdrDecodeInt();
    }
}
