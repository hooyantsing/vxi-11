/*
 * Automatically generated by jrpcgen 1.1.6 on 24-8-23 上午9:09
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package xyz.hooy.vxi11.rpc.idl;
import org.acplt.oncrpc.*;
import java.io.IOException;

/** TODO */
public class Create_LinkResp implements XdrAble {
    /** TODO */
    public Device_ErrorCode error;
    /** TODO */
    public Device_Link lid;
    /** TODO */
    public short abortPort;
    /** TODO */
    public int maxRecvSize;

    public Create_LinkResp() {
    }

    public Create_LinkResp(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public Create_LinkResp(Device_ErrorCode error, Device_Link lid,
           short abortPort, int maxRecvSize) {
        this.error = error;
        this.lid = lid;
        this.abortPort = abortPort;
        this.maxRecvSize = maxRecvSize;
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        error.xdrEncode(xdr);
        lid.xdrEncode(xdr);
        xdr.xdrEncodeShort(abortPort);
        xdr.xdrEncodeInt(maxRecvSize);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        error = new Device_ErrorCode(xdr);
        lid = new Device_Link(xdr);
        abortPort = xdr.xdrDecodeShort();
        maxRecvSize = xdr.xdrDecodeInt();
    }

    @Override
    public String toString() {
        return "Create_LinkResp ["
        + "error=" + error + ", "
        + "lid=" + lid + ", "
        + "abortPort=" + abortPort + ", "
        + "maxRecvSize=" + maxRecvSize
        + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Create_LinkResp other = (Create_LinkResp)obj;
        if (! java.util.Objects.equals(this.error, other.error)) return false;
        if (! java.util.Objects.equals(this.lid, other.lid)) return false;
        if (this.abortPort != other.abortPort) return false;
        if (this.maxRecvSize != other.maxRecvSize) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return XdrHashCode.hashCode(this);
    }

}
// End of Create_LinkResp.java
