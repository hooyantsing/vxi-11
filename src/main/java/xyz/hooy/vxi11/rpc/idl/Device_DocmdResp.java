/*
 * Automatically generated by jrpcgen 1.1.6 on 24-8-23 上午9:09
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package xyz.hooy.vxi11.rpc.idl;
import org.acplt.oncrpc.*;
import java.io.IOException;

/** TODO */
public class Device_DocmdResp implements XdrAble {
    /** TODO */
    public Device_ErrorCode error;
    /** TODO */
    public byte[] data_out;

    public Device_DocmdResp() {
    }

    public Device_DocmdResp(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public Device_DocmdResp(Device_ErrorCode error, byte[] data_out) {
        this.error = error;
        this.data_out = data_out;
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        error.xdrEncode(xdr);
        xdr.xdrEncodeDynamicOpaque(data_out);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        error = new Device_ErrorCode(xdr);
        data_out = xdr.xdrDecodeDynamicOpaque();
    }

    @Override
    public String toString() {
        return "Device_DocmdResp ["
        + "error=" + error + ", "
        + "data_out=" + java.util.Arrays.toString(data_out)
        + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Device_DocmdResp other = (Device_DocmdResp)obj;
        if (! java.util.Objects.equals(this.error, other.error)) return false;
        if (! java.util.Arrays.equals(this.data_out, other.data_out)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return XdrHashCode.hashCode(this);
    }

}
// End of Device_DocmdResp.java
