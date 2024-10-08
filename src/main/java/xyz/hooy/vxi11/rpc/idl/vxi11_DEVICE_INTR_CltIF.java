/*
 * Automatically generated by jrpcgen 1.1.6 on 24-8-23 上午9:09
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package xyz.hooy.vxi11.rpc.idl;
import org.acplt.oncrpc.*;
import java.io.IOException;

/******************************************************************************
 *
 * vxi11intr.rpcl
 *
 *	This file is best viewed with a tabwidth of 4
 *
 ******************************************************************************
 *
 * TODO:
 *
 ******************************************************************************
 *
 *	Original Author:	someone from VXIbus Consortium
 *	Current Author:		Benjamin Franksen
 *	Date:				03-06-97
 *
 *	RPCL description of the intr-channel of the TCP/IP Instrument Protocol 
 *	Specification.
 *
 *
 * Modification Log:
 * -----------------
 * .00	03-06-97	bfr		created this file
 *
 ******************************************************************************
 *
 * Notes: 
 *
 *	This stuff is literally from
 *
 *		"VXI-11, Ref 1.0 : TCP/IP Instrument Protocol Specification"
 *
 */
public interface vxi11_DEVICE_INTR_CltIF {

    /**
     * Call remote procedure device_intr_srq_1.
     * @param arg1 parameter (of type Device_SrqParms) to the remote procedure call.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    void device_intr_srq_1(Device_SrqParms arg1) throws OncRpcException, IOException;


}
// End of vxi11_DEVICE_INTR_CltIF.java
