/*
 * Automatically generated by jrpcgen 1.1.6 on 24-8-23 上午9:09
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package xyz.hooy.vxi11.rpc.idl;
import org.acplt.oncrpc.*;
import java.io.IOException;

/** TODO */
public class Device_ReadParms implements XdrAble {
    /** TODO */
    public Device_Link lid;
    /** TODO */
    public int requestSize;
    /** TODO */
    public int io_timeout;
    /** TODO */
    public int lock_timeout;
    /** TODO */
    public Device_Flags flags;
    /** TODO */
    public byte termChar;

    public Device_ReadParms() {
    }

    public Device_ReadParms(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public Device_ReadParms(Device_Link lid, int requestSize, int io_timeout,
           int lock_timeout, Device_Flags flags, byte termChar) {
        this.lid = lid;
        this.requestSize = requestSize;
        this.io_timeout = io_timeout;
        this.lock_timeout = lock_timeout;
        this.flags = flags;
        this.termChar = termChar;
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        lid.xdrEncode(xdr);
        xdr.xdrEncodeInt(requestSize);
        xdr.xdrEncodeInt(io_timeout);
        xdr.xdrEncodeInt(lock_timeout);
        flags.xdrEncode(xdr);
        xdr.xdrEncodeByte(termChar);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        lid = new Device_Link(xdr);
        requestSize = xdr.xdrDecodeInt();
        io_timeout = xdr.xdrDecodeInt();
        lock_timeout = xdr.xdrDecodeInt();
        flags = new Device_Flags(xdr);
        termChar = xdr.xdrDecodeByte();
    }

    @Override
    public String toString() {
        return "Device_ReadParms ["
        + "lid=" + lid + ", "
        + "requestSize=" + requestSize + ", "
        + "io_timeout=" + io_timeout + ", "
        + "lock_timeout=" + lock_timeout + ", "
        + "flags=" + flags + ", "
        + "termChar=" + termChar
        + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Device_ReadParms other = (Device_ReadParms)obj;
        if (! java.util.Objects.equals(this.lid, other.lid)) return false;
        if (this.requestSize != other.requestSize) return false;
        if (this.io_timeout != other.io_timeout) return false;
        if (this.lock_timeout != other.lock_timeout) return false;
        if (! java.util.Objects.equals(this.flags, other.flags)) return false;
        if (this.termChar != other.termChar) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return XdrHashCode.hashCode(this);
    }

}
// End of Device_ReadParms.java
