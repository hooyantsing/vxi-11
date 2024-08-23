/*
 * Automatically generated by jrpcgen 1.1.6 on 24-8-23 上午9:09
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package xyz.hooy.vxi11.rpc.idl;
import org.acplt.oncrpc.*;
import java.io.IOException;

import java.net.InetAddress;

import org.acplt.oncrpc.server.*;

/**
 */
public abstract class vxi11_DEVICE_INTR_ServerStub extends OncRpcServerStub implements OncRpcDispatchable {

    public vxi11_DEVICE_INTR_ServerStub()
           throws OncRpcException, IOException {
        this(0);
    }

    public vxi11_DEVICE_INTR_ServerStub(int port)
           throws OncRpcException, IOException {
        this(null, port);
    }

    public vxi11_DEVICE_INTR_ServerStub(InetAddress bindAddr, int port)
           throws OncRpcException, IOException {
        info = new OncRpcServerTransportRegistrationInfo [] {
            new OncRpcServerTransportRegistrationInfo(vxi11.DEVICE_INTR, 1)
        };
        transports = new OncRpcServerTransport [] {
            new OncRpcUdpServerTransport(this, bindAddr, port, info, 32768),
            new OncRpcTcpServerTransport(this, bindAddr, port, info, 32768)
        };
    }

    public void dispatchOncRpcCall(OncRpcCallInformation call, int program, int version, int procedure)
           throws OncRpcException, IOException {
        if ( version == 1 ) {
            switch ( procedure ) {
            case 0:
                call.retrieveCall(XdrVoid.XDR_VOID);
                call.reply(XdrVoid.XDR_VOID);
                break;
            case 30: {
                Device_SrqParms args$ = new Device_SrqParms();
                call.retrieveCall(args$);
                device_intr_srq_1(args$);
                call.reply(XdrVoid.XDR_VOID);
                break;
            }
            default:
                call.failProcedureUnavailable();
            }
        } else {
            call.failProgramUnavailable();
        }
    }

    /**
     * Call remote procedure device_intr_srq_1.
     * @param arg1 parameter (of type Device_SrqParms) to the remote procedure call.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void device_intr_srq_1(Device_SrqParms arg1);

}
// End of vxi11_DEVICE_INTR_ServerStub.java
