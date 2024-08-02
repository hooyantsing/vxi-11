package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrAble;
import org.acplt.oncrpc.XdrDecodingStream;
import org.acplt.oncrpc.XdrEncodingStream;

import java.io.IOException;

public class DeviceReadParams implements XdrAble {

    private DeviceLink link;
    private int requestSize;
    private int ioTimeout;
    private int lockTimeout;
    private DeviceFlags flags;
    private byte terminationCharacter;

    public DeviceReadParams(DeviceLink link, int requestSize, int ioTimeout, int lockTimeout, byte terminationCharacter, DeviceFlags flags) {
        this.link = link;
        this.requestSize = requestSize;
        this.ioTimeout = ioTimeout;
        this.lockTimeout = lockTimeout;
        this.flags = flags;
        this.terminationCharacter = terminationCharacter;
    }

    protected DeviceReadParams(XdrDecodingStream xdr) throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        link.xdrEncode(xdr);
        xdr.xdrEncodeInt(requestSize);
        xdr.xdrEncodeInt(ioTimeout);
        xdr.xdrEncodeInt(lockTimeout);
        flags.xdrEncode(xdr);
        xdr.xdrEncodeByte(terminationCharacter);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        link = new DeviceLink(xdr);
        requestSize = xdr.xdrDecodeInt();
        ioTimeout = xdr.xdrDecodeInt();
        lockTimeout = xdr.xdrDecodeInt();
        flags = new DeviceFlags(xdr);
        terminationCharacter = xdr.xdrDecodeByte();
    }
}