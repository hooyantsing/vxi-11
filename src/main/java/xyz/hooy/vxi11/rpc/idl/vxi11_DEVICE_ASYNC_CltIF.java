/*
 * Automatically generated by jrpcgen 1.1.6 on 24-8-23 上午9:09
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package xyz.hooy.vxi11.rpc.idl;
import org.acplt.oncrpc.*;
import java.io.IOException;

/** TODO */
public interface vxi11_DEVICE_ASYNC_CltIF {

    /**
     * Call remote procedure device_abort_1.
     * @param arg1 parameter (of type Device_Link) to the remote procedure call.
     * @return Result from remote procedure call (of type Device_Error).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    Device_Error device_abort_1(Device_Link arg1) throws OncRpcException, IOException;


}
// End of vxi11_DEVICE_ASYNC_CltIF.java
