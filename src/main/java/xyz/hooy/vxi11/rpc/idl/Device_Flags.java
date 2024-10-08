/*
 * Automatically generated by jrpcgen 1.1.6 on 24-8-23 上午9:09
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package xyz.hooy.vxi11.rpc.idl;
import org.acplt.oncrpc.*;
import java.io.IOException;

/** TODO */
public class Device_Flags implements XdrAble {
    /** TODO */
    public int value;

    public Device_Flags() {
    }

    public Device_Flags(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public Device_Flags(int value) {
        this.value = value;
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(value);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        value = xdr.xdrDecodeInt();
    }

    @Override
    public String toString() {
        return "Device_Flags ["
        + "value=" + value
        + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Device_Flags other = (Device_Flags)obj;
        if (this.value != other.value) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return XdrHashCode.hashCode(this);
    }

}
// End of Device_Flags.java
